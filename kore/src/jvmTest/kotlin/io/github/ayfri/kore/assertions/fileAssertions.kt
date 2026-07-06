package io.github.ayfri.kore.assertions

import io.github.ayfri.kore.utils.*
import io.kotest.matchers.shouldBe
import java.util.zip.ZipInputStream

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
