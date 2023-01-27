package commands

import arguments.*
import functions.Function

class Item(private val fn: Function) {
	fun modifyBlock(pos: Vec3, slot: ItemSlotType, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), pos, slot, literal(modifier)
		)
	)

	fun modifyEntity(entity: Argument.Entity, slot: ItemSlotType, modifier: String) = fn.addLine(
		command(
			"item", literal("modify"), entity, slot, literal(modifier)
		)
	)

	fun replaceBlock(pos: Vec3, slot: ItemSlotType, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("block"), pos, slot, literal("with"), item, int(count)
		)
	)

	fun replaceBlock(pos: Vec3, slot: ItemSlotType, withBlock: Vec3, withSlot: ItemSlotType, modifier: String? = null) = fn.addLine(
		command(
			"item",
			literal("replace"),
			literal("block"),
			pos,
			slot,
			literal("from"),
			literal("block"),
			withBlock,
			withSlot,
			literal(modifier)
		)
	)

	fun replaceBlock(pos: Vec3, slot: ItemSlotType, withEntity: Argument.Entity, withSlot: ItemSlotType, modifier: String? = null) =
		fn.addLine(
			command(
				"item",
				literal("replace"),
				literal("block"),
				pos,
				slot,
				literal("from"),
				literal("entity"),
				withEntity,
				withSlot,
				literal(modifier)
			)
	)

	fun replaceEntity(entity: Argument.Entity, slot: ItemSlotType, item: Argument.Item, count: Int? = null) = fn.addLine(
		command(
			"item", literal("replace"), literal("entity"), entity, slot, literal("with"), item, int(count)
		)
	)

	fun replaceEntity(entity: Argument.Entity, slot: ItemSlotType, withBlock: Vec3, withSlot: ItemSlotType, modifier: String? = null) =
		fn.addLine(
			command(
				"item",
				literal("replace"),
				literal("entity"),
				entity,
				slot,
				literal("from"),
				literal("block"),
				withBlock,
				withSlot,
				literal(modifier)
			)
		)

	fun replaceEntity(
		entity: Argument.Entity,
		slot: ItemSlotType,
		withEntity: Argument.Entity,
		withSlot: ItemSlotType,
		modifier: String? = null
	) = fn.addLine(
		command(
			"item",
			literal("replace"),
			literal("entity"),
			entity,
			slot,
			literal("from"),
			literal("entity"),
			withEntity,
			withSlot,
			literal(modifier)
		)
	)
}

val Function.items get() = Item(this)
fun Function.items(block: Item.() -> Command) = Item(this).block()
