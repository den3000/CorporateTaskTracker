# :androidApp

Thin **Android application** (`com.android.application`) that hosts the shared UI from `:shared-ui`.

## Contents

- `MainActivity.kt` — entry point; calls `App(koinConfig = { androidContext(...) })`.
- `AndroidManifest.xml` — launcher `<application>` + activity, `INTERNET`, cleartext (dev server).
- `res/` — launcher icons (mipmaps) and `strings.xml`.

## Build / run

```bash
./gradlew :androidApp:assembleDebug
```
Only built in the **upstream** variant (`compose.aurora.enabled=false`, the default); it is excluded
from the Gradle build when the Aurora variant is active.

## Notes

- Applies the Kotlin Android plugin by `id("org.jetbrains.kotlin.android")` **without a version**
  (it is already on the build classpath via `:shared-ui`'s `kotlinMultiplatform`).
- Adds `-Xskip-prerelease-check` to consume `:shared-ui` (compiled with `-Xexplicit-backing-fields`).
- The physical folder is `apps/androidApp`; the Gradle path stays `:androidApp` (projectDir remap in
  `settings.gradle.kts`).
