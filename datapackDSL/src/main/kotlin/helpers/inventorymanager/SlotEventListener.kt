package helpers.inventorymanager

import arguments.*
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

enum class SlotEventType {
	DURING_TAKEN,
	ONCE_TAKEN,
	WHEN_TAKEN,
}

data class SlotEventListener(
	val container: Argument.Container,
	val slot: ItemSlotType,
	var item: Argument.Item,
) {
	val events = mutableListOf<SlotEvent>()
	var randomTag = randomUUID().asString()
	internal var onTick: (Function.() -> Unit)? = null
	internal var onTickFunction: Argument.Function? = null

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

	fun onTick(function: Argument.Function) {
		onTickFunction = function
	}

	context(Function)
	fun clearAllItemsNotInSlot(fromContainer: Argument.Container = container) = when (fromContainer) {
		is Argument.Entity -> items {
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
	item: Argument.Item,
	block: SlotEventListener.() -> Unit
) {
	slotsListeners += SlotEventListener(container, slot, item).apply(block)
}
