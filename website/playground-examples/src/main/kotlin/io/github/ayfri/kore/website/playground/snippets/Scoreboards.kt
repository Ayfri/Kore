package io.github.ayfri.kore.website.playground.snippets.scoreboards

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("coins") {
	pack { description = textComponent("Objectives, a sidebar and score math") }

	load("setup") {
		scoreboard {
			objective("coins") {
				add(ScoreboardCriteria.DUMMY, displayName = textComponent("Coins", Color.GOLD))
				setDisplaySlot(DisplaySlots.sidebar)
			}
		}

		scoreboard.objectives.add("bonus")
	}

	function("reward") {
		scoreboard {
			player(self()) {
				add("coins", 10)
				operation("coins", Operation.ADD, self(), "bonus")
			}
		}

		execute {
			ifCondition { score(self(), "coins", rangeOrIntStart(100)) }
			run { tellraw(self(), textComponent("You can afford the shop!", Color.GREEN)) }
		}
	}
}
