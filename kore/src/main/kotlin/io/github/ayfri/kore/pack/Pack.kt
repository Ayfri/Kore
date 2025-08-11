package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a pack that contains game data and resources.
 *
 * @property format The format of the pack, can be a constant or a range.
 * @property description The description of the pack.
 * @property supportedFormats The supported formats of the pack.
 */
@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
	var supportedFormats: SupportedFormats? = null,
)

/** Sets the pack of the datapack. */
fun DataPack.pack(block: Pack.() -> Unit) = pack.run(block)

/** Sets the supported format of the pack. */
fun Pack.supportedFormat(constant: Int) {
	supportedFormats = SupportedFormats(constant)
}

/** Sets the supported format of the pack. */
fun Pack.supportedFormat(values: Collection<Int>) {
	supportedFormats = SupportedFormats(list = values.toList())
}

/** Sets the supported format of the pack. */
fun Pack.supportedFormat(vararg values: Int) {
	supportedFormats = SupportedFormats(list = values.toList())
}

/** Sets the supported format of the pack. */
fun Pack.supportedFormat(range: IntRange) {
	supportedFormats = SupportedFormats(minInclusive = range.first, maxInclusive = range.last)
}

/** Sets the supported format of the pack. */
fun Pack.supportedFormat(minInclusive: Int, maxInclusive: Int = Int.MAX_VALUE) {
	supportedFormats = SupportedFormats(minInclusive = minInclusive, maxInclusive = maxInclusive)
}
