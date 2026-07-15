plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

application {
    mainClass.set("AllureMergeKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.named<JavaExec>("run") {
    dependsOn(rootProject.tasks.named("runMarathon"))
    workingDir = rootProject.projectDir

    inputs.dir(
        rootProject.layout.buildDirectory.dir(
            "reports/marathon/allure-results"
        )
    )
    inputs.dir(
        rootProject.layout.buildDirectory.dir(
            "reports/marathon/device-files/allure-results"
        )
    )

    outputs.dir(
        rootProject.layout.buildDirectory.dir(
            "reports/marathon/merged-allure-results"
        )
    )
}