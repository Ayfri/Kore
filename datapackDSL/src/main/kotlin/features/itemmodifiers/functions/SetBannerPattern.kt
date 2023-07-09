package features.itemmodifiers.functions

import data.item.BannerPattern
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetBannerPattern(
	var patterns: List<BannerPattern>,
	var append: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setBannerPattern(append: Boolean? = null, patterns: MutableList<BannerPattern>.() -> Unit = {}) {
	function = SetBannerPattern(buildList(patterns), append)
}
