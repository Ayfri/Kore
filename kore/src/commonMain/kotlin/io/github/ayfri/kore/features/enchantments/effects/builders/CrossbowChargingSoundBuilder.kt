package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.special.CrossbowChargingSound
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The sounds of the `crossbow_charging_sounds` component, one entry per enchantment level.
 *
 * Serialized as the bare list, the last entry being reused for levels beyond it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#crossbow_charging_sounds
 */
@Serializable(with = CrossbowChargingSoundBuilder.Companion.CrossbowChargingSoundBuilderSerializer::class)
data class CrossbowChargingSoundBuilder(var effects: List<CrossbowChargingSound> = emptyList()) : EffectBuilder() {
	companion object {
		data object CrossbowChargingSoundBuilderSerializer :
			InlineAutoSerializer<CrossbowChargingSoundBuilder, List<CrossbowChargingSound>>(
				serializer<List<CrossbowChargingSound>>(),
				CrossbowChargingSoundBuilder::effects,
				::CrossbowChargingSoundBuilder,
				serialName = "CrossbowChargingSoundBuilder",
			)
	}
}

/**
 * Appends the sounds used at the next enchantment level, each stage being optional.
 *
 * ```kotlin
 * crossbowChargingSounds {
 *     level { start(SoundEvents.Item.Crossbow.QUICK_CHARGE_1) }
 *     level { start(SoundEvents.Item.Crossbow.QUICK_CHARGE_2) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#crossbow_charging_sounds
 */
fun CrossbowChargingSoundBuilder.level(block: CrossbowChargingSound.() -> Unit = {}) =
	apply { effects += CrossbowChargingSound().apply(block) }
