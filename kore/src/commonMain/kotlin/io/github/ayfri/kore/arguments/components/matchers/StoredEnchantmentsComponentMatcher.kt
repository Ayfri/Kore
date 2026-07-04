package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.Enchantment
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = StoredEnchantmentsComponentMatcher.Companion.StoredEnchantmentsComponentMatcherSerializer::class)
data class StoredEnchantmentsComponentMatcher(var enchantments: List<Enchantment> = emptyList()) : ComponentMatcher() {
	companion object {
		data object StoredEnchantmentsComponentMatcherSerializer :
			InlineAutoSerializer<StoredEnchantmentsComponentMatcher, List<Enchantment>>(
				serializer<List<Enchantment>>(),
				StoredEnchantmentsComponentMatcher::enchantments,
				::StoredEnchantmentsComponentMatcher,
				"StoredEnchantmentsComponentMatcher",
			)
	}
}

fun ItemStackSubPredicates.storedEnchantments(block: MutableList<Enchantment>.() -> Unit) {
	matchers += StoredEnchantmentsComponentMatcher().apply { enchantments = buildList(block) }
}

fun ItemStackSubPredicates.storedEnchantments(vararg enchantments: Enchantment) = storedEnchantments { addAll(enchantments) }
