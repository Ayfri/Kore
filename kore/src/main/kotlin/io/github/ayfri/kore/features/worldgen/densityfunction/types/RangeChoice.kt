package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class RangeChoice(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var minInclusive: Double = 0.0,
	var maxExclusive: Double = 0.0,
	var whenInRange: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var whenOutOfRange: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun rangeChoice(block: RangeChoice.() -> Unit = {}) = RangeChoice().also(block)

fun RangeChoice.input(constant: Double) = run { input = densityFunctionOrDouble(constant) }
fun RangeChoice.input(reference: DensityFunctionArgument) = run { input = densityFunctionOrDouble(reference) }

fun RangeChoice.whenInRange(constant: Double) = run { whenInRange = densityFunctionOrDouble(constant) }
fun RangeChoice.whenInRange(reference: DensityFunctionArgument) = run { whenInRange = densityFunctionOrDouble(reference) }

fun RangeChoice.whenOutOfRange(constant: Double) = run { whenOutOfRange = densityFunctionOrDouble(constant) }
fun RangeChoice.whenOutOfRange(reference: DensityFunctionArgument) = run { whenOutOfRange = densityFunctionOrDouble(reference) }
