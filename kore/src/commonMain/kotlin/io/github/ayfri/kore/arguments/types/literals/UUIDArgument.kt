package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtIntArray
import kotlin.uuid.Uuid

@Serializable(with = Argument.ArgumentSerializer::class)
data class UUIDArgument(val uuid: Uuid) : EntityArgument, ScoreHolderArgument {
	override fun asString() = uuid.toString()

	fun toIntArray() = uuid.toLongs { mostSignificantBits, leastSignificantBits ->
		intArrayOf(
			(mostSignificantBits ushr 32).toInt(),
			mostSignificantBits.toInt(),
			(leastSignificantBits ushr 32).toInt(),
			leastSignificantBits.toInt(),
		)
	}

	fun toNBTIntArray() = NbtIntArray(toIntArray())

	companion object {
		fun random() = UUIDArgument(Uuid.random())
	}
}

fun randomUUID() = UUIDArgument.random()
fun uuid(uuid: Uuid) = UUIDArgument(uuid)
fun uuid(uuid: String) = UUIDArgument(Uuid.parse(uuid))
