package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.data.item.BannerPattern
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.BannerPatternArgument
import kotlinx.serialization.Serializable

/**
 * Sets or appends banner patterns on a banner item. Mirrors `minecraft:set_banner_pattern`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
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

fun MutableList<BannerPattern>.bannerPattern(
	type: BannerPatternArgument,
	color: FormattingColor,
	block: BannerPattern.() -> Unit = {},
) = apply { add(BannerPattern(type, color).apply(block)) }
