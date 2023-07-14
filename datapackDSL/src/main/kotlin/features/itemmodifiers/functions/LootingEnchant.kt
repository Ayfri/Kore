package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class LootingEnchant(
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunction()

fun ItemModifier.lootingEnchant(count: NumberProvider, limit: Int? = null, block: LootingEnchant.() -> Unit = {}) {
	modifiers += LootingEnchant(count, limit).apply(block)
}
