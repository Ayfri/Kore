package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.transferTests() {
	transfer("localhost") assertsIs "transfer localhost"
	transfer("localhost", 25565) assertsIs "transfer localhost 25565"
	transfer("localhost", players = allPlayers()) assertsIs "transfer localhost @a"
}
