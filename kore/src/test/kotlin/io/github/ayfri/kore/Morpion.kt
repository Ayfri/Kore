package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.chatcomponents.clickEvent
import io.github.ayfri.kore.arguments.chatcomponents.events.runCommand
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load

fun DataPack.resetScoreboard() = function("reset_scoreboard") {
	scoreboard.objectives.remove("morpion")
	scoreboard.objectives.add("morpion", ScoreboardCriteria.DUMMY)
}

fun DataPack.resetGame() = function("reset_game") {
	function(resetScoreboard())
	tellraw(allPlayers(), textComponent("Game reset! Let's play again!") { color = Color.GREEN })
}

fun DataPack.startGame() = function("start_game") {
	function(resetGame())
	tellraw(allPlayers(), textComponent("Welcome to Morpion! Click a button to play!") { color = Color.AQUA })
	displayBoard()
}

fun DataPack.displayBoard(): FunctionArgument = function("display_board") {
	repeat(3) { row ->
		repeat(3) { col ->
			tellraw(allPlayers(), textComponent("[$row,$col]") {
				color = Color.YELLOW
				clickEvent {
					runCommand {
						function(makeMove(row, col))
					}
				}
			})
		}
	}
}

fun DataPack.makeMove(row: Int, col: Int) = function("make_move_${row}_$col") {
	// Logic to update the board and check for win conditions
	tellraw(allPlayers(), textComponent("Move made at [$row,$col]") { color = Color.GOLD })
	function("display_board")
}

fun morpion() {
	dataPack("morpion_game") {
		load {
			function(startGame())
		}

		generateZip()
	}
}
