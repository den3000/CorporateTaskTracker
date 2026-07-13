# AGENTS.md

Guidance for AI agents (and humans) working in this repository. Read this before making
structural, build, or Aurora-related changes.

## What this is

**Corporate Task Tracker** — a Kotlin Multiplatform / Compose Multiplatform task tracker targeting
**Android, iOS, Aurora OS**, plus a **Ktor server**. The shared Compose UI lives in one library
module; each platform has a thin application module that depends on it.

Key versions: Kotlin `2.3.10`, AGP `8.11.2`, Compose MP `1.10.2` (upstream) / `0.0.4-aurora` (fork),
Koin `4.1.1` (upstream) / `4.2.0-aurora` (fork), Room `2.8.4`, Ktor `3.4.1` (`3.4.2-aurora` curl
client), minSdk 24 / compileSdk 36. Aurora plugins `ru.auroraos.kmp.aurora-build` /
`ru.auroraos.kmp.aurora-devices` `0.0.1`.

## Module graph

```
:shared              domain models (Task, TaskPriority, SERVER_PORT). No UI. All targets.
   ▲
:shared-ui           KMP LIBRARY: shared Compose UI + VMs + data/network/di/navigation.
   │                 Declares android/ios/linux targets, owns Room + KSP, builds the iOS
   │                 "ComposeApp" framework. Two build files — see below.
   └── depends on :shared
   ▲                               ▲
apps/androidApp  apps/auroraApp   :server
:androidApp      :auroraApp       Ktor JVM backend, depends on :shared only.
(com.android      (linux exe +
 .application)     RPM/SSH deploy)
```

`apps/iosApp/` is an Xcode project (not a Gradle module) consuming the `ComposeApp` framework.
`:server` stays at repo root (backend, not a client app). Type-safe accessors: `:shared-ui` →
`projects.sharedUi`.

## Build variants — the single most important concept

The property **`buildVariant`** selects the variant. Default (unset) = **upstream** (Android/iOS);
**`-PbuildVariant=aurora`** = Aurora. The switch happens **at `settings.gradle.kts` level** and picks
the whole Compose Gradle plugin version + Aurora plugins:

- upstream → `org.jetbrains.compose 1.10.2` (Maven Central).
- aurora → `org.jetbrains.compose 0.0.4-aurora` (fork) + `ru.auroraos.kmp.aurora-build` +
  `ru.auroraos.kmp.aurora-devices` `0.0.1`.

Because the plugin set is chosen **once, globally per Gradle invocation**:

- **Android/iOS and Aurora can NEVER build in the same invocation.**
- `settings.gradle.kts` conditionally includes the app module (`:androidApp` upstream / `:auroraApp`
  aurora) and overrides `:shared-ui` build file:
  `project(":shared-ui").buildFileName = if (auroraOn) "build.aurora.gradle.kts" else "build.gradle.kts"`.
- **`:shared-ui` has two separate build files** — `build.gradle.kts` (upstream: android/ios,
  `libs.compose.*` Maven, `libs.koin.*`, `libs.navigation.compose`) and `build.aurora.gradle.kts`
  (aurora: linuxArm64/linuxX64, `compose.*` fork accessors incl. `compose.navigation`, aurora
  koin/lifecycle forks). Each build file is self-contained — no in-file variant conditionals.

**Where the Aurora fork resolves from.** `settings.gradle.kts` reads `auroraMavenPath` from
`local.properties` (machine-specific, git-ignored). If set (e.g. `auroraMavenPath=../aurora-maven-0.0.4`,
resolved relative to the repo root), that local maven folder is used **instead of** `mavenLocal()`/`~/.m2`;
if unset, it falls back to the standard `mavenLocal()`. This applies to both variants and lives outside
the git repo.

## Commands

```bash
# --- upstream (default) ---
./gradlew :androidApp:assembleDebug                      # Android APK
./gradlew :shared-ui:linkDebugFrameworkIosSimulatorArm64 # iOS framework (macOS)
xcodebuild -project apps/iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator build
./gradlew :server:run                                    # Ktor server on :8080

# --- Aurora (separate invocation, needs -PbuildVariant=aurora) ---
./gradlew -PbuildVariant=aurora :auroraApp:compileKotlinLinuxX64   # compile-only (works on macOS)
./gradlew -PbuildVariant=aurora :auroraApp:buildReleasePipeline    # init sysroot > link > RPM (Docker + Aurora SDK)
./gradlew -PbuildVariant=aurora :auroraApp:runReleaseOnDevice      # build pipeline + deploy + run on device
```

`runReleaseOnDevice` depends on `buildReleasePipeline` (wired in `apps/auroraApp/aurora-tasks.gradle.kts`).
RPM is built **in Docker** (Aurora SDK image; signing keys `~/AuroraOS/package-signing`) and deployed over
SSH to `AURORA_DEVICE_IP` (see `NETWORK_CONFIG_README.md`). Only compile-only is verifiable on a plain macOS.

## Critical invariants — do not break these

