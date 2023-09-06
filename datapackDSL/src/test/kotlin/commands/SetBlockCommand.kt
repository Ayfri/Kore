package commands

import arguments.maths.vec3
import assertions.assertsIs
import functions.Function
import generated.Blocks

fun Function.setBlockTests() {
	setBlock(vec3(), Blocks.AIR) assertsIs "setblock ~ ~ ~ minecraft:air"
	setBlock(vec3(), Blocks.AIR, SetBlockMode.DESTROY) assertsIs "setblock ~ ~ ~ minecraft:air destroy"
}
