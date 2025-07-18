package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.FeatureConfig
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
	init: ConfiguredFeature.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, featureConfig).apply(init)
	configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: name)
}
