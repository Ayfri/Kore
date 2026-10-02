package io.github.ayfri.kore.website.playground.snippets.dialogs

import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.dialogShow
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.dialogs.action.action
import io.github.ayfri.kore.features.dialogs.body.plainMessage
import io.github.ayfri.kore.features.dialogs.dialogBuilder
import io.github.ayfri.kore.features.dialogs.types.bodies
import io.github.ayfri.kore.features.dialogs.types.confirmation
import io.github.ayfri.kore.features.dialogs.types.no
import io.github.ayfri.kore.features.dialogs.types.yes
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("lobby_dialogs") {
	pack { description = textComponent("A confirmation dialog wired to functions") }

	val start = function("start_round") {
		tellraw(allPlayers(), textComponent("The round starts!", Color.GREEN))
	}

	val readyCheck = dialogBuilder.confirmation("ready_check", "Ready to play?") {
		bodies { plainMessage("The round starts as soon as you confirm.") }

		yes("I'm ready") {
			action { runCommand { function(start) } }
		}

		no("Not yet") {
			action { runCommand { tellraw(self(), textComponent("Take your time.", Color.GRAY)) } }
		}
	}

	function("ask_players") {
		dialogShow(allPlayers(), readyCheck)
	}
}
