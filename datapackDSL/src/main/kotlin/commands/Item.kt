package commands

import arguments.*
import functions.Function

class Item(private val fn: Function) {
	fun modifyBlock(pos: Coordinate, slot: SlotEntry, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), pos, slot(slot), literal(modifier)
		)
	)

	fun modifyEntity(entity: Argument.Entity, slot: SlotEntry, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), entity, slot(slot), literal(modifier)
		)
	)

	fun replaceBlock(pos: Coordinate, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("block"), pos, slot(slot), literal("with"), item, int(count)
		)
	)

	fun replaceBlock(pos: Coordinate, slot: SlotEntry, withBlock: Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("block"), pos, slot(slot), literal("from"), literal("block"), withBlock, slot(withSlot), literal(modifier)
		)
	)

	fun replaceBlock(pos: Coordinate, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("block"), pos, slot(slot), literal("from"), literal("entity"), withEntity, slot(withSlot), literal(modifier)
		)
	)

	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("entity"), entity, slot(slot), literal("with"), item, int(count)
		)
	)

	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withBlock: Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("entity"), entity, slot(slot), literal("from"), literal("block"), withBlock, slot(withSlot), literal(modifier)
		)
	)

	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("entity"), entity, slot(slot), literal("from"), literal("entity"), withEntity, slot(withSlot), literal(modifier)
		)
	)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Command) = Item(this).block()
