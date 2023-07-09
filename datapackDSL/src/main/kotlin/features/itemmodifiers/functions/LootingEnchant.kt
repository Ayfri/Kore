package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class LootingEnchant(
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.lootingEnchant(count: NumberProvider, limit: Int? = null) {
	function = LootingEnchant(count, limit)
}
