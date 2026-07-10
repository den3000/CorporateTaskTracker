import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
    id("ru.auroraos.kmp.aurora-build")
    id("ru.auroraos.kmp.aurora-devices")
}

// IP Aurora-устройства для деплоя по SSH.
// Приоритет: терминал (-P) -> local.properties -> дефолт.
val auroraDeviceIp: String = run {
    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
    (project.findProperty("AURORA_DEVICE_IP") as? String)
        ?: localProperties.getProperty("AURORA_DEVICE_IP")
        ?: "192.168.0.22"
}

// Пустой guard-каталог composeResources обязан существовать: плагин compose
// пакует ресурсы только из своего модуля. Реальный набор приходит через
// customDirectory ниже; сам каталог держим ПУСТЫМ (без .gitkeep — дотфайл
// ломает rpmbuild). Создаём на этапе конфигурации, без doFirst/захвата Project.
layout.projectDirectory.dir("src/commonMain/composeResources").asFile.mkdirs()

auroraBuild {
    rpm {
        id.set("ru.den.writes.code.corporate_task_tracker")
        name.set("Corporate Task Tracker")
        description.set("Corporate Task Tracker built with KMP for Aurora OS")
        version.set("0.0.1")
        permissions.set(listOf("Internet"))
        libs3rdParty.set(listOf("maliit-glib"))
        icons.set(projectDir.toPath().resolve("icons"))
        // ВАЖНО: свойства `resources` у плагина нет — ресурсы приходят через
        // compose.resources.customDirectory (см. ниже), плагин пакует
        // preparedResources этого модуля.
    }
}

auroraDevices {
    devices {
        // Без имени → устройство `device`, таск деплоя = runReleaseOnDevice.
        create {
            host.set(auroraDeviceIp)
            user.set("defaultuser")
            port.set(22)
            sshKey.set(File(System.getProperty("user.home")).resolve(".ssh/qtc_id").toPath())
        }
    }
    packages {
        create("release") {
            targets.set(listOf("aarch64", "x86_64"))
            directory.set(
                layout.buildDirectory.dir("rpm/release/{target}/RPMS/{target}").get().asFile.toPath(),
            )
            mask.set("""(?!.*debug).*\.rpm""")
        }
    }
}

kotlin {
    compilerOptions {
        // :shared-ui скомпилирован с -Xexplicit-backing-fields (pre-release метаданные).
        freeCompilerArgs.add("-Xskip-prerelease-check")
    }

    listOf(
        linuxArm64(),
        linuxX64(),
    ).forEach { target ->
        target.binaries {
            executable {
                entryPoint = "ru.den.writes.code.main"
                // Обязателен: -Xoverride-konan-properties (иначе линковка против sysroot падает).
                freeCompilerArgs += auroraBuild.freeCompilerArgs(target.name)
                // cmpLinkerOpts сам добавляет Qt5Core/maliit/skiko/wayland/EGL/dbus,
                // Qt5Network (для ktor-curl) передаём явно.
                linkerOpts.addAll(auroraBuild.cmpLinkerOpts(target.name, "Qt5Network"))
            }
        }
    }

    sourceSets {
        linuxMain.dependencies {
            implementation(projects.sharedUi)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            // LocalViewModelStoreOwner / ViewModelStore для корневого owner в Main.kt.
            // :shared-ui тянет его как implementation — наружу не виден.
            implementation(libs.aurora.lifecycle.viewmodelCompose)

            implementation(libs.aurora.akPathInfo)
            implementation(libs.ktor.client.curl)
        }
    }
}

compose.resources {
    // Res-класс генерирует :shared-ui, здесь только упаковка ресурсов.
    generateResClass = never
    // Под Аврору берём набор с SVG-иконками из :shared-ui (плагин aurora-build
    // пакует preparedResources ИМЕННО этого модуля, зависимости не видит).
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = provider {
            rootProject.layout.projectDirectory.dir("shared-ui/aurora-composeResources")
        },
    )
}

apply(from = "aurora-tasks.gradle.kts")
