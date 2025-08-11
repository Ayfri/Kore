package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Fills a player head item with the profile of an entity source.
 * Mirrors vanilla `minecraft:fill_player_head`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class FillPlayerHead(
	override var conditions: PredicateAsList? = null,
	val entity: Source,
) : ItemFunction()

/** Add a `fill_player_head` step. */
fun ItemModifier.fillPlayerHead(entity: Source, block: FillPlayerHead.() -> Unit = {}) {
	modifiers += FillPlayerHead(entity = entity).apply(block)
}
