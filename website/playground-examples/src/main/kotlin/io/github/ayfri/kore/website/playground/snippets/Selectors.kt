package io.github.ayfri.kore.website.playground.snippets.selectors

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.scores.greaterThan
import io.github.ayfri.kore.arguments.scores.greaterThanOrEqualTo
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("arena_selectors") {
	pack { description = textComponent("Named selectors reused everywhere") }

	val activePlayers = allPlayers {
		scores {
			"round" greaterThanOrEqualTo 1
			"lives" greaterThan 0
		}
		gamemode = !Gamemode.SPECTATOR
	}

	function("start_wave") {
		effect(activePlayers) { give(Effects.RESISTANCE, duration = 5, amplifier = 0) }
		tellraw(activePlayers, textComponent("Wave started"))
	}
}
