package arguments.types.literals

import arguments.Argument
import arguments.numbers.RotNumber
import arguments.numbers.rot
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class RotationArgument(val yaw: RotNumber, val pitch: RotNumber) : Argument {
	override fun asString() = "$yaw $pitch"
}

fun rotation(yaw: Number = 0, pitch: Number = 0) = RotationArgument(yaw.rot, pitch.rot)
fun rotation(hor: RotNumber, ver: RotNumber) = RotationArgument(hor, ver)
