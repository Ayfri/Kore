package io.github.ayfri.kore.generation.zip

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

private fun skewedLetters(): ByteArray {
	var x = 1L
	return ByteArray(1000) {
		x = (x * 1103515245 + 12345) and 0x7fffffff
		"aaaabbbccd"[((x shr 16) % 10).toInt()].code.toByte()
	}
}

class InflateTests : FunSpec({
	test("stored block") {
		platformInflate("010300fcff616263".hexToBytes(), 3).decodeToString() shouldBe "abc"
	}

	test("fixed huffman block") {
		val compressed = "f348cdc9c9d75128cf2fca495154f018e58df2860c2f242355a1b03433395b21a928bf3c4f212dbf4221ab34b7a05821bf2cb548a104289d935855a990929fae0700"
		val original = "Hello, world! ".repeat(50) + "The quick brown fox jumps over the lazy dog."

		platformInflate(compressed.hexToBytes(), original.length).decodeToString() shouldBe original
	}

	test("dynamic huffman block") {
		val compressed = "3d938b11c33008436705b1ff0c414f386def8a09067d88545daaa91ef58653eade8843efa71c568b9c237eaa4d8cc391a6f3ccbf229732e7b7ad53aa3c572ed7b473fe774fca1dfab479eda5e181230104007cddce339aaee333b53306a5603009057efaef6987665e2e993593770a2280c14d4cc1019568524c9a41229898b7a76d3235d39934d1694ec38a9000fb29a102f0449d27024a2668e1ac827ec515f47b1592834370c13c1999704975e9894c8e86bebf212780cb3c722ab31047557a143db4fa19a57e7b61ea460f055ca0b51009cc781d5ec87af77e0b940502bcaf59c994751cd82c993a2be3ee204e68016c2a349fc671956711037b687bfe55bd2d9a34347b6ba70c451e166315cdb20cd97aab729bdebf39ca2af87c60b2758fc0f65f0c88fcafc3f02ee85e8f08620933c25b6c1ff15dd72424eb57fc7853cdbc89766ec312dffb72527d"

		platformInflate(compressed.hexToBytes(), 1000) shouldBe skewedLetters()
	}

	test("empty input") {
		platformInflate("0300".hexToBytes(), 0) shouldBe ByteArray(0)
	}

	test("truncated stream throws") {
		shouldThrow<Throwable> { platformInflate("f348cdc9c9d75128".hexToBytes(), 744) }
	}
})
