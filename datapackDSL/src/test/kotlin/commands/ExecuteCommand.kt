package commands

import arguments.Color
import arguments.allEntities
import arguments.numbers.rangeOrInt
import arguments.selector.Sort
import functions.Function
import functions.function
import utils.debugEntity

fun Function.executeTests() {
	val testEntity = debugEntity()
	val scores = listOf("a", "b", "c")
	scores.forEachIndexed { index, it ->
		scoreboard.objectives.add(it, "dummy")
		scoreboard.players.set(testEntity.selector, it, index)
	}

	execute {
		ifCondition {
			scores.forEachIndexed { index, it -> score(testEntity.selector, it, rangeOrInt(index)) }
		}

		run {
			debug("Validated ifCondition with multiple scores", Color.GREEN)
		}
	}

	val testFunction = datapack.function("test_execute_direct_return") {
		say("is executing test_execute_direct_return")
	}

	execute {
		asTarget(allEntities(true) {
			sort = Sort.RANDOM
		})

		testFunction
	}
}
