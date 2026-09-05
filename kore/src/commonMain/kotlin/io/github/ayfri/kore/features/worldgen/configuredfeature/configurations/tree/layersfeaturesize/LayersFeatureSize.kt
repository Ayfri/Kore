package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = LayersFeatureSize.Companion.LayersFeatureSizeSerializer::class)
sealed class LayersFeatureSize {
	abstract var minClippedHeight: Int?

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object LayersFeatureSizeSerializer :
			NamespacedPolymorphicSerializer<LayersFeatureSize>(layersFeatureSizeSealedSerializer())
	}
}
