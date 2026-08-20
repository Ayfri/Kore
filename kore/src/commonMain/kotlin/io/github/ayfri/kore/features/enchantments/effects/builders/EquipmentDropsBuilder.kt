package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.value.ValueEffect
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The list of conditional value effects of the `equipment_drops` component, changing how likely the equipment of a
 * killed entity is to drop.
 *
 * Each entry names which side of the fight has to carry the enchantment, so effects are added through
 * `on(enchanted) { }` rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#equipment_drops
 */
@Serializable(with = EquipmentDropsBuilder.Companion.EquipmentDropsBuilderSerializer::class)
data class EquipmentDropsBuilder(var effects: List<EquipmentDropsConditionalEffect> = emptyList()) : EffectBuilder() {
	companion object {
		data object EquipmentDropsBuilderSerializer :
			InlineAutoSerializer<EquipmentDropsBuilder, List<EquipmentDropsConditionalEffect>>(
				serializer<List<EquipmentDropsConditionalEffect>>(),
				EquipmentDropsBuilder::effects,
				::EquipmentDropsBuilder,
				serialName = "EquipmentDropsBuilder",
			)
	}
}

/** Collects the effects an `equipment_drops` entry applies, all sharing the same enchanted side. */
class EquipmentDropsScope internal constructor(
	private val enchanted: EquipmentDropsSpecifier,
	private val builder: EquipmentDropsBuilder,
) : ValueEffectScope {
	override fun addEffect(effect: ValueEffect) {
		builder.effects += EquipmentDropsConditionalEffect(enchanted, effect, effect.requirements)
	}
}

/**
 * Appends every effect built in [block], each applying when [enchanted] carries the enchantment.
 *
 * ```kotlin
 * equipmentDrops {
 *     on(EquipmentDropsSpecifier.ATTACKER) {
 *         add(0.05f) {
 *             requirements { weatherCheck(raining = true) }
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#equipment_drops
 */
fun EquipmentDropsBuilder.on(enchanted: EquipmentDropsSpecifier, block: EquipmentDropsScope.() -> Unit) {
	EquipmentDropsScope(enchanted, this).apply(block)
}
