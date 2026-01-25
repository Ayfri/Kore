package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.RotNumber
import io.github.ayfri.kore.arguments.numbers.relativeRot
import io.github.ayfri.kore.arguments.numbers.rot
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class RotationArgument(val yaw: RotNumber, val pitch: RotNumber) : Argument {
	override fun asString() = "$yaw $pitch"
}

fun rotation(yaw: Number, pitch: Number = 0) = RotationArgument(yaw.rot, pitch.rot)
fun rotation(hor: RotNumber = 0.relativeRot, ver: RotNumber = 0.relativeRot) = RotationArgument(hor, ver)
