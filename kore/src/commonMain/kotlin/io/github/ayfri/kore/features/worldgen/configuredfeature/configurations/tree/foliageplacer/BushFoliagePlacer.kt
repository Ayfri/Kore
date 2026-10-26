package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the small leaf bush of the jungle bushes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the bush, `0` to `16`, `2` for vanilla jungle bushes.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`, `1` for vanilla jungle bushes.
 * @property height Height of the bush, `0` to `16`, `2` for vanilla jungle bushes.
 */
@Serializable
data class BushFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: Int = 0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [BushFoliagePlacer].
 *
 * ```kotlin
 * tree("jungle_bush") {
 *     bushFoliagePlacer(radius = uniform(2, 3), offset = uniform(0, 1), height = 2)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.bushFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: Int = 0,
	block: BushFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = BushFoliagePlacer(radius, offset, height).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [BushFoliagePlacer] of the constant [radius] and [offset].
 *
 * ```kotlin
 * tree("jungle_bush") {
 *     bushFoliagePlacer(radius = 2, offset = 1, height = 2)
 * }
 * ```
 */
fun Tree.bushFoliagePlacer(radius: Int, offset: Int = 0, height: Int = 0, block: BushFoliagePlacer.() -> Unit = {}) =
	bushFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), height, block)
