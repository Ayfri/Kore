package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.literalName
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.encodeToString

/** DSL scope for manipulating a single item slot. */
data class ItemSlot(private val fn: Function, val container: ContainerArgument, val slot: ItemSlotType) {
	/** Applies [modifier] to the item stack in this slot. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(modifier: ItemModifierArgument) = fn.items.modify(container, slot, modifier)
	/** Builds an item modifier with [block] and applies it to this slot. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(block: ItemModifier.() -> Unit) = fn.items.modify(container, slot, block)

	/** Replaces the item stack in this slot with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(item: ItemArgument, count: Int? = null) = fn.items.replace(container, slot, item, count)
	/** Replaces the item stack in this slot with the contents of [with]'s [withSlot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(with: ContainerArgument, withSlot: ItemSlotType, modifier: ItemModifierArgument? = null) =
		fn.items.replace(container, slot, with, withSlot, modifier)

	/** Replaces the item stack in this slot with the contents of [with]'s [withSlot], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(
		with: ContainerArgument,
		withSlot: ItemSlotType,
		block: ItemModifier.() -> Unit
	) = fn.items.replace(container, slot, with, withSlot, block = block)
}

/** DSL scope for the `/item` command. */
data class Item(private val fn: Function) {
	/** Returns a reusable [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun slot(container: ContainerArgument, slot: ItemSlotType) = ItemSlot(fn, container, slot)
	/** Opens the [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun slot(container: ContainerArgument, slot: ItemSlotType, block: ItemSlot.() -> Command) = ItemSlot(fn, container, slot).block()

	/** Applies [modifier] to the item stack in [container]'s [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(container: ContainerArgument, slot: ItemSlotType, modifier: ItemModifierArgument) =
		fn.addLine(command("item", literal("modify"), literal(container.literalName), container, slot, literal(modifier.asString())))

	/** Builds an item modifier with [block] and applies it to [container]'s [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(container: ContainerArgument, slot: ItemSlotType, block: ItemModifier.() -> Unit) =
		fn.addLine(command("item", literal("modify"), literal(container.literalName), container, slot, literal(snbtSerializer.encodeToString(ItemModifier().apply(block)))))

	/** Replaces [container]'s [slot] with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(container: ContainerArgument, slot: ItemSlotType, item: ItemArgument, count: Int? = null) =
		fn.addLine(command("item", literal("replace"), literal(container.literalName), container, slot, literal("with"), item, int(count)))

	/** Replaces [container]'s [slot] with the contents of [with]'s [withSlot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
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
				if (container is Vec3) literal(container.toStringTruncated()) else container,
				slot,
				literal("from"),
				literal(with.literalName),
				if (with is Vec3) literal(with.toStringTruncated()) else with,
				withSlot,
				literal(modifier?.asString())
			)
		)

	/** Replaces [container]'s [slot] with the contents of [with]'s [withSlot], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(
		container: ContainerArgument,
		slot: ItemSlotType,
		with: ContainerArgument,
		withSlot: ItemSlotType,
		block: ItemModifier.() -> Unit
	) =
		fn.addLine(
			command(
				"item",
				literal("replace"),
				literal(container.literalName),
				if (container is Vec3) literal(container.toStringTruncated()) else container,
				slot,
				literal("from"),
				literal(with.literalName),
				if (with is Vec3) literal(with.toStringTruncated()) else with,
				withSlot,
				literal(snbtSerializer.encodeToString(ItemModifier().apply(block)))
			)
		)
}

/** Returns the reusable [Item] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
val Function.items get() = Item(this)
/** Opens the [Item] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
fun Function.items(block: Item.() -> Command) = Item(this).block()
/** Opens the [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
fun Function.itemSlot(container: ContainerArgument, slot: ItemSlotType, block: ItemSlot.() -> Command) =
	ItemSlot(this, container, slot).block()
