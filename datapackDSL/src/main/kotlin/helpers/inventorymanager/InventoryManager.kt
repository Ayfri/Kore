package helpers.inventorymanager

import DataPack
import arguments.*
import arguments.numbers.rangeOrInt
import commands.*
import commands.execute.execute
import commands.execute.run
import functions.Function
import functions.load
import functions.tick
import generated.Entities
import generated.Items
import net.benwoodworth.knbt.addNbtCompound

data class InventoryManager<T : Argument.Container>(val container: T) {
	val slotsListeners = mutableListOf<SlotEventListener>()

	fun getScoreName(dataPack: DataPack) = "_inventory_manager_${dataPack.name}_click_listener_$counter"

	init {
		counter++
	}

	context(Function)
	fun clear(slot: ItemSlotType) = items.replace(container, slot, Items.AIR, 1)

	context(Function)
	fun clearAll() = when (container) {
		is Argument.Entity -> clear(container)
		is Vec3 -> data(container).remove("Items")
		else -> error("Cannot clear items from $container")
	}

	context(Function)
	fun clearAll(item: Argument.Item) = when (container) {
		is Argument.Entity -> clear(container, item)
		is Vec3 -> data(container).remove("Items[{id:\"${item.asId()}\",${item.nbtData?.let { "tag:$it" } ?: ""}}]")
		else -> error("Cannot clear items from $container")
	}

	context(Function)
	fun modify(slot: ItemSlotType, modifier: String) = items.modify(container, slot, modifier)

	context(Function)
	fun replace(slot: ItemSlotType, item: Argument.Item, count: Int? = null) = items.replace(container, slot, item, count)

	context(Function)
	fun replace(fromSlot: ItemSlotType, withSlot: ItemSlotType, modifier: String? = null) =
		items.replace(container, fromSlot, container, withSlot, modifier)

	context(Function)
	operator fun set(slot: ItemSlotType, item: Argument.Item) = replace(slot, item)

	context(Function)
	operator fun set(slot: Int, item: Argument.Item) = replace(CONTAINER[slot], item)

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

		scoreboard.objectives.add(scoreName, "dummy")
		if (container is Argument.Entity) scoreboard.players.set(container as Argument.ScoreHolder, scoreName, 0)
		else {
			summon(Entities.MARKER) {
				this["Tags"] = nbtListOf(entityTag, InventoryManager.INVENTORY_MANAGER_ENTITY_TAG)
			}
			scoreboard.players.set(allEntities { nbt = nbt { this["Tags"] = nbtListOf(entityTag) } }, scoreName, 0)
		}
	}

	tick("tick_inventory_manager_${hashCode()}") {
		slotsListeners.forEach { slotListener ->
			slotListener.onTick?.let(::apply)
			slotListener.onTickFunction?.let(::function)

			val containerNbt = nbt {
				when (slotListener.container) {
					is Argument.Entity -> this["Inventory"] = nbtList {
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
				is Argument.Entity -> self()
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
								score(scoreName, rangeOrInt(1))
							}

							nbt = containerNbt
						}

						if (container is Argument.Entity) asTarget(targets)
						else ifCondition {
							score(scoreBoardSelector, scoreName) equalTo 1
							data(container as Argument.Data, containerNbt.toString())
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

					if (container is Argument.Entity) asTarget(targets)
					else unlessCondition {
						ifCondition {
							score(scoreBoardSelector, scoreName) equalTo 0
						}

						data(container as Argument.Data, containerNbt.toString())
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
				/*
								if (it.type != SlotEventType.WHEN_TAKEN) return@slotListenersLoop
								execute {
									val targets = allEntities {
										scores {
											score(scoreName, rangeOrInt(1))
										}

										nbt = containerNbt
									}

									if (container is Argument.Entity) asTarget(targets)
									else ifCondition {
										score(scoreBoardSelector, scoreName) equalTo 1
										data(container as Argument.Data, containerNbt.toString())
									}

									run {
										scoreboard.players.set(scoreBoardSelector, scoreName, 0)
									}
								} */
			}
		}
	}
}

context(Function)
fun InventoryManager<Vec3>.setBlock(block: Argument.Block) = setBlock(container, block)

fun <T : Argument.Container> inventoryManager(container: T) = InventoryManager<T>(container)

context(DataPack)
fun <T : Argument.Container> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit): InventoryManager<T> =
	InventoryManager<T>(container).apply(block).apply { generateSlotsListeners() }

context(Function)
fun <T : Argument.Container> inventoryManager(container: T, block: InventoryManager<T>.() -> Unit) =
	InventoryManager<T>(container).apply(block).apply { generateSlotsListeners(datapack) }
