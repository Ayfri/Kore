package io.github.ayfri.kore.arguments.colors

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Named bossbar colors. Serialized as lowercase strings (e.g. "yellow").
 *
 * See documentation: https://kore.ayfri.com/docs/colors
 */
@Serializable(with = BossBarColor.Companion.BossBarColorSerializer::class)
enum class BossBarColor : NamedColor {
	BLUE,
	GREEN,
	PINK,
	PURPLE,
	RED,
	WHITE,
	YELLOW;

	override fun asString() = name.lowercase()

	companion object {
		data object BossBarColorSerializer : LowercaseSerializer<BossBarColor>(entries)
	}
}
