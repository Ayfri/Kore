package io.github.ayfri.kore.helpers.inventorymanager

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.CONTAINER
import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ItemModifierArgument
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.addNbtCompound

data class InventoryManager<T : ContainerArgument>(val container: T) {
	val slotsListeners = mutableListOf<SlotEventListener>()

	fun getScoreName(dataPack: DataPack) = "_inventory_manager_${dataPack.name}_click_listener_$counter"

	init {
		counter++
	}

	context(Function)
	fun clear(slot: ItemSlotType) = items.replace(container, slot, Items.AIR, 1)

	context(Function)
	fun clearAll() = when (container) {
		is EntityArgument -> clear(container)
		is Vec3 -> data(container).remove("Items")
		else -> error("Cannot clear items from $container")
	}

	context(Function)
	fun clearAll(item: ItemArgument) = when (container) {
		is EntityArgument -> clear(container, item)
		is Vec3 -> data(container).remove("Items[{id:\"${item.asId()}\",${item.components?.let { "components:$it" } ?: ""}}]")
		else -> error("Cannot clear items from $container")
	}

	context(Function)
	fun modify(slot: ItemSlotType, modifier: ItemModifierArgument) = items.modify(container, slot, modifier)

	context(Function)
	fun replace(slot: ItemSlotType, item: ItemArgument, count: Int? = null) = items.replace(container, slot, item, count)

	context(Function)
	fun replace(fromSlot: ItemSlotType, withSlot: ItemSlotType, modifier: ItemModifierArgument? = null) =
		items.replace(container, fromSlot, container, withSlot, modifier)

	context(Function)
	operator fun set(slot: ItemSlotType, item: ItemArgument) = replace(slot, item)

	context(Function)
	operator fun set(slot: Int, item: ItemArgument) = replace(CONTAINER[slot], item)

	companion object {
		var INVENTORY_MANAGER_ENTITY_TAG = "inventory_manager"
		var counter = 0

		context(DataPack)
		fun removeClickDetectors() {
			load("inventory_manager_${hashCode()}_remover") {
				repeat(counter) {
					scoreboard.objectives.remove("_inventory_manager_${name}_click_detector_$it")
				}
			}
		}
	}
}

fun InventoryManager<*>.generateSlotsListeners(dp: DataPack) = with(dp) { generateSlotsListeners() }

context(Function)
fun InventoryManager<*>.generateSlotsListeners() = generateSlotsListeners(datapack)

context(DataPack)
fun InventoryManager<*>.generateSlotsListeners() {
	val scoreName = getScoreName(this@DataPack)
	val entityTag = randomUUID().asString()

	load("load_inventory_manager_${hashCode()}") {
		kill(allEntities {
			nbt = nbt {
				this["Tags"] = nbtListOf(InventoryManager.INVENTORY_MANAGER_ENTITY_TAG)
			}
		})

		scoreboard.objectives.add(scoreName)
		if (container is EntityArgument) scoreboard.players.set(container as ScoreHolderArgument, scoreName, 0)
		else {
			summon(EntityTypes.MARKER) {
				this["Tags"] = nbtListOf(entityTag, InventoryManager.INVENTORY_MANAGER_ENTITY_TAG)
			}
			scoreboard.players.set(allEntities { nbt = nbt { this["Tags"] = nbtListOf(entityTag) } }, scoreName, 0)
		}
	}

	tick("tick_inventory_manager_${hashCode()}") {
		slotsListeners.forEach { slotListener ->
			slotListener.onTick?.let(::apply)
			slotListener.onTickFunction?.let { function(it) }

			val containerNbt = nbt {
				when (slotListener.container) {
					is EntityArgument -> this["Inventory"] = nbtList {
						addNbtCompound {
							this["Slot"] = slotListener.slot.asIndex().toByte()
							this["tag"] = slotListener.randomTagNbt
						}
					}

					else -> this["Items"] = nbtList {
						addNbtCompound {
							this["Slot"] = slotListener.slot.asIndex().toByte()
							this["tag"] = slotListener.randomTagNbt
						}
					}
				}
			}

			val scoreBoardSelector = when (container) {
				is EntityArgument -> self()
				else -> allEntities(true) {
					nbt = nbt {
						this["Tags"] = nbtListOf(entityTag)
					}
				}
			}

			slotListener.events.sortedBy {
				when (it.type) {
					SlotEventType.DURING_TAKEN -> 0
					SlotEventType.ONCE_TAKEN -> 1
					SlotEventType.WHEN_TAKEN -> 2
				}
			}.forEach slotListenersLoop@{ (function, type) ->
				if (type == SlotEventType.WHEN_TAKEN && slotListener.events.any { it.type == SlotEventType.DURING_TAKEN }) {
					execute {
						val targets = allEntities {
							scores {
								score(scoreName) greaterThanOrEqualTo 1
							}

							nbt = containerNbt
						}

						if (container is EntityArgument) asTarget(targets)
						else ifCondition {
							score(scoreBoardSelector, scoreName) equalTo 1
							data(container as DataArgument, containerNbt.toString())
						}

						run {
							scoreboard.players.set(scoreBoardSelector, scoreName, 0)
						}
					}
					return@slotListenersLoop
				}

				execute {
					val targets = allEntities {
						scores {
							score(scoreName, rangeOrInt(0))
						}

						nbt = !containerNbt
					}

					if (container is EntityArgument) asTarget(targets)
					else unlessCondition {
						ifCondition {
							score(scoreBoardSelector, scoreName) equalTo 0
						}

						data(container as DataArgument, containerNbt.toString())
					}

					run {
						if (type == SlotEventType.WHEN_TAKEN) scoreboard.players.set(scoreBoardSelector, scoreName, 1)
						if (type == SlotEventType.DURING_TAKEN) {
							slotListener.events.filter { it.type == SlotEventType.WHEN_TAKEN }.forEachIndexed { index, whenTakenEvent ->
								if (index == 0) scoreboard.players.set(scoreBoardSelector, scoreName, 1)
								function(whenTakenEvent.function)
							}
						}

						function(function)
					}
				}
			}
		}
	}
}

context(Function)
fun InventoryManager<Vec3>.setBlock(block: BlockArgument) = setBlock(container, block)

fun <T : ContainerArgument> inventoryManager(container: T) = InventoryManager<T>(container)

context(DataPack)
fun <T : ContainerArgument> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit): InventoryManager<T> =
	InventoryManager<T>(container).apply(block).apply { generateSlotsListeners() }

context(Function)
fun <T : ContainerArgument> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit) =
	InventoryManager<T>(container).apply(block).apply { generateSlotsListeners(datapack) }
