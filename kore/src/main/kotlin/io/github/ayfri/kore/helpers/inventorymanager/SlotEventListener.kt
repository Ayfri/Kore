package io.github.ayfri.kore.helpers.inventorymanager

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.addNbtCompound

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
		// TODO : To update.
		/* 		item.components = nbt {
					item.nbtData?.let {
						it.forEach { (key, value) ->
							this[key] = value
						}
					}

					randomTagNbt.entries.forEach { (key, value) ->
						this[key] = value
					}
				} */
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
		type = EntityTypes.ITEM

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
	block: SlotEventListener.() -> Unit,
) {
	slotsListeners += SlotEventListener(container, slot, item).apply(block)
}
