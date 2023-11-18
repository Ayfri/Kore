package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.CONTAINER
import io.github.ayfri.kore.arguments.HOTBAR
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.ItemModifierArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.execute.run
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.inventorymanager.*
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

fun Function.inventoryManagerTests() {
	val inventoryManager = inventoryManager(vec3(0, 0, 0))
	inventoryManager.clear(WEAPON) assertsIs "item replace block 0 0 0 weapon with minecraft:air 1"
	inventoryManager.clearAll() assertsIs "data remove block 0 0 0 Items"
	inventoryManager.clearAll(Items.DIAMOND_SWORD {
		this["Damage"] = 0
	}) assertsIs "data remove block 0 0 0 Items[{id:\"minecraft:diamond_sword\",tag:{Damage:0}}]"
	inventoryManager.modify(WEAPON, ItemModifierArgument("baz")) assertsIs "item modify block 0 0 0 weapon minecraft:baz"

	val counterScoreName = "take_counter"
	val playerInventory = inventoryManager(nearestPlayer())
	playerInventory.slotEvent(HOTBAR[0], Items.NETHER_STAR {
		this["display"] = nbt {
			this["Name"] = textComponent("Do not move me", color = Color.RED).toJsonString()
		}
	}) {
		onTake {
			title(self(), TitleLocation.ACTIONBAR, textComponent("I said don't take me", color = Color.RED))
			scoreboard.players.add(self(), counterScoreName, 1)

			execute {
				asTarget(allEntities {
					scores {
						score(counterScoreName) greaterThanOrEqualTo 3
					}
				})

				run {
					title(self(), TitleLocation.ACTIONBAR, textComponent("I said don't take me ", color = Color.RED) {
						bold = true
					} + scoreComponent(counterScoreName, self()) + textComponent(" more times", color = Color.RED) {
						bold = true
					})
				}
			}
		}

		duringTake {
			setItemInSlot()
		}

		onTick {
			clearAllItemsNotInSlot()
			killAllItemsNotInSlot()
		}

		setItemInSlot()
	}

	playerInventory.generateSlotsListeners()

	datapack.load {
		scoreboard.objectives.add(counterScoreName)
		scoreboard.players.set(playerInventory.container as ScoreHolderArgument, counterScoreName, 0)
	}

	inventoryManager(vec3(0, -59, 0)) {
		setBlock(Blocks.CHEST)

		slotEvent(CONTAINER[0], Items.DIAMOND_SWORD) {
			onTake {
				tellraw(allPlayers(), textComponent("You took the diamond sword from the chest", color = Color.RED))
			}

			duringTake {
				setItemInSlot()
			}

			onTick {
				clearAllItemsNotInSlot()
				clearAllItemsNotInSlot(allPlayers())
				killAllItemsNotInSlot()
			}

			setItemInSlot()
		}

		generateSlotsListeners()
	}
}
