import arguments.Color
import arguments.nbt
import commands.DisplaySlot
import entities.getScore
import entities.player
import functions.function
import net.benwoodworth.knbt.put
import scoreboard.copyFrom
import scoreboard.ensureExists
import scoreboard.plusAssign
import scoreboard.scoreboard
import scoreboard.set
import scoreboard.setDisplayName
import scoreboard.setDisplaySlot
import teams.addMembers
import teams.ensureExists
import teams.setColor
import teams.setPrefix
import teams.team

fun main() {
	dataPack("oop") {
		
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")
		
		function("main") {
			player.getScore("test").apply {
				ensureExists()
				
				set(10)
				this += 5
				
				copyFrom(player.asSelector(), "foodLevel")
			}
			
			scoreboard("other.test") {
				ensureExists()
				setDisplaySlot(DisplaySlot.sidebar)
				setDisplayName("Test")
			}
			
			team("red") {
				ensureExists()
				setColor(Color.DARK_RED)
				setPrefix(nbt {
					put("text", "RED ")
					put("color", Color.DARK_RED.name.lowercase())
					put("bold", true)
				})
				
				addMembers(player)
			}
		}
	}.generate()
}
