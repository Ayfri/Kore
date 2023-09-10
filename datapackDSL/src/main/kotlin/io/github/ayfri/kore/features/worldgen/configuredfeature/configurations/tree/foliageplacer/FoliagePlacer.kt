package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FoliagePlacer.Companion.FoliagePlacerSerializer::class)
sealed class FoliagePlacer {
	abstract var radius: IntProvider
	abstract var offset: IntProvider

	companion object {
		data object FoliagePlacerSerializer : NamespacedPolymorphicSerializer<FoliagePlacer>(FoliagePlacer::class)
	}
}
