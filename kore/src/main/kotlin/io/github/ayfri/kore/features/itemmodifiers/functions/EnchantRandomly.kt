package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.EnchantmentOrTagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	override var conditions: PredicateAsList? = null,
	var options: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var onlyCompatible: Boolean? = null,
) : ItemFunction()

fun ItemModifier.enchantRandomly(
	enchantments: List<EnchantmentOrTagArgument> = emptyList(),
	onlyCompatible: Boolean? = null,
	block: EnchantRandomly.() -> Unit = {},
) {
	modifiers += EnchantRandomly(options = enchantments, onlyCompatible = onlyCompatible).apply(block)
}

fun ItemModifier.enchantRandomly(
	vararg enchantments: EnchantmentOrTagArgument,
	onlyCompatible: Boolean? = null, block: EnchantRandomly.() -> Unit = {},
) {
	modifiers += EnchantRandomly(options = enchantments.toList(), onlyCompatible = onlyCompatible).apply(block)
}

fun EnchantRandomly.enchantments(block: MutableList<EnchantmentOrTagArgument>.() -> Unit = {}) {
	options = buildList(block)
}
