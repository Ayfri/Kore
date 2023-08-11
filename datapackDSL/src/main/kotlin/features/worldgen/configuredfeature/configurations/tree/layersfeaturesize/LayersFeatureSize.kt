package features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LayersFeatureSize.Companion.LayersFeatureSizeSerializer::class)
sealed class LayersFeatureSize {
	abstract var minClippedHeight: Int?

	companion object {
		data object LayersFeatureSizeSerializer : NamespacedPolymorphicSerializer<LayersFeatureSize>(LayersFeatureSize::class)
	}
}
