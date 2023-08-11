package features.worldgen.configuredfeature.configurations

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FeatureConfig.Companion.FeatureSerializer::class)
sealed class FeatureConfig {
	companion object {
		data object FeatureSerializer : NamespacedPolymorphicSerializer<FeatureConfig>(FeatureConfig::class, moveIntoProperty = "config")
	}
}
