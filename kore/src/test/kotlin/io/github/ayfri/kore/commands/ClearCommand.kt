package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items

fun Function.clearTests() {
	clear() assertsIs "clear"
	clear(self()) assertsIs "clear @s"
	clear(self(), Items.STONE) assertsIs "clear @s minecraft:stone"
	clear(self(), Items.STONE, 1) assertsIs "clear @s minecraft:stone 1"
}
