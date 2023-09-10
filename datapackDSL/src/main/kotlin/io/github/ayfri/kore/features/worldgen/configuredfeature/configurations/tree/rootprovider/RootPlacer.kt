package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RootPlacer.Companion.RootPlacerSerializer::class)
sealed class RootPlacer {
	abstract var rootProvider: BlockStateProvider
	abstract var trunkOffsetY: IntProvider
	abstract var aboveRootProvider: AboveRootPlacement?

	companion object {
		data object RootPlacerSerializer : NamespacedPolymorphicSerializer<RootPlacer>(RootPlacer::class)
	}
}

@Serializable
data class AboveRootPlacement(
	var aboveRootProvider: BlockStateProvider = simpleStateProvider(),
	var aboveRootPlacementChance: Double = 0.0,
)

fun RootPlacer.aboveRootPlacement(
	aboveRootProvider: BlockStateProvider = simpleStateProvider(),
	aboveRootPlacementChance: Double = 0.0,
	block: AboveRootPlacement.() -> Unit = {},
) = AboveRootPlacement(aboveRootProvider, aboveRootPlacementChance).apply(block)
