package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.Enchantment
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Enchantments.Companion.EnchantmentsComponentMatcherSerializer::class)
data class Enchantments(var enchantments: List<Enchantment> = emptyList()) : ComponentMatcher() {
	companion object {
		data object EnchantmentsComponentMatcherSerializer : InlineAutoSerializer<Enchantments>(Enchantments::class)
	}
}

fun ItemStackSubPredicates.enchantments(block: MutableList<Enchantment>.() -> Unit) {
	matchers += Enchantments().apply { enchantments = buildList(block) }
}

fun ItemStackSubPredicates.enchantments(vararg enchantments: Enchantment) = enchantments { addAll(enchantments) }
fun ItemStackSubPredicates.enchantments(vararg enchantments: EnchantmentOrTagArgument, level: IntRangeOrIntJson = rangeOrInt(1)) =
	enchantments { addAll(enchantments.map { Enchantment(listOf(it), level) }) }

fun MutableList<Enchantment>.enchantment(init: Enchantment.() -> Unit) = add(Enchantment().apply(init))
fun MutableList<Enchantment>.enchantment(vararg type: EnchantmentOrTagArgument, level: IntRangeOrIntJson = rangeOrInt(1)) =
	add(Enchantment(type.toList(), level))

fun MutableList<Enchantment>.enchantment(vararg type: EnchantmentOrTagArgument, level: Int) =
	add(Enchantment(type.toList(), rangeOrInt(level)))
