package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class EnchantWithLevels(
	override var conditions: PredicateAsList? = null,
	var levels: NumberProvider,
	var treasure: Boolean? = null,
) : ItemFunction()

fun ItemModifier.enchantWithLevels(levels: NumberProvider, treasure: Boolean? = null, block: EnchantWithLevels.() -> Unit = {}) {
	modifiers += EnchantWithLevels(levels = levels, treasure = treasure).apply(block)
}
