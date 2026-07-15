import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun main() {
    val rootDir = Path.of(System.getProperty("user.dir"))
    val marathonDir = rootDir.resolve("build/reports/marathon/allure-results")
    val deviceDir = rootDir.resolve("build/reports/marathon/device-files/allure-results")
    val mergedDir = rootDir.resolve("build/reports/marathon/merged-allure-results")

    require(Files.exists(marathonDir)) {
        "Marathon results not found: $marathonDir"
    }
    require(Files.exists(deviceDir)) {
        "Device results not found: $deviceDir"
    }

    if (Files.exists(mergedDir)) {
        mergedDir.toFile().deleteRecursively()
    }
    mergedDir.createDirectories()

    val deviceResultsByFullName = deviceDir
        .listDirectoryEntries("*-result.json")
        .associateBy { path ->
            readJson(path)
                .getValue("fullName")
                .jsonPrimitive
                .content
        }

    marathonDir
        .listDirectoryEntries("*-result.json")
        .forEach { marathonResultPath ->
            val marathonResult = readJson(marathonResultPath)
            val fullName = marathonResult
                .getValue("fullName")
                .jsonPrimitive
                .content

            val deviceResultPath = deviceResultsByFullName[fullName]
            val rewrittenMarathonAttachments = rewriteAndCopyAttachments(
                attachments = marathonResult["attachments"] as? JsonArray
                    ?: JsonArray(emptyList()),
                mergedDir = mergedDir
            )

            val mergedResult = if (deviceResultPath != null) {
                val deviceResult = readJson(deviceResultPath)

                val deviceAttachments =
                    deviceResult["attachments"] as? JsonArray
                        ?: JsonArray(emptyList())

                JsonObject(
                    marathonResult.toMutableMap().apply {
                        this["steps"] =
                            deviceResult["steps"] ?: JsonArray(emptyList())

                        this["attachments"] = JsonArray(
                            rewrittenMarathonAttachments + deviceAttachments
                        )
                    }
                )
            } else {
                println("Device result not found for: $fullName")

                JsonObject(
                    marathonResult.toMutableMap().apply {
                        this["attachments"] = rewrittenMarathonAttachments
                    }
                )
            }

            Files.writeString(
                mergedDir.resolve(marathonResultPath.name),
                json.encodeToString(mergedResult)
            )
        }

    copyDeviceArtifacts(deviceDir, mergedDir)
    copyServiceFiles(marathonDir, mergedDir)

    println("Merged Allure results created:")
    println(mergedDir)
}

private fun rewriteAndCopyAttachments(
    attachments: JsonArray,
    mergedDir: Path
): JsonArray {
    return JsonArray(
        attachments.map { attachmentElement ->
            val attachment = attachmentElement.jsonObject
            val source = attachment["source"]?.jsonPrimitive?.content

            if (source.isNullOrBlank()) {
                attachment
            } else {
                val sourcePath = Path.of(source)

                if (!Files.exists(sourcePath)) {
                    println("Marathon attachment not found: $sourcePath")
                    attachment
                } else {
                    val extension = sourcePath.fileName
                        .toString()
                        .substringAfterLast(".", missingDelimiterValue = "")

                    val targetFileName = buildString {
                        append(UUID.randomUUID())
                        append("-attachment")
                        if (extension.isNotBlank()) {
                            append(".")
                            append(extension)
                        }
                    }

                    Files.copy(
                        sourcePath,
                        mergedDir.resolve(targetFileName),
                        StandardCopyOption.REPLACE_EXISTING
                    )

                    JsonObject(
                        attachment.toMutableMap().apply {
                            this["source"] = JsonPrimitive(targetFileName)
                        }
                    )
                }
            }
        }
    )
}

private fun copyDeviceArtifacts(
    sourceDir: Path,
    targetDir: Path
) {
    sourceDir.listDirectoryEntries().forEach { source ->
        if (
            source.isRegularFile() &&
            !source.name.endsWith("-result.json")
        ) {
            Files.copy(
                source,
                targetDir.resolve(source.name),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
}

private fun copyServiceFiles(
    sourceDir: Path,
    targetDir: Path
) {
    listOf(
        "environment.xml",
        "categories.json",
        "executor.json"
    ).forEach { fileName ->
        val source = sourceDir.resolve(fileName)

        if (Files.exists(source)) {
            Files.copy(
                source,
                targetDir.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
}

private fun readJson(path: Path): JsonObject =
    json.parseToJsonElement(Files.readString(path)).jsonObject