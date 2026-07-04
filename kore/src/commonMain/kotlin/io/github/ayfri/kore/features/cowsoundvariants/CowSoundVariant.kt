package io.github.ayfri.kore.features.cowsoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.CowSoundVariantArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven cow sound variant definition.
 *
 * Cow sound variants define custom sounds for cows.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Cow#Sound_variants
 */
@Serializable
data class CowSoundVariant(
	@Transient
	override var fileName: String = "cow_sound_variant",
	/** The ambient sound. */
	var ambientSound: SoundEventArgument,
	/** The death sound. */
	var deathSound: SoundEventArgument,
	/** The hurt sound. */
	var hurtSound: SoundEventArgument,
	/** The step sound. */
	var stepSound: SoundEventArgument,
) : Generator("cow_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a [CowSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/cow_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 */
fun DataPack.cowSoundVariant(
	fileName: String = "cow_sound_variant",
	ambientSound: SoundEventArgument = SoundEvents.Entity.Cow.AMBIENT,
	deathSound: SoundEventArgument = SoundEvents.Entity.Cow.DEATH,
	hurtSound: SoundEventArgument = SoundEvents.Entity.Cow.HURT,
	stepSound: SoundEventArgument = SoundEvents.Entity.Cow.STEP,
	block: CowSoundVariant.() -> Unit = {},
): CowSoundVariantArgument {
	val cowSoundVariant = CowSoundVariant(fileName, ambientSound, deathSound, hurtSound, stepSound).apply(block)
	cowSoundVariants += cowSoundVariant
	return CowSoundVariantArgument(fileName, cowSoundVariant.namespace ?: name)
}
