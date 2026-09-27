package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(PlaySoundMixer.Companion.PlaySoundSourceSerializer::class)
enum class PlaySoundMixer {
	AMBIENT,
	BLOCK,
	HOSTILE,
	MASTER,
	MUSIC,
	NEUTRAL,
	PLAYER,
	RECORD,
	UI,
	VOICE,
	WEATHER;

	companion object {
		data object PlaySoundSourceSerializer : LowercaseSerializer<PlaySoundMixer>(entries)
	}
}

/**
 * Plays a sound event in the world with the given [source], [target], [pos], [volume], [pitch],
 * and [minVolume].
 * The arguments are positional, so an omitted one followed by a given one falls back to the game default:
 * `master` for [source], `@s` for [target] and `~ ~ ~` for [pos].
 * ```
 * playSound(SoundEvents.Entity.Bat.TAKEOFF, volume = 2.0) // playsound minecraft:entity.bat.takeoff master @s ~ ~ ~ 2
 * ```
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/playsound)
 */
fun Function.playSound(
	sound: SoundEventArgument,
	source: PlaySoundMixer? = null,
	target: EntityArgument? = null,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
): Command {
	val hasVolume = volume != null || pitch != null || minVolume != null
	val finalPos = pos ?: vec3().takeIf { hasVolume }
	val finalTarget = target ?: self().takeIf { finalPos != null }
	val finalSource = source ?: PlaySoundMixer.MASTER.takeIf { finalTarget != null }
	return addLine(
		command(
			"playsound",
			sound,
			literal(finalSource?.asArg()),
			finalTarget,
			finalPos,
			float(volume ?: 1.0.takeIf { pitch != null || minVolume != null }),
			float(pitch ?: 1.0.takeIf { minVolume != null }),
			float(minVolume),
		)
	)
}
