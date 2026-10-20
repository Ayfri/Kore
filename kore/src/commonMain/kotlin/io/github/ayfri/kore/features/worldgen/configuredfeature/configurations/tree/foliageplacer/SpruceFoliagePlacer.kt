package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the stacked, widening leaf cones of the spruce trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Maximum horizontal radius of the cones, `0` to `16`, `2` to `3` for vanilla spruces.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`, `0` to `2` for vanilla spruces.
 * @property trunkHeight Height parameter of the cones, `0` to `24`, `1` to `2` for vanilla spruces.
 */
@Serializable
data class SpruceFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var trunkHeight: IntProvider = ConstantIntProvider(0),
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [SpruceFoliagePlacer].
 *
 * ```kotlin
 * tree("spruce") {
 *     spruceFoliagePlacer(radius = uniform(2, 3), offset = uniform(0, 2), trunkHeight = uniform(1, 2))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.spruceFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	trunkHeight: IntProvider = ConstantIntProvider(0),
	block: SpruceFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = SpruceFoliagePlacer(radius, offset, trunkHeight).apply(block)
}
