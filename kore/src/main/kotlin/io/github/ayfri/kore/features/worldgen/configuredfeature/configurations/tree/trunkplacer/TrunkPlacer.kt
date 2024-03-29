package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TrunkPlacer.Companion.TrunkPlacerSerializer::class)
sealed class TrunkPlacer {
	abstract var baseHeight: Int
	abstract var heightRandA: Int
	abstract var heightRandB: Int

	companion object {
		data object TrunkPlacerSerializer : NamespacedPolymorphicSerializer<TrunkPlacer>(TrunkPlacer::class)
	}
}
