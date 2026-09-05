package io.github.ayfri.kore.generation

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.display
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.asInvariantPathSeparator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.io.files.Path

fun exportAsStringsTests() {
	val files = dataPack("export_as_strings_test") {
		function("my_function", directory = "sub\\dir") {
			say("Hello, world!")
		}

		advancement("my_advancement") {
			display(Items.DIAMOND_SWORD, "Hello", "World")
		}
	}.exportAsStrings()

	files.keys.none { it.contains("\\") } shouldBe true
	files["pack.mcmeta"]?.isNotBlank() shouldBe true
	files["data/export_as_strings_test/function/sub/dir/my_function.mcfunction"] shouldBe "say Hello, world!"
	("data/export_as_strings_test/advancement/my_advancement.json" in files) shouldBe true
}

class ExportAsStringsTests : FunSpec({
	test("exportAsStrings emits invariant / separators for every entry") {
		exportAsStringsTests()
	}

	test("asInvariantPathSeparator normalizes backslashes without reading the system separator") {
		Path("a\\b").asInvariantPathSeparator shouldBe "a/b"
		Path("a/b").asInvariantPathSeparator shouldBe "a/b"
	}
})
