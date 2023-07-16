package arguments.types.literals

import arguments.Argument
import arguments.types.EntityArgument
import arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtIntArray
import java.util.*

@Serializable(with = Argument.ArgumentSerializer::class)
data class UUIDArgument(val uuid: UUID) : EntityArgument, ScoreHolderArgument {
	override fun asString() = uuid.toString()

	fun toIntArray() = intArrayOf(
		(uuid.mostSignificantBits ushr 32).toInt(),
		(uuid.mostSignificantBits and 0xFFFFFFFF).toInt(),
		(uuid.leastSignificantBits ushr 32).toInt(),
		(uuid.leastSignificantBits and 0xFFFFFFFF).toInt()
	)

	fun toNBTIntArray() = NbtIntArray(toIntArray())

	companion object {
		fun random() = UUIDArgument(UUID.randomUUID())
	}
}

fun randomUUID() = UUIDArgument.random()
fun uuid(uuid: UUID) = UUIDArgument(uuid)
