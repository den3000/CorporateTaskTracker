# :auroraApp

The **Aurora OS application**: Linux/Qt executables plus RPM packaging and SSH deploy. Depends on
`:shared-ui`.

## Contents

- `src/linuxMain/.../Main.kt` — `fun main()` (entryPoint `ru.den.writes.code.main`) +
  `HandleStatusBarOffset`, wrapping `App()` in a root `LocalViewModelStoreOwner` (see Notes).
- `build.gradle.kts` — `linuxArm64()/linuxX64()` executables with Qt5 `cmpLinkerOpts` (+ explicit
  `Qt5Network` for ktor-curl) and `freeCompilerArgs`; the `auroraBuild { rpm {} }` (RPM metadata) and
  `auroraDevices { devices {} packages {} }` (SSH device + RPM package) config.
- `aurora-tasks.gradle.kts` — thin wrapper: `runReleaseOnDevice` depends on `buildReleasePipeline`.
- `icons/` — Aurora launcher icons.

## Build / run

```bash
# compile-only (works on macOS, no SDK needed)
./gradlew -PbuildVariant=aurora :auroraApp:compileKotlinLinuxX64
# RPM build (init sysroot > link > RPM, in Docker)
./gradlew -PbuildVariant=aurora :auroraApp:buildReleasePipeline
# build + deploy + run (needs Aurora SDK + device at AURORA_DEVICE_IP)
./gradlew -PbuildVariant=aurora :auroraApp:runReleaseOnDevice
```
Only built in the **Aurora** variant (`-PbuildVariant=aurora`), which globally switches the Compose
plugin to the Aurora fork and applies the `aurora-build` / `aurora-devices` plugins.

## Notes

- The executable lives here, but the shared UI + `linuxArm64/X64` **library** targets (and Room KSP)
  live in `:shared-ui`.
- **Resources:** the `aurora-build` plugin packages only **this** module's `composeResources`, so
  `compose.resources.customDirectory("commonMain", …)` points at `:shared-ui`'s
  `src/commonMain/composeResources` (the same XML drawables as Android/iOS — rendered on Aurora by the
  `ru.den.writes.code.res.painterResource` polyfill, since the fork loader handles SVG only). The guard-catalog
  `src/commonMain/composeResources` is kept **empty** (created at configuration time, no `.gitkeep`).
  See root `AGENTS.md`.
- Provides a root `LocalViewModelStoreOwner` at the entry point — the desktop `application {}` gives
  none, and the real `koinViewModel()` requires it (else `No ViewModelStoreOwner`).
- Adds `-Xskip-prerelease-check` to consume `:shared-ui`.
- Physical folder `apps/auroraApp`; Gradle path `:auroraApp` (projectDir remap in settings).
