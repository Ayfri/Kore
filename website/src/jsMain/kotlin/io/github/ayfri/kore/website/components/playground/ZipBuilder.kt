package io.github.ayfri.kore.website.components.playground

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/**
 * The generated datapack is zipped in the page rather than in the compiled snippet.
 *
 * Kore's own `generateZipBytes()` stages the archive through OPFS, which would leave files behind in the
 * visitor's origin storage and forces a second `playground()` call, since a `DataPack` is single-use. The
 * files are already in memory as strings here, and a stored (uncompressed) zip is a few dozen lines - which
 * Minecraft reads exactly like a compressed one.
 */

private val crcTable = IntArray(256) { index ->
	var value = index

	repeat(8) {
		value = if (value and 1 != 0) (value ushr 1) xor 0xEDB88320.toInt() else value ushr 1
	}

	value
}

private fun crc32(bytes: ByteArray): Int {
	var crc = -1

	bytes.forEach { byte ->
		crc = (crc ushr 8) xor crcTable[(crc xor byte.toInt()) and 0xFF]
	}

	return crc.inv()
}

/** Writes little-endian zip fields into [buffer] from [position], so several writers can fill one buffer. */
private class ZipWriter(val buffer: ByteArray, var position: Int = 0) {
	fun writeShort(value: Int) {
		buffer[position++] = value.toByte()
		buffer[position++] = (value ushr 8).toByte()
	}

	fun writeInt(value: Int) {
		writeShort(value)
		writeShort(value ushr 16)
	}

	fun writeBytes(bytes: ByteArray) {
		bytes.copyInto(buffer, position)
		position += bytes.size
	}
}

/** Builds a stored zip archive out of [files], entries kept in the order they are given. */
fun buildZip(files: List<GeneratedFile>): ByteArray {
	val entries = files.map { it.path.encodeToByteArray() to it.content.encodeToByteArray() }
	/** Local headers take 30 bytes, central entries 46 and the end record 22, each followed by its variable-length fields. */
	val centralOffset = entries.sumOf { (name, content) -> 30 + name.size + content.size }
	val centralSize = entries.sumOf { (name) -> 46 + name.size }
	val zip = ByteArray(centralOffset + centralSize + 22)
	val output = ZipWriter(zip)
	val central = ZipWriter(zip, centralOffset)

	entries.forEach { (name, content) ->
		val crc = crc32(content)
		val offset = output.position

		output.writeInt(0x04034B50)
		output.writeShort(20)
		output.writeShort(0x0800) // UTF-8 file names.
		output.writeShort(0) // Stored, no compression.
		output.writeShort(0) // Modification time, left at midnight.
		output.writeShort(0x21) // Modification date, the earliest a zip can express (1980-01-01).
		output.writeInt(crc)
		output.writeInt(content.size)
		output.writeInt(content.size)
		output.writeShort(name.size)
		output.writeShort(0)
		output.writeBytes(name)
		output.writeBytes(content)

		central.writeInt(0x02014B50)
		central.writeShort(20)
		central.writeShort(20)
		central.writeShort(0x0800)
		central.writeShort(0)
		central.writeShort(0)
		central.writeShort(0x21)
		central.writeInt(crc)
		central.writeInt(content.size)
		central.writeInt(content.size)
		central.writeShort(name.size)
		central.writeShort(0) // Extra field.
		central.writeShort(0) // Comment.
		central.writeShort(0) // Disk number.
		central.writeShort(0) // Internal attributes.
		central.writeInt(0) // External attributes.
		central.writeInt(offset)
		central.writeBytes(name)
	}

	central.writeInt(0x06054B50)
	central.writeShort(0)
	central.writeShort(0)
	central.writeShort(files.size)
	central.writeShort(files.size)
	central.writeInt(centralSize)
	central.writeInt(centralOffset)
	central.writeShort(0)

	return zip
}

/** The archive's file name: the pack's own namespace, never the `minecraft` one its function tags live in. */
fun zipName(files: List<GeneratedFile>) = files.asSequence()
	.filter { it.path.startsWith("data/") }
	.map { it.path.removePrefix("data/").substringBefore('/') }
	.firstOrNull { it != "minecraft" }
	.let { "${it ?: "kore-playground"}.zip" }

fun downloadZip(files: List<GeneratedFile>) =
	download(Blob(arrayOf(buildZip(files)), BlobPropertyBag(type = "application/zip")), zipName(files))

fun downloadText(text: String, fileName: String) = download(Blob(arrayOf(text), BlobPropertyBag(type = "text/plain")), fileName)

private fun download(blob: Blob, fileName: String) {
	val url = URL.createObjectURL(blob)
	val link = document.createElement("a") as HTMLAnchorElement

	link.href = url
	link.download = fileName
	link.click()
	URL.revokeObjectURL(url)
}
