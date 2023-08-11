package features.worldgen.configuredfeature.configurations.tree.foliageplacer

import features.worldgen.intproviders.IntProvider
import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FoliagePlacer.Companion.FoliagePlacerSerializer::class)
sealed class FoliagePlacer {
	abstract var radius: IntProvider
	abstract var offset: IntProvider

	companion object {
		data object FoliagePlacerSerializer : NamespacedPolymorphicSerializer<FoliagePlacer>(FoliagePlacer::class)
	}
}
