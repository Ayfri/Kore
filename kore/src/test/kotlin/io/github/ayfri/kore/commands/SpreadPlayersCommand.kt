package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec2
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.spreadPlayersTests() {
	spreadPlayers(vec2(), 0.0, 0.0, false, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 false @e"
	spreadPlayers(vec2(), 0.0, 0.0, 0, true, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 0 true @e"
}
