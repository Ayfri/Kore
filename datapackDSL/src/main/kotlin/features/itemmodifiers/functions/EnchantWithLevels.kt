package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class EnchantWithLevels(
	var levels: NumberProvider,
	var treasure: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.enchantWithLevels(levels: NumberProvider, treasure: Boolean? = null) {
	function = EnchantWithLevels(levels, treasure)
}
