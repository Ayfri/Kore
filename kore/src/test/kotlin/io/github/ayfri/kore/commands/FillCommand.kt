package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.kotest.core.spec.style.FunSpec

fun Function.fillTests() {
	fill(vec3(), vec3(), Blocks.AIR) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air"
	fill(vec3(), vec3(), Blocks.AIR, FillOption.HOLLOW) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air hollow"
	fill(vec3(), vec3(), Blocks.AIR, FillOption.HOLLOW, strict = true) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air hollow strict"
	fill(vec3(), vec3(), Blocks.AIR, strict = true) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air strict"
	fill(vec3(), vec3(), Blocks.AIR, Blocks.STONE) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone"
	fill(vec3(), vec3(), Blocks.AIR, Blocks.STONE, strict = true) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone strict"
	fill(
		vec3(),
		vec3(),
		Blocks.AIR,
		Blocks.STONE,
		FillOption.DESTROY
	) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone destroy"
	fill(
		vec3(),
		vec3(),
		Blocks.AIR,
		Blocks.STONE,
		FillOption.HOLLOW
	) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone hollow"
	fill(
		vec3(),
		vec3(),
		Blocks.AIR,
		Blocks.STONE,
		FillOption.OUTLINE
	) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone outline"
}

class FillCommandTests : FunSpec({
	test("fill") {
		dataPack("unit_tests") {
			load { fillTests() }
		}.generate()
	}
})
