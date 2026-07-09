# AGENTS.md

Guidance for AI agents (and humans) working in this repository. Read this before making
structural, build, or Aurora-related changes.

## What this is

**Corporate Task Tracker** — a Kotlin Multiplatform / Compose Multiplatform task tracker targeting
**Android, iOS, Aurora OS**, plus a **Ktor server**. The shared Compose UI lives in one library
module; each platform has a thin application module that depends on it.

Key versions: Kotlin `2.3.10`, AGP `8.11.2`, Compose MP `1.10.2` (upstream) / `0.0.3-aurora` (fork),
Koin `4.1.1`, Room `2.8.4`, Ktor `3.4.1` (`3.1.2-aurora` curl client), minSdk 24 / compileSdk 36.

## Module graph

```
:shared              domain models (Task, TaskPriority, SERVER_PORT). No UI. All targets.
   ▲
:shared-ui           KMP LIBRARY: shared Compose UI + VMs + data/network/di/navigation.
   │                 Declares android/ios/linux targets, owns Room + KSP, builds the iOS
   │                 "ComposeApp" framework. Holds the internal `auroraEnabled` conditional.
   ├── depends on :shared
   └── depends on :compResAuroraCompat        (only in the Aurora variant)
   ▲              ▲                ▲
apps/androidApp  apps/auroraApp   :server
:androidApp      :auroraApp       Ktor JVM backend, depends on :shared only.
(com.android      (linux exe +
 .application)     RPM/SSH deploy)
```

`apps/iosApp/` is an Xcode project (not a Gradle module) consuming the `ComposeApp` framework.
`:server` stays at repo root (backend, not a client app). Type-safe accessors: `:shared-ui` →
`projects.sharedUi`.

## Build variants — the single most important concept

The property **`compose.aurora.enabled`** (in `gradle.properties`, default `false` = upstream)
switches, **at `settings.gradle.kts` level**, the whole Compose Gradle plugin version:
`1.10.2` (upstream, Maven Central) vs `0.0.3-aurora` (Aurora fork). Because the plugin is chosen
**once, globally per Gradle invocation**:

- **Android/iOS and Aurora can NEVER build in the same invocation.**
- `settings.gradle.kts` also conditionally includes the app module: `:androidApp` when off,
  `:auroraApp` when on.
- Inside `:shared-ui/build.gradle.kts`, `auroraEnabled` switches targets (android/ios vs linux),
  dependency origin (`libs.compose.*` Maven vs `compose.*` fork accessor), extra source dirs
  (`koinCompat`, `previewStub`), and KSP targets.

To build/run Aurora, pass `-Pcompose.aurora.enabled=true`. Everything else defaults to upstream.

## Commands

```bash
# --- upstream (default: aurora off) ---
./gradlew :androidApp:assembleDebug                      # Android APK
./gradlew :shared-ui:linkDebugFrameworkIosSimulatorArm64 # iOS framework (macOS)
xcodebuild -project apps/iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator build
./gradlew :server:run                                    # Ktor server on :8080

# --- Aurora (separate invocation, needs the flag) ---
./gradlew -Pcompose.aurora.enabled=true :auroraApp:compileKotlinLinuxX64          # compile-only (works on macOS)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:appRunReleaseAfterBuild        # build + RPM + deploy (needs Aurora SDK + device)
```

Aurora link/RPM/deploy require the **Aurora SDK** and a device reachable at `AURORA_DEVICE_IP`
(see `NETWORK_CONFIG_README.md`). Only compile-only is verifiable on a plain macOS.

## Critical invariants — do not break these

1. **Compose resources package MUST end in `.generated.resources`.** The Aurora resource polyfill
   `compResAuroraCompat/.../ResourceReader.linux.kt` extracts the on-device path via
   `substringAfter(".generated.resources/")`. `:shared-ui` pins
   `packageOfResClass = "ru.den.writes.code.generated.resources"`. Dropping the suffix compiles
   fine on Android/iOS but makes **every** Aurora resource load fail → blank white screen.
2. **`:shared-ui` compiles with `-Xexplicit-backing-fields`** (uses `field =` in view models),
   which marks its metadata pre-release. Every module that consumes it (`:androidApp`, `:auroraApp`)
   must add `freeCompilerArgs += "-Xskip-prerelease-check"`.
3. **iOS framework baseName is `ComposeApp`** and must stay so — the Swift `import ComposeApp` and
   the pbxproj embed refs depend on it. The pbxproj "Compile Kotlin Framework" phase runs
   `cd "$SRCROOT/../.."` (repo root) then `./gradlew :shared-ui:embedAndSignAppleFrameworkForXcode`.
   The iOS build only works with the upstream variant (aurora off) — the framework task doesn't
   exist under the Aurora fork.
4. **Aurora-only polyfills live in `:shared-ui` source dirs** added conditionally: `src/koinCompat`
   (Koin Compose polyfill) and `src/previewStub` (`@Preview` stub). `:compResAuroraCompat` polyfills
   the `org.jetbrains.compose.resources` API for Linux. Navigation on Aurora is pulled via a
   reflection hack (`compose.javaClass.getMethod("getNavigation")`) to dodge the Kotlin DSL analyzer.
5. **Room KSP stays entirely in `:shared-ui`** (the only module declaring android/ios/linux targets).
   App modules declare no targets and no KSP.

## Entry points

- Android: `apps/androidApp/.../MainActivity.kt` → `App(koinConfig = { androidContext(...) })`
- iOS: `shared-ui/src/iosMain/.../MainViewController.kt` (`fun MainViewController()`, consumed by Xcode)
- Aurora: `apps/auroraApp/.../Main.kt` (`fun main()`, entryPoint `ru.den.writes.code.main`)
- Root composable: `shared-ui/src/commonMain/.../App.kt`
- Platform `actual`s: `shared-ui/.../PlatformModifier.{android,linux}.kt`, `PlatformModule.*`, `Database.*`

## Conventions

- Work in a feature branch; don't commit to `main` directly.
- Keep commits small and self-contained; validate the relevant target(s) before committing.
- End commit messages with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.

## Deferred / planned work

Migration to **aurora-maven 0.0.4**: replace `compose.aurora.enabled` with a `buildVariant` property,
per-variant build files (`build.aurora.gradle.kts`), and the `aurora-build` / `aurora-devices` plugins
(splitting the old monolithic `auroraBuildTools`). Gated on 0.0.4 artifacts in `~/.m2` + Aurora SDK
`5.2.0.180`. See the module restructure plan for details.
