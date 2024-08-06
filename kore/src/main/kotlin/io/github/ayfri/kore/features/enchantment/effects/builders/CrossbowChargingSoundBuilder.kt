package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.enchantment.effects.SoundRangeable
import io.github.ayfri.kore.features.enchantment.effects.special.CrossbowChargingSound
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = CrossbowChargingSoundBuilder.Companion.CrossbowChargingSoundBuilderSerializer::class)
data class CrossbowChargingSoundBuilder(
	var effects: List<CrossbowChargingSound> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object CrossbowChargingSoundBuilderSerializer : InlineSerializer<CrossbowChargingSoundBuilder, List<CrossbowChargingSound>>(
			kSerializer = ListSerializer(CrossbowChargingSound.serializer()),
			property = CrossbowChargingSoundBuilder::effects
		)
	}
}

fun CrossbowChargingSoundBuilder.crossbowChargingSound(
	start: SoundRangeable = SoundRangeable(),
	mid: SoundRangeable = SoundRangeable(),
	end: SoundRangeable = SoundRangeable(),
	block: CrossbowChargingSound.() -> Unit = {},
) = apply { effects += CrossbowChargingSound(start, mid, end).apply(block) }

fun CrossbowChargingSoundBuilder.crossbowChargingSound(
	start: SoundArgument,
	mid: SoundArgument,
	end: SoundArgument,
	block: CrossbowChargingSound.() -> Unit = {},
) = apply { effects += CrossbowChargingSound(SoundRangeable(start), SoundRangeable(mid), SoundRangeable(end)).apply(block) }
