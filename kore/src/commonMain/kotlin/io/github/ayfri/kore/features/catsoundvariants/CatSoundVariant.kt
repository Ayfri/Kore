package io.github.ayfri.kore.features.catsoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.CatSoundVariantArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sound set used by one age group within a [CatSoundVariant].
 *
 * JSON format reference: https://minecraft.wiki/w/Cat#Sound_variants
 */
@Serializable
data class CatSoundSet(
	/** The ambient sound. */
	var ambientSound: SoundEventArgument = SoundEvents.Entity.Cat.AMBIENT,
	/** The beg-for-food sound. */
	var begForFoodSound: SoundEventArgument = SoundEvents.Entity.Cat.BEG_FOR_FOOD,
	/** The death sound. */
	var deathSound: SoundEventArgument = SoundEvents.Entity.Cat.DEATH,
	/** The eat sound. */
	var eatSound: SoundEventArgument = SoundEvents.Entity.Cat.EAT,
	/** The hiss sound. */
	var hissSound: SoundEventArgument = SoundEvents.Entity.Cat.HISS,
	/** The hurt sound. */
	var hurtSound: SoundEventArgument = SoundEvents.Entity.Cat.HURT,
	/** The purreow sound. */
	var purreowSound: SoundEventArgument = SoundEvents.Entity.Cat.PURREOW,
	/** The purr sound. */
	var purrSound: SoundEventArgument = SoundEvents.Entity.Cat.PURR,
	/** The stray ambient sound. */
	var strayAmbientSound: SoundEventArgument = SoundEvents.Entity.Cat.STRAY_AMBIENT,
)

/**
 * Data-driven cat sound variant definition.
 *
 * Cat sound variants define custom sounds for cats split by age group.
 * Sounds are split into [adultSounds] (required) and [babySounds] (optional - falls back to [adultSounds] when absent).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Cat#Sound_variants
 */
@Serializable
data class CatSoundVariant(
	@Transient
	override var fileName: String = "cat_sound_variant",
	/** The sounds used by adult cats. */
	var adultSounds: CatSoundSet,
	/** The sounds used by baby cats. Defaults to [adultSounds] when null. */
	var babySounds: CatSoundSet? = null,
) : Generator("cat_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the adult sounds for this [CatSoundVariant] using a builder block. */
fun CatSoundVariant.adultSounds(block: CatSoundSet.() -> Unit) = apply { adultSounds = CatSoundSet().apply(block) }

/** Sets the baby sounds for this [CatSoundVariant] using a builder block. */
fun CatSoundVariant.babySounds(block: CatSoundSet.() -> Unit) = apply { babySounds = CatSoundSet().apply(block) }

/**
 * Create and register a [CatSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/cat_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 */
fun DataPack.catSoundVariant(
	fileName: String = "cat_sound_variant",
	adultSounds: CatSoundSet = CatSoundSet(),
	babySounds: CatSoundSet? = null,
	block: CatSoundVariant.() -> Unit = {},
): CatSoundVariantArgument {
	val catSoundVariant = CatSoundVariant(fileName, adultSounds, babySounds).apply(block)
	catSoundVariants += catSoundVariant
	return CatSoundVariantArgument(fileName, catSoundVariant.namespace ?: name)
}
