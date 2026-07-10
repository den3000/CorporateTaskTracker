plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

apply(from = "generateAppConfig.gradle.kts")
val generateAppConfigTask: TaskProvider<Task?>? = tasks.named("generateAppConfig")

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    // Библиотечные linux-таргеты: общий UI + linux-actual'ы + kspLinux*.
    // Исполняемый бинарник (entryPoint, Qt-линковка) объявлен в :auroraApp.
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain {
            kotlin.srcDir(generateAppConfigTask)
            // Заглушка @Preview — под linux в форке нет ui-tooling-preview.
            kotlin.srcDir("src/previewStub/kotlin")

            dependencies {
                // Форк-аксессоры Compose 0.0.4-aurora
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                // Навигация теперь есть как DSL-аксессор форка (reflection не нужен).
                implementation(compose.navigation)
                // Ресурсы — настоящий форк components-resources (рендерит только SVG).
                implementation(compose.components.resources)

                // Koin-форк под Аврору (заменяет полифил koinCompat)
                implementation(libs.aurora.koin.core)
                implementation(libs.aurora.koin.compose)
                implementation(libs.aurora.koin.compose.viewmodel)

                // Lifecycle-форк под Аврору
                implementation(libs.aurora.lifecycle.viewmodelCompose)
                implementation(libs.aurora.lifecycle.runtimeCompose)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.core)

                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)

                implementation(projects.shared)
            }
        }

        linuxMain.dependencies {
            implementation(libs.aurora.akPathInfo)
            implementation(libs.ktor.client.curl)
        }
    }
}

dependencies {
    add("kspLinuxArm64", libs.androidx.room.compiler)
    add("kspLinuxX64", libs.androidx.room.compiler)
}

compose.resources {
    generateResClass = always
    // Пакет сгенерированного Res закреплён явно, чтобы не зависеть от имени модуля.
    packageOfResClass = "ru.den.writes.code.generated.resources"
}
