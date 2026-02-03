package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.stringifiedNbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.Serializable

/**
 * Writes an SNBT blob to the item's `custom_data` component. Mirrors `minecraft:set_custom_data`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCustomData(
	override var conditions: PredicateAsList? = null,
	val nbt: String,
) : ItemFunction() {
	constructor(nbt: NbtTag) : this(nbt = stringifiedNbt(nbt))
}

/** Add a `set_custom_data` step with a built NBT tag. */
fun ItemModifier.setCustomData(nbt: NbtTag, block: SetCustomData.() -> Unit = {}) {
	modifiers += SetCustomData(nbt).apply(block)
}

/** Add a `set_custom_data` step building a compound with the Kotlin DSL. */
fun ItemModifier.setCustomData(block: NbtCompoundBuilder.() -> Unit) =
    SetCustomData(nbt(block)).also { modifiers += it }
