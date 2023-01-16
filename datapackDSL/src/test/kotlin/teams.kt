
import arguments.*
import arguments.numbers.asStartRangeOrInt
import arguments.selector.SelectorNbtData
import commands.*
import functions.Function
import functions.function
import functions.setTag
import generated.Effects
import generated.Items
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
const val vanishItemTag = "vanish"
const val vanishItemTrigger = "vanish_item"
val vanishItem = Items.WARPED_FUNGUS_ON_A_STICK

fun teamPlayer(team: Team, selector: SelectorNbtData.() -> Unit = {}) = allPlayers {
	this.team = team.name
	selector()
}

fun loadTeams(dataPack: DataPack) = dataPack.function("load") {
	detectVanishItemClick(dataPack)
	giveVanishItem(dataPack)
	vanish(dataPack)

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
		remove(vanishTrigger)
		remove(vanishItemTrigger)
		add(vanishTrigger, "trigger")
		add(vanishItemTrigger, "minecraft.used:${vanishItem.asArg().replace(":", ".")}")
	}

	setTag("load", tagNamespace = "minecraft")
}

private fun Function.displayVanish(value: Boolean) {
	startDebug()
	debug("Display vanish: $value") {
		color = Color.GRAY
	}
	endDebug()

	debug {
		title(self(), TitleLocation.ACTIONBAR, textComponent("Vanish: ") + textComponent(if (value) "ON" else "OFF") {
			color = if (value) Color.GREEN else Color.RED
		})
	}
}

fun Function.vanishPlayer() = effect(self()) {
	give(Effects.INVISIBILITY, 1, 0, true)
}

fun Function.unVanishPlayer() = effect(self()) {
	clear(Effects.INVISIBILITY)
}

fun vanish(dataPack: DataPack) = dataPack.function("vanish") {
	teamList.filter { it.vanish }.forEach {
		scoreboard.player(teamPlayer(it)) {
			enable(vanishTrigger)
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishTrigger, 1)
				}
			})

			run(Function::vanishPlayer)
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishTrigger, 0)
				}

				nbt = nbt {
					this["ActiveEffects"] = nbtList {
						addNbtCompound {
							this["Id"] = Effects.INVISIBILITY.ordinal
							this["ShowParticles"] = 0.toByte()
						}
					}
				}
			})

			run(Function::unVanishPlayer)
		}
	}

	execute {
		asTarget(allPlayers {
			scores {
				score(vanishTrigger, 2.asStartRangeOrInt())
			}
		})

		run {
			scoreboard.player(self()) {
				reset(vanishTrigger)
			}
		}
	}

	setTag("tick", tagNamespace = "minecraft")
}

fun giveVanishItem(dataPack: DataPack) = dataPack.function("give_vanish_item") {
	teamList.filter { it.vanish }.forEach {
		execute {
			asTarget(teamPlayer(it))

			run {
				give(self(), item(vanishItem.asArg().removePrefix("minecraft:"), nbtData = nbt {
					this[vanishItemTag] = 1.toByte()
				}))
			}
		}
	}
}

fun detectVanishItemClick(dataPack: DataPack) = dataPack.function("detect_vanish_item_click") {
	teamList.filter { it.vanish }.forEach {
		val itemNbt = nbt {
			this["SelectedItem"] = nbt {
				this["tag"] = nbt {
					this[vanishItemTag] = 1.toByte()
				}
			}
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishItemTrigger, 1)
					score(vanishTrigger, 0)
				}
				nbt = itemNbt
			})

			run {
				displayVanish(true)
				scoreboard.player(self()) {
					reset(vanishItemTrigger)
					set(vanishTrigger, 1)
				}
			}
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishItemTrigger, 1)
					score(vanishTrigger, 1)
				}
				nbt = itemNbt
			})

			run {
				displayVanish(false)
				scoreboard.player(self()) {
					reset(vanishItemTrigger)
					set(vanishTrigger, 0)
				}
			}
		}

		execute {
			asTarget(teamPlayer(it) {
				nbt = !itemNbt
			})

			run {
				scoreboard.player(self()) {
					reset(vanishItemTrigger)
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
