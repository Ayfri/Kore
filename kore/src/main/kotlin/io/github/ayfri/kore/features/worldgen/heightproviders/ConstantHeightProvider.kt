package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.AboveBottom
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Absolute
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.BelowTop
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.HeightConstant
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
