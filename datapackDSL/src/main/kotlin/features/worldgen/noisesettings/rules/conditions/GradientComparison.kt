package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable

@Serializable
sealed interface GradientComparison

@Serializable
data class Absolute(var absolute: Int) : GradientComparison

@Serializable
data class AboveBottom(var aboveBottom: Int) : GradientComparison

@Serializable
data class BelowTop(var belowTop: Int) : GradientComparison

fun absolute(absolute: Int) = Absolute(absolute)
fun aboveBottom(aboveBottom: Int) = AboveBottom(aboveBottom)
fun belowTop(belowTop: Int) = BelowTop(belowTop)
