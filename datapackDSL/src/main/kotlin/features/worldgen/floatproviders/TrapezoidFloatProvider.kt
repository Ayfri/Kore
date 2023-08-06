package features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:trapezoid")
data class TrapezoidFloatProvider(
	var min: Float,
	var max: Float,
	var plateau: Float,
) : FloatProvider

fun trapezoid(min: Float, max: Float, plateau: Float) = TrapezoidFloatProvider(min, max, plateau)
fun trapezoidFloatProvider(min: Float, max: Float, plateau: Float) = TrapezoidFloatProvider(min, max, plateau)
