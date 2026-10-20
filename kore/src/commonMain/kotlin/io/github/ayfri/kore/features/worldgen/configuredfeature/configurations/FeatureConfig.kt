package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * The configuration of a configured feature, one subtype per feature type, written with its `type` and its fields.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature
 */
@GeneratedSealedSerializer
@Serializable(with = FeatureConfig.Companion.FeatureSerializer::class)
sealed class FeatureConfig {
	companion object {
		data object FeatureSerializer : NamespacedPolymorphicSerializer<FeatureConfig>(featureConfigSealedSerializer())
	}
}
