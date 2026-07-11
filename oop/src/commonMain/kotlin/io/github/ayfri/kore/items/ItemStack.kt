package io.github.ayfri.kore.items

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.item.CustomNameComponent
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.coordinate
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.entities.summon.ItemEntitySummonData
import io.github.ayfri.kore.entities.summon.applyCommonEntitySummonNbt
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.NbtCompoundBuilder

private fun ItemStack.itemEntityItemCompound(effectiveComponents: Components) = nbt {
	this["id"] = id
	count?.let { this["count"] = it.toInt() }
	if (effectiveComponents.components.isNotEmpty()) {
		this["components"] = effectiveComponents.asNbt()
	}
}

/**
 * Summons this stack as a `minecraft:item` entity. The `Item` compound uses the post-1.20.5 shape (`id`, optional
 * `count`, optional `components` as NBT compound). Entity-only tags are written on the root compound, not inside
 * `Item`.
 *
 * Configure spawn behavior in [configure] on [ItemEntitySummonData] (for example `tags = listOf("shop")`).
 *
 * @param extraEntityNbt Applied last so callers can set any other entity tags (for example `UUID`, `Air`, modded data).
 */
context(fn: Function)
fun ItemStack.summon(
	position: Vec3 = coordinate(),
	extraEntityNbt: (NbtCompoundBuilder.() -> Unit)? = null,
	configure: ItemEntitySummonData.() -> Unit = {},
) = fn.summon(EntityTypes.ITEM, position) {
	val opts = ItemEntitySummonData().apply(configure)
	if (opts.showcase) {
		if (opts.pickupDelay == null) opts.pickupDelay = 32767.toShort()
		if (opts.age == null) opts.age = (-32768).toShort()
		if (opts.invulnerable == null) opts.invulnerable = true
		if (opts.noGravity == null) opts.noGravity = true
	}

	val effectiveComponents = components ?: Components()
	opts.applyCommonEntitySummonNbt(this)

	this["Item"] = itemEntityItemCompound(effectiveComponents)

	opts.pickupDelay?.let { this["PickupDelay"] = it }
	opts.age?.let { this["Age"] = it }
	opts.health?.let { this["Health"] = it }
	opts.owner?.let { this["Owner"] = it.toNBTIntArray() }
	opts.thrower?.let { this["Thrower"] = it.toNBTIntArray() }

	if (opts.showcase && opts.displayName == null) {
		val fromComponent =
			(effectiveComponents[ItemComponentTypes.CUSTOM_NAME.name.lowercase()] as? CustomNameComponent)?.component
		fromComponent?.let {
			this["CustomName"] = it.toNbtTag()
			this["CustomNameVisible"] = opts.displayNameVisible
		}
	}

	extraEntityNbt?.invoke(this)
}

context(fn: Function)
fun ItemStack.summon(
	customName: ChatComponents,
	visible: Boolean = true,
	extraEntityNbt: (NbtCompoundBuilder.() -> Unit)? = null
) =
	summon(coordinate(), extraEntityNbt) {
		displayName = customName
		displayNameVisible = visible
	}

context(fn: Function)
fun ItemStack.summon(
	text: String,
	color: Color,
	visible: Boolean = true,
	extraEntityNbt: (NbtCompoundBuilder.() -> Unit)? = null
) =
	summon(coordinate(), extraEntityNbt) {
		displayName = textComponent(text, color)
		displayNameVisible = visible
	}
