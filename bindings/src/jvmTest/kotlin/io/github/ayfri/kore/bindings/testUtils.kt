package io.github.ayfri.kore.bindings

import io.kotest.matchers.shouldBe

infix fun <T> T.assertsIs(expected: T) {
	this shouldBe expected
}

infix fun String.assertsIs(expected: String) {
	this shouldBe expected
}
