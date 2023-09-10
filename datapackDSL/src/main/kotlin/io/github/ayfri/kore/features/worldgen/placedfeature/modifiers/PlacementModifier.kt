package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PlacementModifier.Companion.PlacementModifierSerializer::class)
sealed class PlacementModifier {
	companion object {
		data object PlacementModifierSerializer : NamespacedPolymorphicSerializer<PlacementModifier>(PlacementModifier::class)
	}
}
