package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Blocks

fun Function.setBlockTests() {
	setBlock(vec3(), Blocks.AIR) assertsIs "setblock ~ ~ ~ minecraft:air"
	setBlock(vec3(), Blocks.AIR, SetBlockMode.DESTROY) assertsIs "setblock ~ ~ ~ minecraft:air destroy"
	setBlock(vec3(), Blocks.AIR, SetBlockMode.DESTROY, strict = true) assertsIs "setblock ~ ~ ~ minecraft:air destroy strict"
	setBlock(vec3(), Blocks.AIR, strict = true) assertsIs "setblock ~ ~ ~ minecraft:air strict"
}
