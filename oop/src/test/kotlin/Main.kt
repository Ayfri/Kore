import arguments.DisplaySlots
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.enums.Gamemode
import arguments.scores.score
import commands.execute.execute
import commands.execute.run
import commands.say
import entities.*
import functions.function
import items.itemStack
import items.summon
import pack.pack
import scoreboard.*
import teams.*

fun main() {
	dataPack("oop") {
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")

		pack {
			description = textComponent("This is a test")
			format = 10
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
