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
			"item", fn.literal("modify"), pos, fn.slot(slot), fn.literal(modifier)
		)
	)
	
	fun modifyEntity(entity: Argument.Entity, slot: SlotEntry, modifier: String) = fn.addLine(
		command(
			"item", fn.literal("modify"), entity, fn.slot(slot), fn.literal(modifier)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), pos, fn.slot(slot), fn.literal("with"), item, fn.int(count)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, withBlock: Argument.Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), pos, fn.slot(slot), fn.literal("from"), fn.literal("block"), withBlock, fn.slot(withSlot), fn.literal(modifier)
		)
	)
	
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), pos, fn.slot(slot), fn.literal("from"), fn.literal("entity"), withEntity, fn.slot(withSlot), fn.literal(modifier)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), entity, fn.slot(slot), fn.literal("with"), item, fn.int(count)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withBlock: Argument.Coordinate, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), entity, fn.slot(slot), fn.literal("from"), fn.literal("block"), withBlock, fn.slot(withSlot), fn.literal(modifier)
		)
	)
	
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, withEntity: Argument.Entity, withSlot: SlotEntry, modifier: String? = null) = fn.addLine(
		command(
			"item", fn.literal("replace"), entity, fn.slot(slot), fn.literal("from"), fn.literal("entity"), withEntity, fn.slot(withSlot), fn.literal(modifier)
		)
	)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Unit) = Item(this).apply(block)
