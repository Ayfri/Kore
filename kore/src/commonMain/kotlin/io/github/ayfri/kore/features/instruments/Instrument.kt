package io.github.ayfri.kore.features.instruments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.InstrumentArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for an instrument, as used in Minecraft Java Edition 1.21+.
 *
 * An instrument specifies a sound event, its audible range, use duration, and a description.
 * Instruments are currently used for goat horns, but can be defined in data packs to add custom instruments.
 *
 * JSON format reference: https://minecraft.wiki/w/Instrument_definition
 *
 * @param description - The text component used as a description in tooltips.
 * @param range - The non-negative float for the audible range of the instrument.
 * @param soundEvent - The sound event to play for the instrument (string ID or sound event object).
 * @param useDuration - The non-negative float for how long the use duration is.
 */
@Serializable
data class Instrument(
	@Transient
	override var fileName: String = "instrument",
	var soundEvent: SoundEventArgument,
	var range: Float,
	var useDuration: Float,
	var description: ChatComponents,
) : Generator("instrument") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register an instrument in this [DataPack].
 *
 * Produces `data/<namespace>/instrument/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Instrument_definition
 */
fun DataPack.instrument(
	fileName: String = "instrument",
	soundEvent: SoundEventArgument = SoundEvents.Item.GoatHorn.Sound.`0`,
	range: Float = 256f,
	useDuration: Float = 7f,
	description: ChatComponents = textComponent(),
	init: Instrument.() -> Unit = {},
): InstrumentArgument {
	val instrument = Instrument(fileName, soundEvent, range, useDuration, description).apply(init)
	instruments += instrument
	return InstrumentArgument(fileName, instrument.namespace ?: name)
}
