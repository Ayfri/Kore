package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LayersFeatureSize.Companion.LayersFeatureSizeSerializer::class)
sealed class LayersFeatureSize {
	abstract var minClippedHeight: Int?

	companion object {
		data object LayersFeatureSizeSerializer : NamespacedPolymorphicSerializer<LayersFeatureSize>(LayersFeatureSize::class)
	}
}
