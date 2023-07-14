package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class EnchantWithLevels(
	var levels: NumberProvider,
	var treasure: Boolean? = null,
) : ItemFunction()

fun ItemModifier.enchantWithLevels(levels: NumberProvider, treasure: Boolean? = null, block: EnchantWithLevels.() -> Unit = {}) {
	modifiers += EnchantWithLevels(levels, treasure).apply(block)
}
