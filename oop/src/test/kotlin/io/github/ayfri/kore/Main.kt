package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.scores.score
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

fun main() {
	dataPack("oop") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")

		pack {
			description = textComponent("This is a test")
			minFormat = packFormat(10)
			maxFormat = packFormat(10)
			packFormat = packFormat(10)
		}

		function("main") {
			player.giveItem(item)
			player.executeAt {
				run {
					item.summon(textComponent {
						text = "MY ITEM"
						color = Color.RED
						bold = true
					})
				}
			}

			player.executeAs {
				run {
					it.teleportTo(it)
				}
			}

			execute {
				ifCondition {
					score(player.asSelector(), "deathCount") greaterThanOrEqualTo 2
				}

				run {
					player.joinTeam("red")
				}
			}

			player.getScoreEntity("test").apply {
				set(10)
				this += 5

				copyFrom(player.asSelector(), "foodLevel")
			}

			scoreboard("other.test") {
				create()
				setDisplaySlot(DisplaySlots.sidebar)
				setDisplayName("Test")
			}

			team("red") {
				ensureExists()
				setColor(Color.DARK_RED)
				setPrefix(textComponent {
					text = "RED "
					color = Color.DARK_RED
					bold = true
				})

				addMembers(player)
			}

			say("Loaded")
		}
	}.generate()
}
