package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.NoiseArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class ShiftedNoise(
	var noise: NoiseArgument,
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
	var shiftX: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var shiftY: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var shiftZ: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun shiftedNoise(noise: NoiseArgument, block: ShiftedNoise.() -> Unit = {}) = ShiftedNoise(noise).apply(block)

fun ShiftedNoise.shiftX(constant: Double) = run { shiftX = densityFunctionOrDouble(constant) }
fun ShiftedNoise.shiftX(reference: DensityFunctionArgument) = run { shiftX = densityFunctionOrDouble(reference) }

fun ShiftedNoise.shiftY(constant: Double) = run { shiftY = densityFunctionOrDouble(constant) }
fun ShiftedNoise.shiftY(reference: DensityFunctionArgument) = run { shiftY = densityFunctionOrDouble(reference) }

fun ShiftedNoise.shiftZ(constant: Double) = run { shiftZ = densityFunctionOrDouble(constant) }
fun ShiftedNoise.shiftZ(reference: DensityFunctionArgument) = run { shiftZ = densityFunctionOrDouble(reference) }
