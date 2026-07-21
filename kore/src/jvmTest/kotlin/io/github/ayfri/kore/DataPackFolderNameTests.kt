package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertFileGeneratedInJar
import io.github.ayfri.kore.assertions.assertFileGeneratedInZip
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generation.fabric.fabric
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun dataPackFolderNameTests() = testDataPack("mymod") {
	folderName("generated")

	function("test") {}

	predicate("always_true")
}.apply {
	val data = "generated/data"
	assertFileGenerated("generated/pack.mcmeta")
	assertFileGenerated("$data/mymod/function/test.mcfunction")
	assertFileGenerated("$data/mymod/predicate/always_true.json")
	generate()
}

fun dataPackFolderNameJarTests() = testDataPack("mymod_jar") {
	folderName("generated_jar")

	function("test") {}
}.apply {
	assertFileGeneratedInJar("data/mymod_jar/function/test.mcfunction")
	generateJar { fabric { version = "0.0.1" } }
}

fun dataPackFolderNameZipTests() = testDataPack("mymod_zip") {
	folderName("generated_zip")

	function("test") {}
}.apply {
	assertFileGeneratedInZip("data/mymod_zip/function/test.mcfunction")
	generateZip()
}

class DataPackFolderNameTests : FunSpec({
	test("folder name decoupled from namespace, folder mode") {
		dataPackFolderNameTests()
	}

	test("folder name decoupled from namespace, jar mode") {
		dataPackFolderNameJarTests()
	}

	test("folder name decoupled from namespace, zip mode") {
		dataPackFolderNameZipTests()
	}
})
