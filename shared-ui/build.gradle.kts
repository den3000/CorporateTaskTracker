import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val Project.auroraEnabled: Boolean
    get() = findProperty("compose.aurora.enabled") == "true"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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

    if (!auroraEnabled) {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
                // Required when using NativeSQLiteDriver
                linkerOpts.add("-lsqlite3")
            }
        }
    } else {
        // Библиотечные linux-таргеты: общий UI + linux-actual'ы + kspLinux*.
        // Исполняемый бинарник (entryPoint, Qt-линковка) объявлен в :auroraApp.
        linuxArm64()
        linuxX64()
    }

    sourceSets {
        sourceSets {
            commonMain {
                kotlin.srcDir(generateAppConfigTask)

                // Добавляем папку с заглушкой для Preview только для сборки Авроры
                if (auroraEnabled) {
                    kotlin.srcDir("src/previewStub/kotlin")
                    kotlin.srcDir("src/koinCompat/kotlin")

                    kotlin.srcDir(tasks.named("generateComposeResClass"))
                }

                dependencies {
                    if (!auroraEnabled) {
                        // Есть аналог для Аврора ОС
                        implementation(libs.compose.runtime)
                        implementation(libs.compose.foundation)
                        implementation(libs.compose.material3)
                        implementation(libs.compose.ui)
                        implementation(libs.navigation.compose)

                        // Не доступно для Аврора ОС
                        // 1. исправлено с помощью Preview-stub
                        implementation(libs.compose.uiToolingPreview)

                        // 2. Исправлено с помощью Koin-Compat
                        implementation(libs.koin.compose)
                        implementation(libs.koin.compose.viewmodel)
                        implementation(libs.androidx.lifecycle.viewmodelCompose)

                        // 3.
                        implementation(libs.compose.components.resources)
                        // Добавляем только нужные иконки в composeResources
                        // implementation("org.jetbrains.compose.material:material-icons-extended:1.7.0")
                    } else {
                        // Аналог для Аврора ОС
                        implementation(compose.runtime)
                        implementation(compose.foundation)
                        implementation(compose.material3)
                        implementation(compose.ui)
                        /*
                           implementation(compose.navigation) - ломает сборку Android / iOS
                           Динамически получаем compose.navigation, чтобы обойти статический
                           анализатор Kotlin DSL в Android Studio при auroraEnabled == false
                         */
                        val auroraNavigation = compose.javaClass.getMethod("getNavigation").invoke(compose)
                        implementation(auroraNavigation)

                        // Зависимость на полифил compose.resources только для Аврора ОС
                        implementation(projects.compResAuroraCompat)
                    }

                    // Доступно везде
                    implementation(libs.androidx.lifecycle.runtimeCompose)
                    implementation(libs.kotlinx.serialization.json)
                    implementation(libs.koin.core)
                    implementation(libs.ktor.client.core)

                    implementation(libs.androidx.room.runtime)
                    implementation(libs.androidx.sqlite.bundled)

                    implementation(projects.shared)
                }
            }

            // 3. Специфичные для платформ Source Sets
            if (!auroraEnabled) {
                androidMain.dependencies {
                    implementation(libs.compose.uiToolingPreview)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.ktor.client.okhttp)
                }
                iosMain.dependencies {
                    implementation(libs.ktor.client.darwin)
                }
                commonTest.dependencies {
                    implementation(libs.kotlin.test)
                }
            } else {
                linuxMain.dependencies {
                    implementation(libs.aurora.akPathInfo)
                    implementation(libs.ktor.client.curl)
                }
            }
        }
    }
}

if (!auroraEnabled) {
    dependencies {
        add("kspAndroid", libs.androidx.room.compiler)
        add("kspIosArm64", libs.androidx.room.compiler)
        add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    }
} else {
    dependencies {
        add("kspLinuxArm64", libs.androidx.room.compiler)
        add("kspLinuxX64", libs.androidx.room.compiler)
    }
}


android {
    namespace = "ru.den.writes.code"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.resources {
    generateResClass = always
    // Пакет сгенерированного Res закреплён явно, чтобы не зависеть от имени модуля.
    packageOfResClass = "ru.den.writes.code.resources"
}
