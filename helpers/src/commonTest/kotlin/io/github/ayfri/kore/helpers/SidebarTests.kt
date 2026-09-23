package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.sidebar.sidebar
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

private fun FunctionArgument.withStableNames() = toString().replace(Regex("generated_-?\\d+"), "generated")

class SidebarTests : FunSpec({
	val lobby = sidebar("lobby") {
		title("Mini-game", Color.GOLD)
		line("Map: Castle")
		emptyLine()
		line("Kills", Color.RED, scoreComponent("kills", literal("Ayfri")))
		line("Richest player") {
			value("Ayfri", Color.GREEN)
			visibleIf { predicate("stonks") }
		}
	}

	test("create") {
		dataPack("sidebar_tests") {
			function("create") { lobby.create() }.withStableNames() assertsIs """
				scoreboard objectives remove lobby
				scoreboard objectives add lobby dummy {type:"text",color:"gold",text:"Mini-game"}
				scoreboard objectives modify lobby numberformat blank
				scoreboard players set $0 lobby 0
				scoreboard players display name $0 lobby "Map: Castle"
				scoreboard players set $1 lobby -1
				scoreboard players display name $1 lobby ""
				scoreboard players set $2 lobby -2
				scoreboard players display name $2 lobby {type:"text",color:"red",text:"Kills"}
				scoreboard players display numberformat $2 lobby fixed {type:"score",score:{name:"Ayfri",objective:"kills"}}
				execute if predicate stonks run function sidebar_tests:generated_scopes/generated
				execute unless predicate stonks run scoreboard players reset $3 lobby
				scoreboard objectives setdisplay sidebar lobby
			""".trimIndent()

			generatedFunctions.single().toString() assertsIs """
				scoreboard players set $3 lobby -3
				scoreboard players display name $3 lobby "Richest player"
				scoreboard players display numberformat $3 lobby fixed {type:"text",color:"green",text:"Ayfri"}
			""".trimIndent()
		}
	}

	test("refresh only rewrites dynamic and conditional lines") {
		dataPack("sidebar_tests") {
			function("refresh") { lobby.refresh() }.withStableNames() assertsIs """
				scoreboard players set $2 lobby -2
				scoreboard players display name $2 lobby {type:"text",color:"red",text:"Kills"}
				scoreboard players display numberformat $2 lobby fixed {type:"score",score:{name:"Ayfri",objective:"kills"}}
				execute if predicate stonks run function sidebar_tests:generated_scopes/generated
				execute unless predicate stonks run scoreboard players reset $3 lobby
			""".trimIndent()
		}
	}

	test("runtime line update, show and hide") {
		dataPack("sidebar_tests") {
			function("update") {
				lobby.setLine(0, textComponent("Map: Forest"))
				lobby.show(DisplaySlots.sidebarTeam(FormattingColor.RED))
				lobby.hide()
				lobby.remove()
			}.toString() assertsIs """
				scoreboard players set $0 lobby 0
				scoreboard players display name $0 lobby "Map: Forest"
				scoreboard objectives setdisplay sidebar.team.red lobby
				scoreboard objectives setdisplay sidebar
				scoreboard objectives remove lobby
			""".trimIndent()
		}
	}

	test("more than 15 lines is rejected") {
		shouldThrow<IllegalArgumentException> {
			sidebar("too_long") { repeat(16) { line("Line $it") } }
		}
	}
})
