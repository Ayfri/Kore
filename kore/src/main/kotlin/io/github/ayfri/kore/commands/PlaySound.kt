package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
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

/** Plays a sound event in the world with the given [source], [target], [pos], [volume], [pitch], and [minVolume]. */
fun Function.playSound(
	sound: SoundEventArgument,
	source: PlaySoundMixer? = null,
	target: EntityArgument? = null,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
) = addLine(command("playsound", sound, literal(source?.asArg()), target, pos, float(volume), float(pitch), float(minVolume)))
