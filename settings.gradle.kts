rootProject.name = "CorporateTaskTracker"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }

    plugins {
        val composeVersion = if (providers.gradleProperty("compose.aurora.enabled").orNull == "true") {
            providers.gradleProperty("compose.version.aurora").get()
        } else {
            providers.gradleProperty("compose.version.upstream").get()
        }
        id("org.jetbrains.compose").version(composeVersion) apply false
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenLocal()
        mavenCentral()
    }
}

include(":shared-ui")
include(":server")
include(":shared")
include(":compResAuroraCompat")

// Приложения-таргеты подключаются по варианту сборки:
// upstream (Android/iOS) — :androidApp; Aurora — :auroraApp.
val auroraOn = providers.gradleProperty("compose.aurora.enabled").orNull == "true"
if (auroraOn) {
    include(":auroraApp")
    project(":auroraApp").projectDir = file("apps/auroraApp")
} else {
    include(":androidApp")
    project(":androidApp").projectDir = file("apps/androidApp")
}
