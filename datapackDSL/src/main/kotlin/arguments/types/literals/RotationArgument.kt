package arguments.types.literals

import arguments.Argument
import arguments.numbers.RotNumber
import arguments.numbers.relativeRot
import arguments.numbers.rot
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class RotationArgument(val yaw: RotNumber, val pitch: RotNumber) : Argument {
	override fun asString() = "$yaw $pitch"
}

fun rotation(yaw: Number, pitch: Number) = RotationArgument(yaw.rot, pitch.rot)
fun rotation(hor: RotNumber = 0.relativeRot, ver: RotNumber = 0.relativeRot) = RotationArgument(hor, ver)
