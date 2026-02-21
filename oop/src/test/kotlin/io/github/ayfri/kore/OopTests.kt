package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.items.itemStack
import io.github.ayfri.kore.items.summon
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.pack.packFormat
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.teams.*
import io.github.ayfri.kore.utils.testDataPack

fun oopTests() = testDataPack("oop") {
	pack {
		description = textComponent("This is a test")
		minFormat = packFormat(10)
		maxFormat = packFormat(10)
		packFormat = packFormat(10)
	}

	function("give_item") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")

		player.giveItem(item) assertsIs "give @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] diamond_sword:minecraft[] 1"
	}

	function("execute_at") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")

		player.executeAt {
			run {
				item.summon(textComponent {
					text = "MY ITEM"
					color = Color.RED
					bold = true
				})
			}
		} assertsIs """execute at @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] run summon minecraft:item 0 0 0 {Item:{components:"{}",CustomName:"{\"text\":\"MY ITEM\",\"bold\":true,\"color\":\"red\"}",CustomNameVisible:1b}}"""
	}

	function("execute_as") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}

		player.executeAs {
			run {
				it.teleportTo(it)
			}
		} assertsIs "execute as @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player,type=minecraft:player] run teleport @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] @s"
	}

	function("execute_if_score") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}

		execute {
			ifCondition {
				score(player.asSelector(), "deathCount") greaterThanOrEqualTo 2
			}

			run {
				player.joinTeam("red")
			}
		} assertsIs "execute if score @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] deathCount matches 2.. run team join red @e[gamemode=survival,limit=1,name=Ayfri,team=red,team=red,team=,type=minecraft:player,type=minecraft:player]"
	}

	function("scoreboard_entity") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}

		player.getScoreEntity("test").apply {
			set(10) assertsIs "scoreboard players set @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] test 10"
			add(5) assertsIs "scoreboard players add @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player,type=minecraft:player] test 5"
			copyFrom(player.asSelector(), "foodLevel")
		}
	}

	function("scoreboard_objectives") {
		scoreboard("other.test") {
			create() assertsIs "scoreboard objectives add other.test dummy"
			setDisplaySlot(DisplaySlots.sidebar) assertsIs "scoreboard objectives setdisplay sidebar other.test"
			setDisplayName("Test") assertsIs """scoreboard objectives modify other.test displayname "Test""""
		}
	}

	function("team_operations") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}

		team("red") {
			ensureExists() assertsIs "team add red"
			setColor(Color.DARK_RED) assertsIs "team modify red color dark_red"
			setPrefix(textComponent {
				text = "RED "
				color = Color.DARK_RED
				bold = true
			}) assertsIs """team modify red prefix {"type":"text","bold":true,"color":"dark_red","text":"RED "}"""

			addMembers(player)
		}

		say("Loaded") assertsIs "say Loaded"
	}
}.apply {
	val n = "oop"
	val d = "$n/data/$n"

	assertFileGenerated("$d/function/give_item.mcfunction")
	assertFileGenerated("$d/function/execute_at.mcfunction")
	assertFileGenerated("$d/function/execute_as.mcfunction")
	assertFileGenerated("$d/function/execute_if_score.mcfunction")
	assertFileGenerated("$d/function/scoreboard_entity.mcfunction")
	assertFileGenerated("$d/function/scoreboard_objectives.mcfunction")
	assertFileGenerated("$d/function/team_operations.mcfunction")
	generate()
}
