package io.github.ayfri.kore.generation.zip

/**
 * Pure-Kotlin ZIP (PKZIP) archive reader: parses the central directory, then extracts each entry from its
 * local file header, decompressing STORE (method 0) or DEFLATE (method 8, via [Inflate]) entries. No platform
 * library involved, so the same code reads third-party `.zip` datapacks on JVM, Node.js and the browser.
 */
internal data class ZipEntryData(val name: String, val isDirectory: Boolean, val content: ByteArray)

private const val LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50
private const val CENTRAL_DIRECTORY_SIGNATURE = 0x02014b50
private const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
private const val END_OF_CENTRAL_DIRECTORY_SIZE = 22
private const val MAX_COMMENT_LENGTH = 0xFFFF

private fun ByteArray.readIntLe(offset: Int): Int =
	(this[offset].toInt() and 0xFF) or
		((this[offset + 1].toInt() and 0xFF) shl 8) or
		((this[offset + 2].toInt() and 0xFF) shl 16) or
		((this[offset + 3].toInt() and 0xFF) shl 24)

private fun ByteArray.readShortLe(offset: Int): Int =
	(this[offset].toInt() and 0xFF) or ((this[offset + 1].toInt() and 0xFF) shl 8)

internal fun readZipEntries(bytes: ByteArray): List<ZipEntryData> {
	val eocdOffset = findEndOfCentralDirectory(bytes)
	val entryCount = bytes.readShortLe(eocdOffset + 10)
	val centralDirectoryOffset = bytes.readIntLe(eocdOffset + 16)

	val entries = ArrayList<ZipEntryData>(entryCount)
	var offset = centralDirectoryOffset

	repeat(entryCount) {
		check(bytes.readIntLe(offset) == CENTRAL_DIRECTORY_SIGNATURE) {
			"Malformed zip: central directory entry not found at offset $offset."
		}

		val compressionMethod = bytes.readShortLe(offset + 10)
		val compressedSize = bytes.readIntLe(offset + 20)
		val uncompressedSize = bytes.readIntLe(offset + 24)
		val nameLength = bytes.readShortLe(offset + 28)
		val extraLength = bytes.readShortLe(offset + 30)
		val commentLength = bytes.readShortLe(offset + 32)
		val localHeaderOffset = bytes.readIntLe(offset + 42)
		val name = bytes.copyOfRange(offset + 46, offset + 46 + nameLength).decodeToString()

		val content = extractEntryContent(bytes, localHeaderOffset, compressionMethod, compressedSize, uncompressedSize)
		entries += ZipEntryData(name.removeSuffix("/"), isDirectory = name.endsWith("/"), content)

		offset += 46 + nameLength + extraLength + commentLength
	}

	return entries
}

private fun extractEntryContent(
	bytes: ByteArray,
	localHeaderOffset: Int,
	compressionMethod: Int,
	compressedSize: Int,
	uncompressedSize: Int,
): ByteArray {
	check(bytes.readIntLe(localHeaderOffset) == LOCAL_FILE_HEADER_SIGNATURE) {
		"Malformed zip: local file header not found at offset $localHeaderOffset."
	}

	val nameLength = bytes.readShortLe(localHeaderOffset + 26)
	val extraLength = bytes.readShortLe(localHeaderOffset + 28)
	val contentStart = localHeaderOffset + 30 + nameLength + extraLength
	val compressed = bytes.copyOfRange(contentStart, contentStart + compressedSize)

	return when (compressionMethod) {
		0 -> compressed
		8 -> Inflate.inflate(compressed, uncompressedSize)
		else -> error("Unsupported zip compression method $compressionMethod (only STORE and DEFLATE are supported).")
	}
}

private fun findEndOfCentralDirectory(bytes: ByteArray): Int {
	val minOffset = maxOf(0, bytes.size - END_OF_CENTRAL_DIRECTORY_SIZE - MAX_COMMENT_LENGTH)
	for (offset in (bytes.size - END_OF_CENTRAL_DIRECTORY_SIZE) downTo minOffset) {
		if (bytes.readIntLe(offset) == END_OF_CENTRAL_DIRECTORY_SIGNATURE) return offset
	}
	error("Not a valid zip file: end of central directory record not found.")
}
