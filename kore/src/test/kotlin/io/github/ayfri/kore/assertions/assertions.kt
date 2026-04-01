package io.github.ayfri.kore.assertions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.utils.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
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

infix fun Command.assertsIs(string: String) = also { toString() shouldBe string }
infix fun Command.assertsMatches(regex: Regex) = also { toString() shouldMatch regex }

infix fun String.assertsIs(string: String) = also { this shouldBe string }
infix fun String.assertsIsJson(@Language("json") string: String) = also { this shouldBe string }
infix fun String.assertsIsNbt(@Language("NBTT") string: String) = also { this shouldBe string }

infix fun ChatComponents.assertsIsJson(@Language("json") string: String) =
	also { toJsonString(jsonStringifier) shouldBe string }

infix fun <T : Any> T.assertsIs(expected: T) = also { toString() shouldBe expected.toString() }

infix fun Argument.assertsIs(string: String) {
	asString() shouldBe string
}

context(dp: DataPack)
infix fun Generator.assertsIs(@Language("json") expected: String) {
	generateJson(dp) shouldBe expected
}

fun TestDataPack.assertFileGenerated(path: String) = callAfterGeneration {
	val file = dp.cleanPath.resolveSafe(path.replace("/", SystemPathSeparatorString))
	if (!file.exists()) {
		error("File for datapack '${dp.name}' at '$file' was not found.")
	}
}

fun TestDataPack.assertFileJsonContent(path: String, content: String) = callAfterGeneration {
	val file = dp.cleanPath.resolveSafe(path.replace("/", SystemPathSeparatorString))
	if (!file.exists()) {
		error("File for datapack '${dp.name}' at '$file' was not found.")
	}
	val fileContent = file.readText()
	fileContent.trimIndent() shouldBe content.trimIndent()
}

private fun TestDataPack.assertFileGeneratedInArchive(path: String, extension: String) = callAfterGeneration {
	val file = dp.cleanPath.resolve("${dp.name}.$extension")
	val invariantPath = path.replace("\\", "/").removePrefix("/")
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

fun TestDataPack.assertFileGeneratedInJar(path: String) {
	assertFileGeneratedInArchive(path, "jar")
}

fun TestDataPack.assertFileGeneratedInZip(path: String) {
	assertFileGeneratedInArchive(path, "zip")
}

fun TestDataPack.assertGeneratorsGenerated() = callAfterGeneration {
	val generators = dp.generators.flatten().sortedBy { it.getFinalPath(dp).toString() }
	generators.forEach {
		val file = it.getFinalPath(dp)
		if (!file.exists()) {
			error("File '${file.name}' for datapack '${dp.name}' for ${it.resourceFolder} generator '${it.namespace ?: dp.name}:${it.fileName}' at '${file.absolute()}' was not found.")
		}
	}
}

fun assertsThrows(message: String, function: () -> Any) {
	val errorMessage = try {
		function()
		"No exception was thrown."
	} catch (e: Exception) {
		if (e.message == message) return
		e.message ?: "Exception thrown without message"
	}

	errorMessage shouldBe message
}
