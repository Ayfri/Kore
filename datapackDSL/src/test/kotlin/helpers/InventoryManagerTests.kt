package helpers

import arguments.*
import arguments.chatcomponents.scoreComponent
import arguments.chatcomponents.textComponent
import arguments.scores.score
import arguments.selector.scores
import commands.TitleLocation
import commands.execute.execute
import commands.execute.run
import commands.scoreboard
import commands.tellraw
import commands.title
import functions.Function
import functions.load
import generated.Blocks
import generated.Items
import helpers.inventorymanager.*
import utils.assertsIs

fun Function.inventoryManagerTests() {
	val inventoryManager = inventoryManager(vec3(0, 0, 0))
	inventoryManager.clear(WEAPON) assertsIs "item replace block 0 0 0 weapon with minecraft:air 1"
	inventoryManager.clearAll() assertsIs "data remove block 0 0 0 Items"
	inventoryManager.clearAll(Items.DIAMOND_SWORD {
		this["Damage"] = 0
	}) assertsIs "data remove block 0 0 0 Items[{id:\"minecraft:diamond_sword\",tag:{Damage:0}}]"
	inventoryManager.modify(WEAPON, "baz") assertsIs "item modify block 0 0 0 weapon baz"

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
		scoreboard.objectives.add(counterScoreName, "dummy")
		scoreboard.players.set(playerInventory.container as Argument.ScoreHolder, counterScoreName, 0)
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
