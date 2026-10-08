package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.SlotsArgument
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.literalName
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
import io.github.ayfri.kore.utils.encodeToSnbt

/**
 * How `/item` spreads the source items over the destination slots.
 *
 * With destination slots `[12345]` and source items `(ABC)`: [FILL] writes `[ABCAB]`, [OVERRIDE] `[ABC__]` and [REPLACE] `[ABC45]`.
 */
enum class ItemEditMode {
	/** Repeats the source items until every destination slot is filled. */
	FILL,

	/** Writes each source item in order and empties the remaining destination slots. */
	OVERRIDE,

	/** Writes each source item in order and leaves the remaining destination slots untouched. */
	REPLACE,
}

/** DSL scope for manipulating the slots selected by [slot]. */
data class ItemSlot(private val fn: Function, val container: ContainerArgument, val slot: SlotsArgument) {
	/** Applies [modifier] to the item stacks in these slots. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(modifier: ItemModifierArgument) = fn.items.modify(container, slot, modifier)
	/** Builds an item modifier with [block] and applies it to these slots. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(block: ItemModifier.() -> Unit) = fn.items.modify(container, slot, block)

	/** Edits these slots with [item], spread following [mode]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(mode: ItemEditMode, item: ItemArgument, count: Int? = null) = fn.items.edit(mode, container, slot, item, count)
	/** Edits these slots with the items of [with]'s [withSlot], spread following [mode]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(mode: ItemEditMode, with: ContainerArgument, withSlot: SlotsArgument, modifier: ItemModifierArgument? = null) =
		fn.items.edit(mode, container, slot, with, withSlot, modifier)

	/** Edits these slots with the items of [with]'s [withSlot], spread following [mode], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(mode: ItemEditMode, with: ContainerArgument, withSlot: SlotsArgument, block: ItemModifier.() -> Unit) =
		fn.items.edit(mode, container, slot, with, withSlot, block)

	/** Fills these slots with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun fill(item: ItemArgument, count: Int? = null) = edit(ItemEditMode.FILL, item, count)
	/** Fills these slots with the items of [with]'s [withSlot], repeated until every slot is filled. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun fill(with: ContainerArgument, withSlot: SlotsArgument, modifier: ItemModifierArgument? = null) =
		edit(ItemEditMode.FILL, with, withSlot, modifier)

	/** Overrides these slots with [item], emptying the other ones. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun override(item: ItemArgument, count: Int? = null) = edit(ItemEditMode.OVERRIDE, item, count)
	/** Overrides these slots with the items of [with]'s [withSlot], emptying the slots left over. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun override(with: ContainerArgument, withSlot: SlotsArgument, modifier: ItemModifierArgument? = null) =
		edit(ItemEditMode.OVERRIDE, with, withSlot, modifier)

	/** Replaces these slots with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(item: ItemArgument, count: Int? = null) = edit(ItemEditMode.REPLACE, item, count)
	/** Replaces these slots with the items of [with]'s [withSlot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(with: ContainerArgument, withSlot: SlotsArgument, modifier: ItemModifierArgument? = null) =
		edit(ItemEditMode.REPLACE, with, withSlot, modifier)

	/** Replaces these slots with the items of [with]'s [withSlot], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(with: ContainerArgument, withSlot: SlotsArgument, block: ItemModifier.() -> Unit) =
		edit(ItemEditMode.REPLACE, with, withSlot, block)
}

/** DSL scope for the `/item` command. */
data class Item(private val fn: Function) {
	/** Returns a reusable [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun slot(container: ContainerArgument, slot: SlotsArgument) = ItemSlot(fn, container, slot)
	/** Opens the [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun slot(container: ContainerArgument, slot: SlotsArgument, block: ItemSlot.() -> Command) = ItemSlot(fn, container, slot).block()

	/** Applies [modifier] to the item stacks in [container]'s [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(container: ContainerArgument, slot: SlotsArgument, modifier: ItemModifierArgument) =
		fn.addLine(command("item", literal("modify"), *target(container), slot, literal(modifier.asString())))

	/** Builds an item modifier with [block] and applies it to [container]'s [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun modify(container: ContainerArgument, slot: SlotsArgument, block: ItemModifier.() -> Unit) =
		fn.addLine(command("item", literal("modify"), *target(container), slot, literal(encodeToSnbt(ItemModifier().apply(block)))))

	/** Edits [container]'s [slot] with [item], spread following [mode]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(mode: ItemEditMode, container: ContainerArgument, slot: SlotsArgument, item: ItemArgument, count: Int? = null) =
		fn.addLine(command("item", literal(mode.name.lowercase()), *target(container), slot, literal("with"), item, int(count)))

	/** Edits [container]'s [slot] with the items of [with]'s [withSlot], spread following [mode]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(
		mode: ItemEditMode,
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		modifier: ItemModifierArgument? = null,
	) = editFrom(mode, container, slot, with, withSlot, literal(modifier?.asString()))

	/** Edits [container]'s [slot] with the items of [with]'s [withSlot], spread following [mode], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun edit(
		mode: ItemEditMode,
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		block: ItemModifier.() -> Unit,
	) = editFrom(mode, container, slot, with, withSlot, literal(encodeToSnbt(ItemModifier().apply(block))))

	/** Fills [container]'s [slot] with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun fill(container: ContainerArgument, slot: SlotsArgument, item: ItemArgument, count: Int? = null) =
		edit(ItemEditMode.FILL, container, slot, item, count)

	/** Fills [container]'s [slot] with the items of [with]'s [withSlot], repeated until every slot is filled. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun fill(
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		modifier: ItemModifierArgument? = null,
	) = edit(ItemEditMode.FILL, container, slot, with, withSlot, modifier)

	/** Overrides [container]'s [slot] with [item], emptying the other ones. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun override(container: ContainerArgument, slot: SlotsArgument, item: ItemArgument, count: Int? = null) =
		edit(ItemEditMode.OVERRIDE, container, slot, item, count)

	/** Overrides [container]'s [slot] with the items of [with]'s [withSlot], emptying the slots left over. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun override(
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		modifier: ItemModifierArgument? = null,
	) = edit(ItemEditMode.OVERRIDE, container, slot, with, withSlot, modifier)

	/** Replaces [container]'s [slot] with [item]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(container: ContainerArgument, slot: SlotsArgument, item: ItemArgument, count: Int? = null) =
		edit(ItemEditMode.REPLACE, container, slot, item, count)

	/** Replaces [container]'s [slot] with the items of [with]'s [withSlot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		modifier: ItemModifierArgument? = null,
	) = edit(ItemEditMode.REPLACE, container, slot, with, withSlot, modifier)

	/** Replaces [container]'s [slot] with the items of [with]'s [withSlot], applying [block] as a modifier. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
	fun replace(
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		block: ItemModifier.() -> Unit,
	) = edit(ItemEditMode.REPLACE, container, slot, with, withSlot, block)

	private fun editFrom(
		mode: ItemEditMode,
		container: ContainerArgument,
		slot: SlotsArgument,
		with: ContainerArgument,
		withSlot: SlotsArgument,
		modifier: Argument?,
	) = fn.addLine(
		command("item", literal(mode.name.lowercase()), *target(container), slot, literal("from"), *target(with), withSlot, modifier)
	)

	/** `block <x y z>` or `entity <selector>`, block positions truncated to integers as `/item` requires. */
	private fun target(container: ContainerArgument) =
		arrayOf(literal(container.literalName), if (container is Vec3) literal(container.toStringTruncated()) else container)
}

/** Returns the reusable [Item] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
val Function.items get() = Item(this)
/** Opens the [Item] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
fun Function.items(block: Item.() -> Command) = Item(this).block()
/** Opens the [ItemSlot] DSL for [container] and [slot]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/item) */
fun Function.itemSlot(container: ContainerArgument, slot: SlotsArgument, block: ItemSlot.() -> Command) =
	ItemSlot(this, container, slot).block()
