import arguments.Color
import arguments.NamedColor
import arguments.textComponent
import commands.CollisionRule
import commands.teams
import functions.function
import functions.setTag

class Team(val name: String, val display: String, val color: NamedColor, val bold: Boolean = false, val canBePushed: Boolean = true)

val teamList = listOf(
	Team("admin", "Admin", Color.DARK_RED, bold = true, canBePushed = false),
	Team("mod", "Mod", Color.DARK_GREEN, bold = true, canBePushed = false),
	Team("helper", "Helper", Color.DARK_AQUA, bold = true, canBePushed = false),
	Team("builder", "Builder", Color.GOLD, bold = true),
	Team("vip", "VIP", Color.LIGHT_PURPLE, bold = true, canBePushed = false),
	Team("member", "Member", Color.WHITE),
	Team("default", "Default", Color.GRAY)
)

fun loadTeams(dataPack: DataPack) = dataPack.function("load") {
	teamList.forEach {
		teams {
			add(it.name, textComponent(it.display))
			modify(it.name) {
				color(it.color)
				friendlyFire(false)
				if (!it.canBePushed) collisionRule(CollisionRule.NEVER)
				prefix(textComponent {
					text = "<${it.display}>"
					color = it.color
					bold = it.bold
				})
			}
		}
	}
	
	setTag("load", tagNamespace = "minecraft")
}

fun unloadTeams(dataPack: DataPack) = dataPack.function("unload") {
	teamList.forEach {
		teams.remove(it.name)
	}
}
