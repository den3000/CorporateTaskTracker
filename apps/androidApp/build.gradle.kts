import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    // Версия не указывается: Kotlin Gradle plugin уже на classpath сборки
    // (принесён kotlinMultiplatform из :shared-ui).
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        // :shared-ui компилируется с -Xexplicit-backing-fields (field = в VM),
        // из-за чего его метаданные помечены pre-release. Разрешаем их загрузку.
        freeCompilerArgs.add("-Xskip-prerelease-check")
    }
}

android {
    namespace = "ru.den.writes.code"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.den.writes.code"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.sharedUi)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.koin.compose)
    debugImplementation(libs.compose.uiTooling)
}
