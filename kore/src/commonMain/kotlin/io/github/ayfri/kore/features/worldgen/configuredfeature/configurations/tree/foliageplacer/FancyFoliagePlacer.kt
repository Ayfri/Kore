package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the leaf clusters at the end of the branches of the large (fancy) oak trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`, `2` for vanilla large oaks.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`, `4` for vanilla large oaks.
 * @property height Height of the leaves, `0` to `16`, `4` for vanilla large oaks.
 */
@Serializable
data class FancyFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: Int = 0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [FancyFoliagePlacer].
 *
 * ```kotlin
 * tree("fancy_oak") {
 *     fancyFoliagePlacer(radius = uniform(2, 3), offset = uniform(3, 4), height = 4)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.fancyFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: Int = 0,
	block: FancyFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = FancyFoliagePlacer(radius, offset, height).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [FancyFoliagePlacer] of the constant [radius] and [offset].
 *
 * ```kotlin
 * tree("fancy_oak") {
 *     fancyFoliagePlacer(radius = 2, offset = 4, height = 4)
 * }
 * ```
 */
fun Tree.fancyFoliagePlacer(radius: Int, offset: Int = 0, height: Int = 0, block: FancyFoliagePlacer.() -> Unit = {}) =
	fancyFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), height, block)
