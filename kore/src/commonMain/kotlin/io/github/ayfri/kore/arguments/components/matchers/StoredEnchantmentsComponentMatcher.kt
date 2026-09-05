package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = StoredEnchantmentsComponentMatcher.Companion.StoredEnchantmentsComponentMatcherSerializer::class)
data class StoredEnchantmentsComponentMatcher(var enchantments: List<EnchantmentPredicate> = emptyList()) : ComponentMatcher() {
	companion object {
		data object StoredEnchantmentsComponentMatcherSerializer :
			InlineAutoSerializer<StoredEnchantmentsComponentMatcher, List<EnchantmentPredicate>>(
				serializer<List<EnchantmentPredicate>>(),
				StoredEnchantmentsComponentMatcher::enchantments,
				::StoredEnchantmentsComponentMatcher,
				"StoredEnchantmentsComponentMatcher",
			)
	}
}

fun DataComponentPredicate.storedEnchantments(block: MutableList<EnchantmentPredicate>.() -> Unit) {
	matchers += StoredEnchantmentsComponentMatcher().apply { enchantments = buildList(block) }
}

fun DataComponentPredicate.storedEnchantments(vararg enchantments: EnchantmentPredicate) = storedEnchantments { addAll(enchantments) }
