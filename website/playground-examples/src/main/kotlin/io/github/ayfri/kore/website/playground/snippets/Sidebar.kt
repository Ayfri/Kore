package io.github.ayfri.kore.website.playground.snippets.sidebar

import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.helpers.sidebar.sidebar
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("sky_wars") {
	pack { description = textComponent("A live sidebar from the helpers module") }

	val lobby = sidebar("lobby") {
		title("Sky Wars", Color.GOLD)
		line("Map: Floating Isles")
		emptyLine()
		line("Players", value = scoreComponent("players_alive", literal("#game")))
		line("Kills", Color.RED, scoreComponent("kills", literal("#top")))
		emptyLine()
		line("play.example.net", Color.YELLOW)
	}

	load("show_sidebar") { lobby.create() }
	tick("refresh_sidebar") { lobby.refresh() }
}
