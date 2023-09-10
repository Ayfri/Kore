package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Blocks

fun Function.fillTests() {
	fill(vec3(), vec3(), Blocks.AIR) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air"
	fill(vec3(), vec3(), Blocks.AIR, FillOption.HOLLOW) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air hollow"
	fill(vec3(), vec3(), Blocks.AIR, Blocks.STONE) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone"
}
