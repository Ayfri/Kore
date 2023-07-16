package helpers.inventorymanager

import arguments.ItemSlotType
import arguments.maths.Vec3
import arguments.types.ContainerArgument
import arguments.types.EntityArgument
import arguments.types.literals.allEntities
import arguments.types.literals.randomUUID
import arguments.types.literals.self
import arguments.types.resources.FunctionArgument
import arguments.types.resources.ItemArgument
import commands.data
import commands.execute.execute
import commands.execute.run
import commands.items
import commands.kill
import functions.Function
import generated.Entities
import generated.Items
import generated.type
import net.benwoodworth.knbt.addNbtCompound
import utils.nbt
import utils.nbtList
import utils.set

enum class SlotEventType {
	DURING_TAKEN,
	ONCE_TAKEN,
	WHEN_TAKEN,
}

data class SlotEventListener(
	val container: ContainerArgument,
	val slot: ItemSlotType,
	var item: ItemArgument,
) {
	val events = mutableListOf<SlotEvent>()
	var randomTag = randomUUID().asString()
	internal var onTick: (Function.() -> Unit)? = null
	internal var onTickFunction: FunctionArgument? = null

	val randomTagNbt
		get() = nbt {
			this["SlotEventListener"] = randomTag
		}

	init {
		item.nbtData = nbt {
			item.nbtData?.let {
				it.forEach { (key, value) ->
					this[key] = value
				}
			}

			randomTagNbt.entries.forEach { (key, value) ->
				this[key] = value
			}
		}
	}

	fun onTick(block: Function.() -> Unit) {
		onTick = block
	}

	fun onTick(function: FunctionArgument) {
		onTickFunction = function
	}

	context(Function)
	fun clearAllItemsNotInSlot(fromContainer: ContainerArgument = container) = when (fromContainer) {
		is EntityArgument -> items {
			fun executeIfItem(index: Int) = execute {
				asTarget(fromContainer)
				asTarget(self {
					nbt = nbt {
						this["Inventory"] = nbtList {
							addNbtCompound {
								this["Slot"] = index.toByte()
								this["tag"] = randomTagNbt
							}
						}
					}
				})

				run {
					items.replace(fromContainer, ItemSlotType.fromIndex(index, true), Items.AIR)
				}
			}

			repeat(36) {
				if (fromContainer == container && it == slot.asIndex()) return@repeat
				executeIfItem(it)
			}
			executeIfItem(-106)
		}

		is Vec3 -> data(fromContainer) {
			val itemNbt = ",tag:$randomTagNbt"
			repeat(52) { index ->
				if (fromContainer == container && index == slot.asIndex()) return@repeat
				remove("Items[{Slot:${index}b$itemNbt}]")
			}
			remove("Items[{Slot:53b$itemNbt}]")
		}

		else -> error("Unsupported container type: $fromContainer")
	}

	context(Function)
	fun killAllItemsNotInSlot() = kill(allEntities {
		type(Entities.ITEM)

		nbt = nbt {
			this["Item"] = nbt {
				this["tag"] = randomTagNbt
			}
		}
	})

	context(Function)
	fun setItemInSlot() = items.replace(container, slot, item)
}

fun InventoryManager<*>.slotEvent(
	slot: ItemSlotType,
	item: ItemArgument,
	block: SlotEventListener.() -> Unit
) {
	slotsListeners += SlotEventListener(container, slot, item).apply(block)
}
