package io.github.ayfri.kore.website.playground.snippets.textcomponents

import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.clickEvent
import io.github.ayfri.kore.arguments.chatcomponents.hover.showText
import io.github.ayfri.kore.arguments.chatcomponents.hoverEvent
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("rich_chat") {
	pack { description = textComponent("Colors, hover text and clickable buttons") }

	val heal = function("heal") {
		effect(self()) { give(Effects.INSTANT_HEALTH, duration = 1, amplifier = 1) }
	}

	function("welcome") {
		tellraw(
			allPlayers(),
			textComponent("Welcome to the arena! ", Color.GOLD) { bold = true } + text("[Heal]", Color.GREEN) {
				hoverEvent { showText("Restores a few hearts") }
				clickEvent { runCommand { function(heal) } }
			},
		)

		tellraw(allPlayers(), textComponent("Your kills: ", Color.GRAY) + scoreComponent("kills", self()))
	}
}
