package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * The free space a `tree` feature needs around its trunk to generate, checked as square areas of a given width at each
 * height above the base of the trunk.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property minClippedHeight Minimum free height required for the tree to generate when lower than the trunk height, `0`
 * to `80`. `null` leaves it out, so the trunk height is used.
 */
@GeneratedSealedSerializer
@Serializable(with = LayersFeatureSize.Companion.LayersFeatureSizeSerializer::class)
sealed class LayersFeatureSize {
	abstract var minClippedHeight: Int?

	companion object {
		data object LayersFeatureSizeSerializer :
			NamespacedPolymorphicSerializer<LayersFeatureSize>(layersFeatureSizeSealedSerializer())
	}
}
