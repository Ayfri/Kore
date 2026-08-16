package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Returns [whenInRange] when [input] falls within [minInclusive] (inclusive) and [maxExclusive]
 * (exclusive), otherwise [whenOutOfRange].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class RangeChoice(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var minInclusive: Double = 0.0,
	var maxExclusive: Double = 0.0,
	var whenInRange: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var whenOutOfRange: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `range_choice` density function to the data pack, returning [RangeChoice.whenInRange] when
 * [RangeChoice.input] falls within [RangeChoice.minInclusive] (inclusive) and [RangeChoice.maxExclusive]
 * (exclusive), otherwise [RangeChoice.whenOutOfRange]. Configure it in [block] using [RangeChoice.input],
 * [RangeChoice.whenInRange] and [RangeChoice.whenOutOfRange].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.rangeChoice(fileName: String, block: RangeChoice.() -> Unit): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, RangeChoice().also(block))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/** Sets [RangeChoice.input] to [constant]. */
fun RangeChoice.input(constant: Double) = run { input = densityFunctionOrDouble(constant) }

/** Sets [RangeChoice.input] to [reference]. */
fun RangeChoice.input(reference: DensityFunctionArgument) = run { input = densityFunctionOrDouble(reference) }

/** Sets [RangeChoice.whenInRange] to [constant]. */
fun RangeChoice.whenInRange(constant: Double) = run { whenInRange = densityFunctionOrDouble(constant) }

/** Sets [RangeChoice.whenInRange] to [reference]. */
fun RangeChoice.whenInRange(reference: DensityFunctionArgument) = run { whenInRange = densityFunctionOrDouble(reference) }

/** Sets [RangeChoice.whenOutOfRange] to [constant]. */
fun RangeChoice.whenOutOfRange(constant: Double) = run { whenOutOfRange = densityFunctionOrDouble(constant) }

/** Sets [RangeChoice.whenOutOfRange] to [reference]. */
fun RangeChoice.whenOutOfRange(reference: DensityFunctionArgument) = run { whenOutOfRange = densityFunctionOrDouble(reference) }
