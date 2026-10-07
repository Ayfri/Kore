package io.github.ayfri.kore.generation

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.returnRun
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.display
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunctionName
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.optimization.optimization
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

	test("exportAsStrings runs the enabled optimization passes") {
		val files = dataPack("export_as_strings_test") {
			configuration {
				optimization()
			}

			val empty = function("empty") {}
			function("caller") {
				say("hello")
				function(empty)
			}
		}.exportAsStrings()

		("data/export_as_strings_test/function/empty.mcfunction" in files) shouldBe false
		files["data/export_as_strings_test/function/caller.mcfunction"] shouldBe "say hello"
	}

	test("generateCommentOfGeneratedFunctionCall lists the callers atop each generated function") {
		fun export(comments: Boolean) = dataPack("callers_test") {
			configuration {
				generateCommentOfGeneratedFunctionCall = comments
			}

			function("a") {
				execute {
					asTarget(allPlayers())
					run {
						say("one")
						say("two")
					}
				}
			}
			function("b") {
				returnRun {
					say("one")
					say("two")
				}
			}
		}.exportAsStrings()

		val name = "generated_scopes/${generatedFunctionName("generated", listOf("say one", "say two"))}"
		val path = "data/callers_test/function/$name.mcfunction"
		val on = export(true)
		val off = export(false)

		on[path] shouldBe """
			# Called by callers_test:a
			# Called by callers_test:b
			say one
			say two
		""".trimIndent()
		off[path] shouldBe "say one\nsay two"
		on["data/callers_test/function/a.mcfunction"] shouldBe "execute as @a run function callers_test:$name"
		on["data/callers_test/function/b.mcfunction"] shouldBe "return run function callers_test:$name"
	}

	test("asInvariantPathSeparator normalizes backslashes without reading the system separator") {
		Path("a\\b").asInvariantPathSeparator shouldBe "a/b"
		Path("a/b").asInvariantPathSeparator shouldBe "a/b"
	}
})
