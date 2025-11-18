package io.github.ayfri.kore.bindings

infix fun <T> T.assertsIs(expected: T) {
	if (this != expected) {
		error("Expected '$expected' but got '$this'")
	}
}

infix fun String.assertsIs(expected: String) {
	if (this != expected) {
		error("Expected:\n$expected\n\nBut got:\n$this")
	}
}
