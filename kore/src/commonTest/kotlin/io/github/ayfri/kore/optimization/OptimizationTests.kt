package io.github.ayfri.kore.optimization

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.allEntities
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
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.optimization.passes.DedupeFunctionsPass
import io.github.ayfri.kore.optimization.utils.ExecuteChain
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.passes.HoistConditionsIntoSelectorsPass
import io.github.ayfri.kore.optimization.passes.PruneEmptyFunctionsPass
import io.github.ayfri.kore.optimization.passes.ReorderSelectorArgumentsPass
import io.github.ayfri.kore.optimization.passes.PruneUnreferencedGeneratedFunctionsPass
import io.github.ayfri.kore.optimization.passes.SimplifyExecuteChainsPass
import io.github.ayfri.kore.optimization.passes.WarnUnreachableCodePass
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
			configuration.optimization.passes.map { it.name } shouldBe listOf(
				"prune-empty-functions",
				"simplify-execute-chains",
				"hoist-conditions-into-selectors",
				"reorder-selector-arguments",
				"dedupe-functions",
				"prune-unreferenced-generated-functions",
				"warn-unreachable-code",
			)
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

			configuration.optimization.passes.map { it.name } shouldBe listOf(
				"simplify-execute-chains",
				"hoist-conditions-into-selectors",
				"reorder-selector-arguments",
				"dedupe-functions",
				"prune-unreferenced-generated-functions",
				"warn-unreachable-code",
				"custom",
			)
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

	test("execute chains are shortened without changing their behavior") {
		SimplifyExecuteChainsPass.simplify("execute run say hi") shouldBe "say hi"
		SimplifyExecuteChainsPass.simplify("execute as @e[type=pig] as @s at @s run say hi") shouldBe "execute as @e[type=pig] at @s run say hi"
		SimplifyExecuteChainsPass.simplify("execute at @s at @s run say hi") shouldBe "execute at @s run say hi"
		SimplifyExecuteChainsPass.simplify("execute as @s run say hi") shouldBe null
		SimplifyExecuteChainsPass.simplify("execute at @e[type=pig] at @e[type=pig] run say hi") shouldBe null
		SimplifyExecuteChainsPass.simplify("execute if score @s obj matches 1 run say hi") shouldBe null
		SimplifyExecuteChainsPass.simplify("\$execute as @s run say hi") shouldBe null
	}

	test("unreferenced generated functions are pruned") {
		dataPack("optimization_tests") {
			function("caller") {
				execute {
					at(self())
					run {
						say("a")
						say("b")
					}
				}
			}

			generatedFunctions.size shouldBe 1
			PruneUnreferencedGeneratedFunctionsPass.run(this).changes shouldBe 0

			functions.first().lines.clear()
			PruneUnreferencedGeneratedFunctionsPass.run(this).changes shouldBe 1
			generatedFunctions shouldBe emptyList()
		}
	}

	test("lines after an unconditional return are reported") {
		dataPack("optimization_tests") {
			function("early_return") {
				say("before")
				addLine("return 1")
				say("after")
			}

			function("conditional_return") {
				addLine("execute if score @s obj matches 1 run return 1")
				say("after")
			}

			WarnUnreachableCodePass.run(this).changes shouldBe 1
		}
	}

	test("selector arguments are sorted from the cheapest filter to the most expensive one") {
		ReorderSelectorArgumentsPass.reorder("say @e[nbt={Age:0},tag=a,type=pig]") shouldBe "say @e[type=pig,tag=a,nbt={Age:0}]"
		ReorderSelectorArgumentsPass.reorder("say @e[predicate=ns:p,scores={a=1,b=2}]") shouldBe "say @e[scores={a=1,b=2},predicate=ns:p]"
		ReorderSelectorArgumentsPass.reorder("say @e[type=pig,tag=a]") shouldBe null
		ReorderSelectorArgumentsPass.reorder("say @a") shouldBe null
	}

	test("score conditions are hoisted into the selector that picked the executor") {
		HoistConditionsIntoSelectorsPass.hoist("execute as @e[type=pig] if score @s obj matches 1 run say hi") shouldBe
			"execute as @e[type=pig,scores={obj=1}] run say hi"

		HoistConditionsIntoSelectorsPass.hoist("execute as @a at @s if score @s a matches 1.. if score @s b matches ..2 run say hi") shouldBe
			"execute as @a[scores={a=1..,b=..2}] at @s run say hi"

		HoistConditionsIntoSelectorsPass.hoist("execute as @e[scores={obj=2}] if score @s obj matches 1 run say hi") shouldBe null
		HoistConditionsIntoSelectorsPass.hoist("execute as @a on owner if score @s obj matches 1 run say hi") shouldBe null
		HoistConditionsIntoSelectorsPass.hoist("execute if score @s obj matches 1 run say hi") shouldBe null
	}

	test("identical generated functions are merged and their calls redirected") {
		dataPack("optimization_tests") {
			val first = generatedFunction("first") { say("hi") }
			val second = generatedFunction("second") { say("ho") }
			generatedFunctions.last().lines[0] = "say hi"
			function("caller") {
				function(first)
				function(second)
				addLine("function ${second.asId()}/nested")
			}

			DedupeFunctionsPass().run(this).changes shouldBe 1
			generatedFunctions.map { it.name } shouldBe listOf("first")
			functions.first().lines shouldBe listOf(
				"function ${first.asId()}",
				"function ${first.asId()}",
				"function ${second.asId()}/nested",
			)
		}
	}

	test("user functions are only merged when the pass is asked to") {
		dataPack("optimization_tests") {
			function("first") { say("hi") }
			function("second") { say("hi") }

			DedupeFunctionsPass().run(this).changes shouldBe 0
			DedupeFunctionsPass(includeUserFunctions = true).run(this).changes shouldBe 1
			functionNames() shouldBe listOf("first")
		}
	}

	test("clause shapes that look like a keyword are not mis-split") {
		ExecuteChain.parse("execute positioned as @s positioned as @s run say hi")?.clauses shouldBe listOf("positioned as @s", "positioned as @s")
		ExecuteChain.parse("execute facing entity @p eyes rotated as @s run say hi")?.clauses shouldBe listOf("facing entity @p eyes", "rotated as @s")
		ExecuteChain.parse("execute store result score #a obj as @s run say hi")?.clauses shouldBe listOf("store result score #a obj", "as @s")
		ExecuteChain.parse("execute at @s run execute as @a run say hi")?.command shouldBe "execute as @a run say hi"
		ExecuteChain.parse("say hi") shouldBe null
	}

	test("selector arguments nesting commas are split on the top level only") {
		Selectors.splitArguments("type=pig,nbt={Tags:[a,b]},tag=x") shouldBe listOf("type=pig", "nbt={Tags:[a,b]}", "tag=x")
		Selectors.splitArguments("") shouldBe emptyList()
		Selectors.splitArguments("type=pig,") shouldBe null
		Selectors.splitArguments("nbt={a:1") shouldBe null

		ReorderSelectorArgumentsPass.reorder("tp @e[nbt={Tags:[a,b]},type=pig] @a[predicate=ns:p,tag=x]") shouldBe
			"tp @e[type=pig,nbt={Tags:[a,b]}] @a[tag=x,predicate=ns:p]"
	}

	test("the whole default pipeline runs on generation") {
		dataPack("optimization_tests") {
			configuration {
				optimization {
					verbose = false
				}
			}

			val unused = function("unused") {}
			function("tick") {
				function(unused)
				execute {
					asTarget(allEntities())
					ifCondition { score(self(), "obj", rangeOrInt(1)) }
					run { say("hi") }
				}
			}

			runOptimizationPasses()

			functionNames() shouldBe listOf("tick")
			functions.first().toString() assertsIs "execute as @e[scores={obj=1}] run say hi"
		}
	}
})
