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

private data class TestIdentifier(
    val testClass: String,
    val testMethod: String
)

private data class AllureResult(
    val path: Path,
    val json: JsonObject,
    val identifier: TestIdentifier,
    val start: Long
)

fun main() {
    val rootDir = Path.of(System.getProperty("user.dir"))

    val marathonDir =
        rootDir.resolve("build/reports/marathon/allure-results")

    val deviceDir =
        rootDir.resolve(
            "build/reports/marathon/device-files/allure-results"
        )

    val mergedDir =
        rootDir.resolve(
            "build/reports/marathon/merged-allure-results"
        )

    require(Files.isDirectory(marathonDir)) {
        "Marathon results not found: $marathonDir"
    }

    require(Files.isDirectory(deviceDir)) {
        "Device results not found: $deviceDir"
    }

    recreateDirectory(mergedDir)

    val marathonResults = loadResults(marathonDir)
    val deviceResults = loadResults(deviceDir)

    val marathonGroups = marathonResults
        .groupBy(AllureResult::identifier)

    val deviceGroups = deviceResults
        .groupBy(AllureResult::identifier)

    var mergedCount = 0
    var unmatchedCount = 0

    marathonGroups.forEach { (identifier, serverResults) ->
        val sortedServerResults = serverResults.sortedBy(AllureResult::start)

        val sortedDeviceResults = deviceGroups[identifier]
            .orEmpty()
            .sortedBy(AllureResult::start)

        if (sortedServerResults.size != sortedDeviceResults.size) {
            println(
                "Result count mismatch for $identifier: " +
                        "marathon=${sortedServerResults.size}, " +
                        "device=${sortedDeviceResults.size}"
            )
        }

        sortedServerResults.forEachIndexed { index, marathonResult ->
            val deviceResult = sortedDeviceResults.getOrNull(index)

            if (deviceResult == null) {
                unmatchedCount++
                println(
                    "Device result not found for " +
                            "$identifier, attempt=${index + 1}"
                )
            }

            val mergedResult = mergeResults(
                marathonResult = marathonResult.json,
                deviceResult = deviceResult?.json,
                mergedDir = mergedDir
            )

            Files.writeString(
                mergedDir.resolve(marathonResult.path.name),
                json.encodeToString(
                    JsonObject.serializer(),
                    mergedResult
                )
            )

            mergedCount++
        }
    }

    copyDeviceArtifacts(
        sourceDir = deviceDir,
        targetDir = mergedDir
    )

    copyServiceFiles(
        sourceDir = marathonDir,
        targetDir = mergedDir,
        overwrite = true
    )

    copyServiceFiles(
        sourceDir = deviceDir,
        targetDir = mergedDir,
        overwrite = false
    )

    println()
    println("Merged Allure results created: $mergedDir")
    println("Merged results: $mergedCount")
    println("Unmatched Marathon results: $unmatchedCount")
}

private fun loadResults(directory: Path): List<AllureResult> =
    directory
        .listDirectoryEntries("*-result.json")
        .map { path ->
            val resultJson = readJson(path)

            AllureResult(
                path = path,
                json = resultJson,
                identifier = resultJson.toTestIdentifier(),
                start = resultJson.longValue("start") ?: 0L
            )
        }

private fun mergeResults(
    marathonResult: JsonObject,
    deviceResult: JsonObject?,
    mergedDir: Path
): JsonObject {
    val rewrittenMarathonAttachments =
        rewriteAndCopyMarathonAttachments(
            attachments = marathonResult.arrayValue("attachments"),
            mergedDir = mergedDir
        )

    if (deviceResult == null) {
        return JsonObject(
            marathonResult.toMutableMap().apply {
                this["attachments"] = rewrittenMarathonAttachments
            }
        )
    }

    val deviceAttachments =
        deviceResult.arrayValue("attachments")

    return JsonObject(
        marathonResult.toMutableMap().apply {
            this["steps"] =
                deviceResult["steps"] ?: JsonArray(emptyList())
            this["attachments"] = JsonArray(
                rewrittenMarathonAttachments + deviceAttachments
            )
            if (
                this["statusDetails"] == null &&
                deviceResult["statusDetails"] != null
            ) {
                this["statusDetails"] =
                    deviceResult.getValue("statusDetails")
            }
        }
    )
}

