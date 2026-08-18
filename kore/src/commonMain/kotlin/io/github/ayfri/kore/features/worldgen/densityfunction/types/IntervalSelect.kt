package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Selects one of [functions] by comparing [input] against the sorted [thresholds].
 *
 * [functions] must hold exactly one more entry than [thresholds]: the first function is used when [input]
 * is below the first threshold, the last one when [input] is at or above the last threshold.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class IntervalSelect(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var thresholds: List<Float> = emptyList(),
	var functions: List<DensityFunctionOrDouble> = emptyList(),
) : DensityFunctionType()

/**
 * Adds an `interval_select` density function to the data pack, selecting one of [IntervalSelect.functions] by
 * comparing [IntervalSelect.input] against [IntervalSelect.thresholds]. Configure it in [block] using
 * [IntervalSelect.input], [IntervalSelect.thresholds] and [IntervalSelect.functions].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.intervalSelect(fileName: String, block: IntervalSelect.() -> Unit = {}): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, IntervalSelect().also(block))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/** Sets [IntervalSelect.input] to [constant]. */
fun IntervalSelect.input(constant: Double) = run { input = densityFunctionOrDouble(constant) }

/** Sets [IntervalSelect.input] to [reference]. */
fun IntervalSelect.input(reference: DensityFunctionArgument) = run { input = densityFunctionOrDouble(reference) }

/** Sets [IntervalSelect.thresholds] to [thresholds]. */
fun IntervalSelect.thresholds(vararg thresholds: Float) = run { this.thresholds = thresholds.toList() }

/** Sets [IntervalSelect.functions] to [functions]. */
fun IntervalSelect.functions(vararg functions: DensityFunctionArgument) = run {
	this.functions = functions.map(::densityFunctionOrDouble)
}

/** Sets [IntervalSelect.functions] to [functions]. */
fun IntervalSelect.functions(vararg functions: Double) = run {
	this.functions = functions.map(::densityFunctionOrDouble)
}

/** Appends [reference] to [IntervalSelect.functions]. */
fun IntervalSelect.function(reference: DensityFunctionArgument) = run { functions += densityFunctionOrDouble(reference) }

/** Appends [constant] to [IntervalSelect.functions]. */
fun IntervalSelect.function(constant: Double) = run { functions += densityFunctionOrDouble(constant) }
