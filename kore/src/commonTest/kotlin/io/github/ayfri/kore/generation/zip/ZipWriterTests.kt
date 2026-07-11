package io.github.ayfri.kore.generation.zip

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun zipWriterTests() {
	Crc32.of("123456789".encodeToByteArray()) shouldBe 0xCBF43926u
	Crc32.of(ByteArray(0)) shouldBe 0u

	val writer = ZipWriter()
	writer.addEntry("pack.mcmeta", """{"pack":{}}""")
	writer.addEntry("data\\namespace\\function\\test.mcfunction", "say hi")
	writer.addDirectory("data/namespace/function")
	writer.addEntry("data/namespace/lang/en_us.json", "café, 日本語, emoji 🎉")

	val bytes = writer.toByteArray()
	val entries = parseLocalEntries(bytes)

	entries.size shouldBe 4
	endOfCentralDirectoryEntryCount(bytes) shouldBe 4

	val packMcmeta = entries[0]
	packMcmeta.name shouldBe "pack.mcmeta"
	packMcmeta.content.decodeToString() shouldBe """{"pack":{}}"""
	packMcmeta.compressionMethod shouldBe 0
	packMcmeta.compressedSize shouldBe packMcmeta.uncompressedSize
	packMcmeta.crc32 shouldBe Crc32.of(packMcmeta.content)
	(packMcmeta.generalPurposeFlag and UTF8_NAME_FLAG) shouldBe UTF8_NAME_FLAG

	val backslashEntry = entries[1]
	backslashEntry.name shouldBe "data/namespace/function/test.mcfunction"
	backslashEntry.name.contains("\\") shouldBe false

	val directoryEntry = entries[2]
	directoryEntry.name shouldBe "data/namespace/function/"
	directoryEntry.compressedSize shouldBe 0
	directoryEntry.content.size shouldBe 0

	val unicodeEntry = entries[3]
	unicodeEntry.name shouldBe "data/namespace/lang/en_us.json"
	unicodeEntry.content.decodeToString() shouldBe "café, 日本語, emoji 🎉"
	unicodeEntry.crc32 shouldBe Crc32.of(unicodeEntry.content)

	val directoryOnlyWriter = ZipWriter()
	directoryOnlyWriter.addDirectory("data\\namespace\\function")
	parseLocalEntries(directoryOnlyWriter.toByteArray()).single().name shouldBe "data/namespace/function/"
}

class ZipWriterTests : FunSpec({
	test("zip writer produces a structurally valid, path-normalized, UTF-8 archive") {
		zipWriterTests()
	}
})
