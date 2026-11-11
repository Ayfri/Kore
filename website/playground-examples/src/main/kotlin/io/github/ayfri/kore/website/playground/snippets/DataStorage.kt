package io.github.ayfri.kore.website.playground.snippets.datastorage

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set

fun playground() = dataPack("player_state") {
	pack { description = textComponent("Values kept in command storage and read back") }

	val state = storage("state", name)

	load("init_state") {
		scoreboard { objectives { add("levels", ScoreboardCriteria.DUMMY) } }

		data(state) {
			merge {
				this["player_name"] = "Steve"
				this["level"] = 1
				this["inventory"] = nbtListOf("sword", "shield")
			}
		}
	}

	function("level_up") {
		data(state) {
			modify("level") { set(2) }
			modify("inventory") { append("bow") }
		}
	}

	function("level_to_score") {
		execute {
			storeResult { score(self(), "levels") }
			run { data(state) { get("level") } }
		}
	}
}
