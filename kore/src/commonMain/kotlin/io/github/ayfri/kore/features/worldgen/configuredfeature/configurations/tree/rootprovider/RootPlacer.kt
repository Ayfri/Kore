package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Places the roots of a tree below its trunk, currently only used by the mangrove trees.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property rootProvider The block states making up the roots.
 * @property trunkOffsetY The vertical offset applied to the trunk once the roots are placed.
 * @property aboveRootProvider The blocks placed on top of the roots, none when `null`.
 */
@GeneratedSealedSerializer
@Serializable(with = RootPlacer.Companion.RootPlacerSerializer::class)
sealed class RootPlacer : BlockStateProviderScope {
	abstract var rootProvider: BlockStateProvider
	abstract var trunkOffsetY: IntProvider
	abstract var aboveRootProvider: AboveRootPlacement?

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object RootPlacerSerializer : NamespacedPolymorphicSerializer<RootPlacer>(rootPlacerSealedSerializer())
	}
}

/**
 * The blocks placed on top of the roots of a [RootPlacer].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property aboveRootProvider The block states placed on top of the roots.
 * @property aboveRootPlacementChance The chance of placing a block above a root, between `0.0` and `1.0`.
 */
@Serializable
data class AboveRootPlacement(
	var aboveRootProvider: BlockStateProvider = SimpleStateProvider(),
	var aboveRootPlacementChance: Double = 0.0,
) : BlockStateProviderScope

/**
 * Sets [RootPlacer.aboveRootProvider] to the placement built in [block], placing a block above a root with a
 * probability of [AboveRootPlacement.aboveRootPlacementChance].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * mangroveRootPlacer {
 *     aboveRootPlacement(chance = 0.5) {
 *         aboveRootProvider = simpleStateProvider(Blocks.MOSS_CARPET)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun RootPlacer.aboveRootPlacement(chance: Double = 0.0, block: AboveRootPlacement.() -> Unit = {}) {
	aboveRootProvider = AboveRootPlacement(aboveRootPlacementChance = chance).apply(block)
}
