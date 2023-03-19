package commands

import arguments.*
import arguments.enums.DataType
import arguments.enums.Dimension
import arguments.numbers.*
import arguments.selector.Sort
import functions.Function
import functions.function
import utils.assertsIs
import utils.debugEntity

fun Function.executeTests() {
	val testEntity = debugEntity()
	val scores = listOf("a", "b", "c")
	scores.forEachIndexed { index, it ->
		scoreboard.objectives.add(it, "dummy")
		scoreboard.players.set(testEntity.selector, it, index)
	}

	val selectorAsString = testEntity.selector.asString()
	execute {
		ifCondition {
			scores.forEachIndexed { index, it -> score(testEntity.selector, it, rangeOrInt(index)) }
		}

		run {
			debug("Validated ifCondition with multiple scores", Color.GREEN)
		}
	} assertsIs """
		execute if score $selectorAsString a matches 0 if score $selectorAsString b matches 1 if score $selectorAsString c matches 2 run tellraw @a {"text":"Validated ifCondition with multiple scores","color":"green"}
	""".trimIndent()

	val testFunction = datapack.function("test_execute_direct_return") {
		say("is executing test_execute_direct_return")
	}

	execute {
		asTarget(allEntities(true) {
			sort = Sort.RANDOM
		})

		testFunction
	} assertsIs """
		execute as @e[limit=1,sort=random] run function ${testFunction.namespace}:${testFunction.name}
	""".trimIndent()
}
