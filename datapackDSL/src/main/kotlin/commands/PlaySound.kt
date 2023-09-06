package commands

import arguments.maths.Vec3
import arguments.types.EntityArgument
import arguments.types.literals.float
import arguments.types.literals.literal
import functions.Function
import generated.Sounds
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

@Serializable(PlaySoundSource.Companion.PlaySoundSourceSerializer::class)
enum class PlaySoundSource {
	MASTER,
	MUSIC,
	RECORD,
	WEATHER,
	BLOCK,
	HOSTILE,
	NEUTRAL,
	PLAYER,
	AMBIENT,
	VOICE;

	companion object {
		data object PlaySoundSourceSerializer : LowercaseSerializer<PlaySoundSource>(entries)
	}
}

fun Function.playSound(
	sound: Sounds,
	source: PlaySoundSource,
	target: EntityArgument,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
) = addLine(command("playsound", sound, literal(source.asArg()), target, pos, float(volume), float(pitch), float(minVolume)))
