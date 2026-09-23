package io.github.ayfri.kore.features.worldgen.multinoisebiomesourceparameterlist

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.BiomePresets
import io.github.ayfri.kore.generated.arguments.worldgen.types.MultiNoiseBiomeSourceParameterListArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven multi-noise biome source parameter list, a named set of biome placements built from a hardcoded preset.
 *
 * Multi-noise biome sources of dimensions reference it through their `preset` field.
 *
 * Produces `data/<namespace>/worldgen/multi_noise_biome_source_parameter_list/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions#multi-noise-parameter-lists
 * Minecraft Wiki: https://minecraft.wiki/w/Multi-noise_biome_source_parameter_list
 */
@Serializable
data class MultiNoiseBiomeSourceParameterList(
	@Transient
	override var fileName: String = "multi_noise_biome_source_parameter_list",
	var preset: BiomePresets,
) : Generator("worldgen/multi_noise_biome_source_parameter_list") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a multi-noise biome source parameter list built from [preset].
 *
 * Produces `data/<namespace>/worldgen/multi_noise_biome_source_parameter_list/<fileName>.json`.
 *
 * ```kotlin
 * val parameters = multiNoiseBiomeSourceParameterList("my_overworld", BiomePresets.OVERWORLD)
 * dimension("my_dimension", type = DimensionTypes.OVERWORLD) {
 *     noiseGenerator(settings = NoiseSettings.OVERWORLD, biomeSource = multiNoise(parameters))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/dimensions#multi-noise-parameter-lists
 * Minecraft Wiki: https://minecraft.wiki/w/Multi-noise_biome_source_parameter_list
 */
fun DataPack.multiNoiseBiomeSourceParameterList(
	fileName: String = "multi_noise_biome_source_parameter_list",
	preset: BiomePresets,
	init: MultiNoiseBiomeSourceParameterList.() -> Unit = {},
): MultiNoiseBiomeSourceParameterListArgument {
	val parameterList = MultiNoiseBiomeSourceParameterList(fileName = fileName, preset = preset).apply(init)
	multiNoiseBiomeSourceParameterLists += parameterList
	return MultiNoiseBiomeSourceParameterListArgument(fileName, parameterList.namespace ?: name)
}
