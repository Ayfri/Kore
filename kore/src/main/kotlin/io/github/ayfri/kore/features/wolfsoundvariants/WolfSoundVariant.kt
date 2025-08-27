package io.github.ayfri.kore.features.wolfsoundvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.generated.arguments.types.WolfSoundVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven wolf sound variant definition.
 *
 * Wolf can have sound variants, these variants are meant to reflect a wolf's personality but have no effect on the wolf's behavior.
 * Wolf sound variants are assigned independently of a wolf's spawning biome.
 * Wolves will make the sounds associated with their variant when they make bark, pant, whine, growl, death or hurt sounds.
 * Sound variants are unrelated to color variants. Similarly, the angry sound variant is unrelated to the angry wolf state.
 *
 * Documentation: https://minecraft.wiki/w/Wolf#Sound_variants, https://minecraft.wiki/w/Wolf#Sound_variants_2
 */
@Serializable
data class WolfSoundVariant(
	@Transient
	override var fileName: String = "wolf_sound_variant",
	/** The ambient sound for this wolf sound variant. */
	var ambientSound: SoundEventArgument,
	/** The death sound for this wolf sound variant. */
	var deathSound: SoundEventArgument,
	/** The growl sound for this wolf sound variant. */
	var growlSound: SoundEventArgument,
	/** The hurt sound for this wolf sound variant. */
	var hurtSound: SoundEventArgument,
	/** The pant sound for this wolf sound variant. */
	var pantSound: SoundEventArgument,
	/** The whine sound for this wolf sound variant. */
	var whineSound: SoundEventArgument,
) : Generator("wolf_sound_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a [WolfSoundVariant] in this [DataPack].
 *
 * Produces `data/<namespace>/wolf_sound_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/wolf_sound_variants
 */
fun DataPack.wolfSoundVariant(
	fileName: String = "wolf_sound_variant",
	ambientSound: SoundEventArgument = SoundEvents.Entity.Wolf.AMBIENT,
	deathSound: SoundEventArgument = SoundEvents.Entity.Wolf.DEATH,
	growlSound: SoundEventArgument = SoundEvents.Entity.Wolf.GROWL,
	hurtSound: SoundEventArgument = SoundEvents.Entity.Wolf.HURT,
	pantSound: SoundEventArgument = SoundEvents.Entity.Wolf.PANT,
	whineSound: SoundEventArgument = SoundEvents.Entity.Wolf.WHINE,
	block: WolfSoundVariant.() -> Unit = {}
): WolfSoundVariantArgument {
	val wolfSoundVariant = WolfSoundVariant(
		fileName,
		ambientSound,
		deathSound,
		growlSound,
		hurtSound,
		pantSound,
		whineSound
	).apply(block)
	wolfSoundVariants += wolfSoundVariant
	return WolfSoundVariantArgument(fileName, wolfSoundVariant.namespace ?: name)
}
