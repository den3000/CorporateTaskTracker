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
        // Локальный форк Compose под Аврору. Путь задаётся в local.properties (auroraMavenPath),
        // например ../aurora-maven-0.0.3. Если значения нет — обычный mavenLocal().
        // От compose.aurora.enabled НЕ зависит.
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
        // Локальный форк Compose под Аврору (см. pluginManagement выше): путь из
        // local.properties (auroraMavenPath), иначе mavenLocal(). Не зависит от aurora-флага.
        val auroraMavenPath = java.util.Properties().apply {
            val f = rootDir.resolve("local.properties")
            if (f.exists()) f.inputStream().use { load(it) }
        }.getProperty("auroraMavenPath")
        if (auroraMavenPath != null) {
            maven { url = rootDir.resolve(auroraMavenPath).canonicalFile.toURI() }
        } else {
            mavenLocal()
        }
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
