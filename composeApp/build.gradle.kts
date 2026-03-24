import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val Project.auroraEnabled: Boolean
    get() = findProperty("compose.aurora.enabled") == "true"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)

    alias(libs.plugins.auroraBuildTools)
}

apply(from = "generateAppConfig.gradle.kts")
val generateAppConfigTask: TaskProvider<Task?>? = tasks.named("generateAppConfig")

room {
    schemaDirectory("$projectDir/schemas")
}

buildTools {
    rpm {
        id = "ru.den.writes.code.corporate_task_tracker"
        name = "Corporate Task Tracker"
        description = "Corpora Task Tracker built with KMP for Aurora OS"
        version = "0.0.1"
        permissions = listOf("Internet")
        libs3rdParty = listOf("maliit-glib")
        icons = projectDir.toPath().resolve("icons")
        resources = projectDir.toPath().resolve("src/commonMain/composeResources")
    }

    // Run on device
    run {
        host = "192.168.0.18"
        user = "defaultuser"
        port = 22
        validate = true
        sshKey = File(System.getProperty("user.home")).resolve(".ssh/qtc_id").toPath()
    }
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
        listOf(
            linuxArm64(),
            linuxX64()
        ).forEach { target ->
            target.binaries {
                executable {
                    entryPoint = "ru.den.writes.code.main"
                    linkerOpts.addAll(buildTools.cmpLinkerOpts(
                        project = project,
                        targetName = target.name,
                        "Qt5Core",
                        "Qt5Network",
                    ))
                }
            }
        }
    }

    sourceSets {
        sourceSets {
            commonMain {
                kotlin.srcDir(generateAppConfigTask)
                
                // Добавляем папку с заглушкой для Preview только для сборки Авроры
                if (auroraEnabled) {
                    kotlin.srcDir("src/previewStub/kotlin")
                    kotlin.srcDir("src/koinCompat/kotlin")
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
                        implementation("org.jetbrains.compose.material:material-icons-extended:1.7.0")
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
    debugImplementation(libs.compose.uiTooling)
}

apply(from = "aurora-tasks.gradle.kts")
