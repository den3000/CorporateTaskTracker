# :auroraApp

The **Aurora OS application**: Linux/Qt executables plus RPM packaging and SSH deploy. Depends on
`:shared-ui` and `:compResAuroraCompat`.

## Contents

- `src/linuxMain/.../Main.kt` — `fun main()` (entryPoint `ru.den.writes.code.main`) +
  `HandleStatusBarOffset`, wrapping `App()`.
- `build.gradle.kts` — `linuxArm64()/linuxX64()` executables with Qt5 `cmpLinkerOpts`; the
  `buildTools { rpm {} run {} }` config (RPM metadata + SSH device).
- `aurora-tasks.gradle.kts` — orchestration tasks (`appSysroot` → build → package → run).
- `icons/` — Aurora launcher icons.

## Build / run

```bash
# compile-only (works on macOS, no SDK needed)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:compileKotlinLinuxX64
# full build + RPM + deploy (needs Aurora SDK 5.2.0.180 + device at AURORA_DEVICE_IP)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:appRunReleaseAfterBuild
```
Only built in the **Aurora** variant (`compose.aurora.enabled=true`), which globally switches the
Compose plugin to the Aurora fork.

## Notes

- The executable lives here, but the shared UI + `linuxArm64/X64` **library** targets (and Room KSP)
  live in `:shared-ui`. `rpm.resources` points at `:shared-ui`'s generated `composeResources` dir.
- Adds `-Xskip-prerelease-check` to consume `:shared-ui`.
- Physical folder `apps/auroraApp`; Gradle path `:auroraApp` (projectDir remap in settings).
- Runtime resource loading requires the `.generated.resources` package suffix — see root `AGENTS.md`.
