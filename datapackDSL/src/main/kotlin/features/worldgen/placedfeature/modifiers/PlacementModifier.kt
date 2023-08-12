package features.worldgen.placedfeature.modifiers

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PlacementModifier.Companion.PlacementModifierSerializer::class)
sealed class PlacementModifier {
	companion object {
		data object PlacementModifierSerializer : NamespacedPolymorphicSerializer<PlacementModifier>(PlacementModifier::class)
	}
}
