plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.compose")
    alias(libs.plugins.composeCompiler)
}

kotlin {
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        
        linuxMain.dependencies {
            implementation(libs.aurora.akPathInfo)
        }
    }
}