1. **Aurora renders Android vector XML via our own parser; resources are packaged through
   `customDirectory` + an empty guard-catalog.** The fork `components-resources` under Linux renders
   **SVG only** — feeding `painterResource` an Android Vector Drawable (`<vector android:pathData>`)
   hangs on a blank white screen (no crash). So Aurora keeps the **same XML drawables** as
   Android/iOS but never lets the fork's loader touch a drawable. Common UI imports a **drop-in
   `painterResource(DrawableResource)`** from the polyfill package `ru.den.writes.code.res` (so call
   sites stay canonical — `painterResource(Res.drawable.some_icon)`, no path string). It's expect/actual:
   Android/iOS delegate to the native `painterResource`; the **linux** actual resolves the drawable's
   bytes via the public `getDrawableResourceBytes(env, resource)` and dispatches by **content signature**
   (magic bytes, so no path/extension is needed): PNG/JPEG/WebP/BMP → `BitmapPainter` (raster), `<svg>` →
   Skia SVGDOM painter, otherwise Android vector XML → an `ImageVector` via the vendored parser in
   `shared-ui/src/linuxMain/.../vectorxml/` (`parse` + `toImageVector` + `ValueParser`, pure Kotlin,
   AOSP-derived). For vectors the result is an `ImageVector`, so `Icon(tint = …)`
   and intrinsic dp sizes work normally (incl. dark theme); SVG colors are baked (tint doesn't apply,
   same as the fork's own SVG painter).
   **Packaging:** the `aurora-build` plugin only packages the resources of **its own** module,
   so `:auroraApp/build.gradle.kts` sets `compose.resources.customDirectory("commonMain",
   shared-ui/src/commonMain/composeResources)`. Additionally `:auroraApp/src/commonMain/composeResources`
   must exist **empty** (created at configuration time via `…asFile.mkdirs()`) — **no `.gitkeep`**,
   because a dotfile breaks `rpmbuild` (`resources/*` doesn't match dotfiles → "installed but unpackaged").
2. **`:shared-ui` compiles with `-Xexplicit-backing-fields`** (uses `field =` in view models),
   which marks its metadata pre-release. Every module that consumes it (`:androidApp`, `:auroraApp`)
   must add `freeCompilerArgs += "-Xskip-prerelease-check"`.
3. **iOS framework baseName is `ComposeApp`** and must stay so — the Swift `import ComposeApp` and
   the pbxproj embed refs depend on it. The pbxproj "Compile Kotlin Framework" phase runs
   `cd "$SRCROOT/../.."` (repo root) then `./gradlew :shared-ui:embedAndSignAppleFrameworkForXcode`.
   The iOS build only works with the upstream variant — the framework task doesn't exist under the fork.
4. **Two small Aurora polyfills exist.** `shared-ui/src/previewStub` (a no-op `@Preview` annotation —
   the fork ships `ui-tooling-preview` only for jvm/android) and `shared-ui/src/linuxMain/.../vectorxml`
   (Android-vector-XML → `ImageVector` parser, because the fork's `components-resources` renders SVG
   only — see invariant #1). Everything else uses **real fork libraries**: Koin `4.2.0-aurora`, the
   `compose.navigation` DSL accessor, and `components-resources`.
5. **Room KSP stays entirely in `:shared-ui`** (the only module declaring android/ios/linux targets).
   App modules declare no targets and no KSP.

## Aurora runtime gotchas — hard-won, don't regress

These only bite on Aurora (its Main dispatcher is stricter); Android/iOS are unaffected.

1. **Keep blocking work off the Main dispatcher.** All HTTP (status polling, `isOnline()` before each
   write, add/delete/get in `RemoteTasksDataSource`) runs on `Dispatchers.IO` (`flowOn` / `withContext`)
   with a ping timeout. On Aurora a blocking call on Main freezes recomposition, navigation and gesture
   animations. Note `Dispatchers.IO` is `internal` in common/native — import the `kotlinx.coroutines.IO`
   extension (provided by Room), as `Database.kt` does. Keep the Qt keyboard poll
   (`PlatformModifier.linux.kt`) on Main — Qt objects are thread-affine.

2. **`fillMaxHeight(fraction)` throws if `fraction` is outside `(0, 1]`.** The Aurora keyboard modifier
   derives the fraction from keyboard/window height; clamp it (apply a partial height only when
   `kbdHeight in 1 until windowHeight`, else `1f`), or the app crashes when the keyboard opens before
   the window is measured. Stabilize the 100 ms poll flow with `remember` + `distinctUntilChanged`.

3. **Provide a root `LocalViewModelStoreOwner` at the Aurora entry point.** The desktop `application {}`
   gives no `LocalViewModelStoreOwner` (Android gets it from Activity, iOS from ViewController), but the
   real `koinViewModel()` requires one already at the `App` root (`SettingsViewModel` sits above
   navigation) → without it the app crashes with `No ViewModelStoreOwner`. `apps/auroraApp/.../Main.kt`
   wraps `App()` in `CompositionLocalProvider(LocalViewModelStoreOwner provides remember { … })`.

4. **Import `painterResource` from `ru.den.writes.code.res`, not `org.jetbrains.compose.resources`.**
   The two have the same signature, so call sites read canonically; the difference is the import. The
   polyfill keeps Android/iOS on the native painter and routes Aurora through the vector-XML parser (see
   invariant #1), so the fork's SVG-only loader never sees a drawable. The resulting `ImageVector`
   honours `Icon(tint = …)` and intrinsic dp sizes (the `.size(24.dp)` modifiers on the icons are
   belt-and-suspenders — the `ImageVector` already carries the intrinsic 24dp size). On Aurora the read
   is async; a transparent 24dp placeholder shows for ~1 frame while the vector loads.

## Entry points

- Android: `apps/androidApp/.../MainActivity.kt` → `App(koinConfig = { androidContext(...) })`
- iOS: `shared-ui/src/iosMain/.../MainViewController.kt` (`fun MainViewController()`, consumed by Xcode)
- Aurora: `apps/auroraApp/.../Main.kt` (`fun main()`, entryPoint `ru.den.writes.code.main`; provides the
  root `LocalViewModelStoreOwner`)
- Root composable: `shared-ui/src/commonMain/.../App.kt`
- Platform `actual`s: `shared-ui/.../PlatformModifier.{android,linux}.kt`, `PlatformModule.*`, `Database.*`

## Conventions

- Work in a feature branch; don't commit to `main` directly.
- Keep commits small and self-contained; validate the relevant target(s) before committing.
- End commit messages with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
