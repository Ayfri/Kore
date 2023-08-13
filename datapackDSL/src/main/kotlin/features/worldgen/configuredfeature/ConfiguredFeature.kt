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
) : Generator("worldgen/configured_feature") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(featureConfig)
}

// TODO : #32
fun DataPack.configuredFeature(
	fileName: String = "configured_feature",
	featureConfig: FeatureConfig,
	block: ConfiguredFeature.() -> Unit = {},
): ConfiguredFeatureArgument {
	configuredFeatures += ConfiguredFeature(fileName, featureConfig).apply(block)
	return ConfiguredFeatureArgument(fileName, name)
}
