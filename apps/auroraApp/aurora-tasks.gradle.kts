val Project.auroraEnabled: Boolean
    get() = findProperty("compose.aurora.enabled") == "true"

if (auroraEnabled) {
    // 1. Init sysroot for dependency
    tasks.register("appSysroot") {
        dependsOn("initSysroot")
    }

    // 2. Build KMP application
    tasks.register("appBuildRelease") {
        dependsOn("linkReleaseExecutableLinuxX64")
        dependsOn("linkReleaseExecutableLinuxArm64")
    }

    // 3. Build RPM package
    tasks.register("appPackageRelease") {
        dependsOn("buildReleasePackageLinuxX64")
        dependsOn("buildReleasePackageLinuxArm64")
    }

    // 4. Run application
    tasks.register("appRunRelease") {
        dependsOn("runReleasePackage")
    }

    // Strict order of command execution
    val appPackageReleaseOrder = tasks.register("appPackageReleaseOrder") {
        dependsOn("appPackageRelease")
        finalizedBy("appRunRelease")
    }
    val appBuildReleaseOrder = tasks.register("appBuildReleaseOrder") {
        dependsOn("appBuildRelease")
        finalizedBy(appPackageReleaseOrder)
    }
    tasks.register("appRunReleaseAfterBuild") {
        group = "Aurora Build-Tools"
        description = "Strict order: initSysroot > build KMP > build RPM > run."
        dependsOn("appSysroot")
        finalizedBy(appBuildReleaseOrder)
    }
}
