package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(PlaySoundMixer.Companion.PlaySoundSourceSerializer::class)
enum class PlaySoundMixer {
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
		data object PlaySoundSourceSerializer : LowercaseSerializer<PlaySoundMixer>(entries)
	}
}

fun Function.playSound(
	sound: SoundArgument,
	source: PlaySoundMixer? = null,
	target: EntityArgument? = null,
	pos: Vec3? = null,
	volume: Double? = null,
	pitch: Double? = null,
	minVolume: Double? = null,
) = addLine(command("playsound", sound, literal(source?.asArg()), target, pos, float(volume), float(pitch), float(minVolume)))
