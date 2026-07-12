package io.github.ayfri.kore.generation.zip

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.zip.Deflater

private fun rawDeflate(bytes: ByteArray): ByteArray {
	val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, true)
	deflater.setInput(bytes)
	deflater.finish()

	val output = ByteArray(bytes.size * 2 + 64)
	val length = deflater.deflate(output)
	deflater.end()
	return output.copyOf(length)
}

class InflateTests : FunSpec({
	test("inflate decompresses a raw DEFLATE stream produced by java.util.zip.Deflater") {
		val original = ("Hello, world! ".repeat(50) + "The quick brown fox jumps over the lazy dog.").encodeToByteArray()
		val compressed = rawDeflate(original)

		Inflate.inflate(compressed, original.size) shouldBe original
	}

	test("inflate handles data too small to benefit from compression (stored/uncompressible blocks)") {
		val original = byteArrayOf(1, 2, 3)
		val compressed = rawDeflate(original)

		Inflate.inflate(compressed, original.size) shouldBe original
	}

	test("inflate handles empty input") {
		val original = ByteArray(0)
		val compressed = rawDeflate(original)

		Inflate.inflate(compressed, original.size) shouldBe original
	}
})
