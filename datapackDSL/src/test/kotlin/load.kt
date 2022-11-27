import arguments.Color
import arguments.nbt
import commands.asArg
import commands.teams
import functions.function
import functions.setTag
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.put

fun addLoad(dataPack: DataPack) {
	dataPack.function("load") {
		class Team(val name: String, val display: String, val color: NamedColor, val bold: Boolean = false)
		
		val teams = listOf(
			Team("admin", "Admin", Color.DARK_RED, true),
			Team("mod", "Mod", Color.DARK_GREEN, true),
			Team("helper", "Helper", Color.DARK_AQUA, true),
			Team("builder", "Builder", Color.GOLD, true),
			Team("vip", "VIP", Color.LIGHT_PURPLE, true),
			Team("member", "Member", Color.WHITE, true),
			Team("default", "Default", Color.GRAY, true)
		)
		
		teams.forEach {
			teams {
				add(it.name, NbtString(it.display))
				modify(it.name) {
					color(it.color)
					friendlyFire(false)
					prefix(nbt {
						put("text", "<${it.display}>")
						put("color", it.color.asArg())
						put("bold", it.bold)
					})
				}
			}
		}
		
		setTag("load", tagNamespace = "minecraft")
	}
}
