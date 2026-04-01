package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.bossbar.registerBossBar
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.entities.*
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.items.itemStack
import io.github.ayfri.kore.items.summon
import io.github.ayfri.kore.scoreboard.*
import io.github.ayfri.kore.teams.*
import io.github.ayfri.kore.utils.testDataPack

fun oopTests() = testDataPack("oop") {
	val teamBar = registerBossBar("team_bar", name) {
		color = BossBarColor.BLUE
		max = 20
	}

	function("give_item") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")

		player.giveItem(item) assertsIs "give @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] diamond_sword:minecraft[] 1"
		lines.size assertsIs 1
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
		} assertsIs """execute at @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] run summon minecraft:item 0.0 0.0 0.0 {Item:{components:"{}",CustomName:"{\"text\":\"MY ITEM\",\"bold\":true,\"color\":\"red\"}",CustomNameVisible:1b}}"""
		lines.size assertsIs 1
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
		} assertsIs "execute as @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] run teleport @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] @s"
		lines.size assertsIs 1
	}

	function("entity_factory") {
		entity("MobArena", limitToOne = false) {
			team = "red"
		}.teleportTo(0, 64, 0) assertsIs "teleport @e[name=MobArena,team=red] 0.0 64.0 0.0"

		lines.size assertsIs 1
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
		} assertsIs "execute if score @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] deathCount matches 2.. run team join red @e[gamemode=survival,limit=1,name=Ayfri,team=red,team=red,team=,type=minecraft:player]"
		lines.size assertsIs 1
	}

	function("scoreboard_entity") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}

		player.getScoreEntity("test").apply {
			set(10) assertsIs "scoreboard players set @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] test 10"
			add(5) assertsIs "scoreboard players add @e[gamemode=survival,limit=1,name=Ayfri,team=red,type=minecraft:player] test 5"
			copyFrom(player.asSelector(), "foodLevel")
		}
		lines.size assertsIs 3
	}

	function("scoreboard_objectives") {
		scoreboard("other.test") {
			create() assertsIs "scoreboard objectives add other.test dummy"
			setDisplaySlot(DisplaySlots.sidebar) assertsIs "scoreboard objectives setdisplay sidebar other.test"
			setDisplayName("Test") assertsIs """scoreboard objectives modify other.test displayname "Test""""
		}
		lines.size assertsIs 3
	}

	function("team_operations") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val red = team("red")

		red.apply {
			ensureExists() assertsIs "team add red"
			setColor(Color.DARK_RED) assertsIs "team modify red color dark_red"
			setPrefix(textComponent {
				text = "RED "
				color = Color.DARK_RED
				bold = true
			}) assertsIs """team modify red prefix {"type":"text","bold":true,"color":"dark_red","text":"RED "}"""
			addMembers(player)
			teamBar.setPlayers(this) assertsIs "bossbar set oop:team_bar players @e[team=red]"
			clearMembers() assertsIs "team empty red"
		}

		say("Loaded") assertsIs "say Loaded"
		lines.size assertsIs 7
	}

	function("team_conditions") {
		val player = player("Ayfri")
		val red = team("red")

		execute {
			ifCondition {
				hasMembers(red)
				hasPlayer(red, "Ayfri")
				hasPlayer(red, player)
				hasScore(red, "round.points", rangeOrInt(3))
			}

			run {
				say("Team ready")
			}
		} assertsIs "execute if entity @e[team=red] if entity @e[name=Ayfri,team=red,type=minecraft:player] if entity @e[name=Ayfri,team=red,type=minecraft:player] if score @e[team=red] round.points matches 3 run say Team ready"

		lines.size assertsIs 1
	}

	function("scoreboard_team") {
		val red = team("red")

		scoreboard("round.points").getScore(red).apply {
			set(10) assertsIs "scoreboard players set @e[team=red] round.points 10"
			add(5) assertsIs "scoreboard players add @e[team=red] round.points 5"
			reset() assertsIs "scoreboard players reset @e[team=red] round.points"
		}

		lines.size assertsIs 3
	}

	function("scoreboard_entity_data_and_count") {
		val counter = player("Counter")
		val tracker = counter.getScoreEntity("team.count")
		val red = team("red")

		tracker.copyMemberCountFrom(red) assertsIs "execute store result score @e[limit=1,name=Counter,type=minecraft:player] team.count run execute if entity @e[team=red]"
		tracker.copyDataFrom(
			counter,
			"Inventory[0].count"
		) assertsIs "execute store result score @e[limit=1,name=Counter,type=minecraft:player] team.count run data get entity @e[limit=1,name=Counter,type=minecraft:player] Inventory[0].count 1"
		tracker.copyDataFrom(
			storage("kore", "oop"),
			"stats.red"
		) assertsIs "execute store result score @e[limit=1,name=Counter,type=minecraft:player] team.count run data get storage oop:kore stats.red 1"
		tracker.copyTo(
			counter,
			"kore.team_count",
			DataType.INT
		) assertsIs "execute store result entity @e[limit=1,name=Counter,type=minecraft:player] kore.team_count int 1 run scoreboard players get @e[limit=1,name=Counter,type=minecraft:player] team.count"
		tracker.copyTo(
			storage("kore", "oop"),
			"stats.cached",
			DataType.INT
		) assertsIs "execute store result storage oop:kore stats.cached int 1 run scoreboard players get @e[limit=1,name=Counter,type=minecraft:player] team.count"

		lines.size assertsIs 5
	}
}.apply {
	generate()
}
