package io.github.ayfri.kore.arguments.colors

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Named bossbar colors. Serialized as lowercase strings (e.g. "yellow").
 *
 * See documentation: https://kore.ayfri.com/docs/concepts/colors
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

	fun random(random: Random = Random) = entries.random(random)

	companion object {
		data object BossBarColorSerializer : LowercaseSerializer<BossBarColor>(entries)
	}
}
