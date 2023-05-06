import arguments.*
import arguments.chatcomponents.textComponent
import arguments.colors.NamedColor
import arguments.scores.score
import arguments.selector.SelectorNbtData
import arguments.selector.scores
import commands.*
import commands.execute.execute
import commands.execute.run
import functions.Function
import functions.function
import functions.setTag
import generated.Effects
import generated.Items
import generated.Particles
import net.benwoodworth.knbt.addNbtCompound
import utils.asArg

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

fun DataPack.loadTeams() {
	detectVanishItemClick()
	giveVanishItem()
	vanish()

	function("load") {
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

fun Function.unVanishPlayer(): Command {
	effect(self()) {
		clear(Effects.INVISIBILITY)
	}

	return particle(Particles.WAX_OFF, coordinate(), coordinate(.125, .25, .125), 0.0, 5, ParticleMode.NORMAL)
}

fun DataPack.vanish() = function("vanish") {
	teamList.filter { it.vanish }.forEach {
		scoreboard.player(teamPlayer(it)) {
			enable(vanishTrigger)
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishTrigger) equalTo 1
				}
			})

			run(Function::vanishPlayer)
		}

		execute {
			asTarget(teamPlayer(it) {
				scores {
					score(vanishTrigger) equalTo 0
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
				score(vanishTrigger) greaterThanOrEqualTo 2
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

fun DataPack.giveVanishItem() = function("give_vanish_item") {
	teamList.filter(Team::vanish).forEach {
		execute {
			asTarget(teamPlayer(it))

			run {
				give(self(), vanishItem {
					this[vanishItemTag] = true
				})
			}
		}
	}
}

fun DataPack.detectVanishItemClick() = function("detect_vanish_item_click") {
	teamList.filter(Team::vanish).forEach {
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
					score(vanishItemTrigger) greaterThanOrEqualTo 1
					score(vanishTrigger) equalTo 0
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
					score(vanishItemTrigger) greaterThanOrEqualTo 1
					score(vanishTrigger) equalTo 1
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

fun DataPack.unloadTeams() = function("unload") {
	teamList.forEach {
		teams.remove(it.name)
	}
}
