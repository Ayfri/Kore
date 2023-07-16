package commands

import arguments.types.literals.self
import functions.Function
import generated.Effects
import utils.assertsIs

fun Function.effectTests() {
	effect(self()) {
		clear(Effects.SPEED) assertsIs "effect clear @s minecraft:speed"
		give(Effects.SPEED, 100, 10, true) assertsIs "effect give @s minecraft:speed 100 10 true"
		giveInfinite(Effects.SPEED, 10, true) assertsIs "effect give @s minecraft:speed infinite 10 true"
	}
}
