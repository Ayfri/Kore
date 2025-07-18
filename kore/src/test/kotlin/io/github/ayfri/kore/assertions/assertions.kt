package io.github.ayfri.kore.assertions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.utils.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.intellij.lang.annotations.Language
import java.util.zip.ZipInputStream

@OptIn(ExperimentalSerializationApi::class)
private val jsonStringifier = Json {
	prettyPrint = true
	prettyPrintIndent = "\t"
	namingStrategy = JsonNamingStrategy.SnakeCase
}

private val alreadyPrinted = mutableSetOf<Int>()

infix fun Command.assertsIs(string: String) = also { assertsIs(toString(), string) }
infix fun Command.assertsMatches(regex: Regex) = also { assertsMatches(regex, toString()) }

infix fun String.assertsIs(string: String) = also { assertsIs(this, string) }
infix fun String.assertsIsJson(@Language("json") string: String) = also { assertsIsJson(this, string) }
infix fun String.assertsIsNbt(@Language("NBTT") string: String) = also { assertsIsJson(this, string) }

infix fun ChatComponents.assertsIsJson(@Language("json") string: String) = also { assertsIsJson(toJsonString(jsonStringifier), string) }

infix fun <T : Any> T.assertsIs(expected: T) = also { assertsIs(toString(), expected.toString()) }

infix fun Argument.assertsIs(string: String) = assertsIs(asString(), string)

context(dp: DataPack)
infix fun Generator.assertsIs(@Language("json") expected: String) {
	val result = generateJson(dp)
	if (result == expected || !alreadyPrinted.add(hashCode())) return

	printStackTraceAndDiff(generateDiffStringJson(expected, result), 3)
}

fun TestDataPack.assertFileGenerated(path: String) {
	val file = dp.cleanPath.resolveSafe(path.replace("/", SystemPathSeparatorString))
	callAfterGeneration {
		if (!file.exists()) {
			error("File for datapack '${dp.name}' at '$file' was not found.")
		}
	}
}

fun TestDataPack.assertFileJsonContent(path: String, content: String) {
	val file = dp.cleanPath.resolveSafe(path.replace("/", SystemPathSeparatorString))
	callAfterGeneration {
		if (!file.exists()) {
			error("File for datapack '${dp.name}' at '$file' was not found.")
		}
		val fileContent = file.readText()
		fileContent.trimIndent() assertsIsJson content.trimIndent()
	}
}

private fun TestDataPack.assertFileGeneratedInArchive(path: String, extension: String) {
	val file = dp.cleanPath.resolve("${dp.name}.$extension")
	var invariantPath = path.replace("\\", "/").replace("/", SystemPathSeparatorString)
	if (!invariantPath.startsWith(SystemPathSeparatorString)) invariantPath = "$SystemPathSeparatorString$invariantPath"

	callAfterGeneration {
		val zipFile = file.toJavaFile()
		val inputStream = zipFile.inputStream()
		val zip = ZipInputStream(inputStream)
		var entry = zip.nextEntry
		while (entry != null) {
			if (entry.name == invariantPath) {
				inputStream.close()
				zip.close()
				return@callAfterGeneration
			}
			entry = zip.nextEntry
		}
		inputStream.close()
		zip.close()
		error("File '$path' in datapack '$file' was not found.")
	}
}

fun TestDataPack.assertFileGeneratedInJar(path: String) {
	assertFileGeneratedInArchive(path, "jar")
}

fun TestDataPack.assertFileGeneratedInZip(path: String) {
	assertFileGeneratedInArchive(path, "zip")
}

fun TestDataPack.assertGeneratorsGenerated() {
	callAfterGeneration {
		val generators = dp.generators.flatten().sortedBy { it.getFinalPath(dp).toString() }
		generators.forEach {
			val file = it.getFinalPath(dp)
			if (!file.exists()) {
				error("File '${file.name}' for datapack '${dp.name}' for ${it.resourceFolder} generator '${it.namespace ?: dp.name}:${it.fileName}' at '${file.absolute()}' was not found.")
			}
		}
	}
}

fun assertsThrows(message: String, function: () -> Any) {
	if (!alreadyPrinted.add(function.hashCode())) return

	val errorMessage = try {
		function()
		"No exception was thrown."
	} catch (e: Exception) {
		if (e.message == message) return
		e.message ?: "Exception thrown without message"
	}

	printStackTraceAndDiff(generateDiffString(message, errorMessage), 3)
}

private fun <T : Any> T.assertsIsJson(result: String, expected: String) {
	if (result == expected || !alreadyPrinted.add(hashCode())) return
	printStackTraceAndDiff(generateDiffStringJson(expected, result), 4)
}

private fun <T : Any> T.assertsIs(result: String, expected: String) {
	if (result == expected || !alreadyPrinted.add(hashCode())) return
	printStackTraceAndDiff(generateDiffString(expected, result), 4)
}

private fun <T : Any> T.assertsMatches(regex: Regex, expected: String) {
	if (regex.matches(expected) || !alreadyPrinted.add(hashCode())) return
	printStackTraceAndDiff(generateDiffString(regex, expected), 4)
}
