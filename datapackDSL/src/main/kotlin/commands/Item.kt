package commands

import arguments.Argument
import arguments.SlotEntry
import arguments.int
import arguments.literal
import arguments.slot
import functions.Function

class Item(private val fn: Function) {
	fun modifyBlock(pos: Argument.Coordinate, slot: SlotEntry, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), pos, slot(slot), literal(modifier)
		)
	)
	
	fun modifyEntity(entity: Argument.Entity, slot: SlotEntry, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), entity, slot(slot), literal(modifier)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), pos, slot(slot), literal("with"), item, int(count)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, withBlock: Argument.Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), pos, slot(slot), literal("from"), literal("block"), withBlock, slot(withSlot), literal(modifier)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), pos, slot(slot), literal("from"), literal("entity"), withEntity, slot(withSlot), literal(modifier)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), entity, slot(slot), literal("with"), item, int(count)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withBlock: Argument.Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), entity, slot(slot), literal("from"), literal("block"), withBlock, slot(withSlot), literal(modifier)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", literal("replace"), entity, slot(slot), literal("from"), literal("entity"), withEntity, slot(withSlot), literal(modifier)
		)
	)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Unit) = Item(this).apply(block)
