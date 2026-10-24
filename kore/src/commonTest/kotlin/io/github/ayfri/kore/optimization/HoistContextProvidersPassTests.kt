package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.compute
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.plus
import io.github.ayfri.kore.features.predicates.providers.score
import io.github.ayfri.kore.features.predicates.providers.sqrt
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.optimization.passes.HoistContextProvidersPass
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val KILLS = """{type:"minecraft:add",inputs:[{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}},1]}"""

private fun DataPack.killsUses(count: Int) {
	repeat(count) { index ->
		function("use_$index") { compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
	}
}

class HoistContextProvidersPassTests : FunSpec({
	test("a provider used twice becomes one file referenced by both lines, wherever the compute is written") {
		dataPack("hoist_tests") {
			function("a") { compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
			function("b") { data(storage("foo")).compute("bar", self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
			function("c") {
				execute {
					storeResult { score(self(), "out") }
					run { compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 } }
				}
			}

			HoistContextProvidersPass().run(this).changes shouldBe 3

			functions.map { it.toString() } shouldBe listOf(
				"compute entity @s integer hoist_tests:generated_scopes/provider_28a268a6",
				"data modify storage minecraft:foo bar set compute entity @s integer hoist_tests:generated_scopes/provider_28a268a6",
				"execute store result score @s out run compute entity @s integer hoist_tests:generated_scopes/provider_28a268a6",
			)
			exportAsStrings()["data/hoist_tests/context_int_provider/generated_scopes/provider_28a268a6.json"] shouldBe
				"""{"type":"minecraft:add","inputs":[{"type":"minecraft:score","score":"kills","target":{"type":"minecraft:context","target":"target_entity"}},1]}"""
		}
	}

	test("a provider used once, a constant and a reference stay inline") {
		dataPack("hoist_tests") {
			val bonus = ContextIntProviderArgument("bonus", "my_pack")
			function("a") {
				compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }
				compute(5)
				compute(5)
				compute(bonus)
				compute(bonus)
			}

			HoistContextProvidersPass().run(this).changes shouldBe 0

			functions.single().toString() shouldBe """
				compute entity @s integer $KILLS
				compute default integer {type:"minecraft:constant",value:5}
				compute default integer {type:"minecraft:constant",value:5}
				compute default integer my_pack:bonus
				compute default integer my_pack:bonus
			""".trimIndent()
			exportAsStrings().keys.none { "context_int_provider" in it } shouldBe true
		}
	}

	test("minUses is the number of lines needed to hoist") {
		dataPack("hoist_tests") {
			killsUses(2)
			HoistContextProvidersPass(minUses = 3).run(this).changes shouldBe 0
			functions.forEach { it.toString() shouldBe "compute entity @s integer $KILLS" }
		}

		dataPack("hoist_tests") {
			killsUses(3)
			HoistContextProvidersPass(minUses = 3).run(this).changes shouldBe 3
		}
	}

	test("an int and a float provider are hoisted into their own folders and the scale stays on the line") {
		dataPack("hoist_tests") {
			function("a") {
				compute(self(), 2f) { sqrt(constant(4f)) }
				compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }
			}
			function("b") {
				compute(self(), 2f) { sqrt(constant(4f)) }
				compute(self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }
			}

			HoistContextProvidersPass().run(this).changes shouldBe 4

			functions.first().toString() shouldBe """
				compute entity @s float hoist_tests:generated_scopes/provider_716a4c8 2
				compute entity @s integer hoist_tests:generated_scopes/provider_28a268a6
			""".trimIndent()
			exportAsStrings().keys.filter { "provider_" in it }.sorted() shouldBe listOf(
				"data/hoist_tests/context_float_provider/generated_scopes/provider_716a4c8.json",
				"data/hoist_tests/context_int_provider/generated_scopes/provider_28a268a6.json",
			)
		}
	}

	test("file names come from the provider text, the same for every build") {
		fun build() = dataPack("hoist_tests") {
			killsUses(2)
			HoistContextProvidersPass().run(this)
		}.exportAsStrings()

		build() shouldBe build()
		build().keys.single { "context_int_provider" in it } shouldBe "data/hoist_tests/context_int_provider/generated_scopes/provider_28a268a6.json"
	}

	test("enabled in the configuration, the pass runs with the built-in ones") {
		dataPack("hoist_tests") {
			configuration {
				optimization {
					verbose = false
					this += HoistContextProvidersPass()
				}
			}
			killsUses(2)

			exportAsStrings().keys.any { "context_int_provider/generated_scopes/provider_" in it } shouldBe true
		}
	}

	test("the pass reads the generated functions folder of the configuration") {
		dataPack("hoist_tests") {
			configuration { generatedFunctionsFolder = "shared" }
			killsUses(2)

			HoistContextProvidersPass().run(this)
			functions.first().toString() shouldBe "compute entity @s integer hoist_tests:shared/provider_28a268a6"
		}
	}
})
