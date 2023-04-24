package commands

import arguments.*
import functions.Function

class Item(private val fn: Function) {
	fun modify(container: Argument.Container, slot: ItemSlotType, modifier: String) =
		fn.addLine(command("item", literal("modify"), literal(container.literalName), container, slot, literal(modifier)))

	fun replace(container: Argument.Container, slot: ItemSlotType, item: Argument.Item, count: Int? = null) =
		fn.addLine(command("item", literal("replace"), literal(container.literalName), container, slot, literal("with"), item, int(count)))

	fun replace(container: Argument.Container, slot: ItemSlotType, withBlock: Vec3, withSlot: ItemSlotType, modifier: String? = null) =
		fn.addLine(
			command(
				"item",
				literal("replace"),
				literal(container.literalName),
				container,
				slot,
				literal("from"),
				literal("block"),
				withBlock,
				withSlot,
				literal(modifier)
			)
		)

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
