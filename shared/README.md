# :shared

The lowest-layer **domain module** — pure Kotlin models and constants shared across every target,
including the server. No UI, no Compose, no platform frameworks.

## Contents

- `domain/model/Task.kt`, `domain/model/TaskPriority.kt` — `@Serializable` domain models used by both
  the client (`:shared-ui`) and the server.
- `Constants.kt` — shared constants (e.g. `SERVER_PORT`).
- `Platform.kt` (+ `Platform.{android,ios,jvm,linux}.kt`) — simple `expect/actual` platform info.
- `Greeting.kt` — sample.

## Targets

`androidTarget`, `iosArm64`, `iosSimulatorArm64`, `jvm`, `linuxArm64`, `linuxX64` — the union needed by
all consumers. Compiles identically for every variant (no `auroraEnabled` conditional here).

## Notes

Keep this module dependency-light: it is the common contract between the apps and the server. Changing
a model here affects serialization on both sides.
