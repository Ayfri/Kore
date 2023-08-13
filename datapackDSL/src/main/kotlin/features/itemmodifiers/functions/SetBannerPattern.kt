package features.itemmodifiers.functions

import data.item.BannerPattern
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetBannerPattern(
	override var conditions: PredicateAsList? = null,
	var patterns: List<BannerPattern>,
	var append: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setBannerPattern(append: Boolean? = null, patterns: MutableList<BannerPattern>.() -> Unit = {}) =
	SetBannerPattern(patterns = buildList(patterns), append = append).also { modifiers += it }
