package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import arguments.types.resources.worldgen.NoiseArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class WeirdScaledSampler(
	var rarityValueMapper: RarityValueMapper,
	var noise: NoiseArgument,
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

@Serializable(with = RarityValueMapper.Companion.RarityValueMapperSerializer::class)
enum class RarityValueMapper {
	TYPE_1,
	TYPE_2;

	companion object {
		data object RarityValueMapperSerializer : LowercaseSerializer<RarityValueMapper>(entries)
	}
}

fun weirdScaledSampler(
	rarityValueMapper: RarityValueMapper,
	noise: NoiseArgument,
	input: DensityFunctionArgument,
) = WeirdScaledSampler(rarityValueMapper, noise, densityFunctionOrDouble(input))

fun weirdScaledSampler(
	rarityValueMapper: RarityValueMapper,
	noise: NoiseArgument,
	input: Double,
) = WeirdScaledSampler(rarityValueMapper, noise, densityFunctionOrDouble(input))
