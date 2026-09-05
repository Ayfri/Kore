@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.ayfri.kore.generation.zip

/** Pure-Kotlin CRC-32 (ISO 3309 / ZIP) checksum, table-based, no platform library. */
internal object Crc32 {
	private val table = UIntArray(256) { n ->
		var c = n.toUInt()
		repeat(8) {
			c = if (c and 1u != 0u) (0xEDB88320u xor (c shr 1)) else (c shr 1)
		}
		c
	}

	fun of(bytes: ByteArray): UInt {
		var crc = 0xFFFFFFFFu
		for (byte in bytes) {
			val index = (crc xor byte.toUInt()) and 0xFFu
			crc = table[index.toInt()] xor (crc shr 8)
		}
		return crc xor 0xFFFFFFFFu
	}
}
