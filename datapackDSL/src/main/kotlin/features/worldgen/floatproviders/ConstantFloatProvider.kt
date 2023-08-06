package features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:constant")
data class ConstantFloatProvider(val value: Float) : FloatProvider

fun constant(value: Float) = ConstantFloatProvider(value)
fun constantFloatProvider(value: Float) = ConstantFloatProvider(value)
