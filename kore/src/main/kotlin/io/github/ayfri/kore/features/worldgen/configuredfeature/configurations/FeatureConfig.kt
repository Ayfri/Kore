package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FeatureConfig.Companion.FeatureSerializer::class)
sealed class FeatureConfig {
	companion object {
		data object FeatureSerializer : NamespacedPolymorphicSerializer<FeatureConfig>(FeatureConfig::class, moveIntoProperty = "config")
	}
}
