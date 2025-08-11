package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.data.item.BannerPattern
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Sets or appends banner patterns on a banner item. Mirrors `minecraft:set_banner_pattern`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetBannerPattern(
	override var conditions: PredicateAsList? = null,
	var patterns: List<BannerPattern>,
	var append: Boolean? = null,
) : ItemFunction()

/** Add a `set_banner_pattern` step. */
fun ItemModifier.setBannerPattern(append: Boolean? = null, patterns: MutableList<BannerPattern>.() -> Unit = {}) =
	SetBannerPattern(patterns = buildList(patterns), append = append).also { modifiers += it }
