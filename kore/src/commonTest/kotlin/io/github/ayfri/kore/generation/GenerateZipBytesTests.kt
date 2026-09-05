package io.github.ayfri.kore.generation

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generation.zip.endOfCentralDirectoryEntryCount
import io.github.ayfri.kore.generation.zip.parseLocalEntries
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

suspend fun generateZipBytesTests() {
	val dp = dataPack("gen_zip_bytes_test") {
		function("my_function", directory = "sub\\dir") {
			say("Hello, world!")
		}

		function("with spaces and éàü", directory = "a b/c d") {
			say("weird path")
		}
	}

	val bytes = dp.generateZipBytes()
	val entries = parseLocalEntries(bytes)

	entries.isNotEmpty() shouldBe true
	entries.none { it.name.contains("\\") } shouldBe true
	endOfCentralDirectoryEntryCount(bytes) shouldBe entries.size

	entries.any { it.name == "pack.mcmeta" } shouldBe true

	val plainFunction = entries.first { it.name.endsWith("my_function.mcfunction") }
	plainFunction.name shouldBe "data/gen_zip_bytes_test/function/sub/dir/my_function.mcfunction"
	plainFunction.content.decodeToString() shouldBe "say Hello, world!"

	val weirdFunction = entries.first { it.name.contains("with spaces") }
	weirdFunction.name shouldBe "data/gen_zip_bytes_test/function/a b/c d/with spaces and éàü.mcfunction"
	weirdFunction.content.decodeToString() shouldBe "say weird path"
}

class GenerateZipBytesTests : FunSpec({
	test("generateZipBytes produces a valid, path-normalized zip on every platform") {
		generateZipBytesTests()
	}
})
