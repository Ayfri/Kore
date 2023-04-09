import arguments.Color
import arguments.DisplaySlot
import arguments.chatcomponents.textComponent
import arguments.enums.Gamemode
import arguments.numbers.rangeOrIntStart
import commands.execute.execute
import commands.execute.run
import commands.say
import entities.*
import functions.function
import items.itemStack
import items.summon
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
					score(player.asSelector(), "deathCount", rangeOrIntStart(2))
				}

				run {
					player.joinTeam("red")
				}
			}

			player.getScore("test").apply {
				set(10)
				this += 5

				copyFrom(player.asSelector(), "foodLevel")
			}

			scoreboard("other.test") {
				create()
				setDisplaySlot(DisplaySlot.sidebar)
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
