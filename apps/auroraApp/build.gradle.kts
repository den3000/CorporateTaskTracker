import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.auroraBuildTools)
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

buildTools {
    rpm {
        id = "ru.den.writes.code.corporate_task_tracker"
        name = "Corporate Task Tracker"
        description = "Corpora Task Tracker built with KMP for Aurora OS"
        version = "0.0.1"
        permissions = listOf("Internet")
        libs3rdParty = listOf("maliit-glib")
        icons = projectDir.toPath().resolve("icons")
        // Ресурсы Compose генерируются в :shared-ui.
        resources = rootProject.projectDir
            .resolve("shared-ui/build/generated/compose/resourceGenerator/preparedResources/commonMain/composeResources")
            .toPath()
    }

    // Run on device
    run {
        host = auroraDeviceIp
        user = "defaultuser"
        port = 22
        validate = true
        sshKey = File(System.getProperty("user.home")).resolve(".ssh/qtc_id").toPath()
    }
}

kotlin {
    compilerOptions {
        // :shared-ui скомпилирован с -Xexplicit-backing-fields (pre-release метаданные).
        freeCompilerArgs.add("-Xskip-prerelease-check")
    }

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

    sourceSets {
        linuxMain.dependencies {
            implementation(projects.sharedUi)
            implementation(projects.compResAuroraCompat)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.aurora.akPathInfo)
            implementation(libs.ktor.client.curl)
        }
    }
}

apply(from = "aurora-tasks.gradle.kts")
