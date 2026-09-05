package io.github.ayfri.kore.generation.zip

/**
 * Pure-Kotlin ZIP (PKZIP) archive builder, STORE method only (no compression). No platform library is
 * involved, so the same code produces byte-identical archives on JVM, Node.js and the browser.
 */
class ZipWriter {
	private class Entry(val name: String, val content: ByteArray, val isDirectory: Boolean, val offset: Int)

	private val body = ByteBuffer()
	private val entries = mutableListOf<Entry>()

	fun addEntry(path: String, content: ByteArray) {
		val name = path.replace("\\", "/")
		val offset = body.size
		writeLocalFileHeader(name, content)
		body.write(content)
		entries += Entry(name, content, isDirectory = false, offset)
	}

	fun addEntry(path: String, content: String) = addEntry(path, content.encodeToByteArray())

	fun addDirectory(path: String) {
		val name = path.replace("\\", "/").let { if (it.endsWith("/")) it else "$it/" }
		val offset = body.size
		writeLocalFileHeader(name, ByteArray(0))
		entries += Entry(name, ByteArray(0), isDirectory = true, offset)
	}

	fun toByteArray(): ByteArray {
		val centralDirectory = ByteBuffer()
		for (entry in entries) writeCentralDirectoryHeader(
			centralDirectory,
			entry.name,
			entry.content,
			entry.isDirectory,
			entry.offset
		)

		val result = ByteBuffer()
		result.write(body.toByteArray())
		val centralDirectoryOffset = body.size
		result.write(centralDirectory.toByteArray())
		writeEndOfCentralDirectory(result, entries.size, centralDirectory.size, centralDirectoryOffset)
		return result.toByteArray()
	}

	private fun writeLocalFileHeader(name: String, content: ByteArray) {
		val nameBytes = name.encodeToByteArray()
		val crc = if (content.isEmpty()) 0u else Crc32.of(content)

		body.writeIntLe(LOCAL_FILE_HEADER_SIGNATURE)
		body.writeShortLe(VERSION_NEEDED)
		body.writeShortLe(UTF8_NAME_FLAG) // general purpose bit flag: entry name is UTF-8
		body.writeShortLe(0) // compression method: store
		body.writeShortLe(DOS_TIME)
		body.writeShortLe(DOS_DATE)
		body.writeIntLe(crc.toInt())
		body.writeIntLe(content.size)
		body.writeIntLe(content.size)
		body.writeShortLe(nameBytes.size)
		body.writeShortLe(0) // extra field length
		body.write(nameBytes)
	}

	private fun writeCentralDirectoryHeader(
		out: ByteBuffer,
		name: String,
		content: ByteArray,
		isDirectory: Boolean,
		localHeaderOffset: Int,
	) {
		val nameBytes = name.encodeToByteArray()
		val crc = if (content.isEmpty()) 0u else Crc32.of(content)
		val externalAttributes = if (isDirectory) DIRECTORY_EXTERNAL_ATTRIBUTES else FILE_EXTERNAL_ATTRIBUTES

		out.writeIntLe(CENTRAL_DIRECTORY_SIGNATURE)
		out.writeShortLe(VERSION_MADE_BY)
		out.writeShortLe(VERSION_NEEDED)
		out.writeShortLe(UTF8_NAME_FLAG) // general purpose bit flag: entry name is UTF-8
		out.writeShortLe(0) // compression method: store
		out.writeShortLe(DOS_TIME)
		out.writeShortLe(DOS_DATE)
		out.writeIntLe(crc.toInt())
		out.writeIntLe(content.size)
		out.writeIntLe(content.size)
		out.writeShortLe(nameBytes.size)
		out.writeShortLe(0) // extra field length
		out.writeShortLe(0) // file comment length
		out.writeShortLe(0) // disk number start
		out.writeShortLe(0) // internal file attributes
		out.writeIntLe(externalAttributes)
		out.writeIntLe(localHeaderOffset)
		out.write(nameBytes)
	}

	private fun writeEndOfCentralDirectory(
		out: ByteBuffer,
		entryCount: Int,
		centralDirectorySize: Int,
		centralDirectoryOffset: Int
	) {
		out.writeIntLe(END_OF_CENTRAL_DIRECTORY_SIGNATURE)
		out.writeShortLe(0) // number of this disk
		out.writeShortLe(0) // disk where central directory starts
		out.writeShortLe(entryCount)
		out.writeShortLe(entryCount)
		out.writeIntLe(centralDirectorySize)
		out.writeIntLe(centralDirectoryOffset)
		out.writeShortLe(0) // comment length
	}

	private companion object {
		const val LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50
		const val CENTRAL_DIRECTORY_SIGNATURE = 0x02014b50
		const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
		const val VERSION_NEEDED = 20
		const val VERSION_MADE_BY = 20
		const val DIRECTORY_EXTERNAL_ATTRIBUTES = 0x10
		const val FILE_EXTERNAL_ATTRIBUTES = 0x81a40000.toInt() // rw-r--r-- (unix mode in the high 16 bits)
		const val UTF8_NAME_FLAG =
			0x0800 // bit 11: filename/comment are UTF-8, per the APPNOTE.TXT language encoding flag

		// DOS date/time fixed to 1980-01-01 00:00:00, the DOS epoch: archives don't need real timestamps.
		const val DOS_TIME = 0
		const val DOS_DATE = 0b0000000_0001_00001 // year 0 (1980), month 1, day 1
	}
}

/** Minimal growable little-endian byte buffer, avoids pulling in a platform-specific stream type. */
private class ByteBuffer {
	private var array = ByteArray(256)
	var size = 0
		private set

	private fun ensureCapacity(extra: Int) {
		if (size + extra <= array.size) return
		var newSize = array.size * 2
		while (newSize < size + extra) newSize *= 2
		array = array.copyOf(newSize)
	}

	fun write(bytes: ByteArray) {
		ensureCapacity(bytes.size)
		bytes.copyInto(array, size)
		size += bytes.size
	}

	fun writeShortLe(value: Int) {
		ensureCapacity(2)
		array[size] = (value and 0xFF).toByte()
		array[size + 1] = ((value shr 8) and 0xFF).toByte()
		size += 2
	}

	fun writeIntLe(value: Int) {
		ensureCapacity(4)
		array[size] = (value and 0xFF).toByte()
		array[size + 1] = ((value shr 8) and 0xFF).toByte()
		array[size + 2] = ((value shr 16) and 0xFF).toByte()
		array[size + 3] = ((value shr 24) and 0xFF).toByte()
		size += 4
	}

	fun toByteArray(): ByteArray = array.copyOf(size)
}
