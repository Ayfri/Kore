package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.features.predicates.conditions.killedByPlayer
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.optimization.passes.HoistInlinePredicatesPass
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val CHANCE = """{type:"minecraft:random_chance",chance:0.5d}"""

private fun DataPack.chanceUses(count: Int) {
	repeat(count) { index ->
		function("use_$index") {
			execute {
				ifCondition { predicate { randomChance(0.5f) } }
				run { say("hi") }
			}
		}
	}
}

class HoistInlinePredicatesPassTests : FunSpec({
	test("a predicate written twice becomes one file referenced by both lines, with if and unless") {
		dataPack("hoist_tests") {
			function("a") {
				execute {
					ifCondition { predicate { randomChance(0.5f) } }
					run { say("hi") }
				}
			}
			function("b") {
				execute {
					unlessCondition { predicate { randomChance(0.5f) } }
					run { say("bye") }
				}
			}

			HoistInlinePredicatesPass().run(this).changes shouldBe 2

			functions.map { it.toString() } shouldBe listOf(
				"execute if predicate hoist_tests:generated_scopes/predicate_3c2b6659 run say hi",
				"execute unless predicate hoist_tests:generated_scopes/predicate_3c2b6659 run say bye",
			)
			exportAsStrings()["data/hoist_tests/predicate/generated_scopes/predicate_3c2b6659.json"] shouldBe
				"""{"type":"minecraft:random_chance","chance":0.5}"""
		}
	}

	test("a predicate written once, and a reference used twice, stay as they are") {
		dataPack("hoist_tests") {
			val reference = predicate("shared") { randomChance(0.25f) }
			function("a") {
				execute {
					ifCondition { predicate { randomChance(0.5f) } }
					run { say("hi") }
				}
				execute {
					ifCondition(reference)
					run { say("hi") }
				}
				execute {
					ifCondition(reference)
					run { say("hi") }
				}
			}

			HoistInlinePredicatesPass().run(this).changes shouldBe 0

			functions.single().toString() shouldBe """
				execute if predicate $CHANCE run say hi
				execute if predicate hoist_tests:shared run say hi
				execute if predicate hoist_tests:shared run say hi
			""".trimIndent()
			exportAsStrings().keys.count { "/predicate/" in it } shouldBe 1
		}
	}

	test("minUses is the number of times needed to hoist") {
		dataPack("hoist_tests") {
			chanceUses(2)
			HoistInlinePredicatesPass(minUses = 3).run(this).changes shouldBe 0
			functions.forEach { it.toString() shouldBe "execute if predicate $CHANCE run say hi" }
		}

		dataPack("hoist_tests") {
			chanceUses(3)
			HoistInlinePredicatesPass(minUses = 3).run(this).changes shouldBe 3
		}
	}

	test("two predicates of one line are hoisted separately and several conditions are written as an all_of") {
		dataPack("hoist_tests") {
			repeat(2) { index ->
				function("use_$index") {
					execute {
						ifCondition { predicate { randomChance(0.5f) } }
						unlessCondition { predicate { randomChance(0.5f); killedByPlayer() } }
						run { say("hi") }
					}
				}
			}

			HoistInlinePredicatesPass().run(this).changes shouldBe 2

			functions.first().toString() shouldBe
				"execute if predicate hoist_tests:generated_scopes/predicate_3c2b6659 unless predicate hoist_tests:generated_scopes/predicate_6028c0d8 run say hi"
			exportAsStrings().filterKeys { "/predicate/" in it } shouldBe mapOf(
				"data/hoist_tests/predicate/generated_scopes/predicate_3c2b6659.json" to """{"type":"minecraft:random_chance","chance":0.5}""",
				"data/hoist_tests/predicate/generated_scopes/predicate_6028c0d8.json" to """{"type":"minecraft:all_of","terms":[{"type":"minecraft:random_chance","chance":0.5},{"type":"minecraft:killed_by_player"}]}""",
			)
		}
	}

	test("file names come from the predicate text, the same for every build") {
		fun build() = dataPack("hoist_tests") {
			chanceUses(2)
			HoistInlinePredicatesPass().run(this)
		}.exportAsStrings()

		build() shouldBe build()
		build().keys.single { "/predicate/" in it } shouldBe "data/hoist_tests/predicate/generated_scopes/predicate_3c2b6659.json"
	}

	test("enabled in the configuration, the pass runs with the built-in ones") {
		dataPack("hoist_tests") {
			configuration {
				optimization {
					verbose = false
					this += HoistInlinePredicatesPass()
				}
			}
			chanceUses(2)

			exportAsStrings().keys.any { "predicate/generated_scopes/predicate_" in it } shouldBe true
		}
	}
})
