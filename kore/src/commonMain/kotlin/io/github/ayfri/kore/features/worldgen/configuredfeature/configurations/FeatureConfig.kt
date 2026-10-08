package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = FeatureConfig.Companion.FeatureSerializer::class)
sealed class FeatureConfig {
	companion object {
		data object FeatureSerializer : NamespacedPolymorphicSerializer<FeatureConfig>(featureConfigSealedSerializer())
	}
}
