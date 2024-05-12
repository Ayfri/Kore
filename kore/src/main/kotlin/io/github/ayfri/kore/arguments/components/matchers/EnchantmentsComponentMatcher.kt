package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.item.Enchantment
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = Enchantments.Companion.EnchantmentsComponentMatcherSerializer::class)
data class Enchantments(
	var enchantments: List<Enchantment> = emptyList(),
) : ComponentMatcher() {
	companion object {
		data object EnchantmentsComponentMatcherSerializer : InlineSerializer<Enchantments, List<Enchantment>>(
			ListSerializer(Enchantment.serializer()),
			Enchantments::enchantments
		)
	}
}

fun ItemStackSubPredicates.enchantments(block: MutableList<Enchantment>.() -> Unit) {
	matchers += Enchantments().apply { enchantments = buildList(block) }
}

fun ItemStackSubPredicates.enchantments(vararg enchantments: Enchantment) = enchantments { addAll(enchantments) }

fun MutableList<Enchantment>.enchantment(init: Enchantment.() -> Unit) = add(Enchantment().apply(init))
fun MutableList<Enchantment>.enchantment(type: EnchantmentArgument, level: IntRangeOrIntJson) = add(Enchantment(type, level))
fun MutableList<Enchantment>.enchantment(type: EnchantmentArgument, level: Int) = add(Enchantment(type, rangeOrInt(level)))
