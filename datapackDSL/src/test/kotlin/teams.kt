import arguments.*
import arguments.numbers.asRangeOrInt
import arguments.selector.SelectorNbtData
import commands.*
import functions.function
import functions.setTag
import generated.Effects
import net.benwoodworth.knbt.addNbtCompound

data class Team(
	val name: String,
	val display: String,
	val color: NamedColor,
	val bold: Boolean = false,
	val canBePushed: Boolean = true,
	val vanish: Boolean = false
)

val teamList = listOf(
	Team("admin", "Admin", Color.DARK_RED, bold = true, canBePushed = false, vanish = true),
	Team("mod", "Mod", Color.DARK_GREEN, bold = true, canBePushed = false, vanish = true),
	Team("helper", "Helper", Color.DARK_AQUA, bold = true, canBePushed = false),
	Team("builder", "Builder", Color.GOLD, bold = true),
	Team("vip", "VIP", Color.LIGHT_PURPLE, bold = true, canBePushed = false),
	Team("member", "Member", Color.WHITE),
	Team("default", "Default", Color.GRAY)
)

const val vanishTrigger = "vanish"

fun loadTeams(dataPack: DataPack) = dataPack.function("load") {
	vanish(dataPack)
	debug = true

	teamList.forEach {
		teams {
			add(it.name, textComponent(it.display))
			modify(it.name) {
				color(it.color)
				friendlyFire(false)
				nametagVisibility(if (it.vanish) Visibility.HIDE_FOR_OTHER_TEAMS else Visibility.ALWAYS)
				if (!it.canBePushed) collisionRule(CollisionRule.NEVER)
				prefix(textComponent {
					text = "${it.display} "
					color = it.color
					bold = it.bold
				})
			}
		}
	}

	addBlankLine()

	scoreboard.objectives {
		add(vanishTrigger, "trigger")
	}

	setTag("load", tagNamespace = "minecraft")
}

fun vanish(dataPack: DataPack) = dataPack.function("vanish") {
	teamList.filter { it.vanish }.forEach {
		fun teamPlayer(selector: SelectorNbtData.() -> Unit = {}) = allPlayers {
			team = it.name
			selector()
		}

		scoreboard.player(teamPlayer()) {
			enable(vanishTrigger)
		}

		execute {
			asTarget(teamPlayer())

			ifCondition {
				score(self(), vanishTrigger, 1.asRangeOrInt())
			}

			run {
				effect(self()) {
					give("minecraft:invisibility", 1, 0, true)
				}
			}
		}


		execute {
			asTarget(teamPlayer {
				nbt = nbt {
					this["ActiveEffects"] = nbtList {
						addNbtCompound {
							this["Id"] = Effects.INVISIBILITY.ordinal
							this["ShowParticles"] = 0.toByte()
						}
					}
				}
			})

			ifCondition {
				score(self(), vanishTrigger, 0.asRangeOrInt())
			}

			run {
				effect(self()) {
					clear("minecraft:invisibility")
				}
			}
		}
	}

	setTag("tick", tagNamespace = "minecraft")
}

fun unloadTeams(dataPack: DataPack) = dataPack.function("unload") {
	teamList.forEach {
		teams.remove(it.name)
	}
}
