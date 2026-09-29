package io.github.ayfri.kore.website.playground.snippets.loadandtick

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.scores.lessThanOrEqualTo
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.gamemode
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("arena") {
	pack { description = textComponent("Bootstrap and tick lifecycles") }

	load("setup") {
		scoreboard.objectives.add("lives")
		tellraw(allPlayers(), textComponent("[arena] ready"))
	}

	tick("eliminate") {
		execute {
			asTarget(allPlayers {
				scores { "lives" lessThanOrEqualTo 0 }
				gamemode = !Gamemode.SPECTATOR
			})
			run {
				gamemode(Gamemode.SPECTATOR, self())
			}
		}
	}
}
