package io.github.ayfri.kore.generation.platform

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.io.files.Path

// OPFS only exists in browsers, so this runs in the opt-in `jsBrowserTest` only.
private val hasOpfs: Boolean = js("typeof navigator !== 'undefined' && !!(navigator.storage && navigator.storage.getDirectory)") as Boolean

class OpfsTests : FunSpec({
	test("OPFS round-trips every byte value").config(enabled = hasOpfs) {
		val path = Path("kore_opfs_test/bytes.bin")
		val bytes = ByteArray(256) { it.toByte() }

		Opfs.writeFile(path, bytes)

		Opfs.exists(path) shouldBe true
		Opfs.readFile(path)!!.toList() shouldBe bytes.toList()
	}
})
