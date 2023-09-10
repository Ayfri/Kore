package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.literalName
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ItemModifierArgument
import io.github.ayfri.kore.functions.Function

class ItemSlot(private val fn: Function, val container: ContainerArgument, val slot: ItemSlotType) {
	fun modify(modifier: ItemModifierArgument) = fn.items.modify(container, slot, modifier)
	fun replace(item: ItemArgument, count: Int? = null) = fn.items.replace(container, slot, item, count)
	fun replace(with: ContainerArgument, withSlot: ItemSlotType, modifier: ItemModifierArgument? = null) =
		fn.items.replace(container, slot, with, withSlot, modifier)
}

class Item(private val fn: Function) {
	fun slot(container: ContainerArgument, slot: ItemSlotType) = ItemSlot(fn, container, slot)
	fun slot(container: ContainerArgument, slot: ItemSlotType, block: ItemSlot.() -> Command) = ItemSlot(fn, container, slot).block()

	fun modify(container: ContainerArgument, slot: ItemSlotType, modifier: ItemModifierArgument) =
		fn.addLine(command("item", literal("modify"), literal(container.literalName), container, slot, literal(modifier.asString())))

	fun replace(container: ContainerArgument, slot: ItemSlotType, item: ItemArgument, count: Int? = null) =
		fn.addLine(command("item", literal("replace"), literal(container.literalName), container, slot, literal("with"), item, int(count)))

	fun replace(
		container: ContainerArgument,
		slot: ItemSlotType,
		with: ContainerArgument,
		withSlot: ItemSlotType,
		modifier: ItemModifierArgument? = null
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
				literal(modifier?.asString())
			)
		)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Command) = Item(this).block()
fun Function.itemSlot(container: ContainerArgument, slot: ItemSlotType, block: ItemSlot.() -> Command) =
	ItemSlot(this, container, slot).block()
