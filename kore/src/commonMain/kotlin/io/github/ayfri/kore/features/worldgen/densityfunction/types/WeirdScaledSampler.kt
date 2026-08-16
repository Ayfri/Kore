package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Samples [noise] at [input] and maps the result through [rarityValueMapper] to bias the rarity of caves
 * and ravines.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class WeirdScaledSampler(
	var rarityValueMapper: RarityValueMapper,
	var noise: NoiseArgument,
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/** Selects the lookup table [WeirdScaledSampler] uses to remap the sampled noise value into a rarity bias. */
@Serializable(with = RarityValueMapper.Companion.RarityValueMapperSerializer::class)
enum class RarityValueMapper {
	TYPE_1,
	TYPE_2;

	companion object {
		data object RarityValueMapperSerializer : LowercaseSerializer<RarityValueMapper>(entries)
	}
}

/**
 * Adds a `weird_scaled_sampler` density function to the data pack, sampling [noise] at [input] and mapping
 * the result through [rarityValueMapper] to bias the rarity of caves and ravines.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.weirdScaledSampler(
	fileName: String,
	rarityValueMapper: RarityValueMapper,
	noise: NoiseArgument,
	input: DensityFunctionArgument,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, WeirdScaledSampler(rarityValueMapper, noise, densityFunctionOrDouble(input)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `weird_scaled_sampler` density function to the data pack, sampling [noise] at [input] and mapping
 * the result through [rarityValueMapper] to bias the rarity of caves and ravines.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.weirdScaledSampler(
	fileName: String,
	rarityValueMapper: RarityValueMapper,
	noise: NoiseArgument,
	input: Double,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, WeirdScaledSampler(rarityValueMapper, noise, densityFunctionOrDouble(input)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
