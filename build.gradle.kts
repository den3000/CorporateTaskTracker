plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("org.jetbrains.compose") apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false

    alias(libs.plugins.auroraBuildTools) apply false
    alias(libs.plugins.spotless) apply true
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**build**")
        licenseHeaderFile("$rootDir/copyright")
        ktlint("1.6.0").editorConfigOverride(
            mapOf(
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            )
        )
    }
    format("misc") {
        target("**/*.gradle", "**/*.md", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
}