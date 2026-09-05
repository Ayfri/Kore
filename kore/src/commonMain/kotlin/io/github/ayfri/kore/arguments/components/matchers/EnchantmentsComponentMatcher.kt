package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = EnchantmentsComponentMatcher.Companion.EnchantmentsComponentMatcherSerializer::class)
data class EnchantmentsComponentMatcher(var enchantments: List<EnchantmentPredicate> = emptyList()) : ComponentMatcher() {
	companion object {
		data object EnchantmentsComponentMatcherSerializer : InlineAutoSerializer<EnchantmentsComponentMatcher, List<EnchantmentPredicate>>(
			serializer<List<EnchantmentPredicate>>(),
			EnchantmentsComponentMatcher::enchantments,
			::EnchantmentsComponentMatcher,
			"EnchantmentsComponentMatcher",
		)
	}
}

fun DataComponentPredicate.enchantments(block: MutableList<EnchantmentPredicate>.() -> Unit) {
	matchers += EnchantmentsComponentMatcher().apply { enchantments = buildList(block) }
}

fun DataComponentPredicate.enchantments(vararg enchantments: EnchantmentPredicate) = enchantments { addAll(enchantments) }
fun DataComponentPredicate.enchantments(vararg enchantments: EnchantmentOrTagArgument, level: IntRangeOrIntJson = rangeOrInt(1)) =
	enchantments { addAll(enchantments.map { EnchantmentPredicate(listOf(it), level) }) }

fun MutableList<EnchantmentPredicate>.enchantment(init: EnchantmentPredicate.() -> Unit) = add(EnchantmentPredicate().apply(init))
fun MutableList<EnchantmentPredicate>.enchantment(vararg type: EnchantmentOrTagArgument, level: IntRangeOrIntJson = rangeOrInt(1)) =
	add(EnchantmentPredicate(type.toList(), level))

fun MutableList<EnchantmentPredicate>.enchantment(vararg type: EnchantmentOrTagArgument, level: Int) =
	add(EnchantmentPredicate(type.toList(), rangeOrInt(level)))
