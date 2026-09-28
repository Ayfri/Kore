package io.github.ayfri.kore.website.playground.snippets.helloworld

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("starter_kore") {
	pack { description = textComponent("Starter datapack generated with Kore") }

	load("bootstrap") {
		tellraw(allPlayers(), textComponent("[starter_kore] loaded"))
	}

	function("hello") {
		tellraw(allPlayers(), textComponent("Hello from Kore"))
	}
}
