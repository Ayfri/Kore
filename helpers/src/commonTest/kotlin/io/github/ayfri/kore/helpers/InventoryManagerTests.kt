package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.ARMOR
import io.github.ayfri.kore.arguments.CONTAINER
import io.github.ayfri.kore.arguments.HOTBAR
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.helpers.inventorymanager.*
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

fun Function.inventoryManagerTests() {
	val inventoryManager = inventoryManager(vec3(0, 0, 0))
	inventoryManager.clear(WEAPON) assertsIs "item fill block 0 0 0 weapon with minecraft:air"
	inventoryManager.clear(CONTAINER) assertsIs "item fill block 0 0 0 container.* with minecraft:air"
	inventoryManager.fill(HOTBAR, Items.BREAD, 16) assertsIs "item fill block 0 0 0 hotbar.* with minecraft:bread 16"
	inventoryManager.replace(CONTAINER[0], CONTAINER[1]) assertsIs "item replace block 0 0 0 container.0 from block 0 0 0 container.1"
	inventoryManager[ARMOR] = Items.DIAMOND_HELMET
	lines.last() assertsIs "item replace block 0 0 0 armor.* with minecraft:diamond_helmet"
	inventoryManager.clearAll() assertsIs "data remove block 0 0 0 Items"
	inventoryManager.clearAll(Items.DIAMOND_SWORD {
		damage(0)
	}) assertsIs "data remove block 0 0 0 Items[{id:\"minecraft:diamond_sword\",components:{\"damage\":0}}]"
	inventoryManager.modify(
		WEAPON,
		ItemModifierArgument("baz")
	) assertsIs "item modify block 0 0 0 weapon minecraft:baz"

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

class InventoryManagerTests : FunSpec({
	test("inventory manager") {
		dataPack("helpers_tests") {
			load { inventoryManagerTests() }
		}
	}

	test("slot listeners test the slot with an item predicate and find their marker by tag") {
		dataPack("helpers_tests") {
			inventoryManager(vec3(0, -59, 0)) {
				slotEvent(CONTAINER[0], Items.DIAMOND_SWORD) {
					onTake(FunctionArgument("taken", "helpers_tests"))
					onTick { killAllItemsNotInSlot() }
				}
			}

			val score = "_inventory_manager_helpers_tests_click_listener_0"
			val marker = "@e[limit=1,tag=${score}_marker,type=minecraft:marker]"
			generatedFunctions.first { it.name.endsWith("load_inventory_manager_0") }.lines shouldBe listOf(
				"scoreboard objectives add $score dummy",
				"kill @e[tag=${score}_marker,type=minecraft:marker]",
				"summon minecraft:marker ~ ~ ~ {Tags:[\"${score}_marker\",\"inventory_manager\"]}",
				"scoreboard players set $marker $score 0",
			)
			val tick = generatedFunctions.first { it.name.endsWith("tick_inventory_manager_0") }.lines
			tick.first() shouldBe
				"execute as @e[type=minecraft:item] if items entity @s contents *[custom_data~{slot_event_listener:\"73162f9e\"}] run kill @s"
			tick.last() shouldBe
				"execute if score $marker $score matches 0 unless items block 0 -59 0 container.0 *[custom_data~{slot_event_listener:\"73162f9e\"}] run function helpers_tests:generated_scopes/generated_96d32be4"
		}
	}

	test("clearAllItemsNotInSlot only clears the slots holding the listener's item") {
		dataPack("helpers_tests") {
			inventoryManager(nearestPlayer()) {
				slotEvent(HOTBAR[0], Items.NETHER_STAR) {
					onTick { clearAllItemsNotInSlot() }
				}
			}

			val tick = generatedFunctions.first { it.name.endsWith("tick_inventory_manager_0") }.lines
			val tagged = "*[custom_data~{slot_event_listener:\"1ec09d\"}]"
			tick.filter { it.startsWith("item ") } shouldBe emptyList()
			tick shouldContain "execute if items entity @p hotbar.1 $tagged run item replace entity @p hotbar.1 with minecraft:air"
			tick shouldContain "execute if items entity @p weapon.offhand $tagged run item replace entity @p weapon.offhand with minecraft:air"
			(tick.any { "hotbar.0 " in it && it.startsWith("execute if items") }) shouldBe false
		}
	}

	test("slot events resolve inside a function of a data pack") {
		dataPack("helpers_tests") {
			function("demo") {
				val playerInv = inventoryManager(nearestPlayer())
				playerInv.slotEvent(HOTBAR[0], Items.NETHER_STAR) {
					onTake { say("taken") }
					onceTaken { say("once") }
					duringTake { setItemInSlot() }
					event(SlotEventType.WHEN_TAKEN) { say("event") }
				}
				playerInv.generateSlotsListeners()

				inventoryManager(vec3(0, -59, 0)) {
					slotEvent(CONTAINER[0], Items.STICK) { onTake { say("chest") } }
				}
			}

			generatedFunctions.count { it.name.startsWith("tick_inventory_manager_") } shouldBe 2
			generatedFunctions.count { it.name.startsWith("when_taken_event_") } shouldBe 3
		}
	}

	test("inventory manager names are numbered per pack") {
		repeat(2) {
			dataPack("helpers_tests") {
				val first = inventoryManager(vec3(0, 0, 0))
				val second = inventoryManager(vec3(0, 0, 0))

				second.getScoreName(this) assertsIs "_inventory_manager_helpers_tests_click_listener_0"
				first.getScoreName(this) assertsIs "_inventory_manager_helpers_tests_click_listener_1"
				second.getScoreName(this) assertsIs "_inventory_manager_helpers_tests_click_listener_0"

				InventoryManager.removeClickDetectors()
				generatedFunctions.last().toString() assertsIs """
					scoreboard objectives remove _inventory_manager_helpers_tests_click_listener_0
					scoreboard objectives remove _inventory_manager_helpers_tests_click_listener_1
				""".trimIndent()
			}
		}
	}
})
