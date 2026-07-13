# :shared-ui

The **KMP library** holding the shared Compose Multiplatform UI and application logic for all
client platforms (Android, iOS, Aurora OS). Every app module depends on this one.

## Responsibilities

- Root composable `App.kt` + all screens, components, theme, navigation (`ui/`, `navigation/`).
- View models (Koin-injected).
- Data layer: `data/local` (Room DB + DAO + `expect AppDatabaseConstructor`), `data/remote`
  (Ktor client), `data/repository` (offline-first sync).
- DI (`di/AppModule.kt`, `expect platformModule()`), networking (`network/`).
- Produces the iOS **`ComposeApp`** framework and owns **Room KSP** for all targets.

## Layout

- `commonMain` — platform-agnostic UI + logic; pins the Compose resources package to
  `ru.den.writes.code.generated.resources`.
- `androidMain` / `iosMain` / `linuxMain` — platform `actual`s (`PlatformModifier.*`,
  `PlatformModule.*`, `Database.*`) and `iosMain/MainViewController.kt` (the framework entry).
- `src/previewStub` — Aurora polyfill (no-op `@Preview`), added to `commonMain` in the Aurora variant.
- `res/PainterResource.*` + `src/linuxMain/.../vectorxml` — Aurora resources polyfill. Exposes a
  drop-in `painterResource(DrawableResource)` (package `ru.den.writes.code.res`) so call sites stay
  canonical: `painterResource(Res.drawable.icon)`. Android/iOS delegate to the native painter; Aurora
  resolves the drawable bytes via `getDrawableResourceBytes` and dispatches by content signature —
  raster (PNG/JPEG/WebP/BMP) → `BitmapPainter`, `<svg>` → Skia SVGDOM, otherwise Android vector XML →
  `ImageVector`. Keeps XML icons with working `tint` + dp sizes. Koin and navigation now use real fork
  libraries (see below).

## Notes

- **Two build files**, selected in `settings.gradle.kts` by `buildVariant`: `build.gradle.kts`
  (upstream: `com.android.library` + `androidTarget{}` + iOS, Maven `libs.compose.*`/`libs.koin.*`)
  and `build.aurora.gradle.kts` (Aurora: `linuxArm64/X64`, `compose.*` fork accessors incl.
  `compose.navigation`, Koin `4.2.0-aurora`, lifecycle fork).
- Compiled with `-Xexplicit-backing-fields`; consumers need `-Xskip-prerelease-check`.
- **Aurora runtime gotchas** (network off Main, keyboard `fillMaxHeight` clamp, root
  `LocalViewModelStoreOwner`, SVG icons "tint baked + explicit size") — see the "Aurora runtime
  gotchas" section in the root `AGENTS.md` before touching `network/`, the UI icons, or
  `PlatformModifier.linux.kt`.
