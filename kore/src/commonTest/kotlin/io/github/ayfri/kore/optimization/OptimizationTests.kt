package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.configuration
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.optimization.passes.PruneEmptyFunctionsPass
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun DataPack.functionNames() = functions.map { it.name }.sorted()

class OptimizationTests : FunSpec({
	test("passes are disabled by default and opted in by the configuration block") {
		dataPack("optimization_tests") {
			configuration.optimization.enabled shouldBe false

			configuration {
				optimization()
			}

			configuration.optimization.enabled shouldBe true
			configuration.optimization.passes.map { it.name } shouldBe listOf("prune-empty-functions")
		}
	}

	test("custom passes are appended and built-in ones can be removed") {
		val custom = object : DataPackPass {
			override val name = "custom"
			override fun run(dataPack: DataPack) = PassResult.NONE
		}

		dataPack("optimization_tests") {
			configuration {
				optimization {
					this -= PruneEmptyFunctionsPass
					this += custom
				}
			}

			configuration.optimization.passes.map { it.name } shouldBe listOf("custom")
		}
	}

	test("empty functions and the calls to them are pruned") {
		dataPack("optimization_tests") {
			val empty = function("empty") {}

			function("caller") {
				say("hello")
				function(empty)
				execute {
					at(self())
					run { function(empty) }
				}
			}

			PruneEmptyFunctionsPass.run(this).changes shouldBe 3

			functionNames() shouldBe listOf("caller")
			functions.first().toString() assertsIs "say hello"
		}
	}

	test("a function only left empty by pruning is pruned too") {
		dataPack("optimization_tests") {
			val empty = function("empty") {}
			val relay = function("relay") { function(empty) }
			function("caller") { function(relay) }

			PruneEmptyFunctionsPass.run(this).changes shouldBe 5
			functions shouldBe emptyList()
		}
	}

	test("empty functions referenced by a tag or another resource are kept") {
		dataPack("optimization_tests") {
			val tagged = function("tagged") {}
			val rewarded = function("rewarded") {}
			function("plain") {}

			functionTag("my_tag") {
				add(tagged)
			}

			advancement("adv") {
				rewards {
					function = rewarded
				}
			}

			PruneEmptyFunctionsPass.run(this).changes shouldBe 1
			functionNames() shouldBe listOf("rewarded", "tagged")
		}
	}

	test("calls whose removal would change behavior are kept") {
		dataPack("optimization_tests") {
			val empty = function("empty") {}

			function("caller") {
				addLine("execute store result score #a obj run function ${empty.asId()}")
				addLine("execute summon minecraft:marker run function ${empty.asId()}")
				addLine("return run function ${empty.asId()}")
			}

			PruneEmptyFunctionsPass.run(this).changes shouldBe 1
			functions.first().lines.size shouldBe 3
		}
	}
})
