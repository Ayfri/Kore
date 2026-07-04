package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = FeatureConfig.Companion.FeatureSerializer::class)
sealed class FeatureConfig {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object FeatureSerializer : NamespacedPolymorphicSerializer<FeatureConfig>(
			featureConfigSealedSerializer(),
			moveIntoProperty = "config",
			skipEmptyOutput = false,
		)
	}
}
