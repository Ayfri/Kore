package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.relativePos
import io.github.ayfri.kore.assertions.assertsMatches
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.generated.Tags
import io.kotest.core.spec.style.FunSpec
import kotlin.random.Random

private fun randomPos(from: Int = 0, to: Int = 100) = Random.nextInt(from, to).relativePos

private fun Clone.randomZoneAndDestination() {
	begin = vec3(randomPos(to = 9), randomPos(to = 9), randomPos(to = 9))
	end = vec3(randomPos(10, 20), randomPos(10, 20), randomPos(10, 20))
	destination = vec3(randomPos(), randomPos(), randomPos())
}

fun Function.cloneTests() {
	clone {
		randomZoneAndDestination()
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d*")

	clone {
		randomZoneAndDestination()

		from = Dimensions.THE_NETHER
		to = Dimensions.OVERWORLD
	} assertsMatches Regex("clone from minecraft:the_nether ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* to minecraft:overworld ~\\d* ~\\d* ~\\d*")

	clone {
		randomZoneAndDestination()
		masked(CloneMode.MOVE)
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* masked move")

	clone {
		randomZoneAndDestination()
		filter(Tags.Block.BASE_STONE_OVERWORLD, CloneMode.FORCE)
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* filtered #minecraft:base_stone_overworld force")

	clone {
		randomZoneAndDestination()
		strict = true
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* strict")

	clone {
		randomZoneAndDestination()
		masked(CloneMode.MOVE)
		strict = true
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* strict masked move")

	clone {
		randomZoneAndDestination()
		filter(Tags.Block.BASE_STONE_OVERWORLD, CloneMode.FORCE)
		strict = true
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* strict filtered #minecraft:base_stone_overworld force")

	clone {
		randomZoneAndDestination()
		replace(CloneMode.NORMAL)
	} assertsMatches Regex("clone ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* ~\\d* replace normal")

	cloneFiltered(vec3(), vec3(), vec3(), Tags.Block.BASE_STONE_OVERWORLD, CloneMode.MOVE, strict = true) assertsMatches
		Regex("clone ~ ~ ~ ~ ~ ~ ~ ~ ~ strict filtered #minecraft:base_stone_overworld move")
	cloneMasked(vec3(), vec3(), vec3(), CloneMode.NORMAL, strict = true) assertsMatches
		Regex("clone ~ ~ ~ ~ ~ ~ ~ ~ ~ strict masked normal")
	cloneReplace(vec3(), vec3(), vec3()) assertsMatches Regex("clone ~ ~ ~ ~ ~ ~ ~ ~ ~ replace")
}

class CloneCommandTests : FunSpec({
	test("clone") {
		dataPack("unit_tests") {
			load { cloneTests() }
		}.generate()
	}
})
