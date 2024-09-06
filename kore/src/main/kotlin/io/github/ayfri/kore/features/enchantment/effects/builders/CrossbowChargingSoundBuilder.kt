package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.data.sound.SoundEvent
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
	start: SoundEvent = SoundEvent(),
	mid: SoundEvent = SoundEvent(),
	end: SoundEvent = SoundEvent(),
	block: CrossbowChargingSound.() -> Unit = {},
) = apply { effects += CrossbowChargingSound(start, mid, end).apply(block) }

fun CrossbowChargingSoundBuilder.crossbowChargingSound(
	start: SoundArgument,
	mid: SoundArgument,
	end: SoundArgument,
	block: CrossbowChargingSound.() -> Unit = {},
) = apply { effects += CrossbowChargingSound(SoundEvent(start), SoundEvent(mid), SoundEvent(end)).apply(block) }
