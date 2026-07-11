package io.github.ayfri.kore.generation.zip

private const val LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50
private const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
internal const val UTF8_NAME_FLAG = 0x0800

private fun ByteArray.readIntLe(offset: Int): Int =
	(this[offset].toInt() and 0xFF) or
		((this[offset + 1].toInt() and 0xFF) shl 8) or
		((this[offset + 2].toInt() and 0xFF) shl 16) or
		((this[offset + 3].toInt() and 0xFF) shl 24)

private fun ByteArray.readShortLe(offset: Int): Int =
	(this[offset].toInt() and 0xFF) or ((this[offset + 1].toInt() and 0xFF) shl 8)

internal data class ParsedEntry(
	val name: String,
	val generalPurposeFlag: Int,
	val compressionMethod: Int,
	val crc32: UInt,
	val compressedSize: Int,
	val uncompressedSize: Int,
	val content: ByteArray,
)

internal fun parseLocalEntries(bytes: ByteArray): List<ParsedEntry> {
	val entries = mutableListOf<ParsedEntry>()
	var offset = 0

	while (offset + 4 <= bytes.size && bytes.readIntLe(offset) == LOCAL_FILE_HEADER_SIGNATURE) {
		val generalPurposeFlag = bytes.readShortLe(offset + 6)
		val compressionMethod = bytes.readShortLe(offset + 8)
		val crc = bytes.readIntLe(offset + 14).toUInt()
		val compressedSize = bytes.readIntLe(offset + 18)
		val uncompressedSize = bytes.readIntLe(offset + 22)
		val nameLength = bytes.readShortLe(offset + 26)
		val extraLength = bytes.readShortLe(offset + 28)
		val nameStart = offset + 30
		val name = bytes.copyOfRange(nameStart, nameStart + nameLength).decodeToString()
		val contentStart = nameStart + nameLength + extraLength
		val content = bytes.copyOfRange(contentStart, contentStart + compressedSize)

		entries += ParsedEntry(
			name,
			generalPurposeFlag,
			compressionMethod,
			crc,
			compressedSize,
			uncompressedSize,
			content
		)
		offset = contentStart + compressedSize
	}

	return entries
}

internal fun endOfCentralDirectoryEntryCount(bytes: ByteArray): Int {
	val eocdOffset = bytes.size - 22
	check(bytes.readIntLe(eocdOffset) == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
		"End of central directory record not found where expected."
	}
	return bytes.readShortLe(eocdOffset + 10)
}
