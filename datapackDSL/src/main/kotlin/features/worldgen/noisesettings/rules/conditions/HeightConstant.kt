package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
sealed interface HeightConstant

@Serializable
data class Absolute(var absolute: Int) : HeightConstant

@Serializable
data class AboveBottom(var aboveBottom: Int) : HeightConstant

@Serializable
data class BelowTop(var belowTop: Int) : HeightConstant

fun absolute(absolute: Int) = Absolute(absolute)
fun aboveBottom(aboveBottom: Int) = AboveBottom(aboveBottom)
fun belowTop(belowTop: Int) = BelowTop(belowTop)
