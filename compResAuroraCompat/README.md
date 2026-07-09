# :compResAuroraCompat

A **polyfill** of the JetBrains `org.jetbrains.compose.resources` API for Aurora OS (Linux/Qt), where
the upstream `compose.components.resources` artifact is unavailable. Used only by the Aurora variant.

## What it provides

Re-implements the resources runtime for `linuxArm64`/`linuxX64`: `Res`/`StringResource`/`ImageResource`
types, `stringResource()`/`imageResource()`/`fontResource()` composables, the qualifier/locale system,
caching, XML vector parsing, and a `ResourceReader` backed by Aurora `PathInfo`.

## ⚠️ Critical contract

`src/linuxMain/.../ResourceReader.linux.kt` resolves the on-device file path with:
```kotlin
path.substringAfter(".generated.resources/")
```
So the **Compose resources package in `:shared-ui` MUST end in `.generated.resources`**
(`ru.den.writes.code.generated.resources`). If it doesn't, `substringAfter` returns the whole URI,
every resource lookup fails, and the Aurora app launches to a blank white screen. See root `AGENTS.md`.

## Notes

- Linux-only module; depended on by `:shared-ui` (Aurora variant) and `:auroraApp`.
- Reads bundled resources via `ru.auroraos.kmp.pathInfo.PathInfo`; files are flattened to
  `<type>_<name>` on device.
