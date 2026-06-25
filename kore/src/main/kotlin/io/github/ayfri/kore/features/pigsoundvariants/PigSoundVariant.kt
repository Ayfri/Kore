package io.github.ayfri.kore.features.pigsoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.PigSoundVariantArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sound set used by one age group within a [PigSoundVariant].
 *
 * JSON format reference: https://minecraft.wiki/w/Pig#Sound_variants
 */
@Serializable
data class PigSoundVariantSounds(
	/** The ambient sound. */
	var ambientSound: SoundEventArgument = SoundEvents.Entity.Pig.AMBIENT,
	/** The death sound. */
	var deathSound: SoundEventArgument = SoundEvents.Entity.Pig.DEATH,
	/** The hurt sound. */
	var hurtSound: SoundEventArgument = SoundEvents.Entity.Pig.HURT,
	/** The step sound. */
	var stepSound: SoundEventArgument = SoundEvents.Entity.Pig.STEP,
)

/**
 * Data-driven pig sound variant definition.
 *
 * Pig sound variants define custom sounds for pigs split by age group.
 * Sounds are split into [adultSounds] (required) and [babySounds] (optional - falls back to [adultSounds] when absent).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Pig#Sound_variants
 */
@Serializable
data class PigSoundVariant(
	@Transient
	override var fileName: String = "pig_sound_variant",
	/** The sounds used by adult pigs. */
	var adultSounds: PigSoundVariantSounds,
	/** The sounds used by baby pigs. Defaults to [adultSounds] when null. */
	var babySounds: PigSoundVariantSounds? = null,
) : Generator("pig_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the adult sounds for this [PigSoundVariant] using a builder block. */
fun PigSoundVariant.adultSounds(block: PigSoundVariantSounds.() -> Unit) =
	apply { adultSounds = PigSoundVariantSounds().apply(block) }

/** Sets the baby sounds for this [PigSoundVariant] using a builder block. */
fun PigSoundVariant.babySounds(block: PigSoundVariantSounds.() -> Unit) =
	apply { babySounds = PigSoundVariantSounds().apply(block) }

/**
 * Create and register a [PigSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/pig_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 */
fun DataPack.pigSoundVariant(
	fileName: String = "pig_sound_variant",
	adultSounds: PigSoundVariantSounds = PigSoundVariantSounds(),
	babySounds: PigSoundVariantSounds? = null,
	block: PigSoundVariant.() -> Unit = {},
): PigSoundVariantArgument {
	val pigSoundVariant = PigSoundVariant(fileName, adultSounds, babySounds).apply(block)
	pigSoundVariants += pigSoundVariant
	return PigSoundVariantArgument(fileName, pigSoundVariant.namespace ?: name)
}
