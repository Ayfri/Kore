package io.github.ayfri.kore.generation.zip

/**
 * Pure-Kotlin RFC 1951 (raw DEFLATE) decoder, modeled after Mark Adler's public-domain `puff.c` reference
 * decoder. No platform library involved, so the same code decompresses ZIP entries on JVM, Node.js and the
 * browser - needed because third-party `.zip` datapacks (unlike this library's own STORE-only [ZipWriter])
 * are typically DEFLATE-compressed.
 */
internal object Inflate {
	private const val MAX_BITS = 15

	private val lengthBase = intArrayOf(3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258)
	private val lengthExtraBits = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0)
	private val distanceBase = intArrayOf(1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577)
	private val distanceExtraBits = intArrayOf(0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13)
	private val codeLengthOrder = intArrayOf(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

	private class Huffman(lengths: IntArray, symbolCount: Int) {
		val counts = IntArray(MAX_BITS + 1)
		val symbols = IntArray(symbolCount)

		init {
			for (i in 0 until symbolCount) counts[lengths[i]]++
			counts[0] = 0

			val offsets = IntArray(MAX_BITS + 2)
			for (len in 1..MAX_BITS) offsets[len + 1] = offsets[len] + counts[len]
			for (symbol in 0 until symbolCount) {
				val len = lengths[symbol]
				if (len != 0) symbols[offsets[len]++] = symbol
			}
		}
	}

	private class BitReader(private val input: ByteArray) {
		var bytePos = 0
		private var bitBuffer = 0
		private var bitCount = 0

		fun bit(): Int {
			if (bitCount == 0) {
				bitBuffer = input[bytePos++].toInt() and 0xFF
				bitCount = 8
			}
			val result = bitBuffer and 1
			bitBuffer = bitBuffer shr 1
			bitCount--
			return result
		}

		fun bits(count: Int): Int {
			var value = 0
			for (i in 0 until count) value = value or (bit() shl i)
			return value
		}

		fun alignToByte() {
			bitBuffer = 0
			bitCount = 0
		}
	}

	private fun decodeSymbol(huffman: Huffman, reader: BitReader): Int {
		var code = 0
		var first = 0
		var index = 0
		for (len in 1..MAX_BITS) {
			code = code or reader.bit()
			val count = huffman.counts[len]
			if (code - first < count) return huffman.symbols[index + (code - first)]
			index += count
			first += count
			first = first shl 1
			code = code shl 1
		}
		error("Malformed DEFLATE stream: invalid Huffman code.")
	}

	private val fixedLiteralTree = Huffman(
		IntArray(288) { symbol ->
			when (symbol) {
				in 0..143 -> 8
				in 144..255 -> 9
				in 256..279 -> 7
				else -> 8
			}
		},
		288
	)
	private val fixedDistanceTree = Huffman(IntArray(30) { 5 }, 30)

	private fun readDynamicTrees(reader: BitReader): Pair<Huffman, Huffman> {
		val literalCount = reader.bits(5) + 257
		val distanceCount = reader.bits(5) + 1
		val codeLengthCount = reader.bits(4) + 4

		val codeLengthLengths = IntArray(19)
		for (i in 0 until codeLengthCount) codeLengthLengths[codeLengthOrder[i]] = reader.bits(3)
		val codeLengthTree = Huffman(codeLengthLengths, 19)

		val lengths = IntArray(literalCount + distanceCount)
		var i = 0
		while (i < lengths.size) {
			when (val symbol = decodeSymbol(codeLengthTree, reader)) {
				16 -> {
					val repeat = reader.bits(2) + 3
					repeat(repeat) { lengths[i] = lengths[i - 1]; i++ }
				}

				17 -> {
					val repeat = reader.bits(3) + 3
					repeat(repeat) { lengths[i++] = 0 }
				}

				18 -> {
					val repeat = reader.bits(7) + 11
					repeat(repeat) { lengths[i++] = 0 }
				}

				else -> lengths[i++] = symbol
			}
		}

		return Huffman(lengths.copyOfRange(0, literalCount), literalCount) to
			Huffman(lengths.copyOfRange(literalCount, lengths.size), distanceCount)
	}

	fun inflate(compressed: ByteArray, expectedSize: Int): ByteArray {
		val reader = BitReader(compressed)
		val output = ByteArray(expectedSize)
		var outputPos = 0

		while (true) {
			val isFinal = reader.bit() == 1
			when (reader.bits(2)) {
				0 -> {
					reader.alignToByte()
					val len = (compressed[reader.bytePos].toInt() and 0xFF) or ((compressed[reader.bytePos + 1].toInt() and 0xFF) shl 8)
					reader.bytePos += 4 // skip LEN and NLEN
					compressed.copyInto(output, outputPos, reader.bytePos, reader.bytePos + len)
					reader.bytePos += len
					outputPos += len
				}

				1 -> outputPos = inflateBlock(reader, output, outputPos, fixedLiteralTree, fixedDistanceTree)
				2 -> {
					val (literalTree, distanceTree) = readDynamicTrees(reader)
					outputPos = inflateBlock(reader, output, outputPos, literalTree, distanceTree)
				}

				else -> error("Malformed DEFLATE stream: reserved block type.")
			}

			if (isFinal) break
		}

		return output
	}

	private fun inflateBlock(reader: BitReader, output: ByteArray, start: Int, literalTree: Huffman, distanceTree: Huffman): Int {
		var outputPos = start
		while (true) {
			val symbol = decodeSymbol(literalTree, reader)
			when {
				symbol < 256 -> output[outputPos++] = symbol.toByte()
				symbol == 256 -> return outputPos
				else -> {
					val lengthIndex = symbol - 257
					val length = lengthBase[lengthIndex] + reader.bits(lengthExtraBits[lengthIndex])
					val distanceSymbol = decodeSymbol(distanceTree, reader)
					val distance = distanceBase[distanceSymbol] + reader.bits(distanceExtraBits[distanceSymbol])

					var copyFrom = outputPos - distance
					repeat(length) { output[outputPos++] = output[copyFrom++] }
				}
			}
		}
	}
}
