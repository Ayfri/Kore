package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.enums.Dimension
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.relativePos
import io.github.ayfri.kore.assertions.assertsMatches
import io.github.ayfri.kore.functions.Function
import kotlin.random.Random

private fun randomPos(from: Int = 0, to: Int = 100) = Random.nextInt(from, to).relativePos

private fun Clone.randomZoneAndDestination() {
	begin = vec3(randomPos(0, 9), randomPos(0, 9), randomPos(0, 9))
	end = vec3(randomPos(10, 20), randomPos(10, 20), randomPos(10, 20))
	destination = vec3(randomPos(0, 100), randomPos(0, 100), randomPos(0, 100))
}

fun Function.cloneTests() {
	clone {
		randomZoneAndDestination()
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d*")

	clone {
		randomZoneAndDestination()

		from = Dimension.THE_NETHER
		to = Dimension.OVERWORLD
	} assertsMatches Regex("clone from minecraft:the_nether ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* to minecraft:overworld ~\\d* ~\\d* ~\\d*")

	clone {
		randomZoneAndDestination()
		masked(CloneMode.MOVE)
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* masked move")

	clone {
		randomZoneAndDestination()
		filter(io.github.ayfri.kore.generated.Tags.Blocks.BASE_STONE_OVERWORLD, CloneMode.FORCE)
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* filtered #minecraft:base_stone_overworld force")
}
