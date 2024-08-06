package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.features.enchantment.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EquipmentDropsSpecifier.Companion.EquipmentDropsEnchantedSerializer::class)
enum class EquipmentDropsSpecifier {
	ATTACKER,
	VICTIM;

	companion object {
		data object EquipmentDropsEnchantedSerializer : LowercaseSerializer<EquipmentDropsSpecifier>(entries)
	}
}

@Serializable
data class EquipmentDropsConditionalEffect(
	var enchanted: EquipmentDropsSpecifier,
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)

fun EquipmentDropsConditionalEffect.requirements(block: Predicate.() -> Unit) =
	apply { requirements = Predicate().apply(block).predicateConditions }
