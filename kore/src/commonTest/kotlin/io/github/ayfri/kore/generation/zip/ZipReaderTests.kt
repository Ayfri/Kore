package io.github.ayfri.kore.generation.zip

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ZipReaderTests : FunSpec({
	test("readZipEntries reads back what ZipWriter wrote") {
		val writer = ZipWriter()
		writer.addDirectory("data/my_pack")
		writer.addEntry("data/my_pack/function/hello.mcfunction", "say hi")
		writer.addEntry("pack.mcmeta", """{"pack":{}}""")

		val entries = readZipEntries(writer.toByteArray())

		entries.size shouldBe 3
		entries.first { it.name == "data/my_pack" }.isDirectory shouldBe true
		entries.first { it.name == "data/my_pack/function/hello.mcfunction" }.content.decodeToString() shouldBe "say hi"
		entries.first { it.name == "pack.mcmeta" }.content.decodeToString() shouldBe """{"pack":{}}"""
	}
})
