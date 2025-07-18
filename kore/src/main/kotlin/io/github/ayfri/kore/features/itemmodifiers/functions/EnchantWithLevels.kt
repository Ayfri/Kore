package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class EnchantWithLevels(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var levels: NumberProvider,
) : ItemFunction()

fun ItemModifier.enchantWithLevels(
	enchantments: List<EnchantmentOrTagArgument> = emptyList(),
	levels: NumberProvider = constant(0f),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments, levels = levels).apply(block)
}

fun ItemModifier.enchantWithLevels(
	vararg enchantments: EnchantmentOrTagArgument,
	levels: NumberProvider = constant(0f),
	block: EnchantWithLevels.() -> Unit = {},
) {
	modifiers += EnchantWithLevels(options = enchantments.toList(), levels = levels).apply(block)
}

fun EnchantWithLevels.enchantments(block: MutableList<EnchantmentOrTagArgument>.() -> Unit = {}) {
	options = buildList(block)
}
