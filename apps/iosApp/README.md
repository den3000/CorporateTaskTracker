# iosApp

The **iOS application** — an Xcode project (not a Gradle module). It consumes the `ComposeApp`
framework produced by `:shared-ui` and hosts it via SwiftUI.

## Build / run

Open `apps/iosApp/iosApp.xcodeproj` in Xcode and run, or:
```bash
./gradlew :shared-ui:linkDebugFrameworkIosSimulatorArm64   # produce the framework
xcodebuild -project apps/iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator build
```

## How it links Kotlin

The `iosApp` target has a **"Compile Kotlin Framework"** build phase that runs:
```sh
cd "$SRCROOT/../.."                                        # repo root (iosApp is two levels deep, under apps/)
./gradlew :shared-ui:embedAndSignAppleFrameworkForXcode
```
The framework baseName is **`ComposeApp`** (Swift does `import ComposeApp`) — keep it stable.

## Notes

- Entry symbol is `MainViewController()` in `shared-ui/src/iosMain`.
- Only builds in the **upstream** variant; the framework task does not exist under the Aurora fork,
  so ensure `compose.aurora.enabled=false` (the committed default) when building for iOS.
- `Configuration/Config.xcconfig.template` holds bundle-id / signing config.
