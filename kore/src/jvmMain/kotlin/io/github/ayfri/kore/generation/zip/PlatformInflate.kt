package io.github.ayfri.kore.generation.zip

import java.util.zip.Inflater

internal actual fun platformInflate(compressed: ByteArray, uncompressedSize: Int): ByteArray {
	val inflater = Inflater(true)
	try {
		inflater.setInput(compressed)
		val output = ByteArray(uncompressedSize)
		var written = 0
		while (written < uncompressedSize) {
			val read = inflater.inflate(output, written, uncompressedSize - written)
			if (read == 0 && (inflater.finished() || inflater.needsInput() || inflater.needsDictionary()))
				error("Malformed DEFLATE stream: ended after $written of $uncompressedSize bytes.")
			written += read
		}
		return output
	} finally {
		inflater.end()
	}
}
