package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Copies an entity or block-entity name into the item's `custom_name` component.
 * Mirrors vanilla `minecraft:copy_name`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class CopyName(
	override var conditions: PredicateAsList? = null,
	val source: Source,
) : ItemFunction()

/** Loot-context entity/block source specifier. */
@Serializable(with = Source.Companion.SourceSerializer::class)
enum class Source {
	ATTACKING_ENTITY,
	BLOCK_ENTITY,
	DIRECT_ATTACKER,
	INTERACTING_ENTITY,
	LAST_DAMAGE_PLAYER,
	TARGET_ENTITY,
	THIS,
	;

	companion object {
		data object SourceSerializer : LowercaseSerializer<Source>(entries)
	}
}

/** Add a `copy_name` step to this modifier. */
fun ItemModifier.copyName(source: Source, block: CopyName.() -> Unit = {}) {
	modifiers += CopyName(source = source).apply(block)
}
