package io.github.ayfri.kore.generation

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generation.zip.parseLocalEntries
import io.github.ayfri.kore.path
import io.github.ayfri.kore.utils.TemporaryFiles
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.readText
import io.github.ayfri.kore.utils.resolveSafe
import io.github.ayfri.kore.utils.toSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.io.buffered
import kotlinx.io.readByteArray

class NodeGenerateTests : FunSpec({
	test("generate() writes real files through Node's fs module") {
		val outDir = TemporaryFiles.createTempDirectory("kore_node_generate_test")

		val dp = dataPack("node_generate_test") {
			path(outDir)
			function("hello") { say("Hello from Node!") }
		}
		dp.generate()

		val functionFile = outDir.resolveSafe("node_generate_test", "data", "node_generate_test", "function", "hello.mcfunction")
		functionFile.exists() shouldBe true
		functionFile.readText() shouldBe "say Hello from Node!"
	}

	test("generateZip() writes a real zip file through Node's fs module") {
		val outDir = TemporaryFiles.createTempDirectory("kore_node_generate_zip_test")

		val dp = dataPack("node_generate_zip_test") {
			path(outDir)
			function("hello") { say("Hello from a Node zip!") }
		}
		dp.generateZip()

		val zipFile = outDir.resolveSafe("node_generate_zip_test.zip")
		zipFile.exists() shouldBe true

		val entries = parseLocalEntries(zipFile.toSource().buffered().readByteArray())
		entries.any { it.name == "data/node_generate_zip_test/function/hello.mcfunction" } shouldBe true
	}
})
