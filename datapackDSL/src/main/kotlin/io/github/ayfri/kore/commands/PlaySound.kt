package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Sounds
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
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
