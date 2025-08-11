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
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.addNbtCompound

/**
 * Inventory Manager
 *
 * A high-level helper around Minecraft inventories that lets you:
 * - Register slot listeners which react when a slot contents is taken/changed.
 * - Enforce desired items in slots each tick (e.g. GUIs, kiosks, rule-based containers).
 * - Operate on both entity inventories and block containers (`Vec3`).
 *
 * The manager wires the necessary scoreboard objective and, for block containers, a helper marker entity
 * to detect state changes. It auto-generates minimal `load`/`tick` functions to run the listeners.
 *
 * See docs: https://kore.ayfri.com/docs/inventory-manager
 */
data class InventoryManager<T : ContainerArgument>(val container: T) {
	val slotsListeners = mutableListOf<SlotEventListener>()

	fun getScoreName(dataPack: DataPack) = "_inventory_manager_${dataPack.name}_click_listener_$counter"

	init {
		counter++
	}

	/** Replace the given [slot] with air. */
	context(fn: Function)
	fun clear(slot: ItemSlotType) = fn.items.replace(container, slot, Items.AIR, 1)

	/** Clear all items for this [container] (entity or block). */
	context(fn: Function)
	fun clearAll() = when (container) {
		is EntityArgument -> fn.clear(container)
		is Vec3 -> fn.data(container).remove("Items")
		else -> error("Cannot clear items from $container")
	}

	/** Clear all occurrences of a specific [item] in this [container]. */
	context(fn: Function)
	fun clearAll(item: ItemArgument) = when (container) {
		is EntityArgument -> fn.clear(container, item)
		is Vec3 -> fn.data(container).remove("Items[{id:\"${item.asId()}\",${item.components?.let { "components:${it.asJson()}" } ?: ""}}]")
		else -> error("Cannot clear items from $container")
	}

	/** Apply an item [modifier] to the given [slot]. */
	context(fn: Function)
	fun modify(slot: ItemSlotType, modifier: ItemModifierArgument) = fn.items.modify(container, slot, modifier)

	/** Replace the given [slot] with an [item] and optional [count]. */
	context(fn: Function)
	fun replace(slot: ItemSlotType, item: ItemArgument, count: Int? = null) = fn.items.replace(container, slot, item, count)

	/** Replace items by moving from [fromSlot] to [withSlot], optionally applying a [modifier]. */
	context(fn: Function)
	fun replace(fromSlot: ItemSlotType, withSlot: ItemSlotType, modifier: ItemModifierArgument? = null) =
		fn.items.replace(container, fromSlot, container, withSlot, modifier)

	/** Shortcut: assign an [item] into a typed [slot]. */
	context(fn: Function)
	operator fun set(slot: ItemSlotType, item: ItemArgument) = replace(slot, item)

	/** Shortcut: assign an [item] into a numeric [slot] using `CONTAINER[index]`. */
	context(fn: Function)
	operator fun set(slot: Int, item: ItemArgument) = replace(CONTAINER[slot], item)

	companion object {
		/** Tag assigned to helper marker entities used for block containers. */
		var INVENTORY_MANAGER_ENTITY_TAG = "inventory_manager"
		var counter = 0

		/** Remove scoreboard objectives created by Inventory Manager across runs. */
		context(dp: DataPack)
		fun removeClickDetectors() {
			dp.load("inventory_manager_${hashCode()}_remover") {
				repeat(counter) {
					scoreboard.objectives.remove("_inventory_manager_${name}_click_detector_$it")
				}
			}
		}
	}
}

/** Generate load/tick wiring for all registered slot listeners on a specific [dp]. */
fun InventoryManager<*>.generateSlotsListeners(dp: DataPack) = with(dp) { generateSlotsListeners() }

/** Generate load/tick wiring using the current function’s datapack. */
context(fn: Function)
fun InventoryManager<*>.generateSlotsListeners() = generateSlotsListeners(fn.datapack)

/**
 * Emit the `load` and `tick` functions that power all registered slot listeners for this manager.
 * Handles scoreboard set-up and entity scoping for both entity and block containers.
 */
context(dp: DataPack)
fun InventoryManager<*>.generateSlotsListeners() {
	val scoreName = getScoreName(dp)
	val entityTag = randomUUID().asString()

	dp.load("load_inventory_manager_${hashCode()}") {
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

	dp.tick("tick_inventory_manager_${hashCode()}") {
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

/** For block containers, place a [block] at the managed position. */
context(fn: Function)
fun InventoryManager<Vec3>.setBlock(block: BlockArgument) = fn.setBlock(container, block)

/** Create an Inventory Manager for the given [container]. */
fun <T : ContainerArgument> inventoryManager(container: T) = InventoryManager(container)

/** Create, configure via [block], then generate listeners using this datapack context. */
context(fn: DataPack)
fun <T : ContainerArgument> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit) =
	InventoryManager(container).apply(block).apply { generateSlotsListeners() }

/** Create, configure via [block], then generate listeners using the current function’s datapack. */
context(fn: Function)
fun <T : ContainerArgument> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit) =
	InventoryManager(container).apply(block).apply { generateSlotsListeners(fn.datapack) }
