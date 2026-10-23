package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Places the roots of a tree and moves its trunk upwards, currently only used by the mangrove trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property rootProvider The block states making up the roots.
 * @property trunkOffsetY How far the trunk is moved upwards, `1` to `3` for vanilla mangroves.
 * @property aboveRootPlacement The blocks placed on top of the roots, none when `null`.
 */
@GeneratedSealedSerializer
@Serializable(with = RootPlacer.Companion.RootPlacerSerializer::class)
sealed class RootPlacer : BlockStateProviderScope, IntProviderScope {
	abstract var rootProvider: BlockStateProvider
	abstract var trunkOffsetY: IntProvider
	abstract var aboveRootPlacement: AboveRootPlacement?

	companion object {
		data object RootPlacerSerializer : NamespacedPolymorphicSerializer<RootPlacer>(rootPlacerSealedSerializer())
	}
}

/**
 * The blocks placed on top of the roots of a [RootPlacer].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property aboveRootProvider The block states placed on top of the roots.
 * @property aboveRootPlacementChance The chance of placing a block above a root, `0.0` to `1.0`, `0.5` for vanilla mangroves.
 */
@Serializable
data class AboveRootPlacement(
	var aboveRootProvider: BlockStateProvider = SimpleBlockStateProvider(),
	var aboveRootPlacementChance: Double = 0.0,
) : BlockStateProviderScope

/**
 * Sets [RootPlacer.aboveRootPlacement] to the placement built in [block], placing a block above a root with a
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
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun RootPlacer.aboveRootPlacement(chance: Double = 0.0, block: AboveRootPlacement.() -> Unit = {}) {
	aboveRootPlacement = AboveRootPlacement(aboveRootPlacementChance = chance).apply(block)
}
