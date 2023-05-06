package commands

import arguments.*
import functions.Function

class ItemSlot(private val fn: Function, val container: Argument.Container, val slot: ItemSlotType) {
	fun modify(modifier: String) = fn.items.modify(container, slot, modifier)
	fun replace(item: Argument.Item, count: Int? = null) = fn.items.replace(container, slot, item, count)
	fun replace(with: Argument.Container, withSlot: ItemSlotType, modifier: String? = null) =
		fn.items.replace(container, slot, with, withSlot, modifier)
}

class Item(private val fn: Function) {
	fun slot(container: Argument.Container, slot: ItemSlotType) = ItemSlot(fn, container, slot)
	fun slot(container: Argument.Container, slot: ItemSlotType, block: ItemSlot.() -> Command) = ItemSlot(fn, container, slot).block()

	fun modify(container: Argument.Container, slot: ItemSlotType, modifier: String) =
		fn.addLine(command("item", literal("modify"), literal(container.literalName), container, slot, literal(modifier)))

	fun replace(container: Argument.Container, slot: ItemSlotType, item: Argument.Item, count: Int? = null) =
		fn.addLine(command("item", literal("replace"), literal(container.literalName), container, slot, literal("with"), item, int(count)))

	fun replace(
		container: Argument.Container,
		slot: ItemSlotType,
		with: Argument.Container,
		withSlot: ItemSlotType,
		modifier: String? = null
	) =
		fn.addLine(
			command(
				"item",
				literal("replace"),
				literal(container.literalName),
				container,
				slot,
				literal("from"),
				literal(with.literalName),
				with,
				withSlot,
				literal(modifier)
			)
		)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Command) = Item(this).block()
fun Function.itemSlot(container: Argument.Container, slot: ItemSlotType, block: ItemSlot.() -> Command) =
	ItemSlot(this, container, slot).block()
