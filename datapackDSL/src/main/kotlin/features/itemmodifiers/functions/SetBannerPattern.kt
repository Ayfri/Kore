package features.itemmodifiers.functions

import data.item.BannerPattern
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetBannerPattern(
	var patterns: List<BannerPattern>,
	var append: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setBannerPattern(append: Boolean? = null, patterns: MutableList<BannerPattern>.() -> Unit = {}) =
	SetBannerPattern(buildList(patterns), append).also { modifiers += it }
