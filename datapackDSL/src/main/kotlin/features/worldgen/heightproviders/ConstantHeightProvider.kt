package features.worldgen.heightproviders

import features.worldgen.noisesettings.rules.conditions.AboveBottom
import features.worldgen.noisesettings.rules.conditions.Absolute
import features.worldgen.noisesettings.rules.conditions.BelowTop
import features.worldgen.noisesettings.rules.conditions.HeightConstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minecraft:constant")
data class ConstantHeightProvider(
	val value: HeightConstant,
) : HeightProvider

fun constant(value: HeightConstant) = ConstantHeightProvider(value)
fun constantAbsolute(absolute: Int) = ConstantHeightProvider(Absolute(absolute))
fun constantAboveBottom(aboveBottom: Int) = ConstantHeightProvider(AboveBottom(aboveBottom))
fun constantBelowTop(belowTop: Int) = ConstantHeightProvider(BelowTop(belowTop))
