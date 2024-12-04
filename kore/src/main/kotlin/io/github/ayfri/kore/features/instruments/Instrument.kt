package io.github.ayfri.kore.features.instruments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.InstrumentArgument
import io.github.ayfri.kore.arguments.types.resources.SoundEventArgument
import io.github.ayfri.kore.generated.SoundEvents
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

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
