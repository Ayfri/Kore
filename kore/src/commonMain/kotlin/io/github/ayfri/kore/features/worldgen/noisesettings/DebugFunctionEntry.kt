package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * A density function of [NoiseSettings.debugFunctions], sampled and shown as [label] in the `chunk_generation_stats` debug entry.
 *
 * @property function The density function to sample.
 * @property label The short text naming the function in the debug entry.
 */
@Serializable
data class DebugFunctionEntry(
	var function: DensityFunctionOrDouble,
	var label: String,
)

/**
 * Appends a debug function to the noise settings, sampling [function] and showing it as [label] in the `chunk_generation_stats` debug entry.
 *
 * ```kotlin
 * debugFunction(DensityFunctions.Overworld.CONTINENTS, "C")
 * ```
 */
fun NoiseSettings.debugFunction(function: DensityFunctionArgument, label: String) =
	DebugFunctionEntry(densityFunctionOrDouble(function), label).also { debugFunctions = (debugFunctions ?: emptyList()) + it }

/** Appends a debug function to the noise settings, showing the constant [function] as [label] in the `chunk_generation_stats` debug entry. */
fun NoiseSettings.debugFunction(function: Double, label: String) =
	DebugFunctionEntry(densityFunctionOrDouble(function), label).also { debugFunctions = (debugFunctions ?: emptyList()) + it }
