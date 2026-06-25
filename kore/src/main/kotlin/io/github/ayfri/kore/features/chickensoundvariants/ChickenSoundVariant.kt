package io.github.ayfri.kore.features.chickensoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.ChickenSoundVariantArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sound set used by one age group within a [ChickenSoundVariant].
 *
 * JSON format reference: https://minecraft.wiki/w/Chicken#Sound_variants
 */
@Serializable
data class ChickenSoundVariantSounds(
	/** The ambient sound. */
	var ambientSound: SoundEventArgument = SoundEvents.Entity.Chicken.AMBIENT,
	/** The death sound. */
	var deathSound: SoundEventArgument = SoundEvents.Entity.Chicken.DEATH,
	/** The hurt sound. */
	var hurtSound: SoundEventArgument = SoundEvents.Entity.Chicken.HURT,
	/** The step sound. */
	var stepSound: SoundEventArgument = SoundEvents.Entity.Chicken.STEP,
)

/**
 * Data-driven chicken sound variant definition.
 *
 * Chicken sound variants define custom sounds for chickens split by age group.
 * Sounds are split into [adultSounds] (required) and [babySounds] (optional - falls back to [adultSounds] when absent).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Chicken#Sound_variants
 */
@Serializable
data class ChickenSoundVariant(
	@Transient
	override var fileName: String = "chicken_sound_variant",
	/** The sounds used by adult chickens. */
	var adultSounds: ChickenSoundVariantSounds,
	/** The sounds used by baby chickens. Defaults to [adultSounds] when null. */
	var babySounds: ChickenSoundVariantSounds? = null,
) : Generator("chicken_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the adult sounds for this [ChickenSoundVariant] using a builder block. */
fun ChickenSoundVariant.adultSounds(block: ChickenSoundVariantSounds.() -> Unit) =
	apply { adultSounds = ChickenSoundVariantSounds().apply(block) }

/** Sets the baby sounds for this [ChickenSoundVariant] using a builder block. */
fun ChickenSoundVariant.babySounds(block: ChickenSoundVariantSounds.() -> Unit) =
	apply { babySounds = ChickenSoundVariantSounds().apply(block) }

/**
 * Create and register a [ChickenSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/chicken_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 */
fun DataPack.chickenSoundVariant(
	fileName: String = "chicken_sound_variant",
	adultSounds: ChickenSoundVariantSounds = ChickenSoundVariantSounds(),
	babySounds: ChickenSoundVariantSounds? = null,
	block: ChickenSoundVariant.() -> Unit = {},
): ChickenSoundVariantArgument {
	val chickenSoundVariant = ChickenSoundVariant(fileName, adultSounds, babySounds).apply(block)
	chickenSoundVariants += chickenSoundVariant
	return ChickenSoundVariantArgument(fileName, chickenSoundVariant.namespace ?: name)
}
