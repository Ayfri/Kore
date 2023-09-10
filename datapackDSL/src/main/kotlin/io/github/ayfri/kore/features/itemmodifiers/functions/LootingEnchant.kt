package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

@Serializable
data class LootingEnchant(
	override var conditions: PredicateAsList? = null,
	var count: NumberProvider,
	var limit: Int? = null,
) : ItemFunction()

fun ItemModifier.lootingEnchant(count: NumberProvider, limit: Int? = null, block: LootingEnchant.() -> Unit = {}) {
	modifiers += LootingEnchant(count = count, limit = limit).apply(block)
}
