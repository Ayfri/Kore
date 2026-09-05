package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/**
 * Writes an SNBT blob to the item's `custom_data` component. Mirrors `minecraft:set_custom_data`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetCustomData(
	override var conditions: PredicateAsList? = null,
	@Serializable(with = NbtAsJsonSerializer::class)
	val tag: NbtTag,
) : ItemFunction()

/** Add a `set_custom_data` step with a built NBT tag. */
fun ItemModifier.setCustomData(tag: NbtTag, block: SetCustomData.() -> Unit = {}) {
	modifiers += SetCustomData(tag = tag).apply(block)
}

/** Add a `set_custom_data` step building a compound with the Kotlin DSL. */
fun ItemModifier.setCustomData(block: NbtCompoundBuilder.() -> Unit) =
	SetCustomData(tag = nbt(block)).also { modifiers += it }
