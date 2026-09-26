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

	test("readZipEntries inflates DEFLATE entries written by another tool") {
		val zip = "504b03041400000000000000210000000000000000000000000005000000646174612f504b030414000000080011323a5db9dff89534000000cc0000000b0000007061636b2e6d636d657461ab562a484cce56b2aa564a492d4e2eca2c28c9cccf53b202f2d272124b52539474c00ae2d3f28b72134b94ac0c6b6bab07ab1600504b030414000000080011323a5d8c44a2530f000000c80000001d000000646174612f6e732f66756e6374696f6e2f612e6d6366756e6374696f6e2b4eac54c848cdc9c9e72a1ed22c00504b0102140014000000000000002100000000000000000000000000050000000000000000000000800100000000646174612f504b0102140014000000080011323a5db9dff89534000000cc0000000b00000000000000000000008001230000007061636b2e6d636d657461504b0102140014000000080011323a5d8c44a2530f000000c80000001d0000000000000000000000800180000000646174612f6e732f66756e6374696f6e2f612e6d6366756e6374696f6e504b05060000000003000300b7000000ca0000000000"

		val entries = readZipEntries(zip.chunked(2).map { it.toInt(16).toByte() }.toByteArray())

		entries.map { it.name } shouldBe listOf("data", "pack.mcmeta", "data/ns/function/a.mcfunction")
		entries[1].content.decodeToString() shouldBe """{"pack":{"description":"deflated","pack_format":1}}""".repeat(4)
		entries[2].content.decodeToString() shouldBe "say hello\n".repeat(20)
	}
})
