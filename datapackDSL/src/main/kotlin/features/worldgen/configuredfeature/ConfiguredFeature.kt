package features.worldgen.configuredfeature

import DataPack
import Generator
import arguments.types.resources.worldgen.ConfiguredFeatureArgument
import features.worldgen.configuredfeature.configurations.FeatureConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class ConfiguredFeature(
	@Transient
	override var fileName: String = "configured_feature",
	val featureConfig: FeatureConfig,
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(featureConfig)
}

fun DataPack.configuredFeature(fileName: String = "configured_feature", featureConfig: FeatureConfig): ConfiguredFeatureArgument {
	configuredFeatures += ConfiguredFeature(fileName, featureConfig)
	return ConfiguredFeatureArgument(fileName, name)
}
