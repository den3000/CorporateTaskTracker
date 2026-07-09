This is a Kotlin Multiplatform project targeting Android, iOS, Aurora OS, Server.

The shared Compose UI lives in a library module, while each platform has a thin
application module that depends on it:

* [/shared-ui](./shared-ui/src) — KMP **library** with the shared Compose UI, view models,
  data/repository/network layers and DI. It declares the Android/iOS/Linux targets, owns the
  Room database (+ KSP), and produces the iOS `ComposeApp` framework.
  - [commonMain](./shared-ui/src/commonMain/kotlin) is for code that’s common for all targets.
  - Platform folders ([androidMain](./shared-ui/src/androidMain/kotlin), [iosMain](./shared-ui/src/iosMain/kotlin),
    [linuxMain](./shared-ui/src/linuxMain/kotlin)) hold the platform `actual` declarations.
* [/apps/androidApp](./apps/androidApp/src) — thin Android application (`MainActivity`, launcher manifest, icons)
  depending on `:shared-ui`. Built for the upstream variant (default).
* [/apps/auroraApp](./apps/auroraApp/src) — Aurora OS application: Linux executables, the entry point,
  RPM packaging and SSH deploy. Built only for the Aurora variant
  (`-Pcompose.aurora.enabled=true`), which globally switches the Compose plugin to the Aurora fork.
* [/shared](./shared/src) — domain models shared across all targets (and the server).

* [/apps/iosApp](./apps/iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/apps/iosApp](./apps/iosApp) directory in Xcode and run it from there.

### Build and Run Aurora OS Application

The Aurora variant globally switches the Compose plugin to the Aurora fork, so it builds in a separate
Gradle invocation via `-Pcompose.aurora.enabled=true`. It requires the Aurora SDK and (for deploy) a
device reachable at `AURORA_DEVICE_IP` (see [NETWORK_CONFIG_README.md](./NETWORK_CONFIG_README.md)).
```shell
# compile-only check (works without the Aurora SDK)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:compileKotlinLinuxX64
# full build + RPM package + deploy to a device (needs Aurora SDK + device)
./gradlew -Pcompose.aurora.enabled=true :auroraApp:appRunReleaseAfterBuild
```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…