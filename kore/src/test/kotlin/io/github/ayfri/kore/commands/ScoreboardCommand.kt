package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.criteriaCrafted
import io.github.ayfri.kore.arguments.scores.criteriaCustom
import io.github.ayfri.kore.arguments.scores.criteriaTeamKill
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.RenderType
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.CustomStats
import io.github.ayfri.kore.generated.Items

fun Function.scoreboardTests() {
	scoreboard {
		objectives {
			add("test") assertsIs "scoreboard objectives add test dummy"
			add("test", ScoreboardCriteria.DUMMY, "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			add("test", ScoreboardCriteria.PLAYER_KILL_COUNT) assertsIs "scoreboard objectives add test playerKillCount"
			add("test", ScoreboardCriteria.TOTAL_KILL_COUNT) assertsIs "scoreboard objectives add test totalKillCount"

			add("test", criteriaCrafted(Items.STONE)) assertsIs "scoreboard objectives add test minecraft.crafted:minecraft.stone"
			add("test", criteriaCustom(CustomStats.JUMP)) assertsIs "scoreboard objectives add test minecraft.custom:minecraft.jump"
			add("test", criteriaTeamKill(Color.RED)) assertsIs "scoreboard objectives add test teamkill.red"

			clearNumberFormat("test") assertsIs "scoreboard objectives modify test numberformat"

			list() assertsIs "scoreboard objectives list"

			modifyDisplayAutoUpdate("test", true) assertsIs "scoreboard objectives modify test displayautoupdate true"
			modifyDisplayName("test", "Test") assertsIs "scoreboard objectives modify test displayname \"Test\""

			modifyNumberFormatBlank("test") assertsIs "scoreboard objectives modify test numberformat blank"
			modifyNumberFormatFixed("test", textComponent("test")) assertsIs "scoreboard objectives modify test numberformat fixed \"test\""
			modifyNumberFormatFixed(
				"test",
				"test",
				color = Color.RED
			) assertsIs "scoreboard objectives modify test numberformat fixed {\"type\":\"text\",\"color\":\"red\",\"text\":\"test\"}"

			modifyNumberFormatStyled("test") {
				bold = true
				color = Color.RED
				italic = true
				obfuscated = true
				strikethrough = true
				underlined = true
			} assertsIs "scoreboard objectives modify test numberformat styled {\"bold\":true,\"color\":\"red\",\"italic\":true,\"obfuscated\":true,\"strikethrough\":true,\"underlined\":true}"

			modifyRenderType("test", RenderType.HEARTS) assertsIs "scoreboard objectives modify test rendertype hearts"

			remove("test") assertsIs "scoreboard objectives remove test"
			setDisplay(DisplaySlots.list, "test") assertsIs "scoreboard objectives setdisplay list test"
		}

		players {
			add(self(), "test", 5) assertsIs "scoreboard players add @s test 5"

			clearDisplayName(self(), "test") assertsIs "scoreboard players display name @s test"
			clearDisplayNumberFormat(self(), "test") assertsIs "scoreboard players display numberformat @s test"

			displayName(self(), "test", textComponent("test")) assertsIs "scoreboard players display name @s test \"test\""

			displayNumberFormatBlank(self(), "test") assertsIs "scoreboard players display numberformat @s test blank"
			displayNumberFormatFixed(
				self(),
				"test",
				textComponent("test")
			) assertsIs "scoreboard players display numberformat @s test fixed \"test\""
			displayNumberFormatFixed(
				self(),
				"test",
				"test",
				color = Color.RED
			) assertsIs "scoreboard players display numberformat @s test fixed {\"type\":\"text\",\"color\":\"red\",\"text\":\"test\"}"

			displayNumberFormatStyled(self(), "test") {
				bold = true
				color = Color.RED
				italic = true
				obfuscated = true
				strikethrough = true
				underlined = true
			} assertsIs "scoreboard players display numberformat @s test styled {\"bold\":true,\"color\":\"red\",\"italic\":true,\"obfuscated\":true,\"strikethrough\":true,\"underlined\":true}"

			enable(self(), "test") assertsIs "scoreboard players enable @s test"
			get(self(), "test") assertsIs "scoreboard players get @s test"
			list() assertsIs "scoreboard players list"
			list(self()) assertsIs "scoreboard players list @s"
			operation(self(), "test", Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
			remove(self(), "test", 5) assertsIs "scoreboard players remove @s test 5"
			reset(self()) assertsIs "scoreboard players reset @s"
			reset(self(), "test") assertsIs "scoreboard players reset @s test"
			set(self(), "test", 5) assertsIs "scoreboard players set @s test 5"
		}

		objective("test") {
			add(ScoreboardCriteria.DUMMY, "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			clearNumberFormat() assertsIs "scoreboard objectives modify test numberformat"
			create(ScoreboardCriteria.DUMMY, "Test") assertsIs "scoreboard objectives add test dummy \"Test\""
			modifyDisplayAutoUpdate(true) assertsIs "scoreboard objectives modify test displayautoupdate true"
			modifyDisplayName("Test") assertsIs "scoreboard objectives modify test displayname \"Test\""

			modifyNumberFormatBlank() assertsIs "scoreboard objectives modify test numberformat blank"
			modifyNumberFormatFixed(textComponent("test")) assertsIs "scoreboard objectives modify test numberformat fixed \"test\""
			modifyNumberFormatFixed(
				"test",
				color = Color.RED
			) assertsIs "scoreboard objectives modify test numberformat fixed {\"type\":\"text\",\"color\":\"red\",\"text\":\"test\"}"

			modifyNumberFormatStyled {
				bold = true
				color = Color.RED
				italic = true
				obfuscated = true
				strikethrough = true
				underlined = true
			} assertsIs "scoreboard objectives modify test numberformat styled {\"bold\":true,\"color\":\"red\",\"italic\":true,\"obfuscated\":true,\"strikethrough\":true,\"underlined\":true}"

			modifyRenderType(RenderType.HEARTS) assertsIs "scoreboard objectives modify test rendertype hearts"
			remove() assertsIs "scoreboard objectives remove test"
			setDisplaySlot(DisplaySlots.list) assertsIs "scoreboard objectives setdisplay list test"

			add(self(), 5) assertsIs "scoreboard players add @s test 5"
			enable(self()) assertsIs "scoreboard players enable @s test"
			get(self()) assertsIs "scoreboard players get @s test"
			operation(self(), Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
			remove(self(), 5) assertsIs "scoreboard players remove @s test 5"
			reset(self()) assertsIs "scoreboard players reset @s test"
			set(self(), 5) assertsIs "scoreboard players set @s test 5"

			player(self()) {
				add(5) assertsIs "scoreboard players add @s test 5"
				enable() assertsIs "scoreboard players enable @s test"
				get() assertsIs "scoreboard players get @s test"
				operation(Operation.ADD, self(), "test") assertsIs "scoreboard players operation @s test += @s test"
				operation(Operation.ADD, this) assertsIs "scoreboard players operation @s test += @s test"
				remove(5) assertsIs "scoreboard players remove @s test 5"
				reset() assertsIs "scoreboard players reset @s test"
				set(5) assertsIs "scoreboard players set @s test 5"
			}
		}
	}

	scoreboard.objectives {
		list() assertsIs "scoreboard objectives list"
	}

	scoreboard.objective("test") {
		get(self()) assertsIs "scoreboard players get @s test"
	}

	scoreboard.objective(self(), "test") {
		get() assertsIs "scoreboard players get @s test"
	}

	scoreboard.players {
		get(self(), "test") assertsIs "scoreboard players get @s test"
	}

	scoreboard.player(self()) {
		get("test") assertsIs "scoreboard players get @s test"
	}

	scoreboard.player(self()) {
		objective("test") {
			get() assertsIs "scoreboard players get @s test"
		}
	}

	scoreboard.players.get(self(), "test") assertsIs "scoreboard players get @s test"
	scoreboard.objectives.list() assertsIs "scoreboard objectives list"
}
