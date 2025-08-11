package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.FeatureConfig
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven configured feature.
 *
 * A configured feature is a feature type (e.g. tree, ore, spring) paired with its configuration
 * (sizes, targets, states, etc.). It describes WHAT to place, independent from WHERE. Combine
 * it with a placed feature to control placement rules.
 *
 * JSON format reference: https://minecraft.wiki/w/Configured_feature
 */
@Serializable
data class ConfiguredFeature(
	@Transient
	override var fileName: String = "configured_feature",
	val featureConfig: FeatureConfig,
) : Generator("worldgen/configured_feature") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(featureConfig)
}

// TODO : #32
/**
 * Creates a configured feature using a builder block.
 *
 * Pass a [FeatureConfig] (e.g. ores, trees) and optionally adjust fields via [init].
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Configured_feature
 * Docs: https://kore.ayfri.com/docs/worldgen
 */
fun DataPack.configuredFeature(
	fileName: String = "configured_feature",
	featureConfig: FeatureConfig,
	init: ConfiguredFeature.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, featureConfig).apply(init)
	configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: name)
}
