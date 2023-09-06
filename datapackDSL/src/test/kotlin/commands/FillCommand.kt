package commands

import arguments.maths.vec3
import assertions.assertsIs
import functions.Function
import generated.Blocks

fun Function.fillTests() {
	fill(vec3(), vec3(), Blocks.AIR) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air"
	fill(vec3(), vec3(), Blocks.AIR, FillOption.HOLLOW) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air hollow"
	fill(vec3(), vec3(), Blocks.AIR, Blocks.STONE) assertsIs "fill ~ ~ ~ ~ ~ ~ minecraft:air replace minecraft:stone"
}
