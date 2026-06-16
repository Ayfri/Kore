package io.github.ayfri.kore.features.wolfsoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.generated.arguments.types.WolfSoundVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Sound set used by one age group within a [WolfSoundVariant].
 *
 * JSON format reference: https://minecraft.wiki/w/Wolf#Sound_variants
 */
@Serializable
data class WolfSoundVariantSounds(
	/** The ambient sound. */
	var ambientSound: SoundEventArgument,
	/** The death sound. */
	var deathSound: SoundEventArgument,
	/** The growl sound. */
	var growlSound: SoundEventArgument,
	/** The hurt sound. */
	var hurtSound: SoundEventArgument,
	/** The pant sound. */
	var pantSound: SoundEventArgument,
	/** The whine sound. */
	var whineSound: SoundEventArgument,
)

/**
 * Data-driven wolf sound variant definition.
 *
 * Wolf can have sound variants, these variants are meant to reflect a wolf's personality but have no effect on the wolf's behavior.
 * Wolf sound variants are assigned independently of a wolf's spawning biome.
 * Wolves will make the sounds associated with their variant when they make bark, pant, whine, growl, death or hurt sounds.
 * Sound variants are unrelated to color variants. Similarly, the angry sound variant is unrelated to the angry wolf state.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Wolf#Sound_variants
 */
@Serializable
data class WolfSoundVariant(
	@Transient
	override var fileName: String = "wolf_sound_variant",
	/** The sounds used by adult wolves. */
	var adultSounds: WolfSoundVariantSounds,
	/** The sounds used by baby wolves. Defaults to [adultSounds] when null. */
	var babySounds: WolfSoundVariantSounds? = null,
) : Generator("wolf_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a [WolfSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/wolf_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/wolf-sound-variants
 */
fun DataPack.wolfSoundVariant(
	fileName: String = "wolf_sound_variant",
	adultSounds: WolfSoundVariantSounds = WolfSoundVariantSounds(
		ambientSound = SoundEvents.Entity.Wolf.AMBIENT,
		deathSound = SoundEvents.Entity.Wolf.DEATH,
		growlSound = SoundEvents.Entity.Wolf.GROWL,
		hurtSound = SoundEvents.Entity.Wolf.HURT,
		pantSound = SoundEvents.Entity.Wolf.PANT,
		whineSound = SoundEvents.Entity.Wolf.WHINE,
	),
	babySounds: WolfSoundVariantSounds? = null,
	block: WolfSoundVariant.() -> Unit = {}
): WolfSoundVariantArgument {
	val wolfSoundVariant = WolfSoundVariant(fileName, adultSounds, babySounds).apply(block)
	wolfSoundVariants += wolfSoundVariant
	return WolfSoundVariantArgument(fileName, wolfSoundVariant.namespace ?: name)
}
