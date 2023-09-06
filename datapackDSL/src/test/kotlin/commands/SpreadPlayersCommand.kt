package commands

import arguments.maths.vec2
import arguments.types.literals.allEntities
import assertions.assertsIs
import functions.Function

fun Function.spreadPlayersTests() {
	spreadPlayers(vec2(), 0.0, 0.0, false, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 false @e"
	spreadPlayers(vec2(), 0.0, 0.0, 0, true, allEntities()) assertsIs "spreadplayers ~ ~ 0 0 0 true @e"
}
