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

        // Локальный форк Compose под Аврору, путь из local.properties (auroraMavenPath), иначе mavenLocal()
        val auroraMavenPath = java.util.Properties().apply {
            val f = rootDir.resolve("local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (auroraMavenPath != null) {
            maven { url = rootDir.resolve(auroraMavenPath).canonicalFile.toURI() }
        } else {
            mavenLocal()
        }

        gradlePluginPortal()
    }

    plugins {
        // Вариант сборки выбирается свойством `buildVariant` (-PbuildVariant=aurora).
        // Aurora тянет форк Compose 0.0.4-aurora и плагины сборки/деплоя под Аврору;
        // upstream — обычный Compose Multiplatform.
        if (providers.gradleProperty("buildVariant").orNull == "aurora") {
            id("org.jetbrains.compose").version("0.0.4-aurora") apply false
            id("ru.auroraos.kmp.aurora-build").version("0.0.1") apply false
            id("ru.auroraos.kmp.aurora-devices").version("0.0.1") apply false
        } else {
            id("org.jetbrains.compose").version("1.10.2") apply false
        }
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
        mavenCentral()

        // Локальный форк Compose под Аврору, путь из local.properties (auroraMavenPath), иначе mavenLocal()
        val auroraMavenPath = java.util.Properties().apply {
            val f = rootDir.resolve("local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (auroraMavenPath != null) {
            maven { url = rootDir.resolve(auroraMavenPath).canonicalFile.toURI() }
        } else {
            mavenLocal()
        }
    }
}

val auroraOn = providers.gradleProperty("buildVariant").orNull == "aurora"

include(":shared-ui")
include(":server")
include(":shared")

// :shared-ui собирается по двум разным build-файлам:
// upstream — build.gradle.kts (Android/iOS), Aurora — build.aurora.gradle.kts (linux).
project(":shared-ui").buildFileName = if (auroraOn) "build.aurora.gradle.kts" else "build.gradle.kts"

// Приложения-таргеты подключаются по варианту сборки:
// upstream (Android/iOS) — :androidApp; Aurora — :auroraApp.
if (auroraOn) {
    include(":auroraApp")
    project(":auroraApp").projectDir = file("apps/auroraApp")
} else {
    include(":androidApp")
    project(":androidApp").projectDir = file("apps/androidApp")
}