private fun rewriteAndCopyMarathonAttachments(
    attachments: JsonArray,
    mergedDir: Path
): JsonArray =
    JsonArray(
        attachments.map { element ->
            val attachment = element.jsonObject
            val source = attachment.stringValue("source")

            if (source.isNullOrBlank()) {
                return@map attachment
            }

            val sourcePath = runCatching {
                Path.of(source)
            }.getOrNull()

            if (
                sourcePath == null ||
                !Files.isRegularFile(sourcePath)
            ) {
                println("Marathon attachment not found: $source")
                return@map attachment
            }

            val originalName = sourcePath.fileName.toString()
            val extension = originalName.substringAfterLast(
                delimiter = ".",
                missingDelimiterValue = ""
            )

            val targetName = buildString {
                append(UUID.randomUUID())
                append("-attachment")

                if (extension.isNotBlank()) {
                    append(".")
                    append(extension)
                }
            }

            Files.copy(
                sourcePath,
                mergedDir.resolve(targetName),
                StandardCopyOption.REPLACE_EXISTING
            )

            JsonObject(
                attachment.toMutableMap().apply {
                    this["source"] = JsonPrimitive(targetName)
                }
            )
        }
    )

private fun copyDeviceArtifacts(
    sourceDir: Path,
    targetDir: Path
) {
    sourceDir.listDirectoryEntries().forEach { source ->
        if (
            source.isRegularFile() &&
            !source.name.endsWith("-result.json") &&
            !isServiceFile(source.name)
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
    targetDir: Path,
    overwrite: Boolean
) {
    sourceDir.listDirectoryEntries().forEach { source ->
        if (!source.isRegularFile() || !isServiceFile(source.name)) {
            return@forEach
        }

        val target = targetDir.resolve(source.name)

        if (!overwrite && Files.exists(target)) {
            return@forEach
        }

        Files.copy(
            source,
            target,
            StandardCopyOption.REPLACE_EXISTING
        )
    }
}

private fun isServiceFile(fileName: String): Boolean =
    fileName.endsWith(".xml") ||
            fileName.endsWith(".properties") ||
            fileName == "categories.json" ||
            fileName == "executor.json"

private fun JsonObject.toTestIdentifier(): TestIdentifier {
    val labels = arrayValue("labels")
        .mapNotNull { it as? JsonObject }

    val testClass = labels
        .firstOrNull {
            it.stringValue("name") == "testClass"
        }
        ?.stringValue("value")

    val testMethod = labels
        .firstOrNull {
            it.stringValue("name") == "testMethod"
        }
        ?.stringValue("value")

    if (!testClass.isNullOrBlank() && !testMethod.isNullOrBlank()) {
        return TestIdentifier(
            testClass = testClass,
            testMethod = testMethod
        )
    }

    val fullName = stringValue("fullName")
        ?: error("Result has neither test labels nor fullName")

    return TestIdentifier(
        testClass = fullName.substringBeforeLast(
            delimiter = ".",
            missingDelimiterValue = fullName
        ),
        testMethod = fullName.substringAfterLast(".")
    )
}

private fun recreateDirectory(directory: Path) {
    if (Files.exists(directory)) {
        directory.toFile().deleteRecursively()
    }

    directory.createDirectories()
}

private fun readJson(path: Path): JsonObject =
    json.parseToJsonElement(
        Files.readString(path)
    ).jsonObject

private fun JsonObject.arrayValue(name: String): JsonArray =
    this[name] as? JsonArray ?: JsonArray(emptyList())

private fun JsonObject.stringValue(name: String): String? =
    this[name]
        ?.runCatching { jsonPrimitive.content }
        ?.getOrNull()

private fun JsonObject.longValue(name: String): Long? =
    stringValue(name)?.toLongOrNull()