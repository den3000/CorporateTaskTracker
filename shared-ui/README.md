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
  `ru.den.writes.code.generated.resources` (the `.generated.resources` suffix is **required** by the
  Aurora polyfill — see root `AGENTS.md`).
- `androidMain` / `iosMain` / `linuxMain` — platform `actual`s (`PlatformModifier.*`,
  `PlatformModule.*`, `Database.*`) and `iosMain/MainViewController.kt` (the framework entry).
- `src/koinCompat`, `src/previewStub` — Aurora-only polyfills, added to `commonMain` when
  `compose.aurora.enabled=true`.

## Notes

- Built as `com.android.library` + `androidTarget{}` (not the newer `androidLibrary{}` DSL).
- Compiled with `-Xexplicit-backing-fields`; consumers need `-Xskip-prerelease-check`.
- Targets are variant-conditional: android+ios (upstream) **or** linux (Aurora). See root `AGENTS.md`.
- **Aurora runtime gotchas** (network off Main, keyboard `fillMaxHeight` clamp, koinCompat ViewModel
  caching) — see the "Aurora runtime gotchas" section in the root `AGENTS.md` before touching
  `network/`, `koinCompat/`, or `PlatformModifier.linux.kt`.
