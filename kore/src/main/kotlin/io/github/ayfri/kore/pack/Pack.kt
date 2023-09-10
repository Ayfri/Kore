package io.github.ayfri.kore.pack

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a pack that contains game data and resources.
 *
 * @property format The format of the pack.
 * @property description The description of the pack.
 */
@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
	var supportedFormats: SupportedFormats? = null,
)

fun DataPack.pack(block: Pack.() -> Unit) = pack.run(block)

fun Pack.supportedFormat(constant: Int) {
	supportedFormats = SupportedFormats(constant)
}

fun Pack.supportedFormat(values: Collection<Int>) {
	supportedFormats = SupportedFormats(list = values.toList())
}

fun Pack.supportedFormat(vararg values: Int) {
	supportedFormats = SupportedFormats(list = values.toList())
}

fun Pack.supportedFormat(range: IntRange) {
	supportedFormats = SupportedFormats(minInclusive = range.first, maxInclusive = range.last)
}

fun Pack.supportedFormat(minInclusive: Int, maxInclusive: Int = Int.MAX_VALUE) {
	supportedFormats = SupportedFormats(minInclusive = minInclusive, maxInclusive = maxInclusive)
}
