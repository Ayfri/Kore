package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = PlacementModifier.Companion.PlacementModifierSerializer::class)
sealed class PlacementModifier {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PlacementModifierSerializer :
			NamespacedPolymorphicSerializer<PlacementModifier>(placementModifierSealedSerializer())
	}
}
