package io.github.ayfri.kore.arguments.colors

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Vanilla named chat/formatting colors. Serialized as lowercase strings (e.g. "red").
 * Commonly used in chat components, teams, scoreboard criteria, and item base colors.
 *
 * See documentation: https://kore.ayfri.com/docs/colors
 */
@Serializable(with = FormattingColor.Companion.FormattingColorSerializer::class)
enum class FormattingColor : NamedColor {
	AQUA,
	BLACK,
	BLUE,
	DARK_AQUA,
	DARK_BLUE,
	DARK_GRAY,
	DARK_GREEN,
	DARK_PURPLE,
	DARK_RED,
	GOLD,
	GRAY,
	GREEN,
	LIGHT_PURPLE,
	RED,
	WHITE,
	YELLOW;

	override fun asString() = name.lowercase()

	fun random(random: Random = Random) = entries.random(random)

	companion object {
		data object FormattingColorSerializer : LowercaseSerializer<FormattingColor>(entries)
	}
}
