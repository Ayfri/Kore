package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Effects

fun Function.effectTests() {
	effect(self()) {
		clear(Effects.SPEED) assertsIs "effect clear @s minecraft:speed"
		give(Effects.SPEED, 100, 10, true) assertsIs "effect give @s minecraft:speed 100 10 true"
		giveInfinite(Effects.SPEED, 10, true) assertsIs "effect give @s minecraft:speed infinite 10 true"
	}
}
