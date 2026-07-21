// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
}
val marathonOutputDir = layout.buildDirectory.dir("reports/marathon")

val cleanMarathonResults by tasks.registering(Delete::class) {
    group = "verification"
    description = "Deletes old Marathon reports"

    delete(marathonOutputDir)
}

val assembleMarathonApks by tasks.registering {
    group = "verification"
    description = "Builds application and test APKs for Marathon"

    dependsOn(
        ":app:assembleDebug",
        ":app:assembleDebugAndroidTest"
    )
}

val runMarathon by tasks.registering(Exec::class) {
    group = "verification"
    description = "Runs tests with Marathon"

    dependsOn(
        cleanMarathonResults,
        assembleMarathonApks
    )
    workingDir = rootProject.projectDir
    commandLine("marathon")
    isIgnoreExitValue = true
    outputs.dir(
        layout.buildDirectory.dir("reports/marathon")
    )
}

tasks.register("runMarathonWithMergedAllure") {
    group = "verification"
    description = "Runs Marathon, merges Allure results and validates test result"

    dependsOn(":allure-merge:run")

    doLast {
        val exitCode = runMarathon.get().executionResult
            .get()
            .exitValue

        if (exitCode != 0) {
            throw GradleException(
                "Marathon tests failed with exit code $exitCode. " +
                        "Merged Allure results were generated successfully."
            )
        }
    }
}
