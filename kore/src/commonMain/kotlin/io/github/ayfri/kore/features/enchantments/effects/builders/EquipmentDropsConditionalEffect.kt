package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Which side of a fight an `equipment_drops` entry looks at.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#equipment_drops
 */
@Serializable(with = EquipmentDropsSpecifier.Companion.EquipmentDropsSpecifierSerializer::class)
enum class EquipmentDropsSpecifier {
	/** The entity that dealt the killing blow. */
	ATTACKER,

	/** The entity that died and drops its equipment. */
	VICTIM,
	;

	companion object {
		data object EquipmentDropsSpecifierSerializer : LowercaseSerializer<EquipmentDropsSpecifier>(entries)
	}
}

/**
 * One entry of the `equipment_drops` component, applying [effect] to the drop chance when [enchanted] carries the
 * enchantment.
 *
 * Built by `equipmentDrops { on(enchanted) { } }` rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#equipment_drops
 *
 * @property enchanted The side that has to carry the enchantment for the entry to apply.
 * @property effect The effect applied to the drop chance.
 * @property requirements The conditions the entry applies under, always applying when `null`.
 */
@Serializable
data class EquipmentDropsConditionalEffect(
	var enchanted: EquipmentDropsSpecifier,
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)

/** Sets [EquipmentDropsConditionalEffect.requirements] to the predicate conditions built in [block]. */
fun EquipmentDropsConditionalEffect.requirements(block: Predicate.() -> Unit) =
	apply { requirements = Predicate().apply(block).predicateConditions }
