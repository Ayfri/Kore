package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.enchantments.effects.special.CrossbowChargingSound
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CrossbowChargingSoundBuilder.Companion.CrossbowChargingSoundBuilderSerializer::class)
data class CrossbowChargingSoundBuilder(var effects: List<CrossbowChargingSound> = emptyList()) : EffectBuilder() {
	companion object {
		data object CrossbowChargingSoundBuilderSerializer : InlineAutoSerializer<CrossbowChargingSoundBuilder>(
			CrossbowChargingSoundBuilder::class
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
	start: SoundEventArgument,
	mid: SoundEventArgument,
	end: SoundEventArgument,
	block: CrossbowChargingSound.() -> Unit = {},
) = apply { effects += CrossbowChargingSound(SoundEvent(start), SoundEvent(mid), SoundEvent(end)).apply(block) }
