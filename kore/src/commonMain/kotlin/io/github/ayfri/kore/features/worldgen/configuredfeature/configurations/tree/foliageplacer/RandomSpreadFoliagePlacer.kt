package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Scatters leaves at random positions around the top of the trunk, giving the irregular crowns of the azalea and
 * mangrove trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the area the leaves are scattered in, `0` to `16`, `3` for vanilla trees.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property foliageHeight Height of the area the leaves are scattered in, `1` to `512`, `2` for vanilla trees.
 * @property leafPlacementAttempts Number of random positions tried for a leaf, `0` to `256`, `50` for vanilla azalea
 * trees and `70` for vanilla mangroves.
 */
@Serializable
data class RandomSpreadFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var foliageHeight: IntProvider = ConstantIntProvider(0),
	var leafPlacementAttempts: Int = 0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [RandomSpreadFoliagePlacer].
 *
 * ```kotlin
 * tree("azalea") {
 *     randomSpreadFoliagePlacer(
 *         radius = uniform(3, 4),
 *         offset = uniform(0, 1),
 *         foliageHeight = uniform(2, 3),
 *         leafPlacementAttempts = 50,
 *     )
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.randomSpreadFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	foliageHeight: IntProvider = ConstantIntProvider(0),
	leafPlacementAttempts: Int = 0,
	block: RandomSpreadFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = RandomSpreadFoliagePlacer(radius, offset, foliageHeight, leafPlacementAttempts).apply(block)
}

/**
 * Sets [Tree.foliagePlacer] to a [RandomSpreadFoliagePlacer] of the constant [radius], [offset] and [foliageHeight].
 *
 * ```kotlin
 * tree("azalea") {
 *     randomSpreadFoliagePlacer(radius = 3, offset = 0, foliageHeight = 2, leafPlacementAttempts = 50)
 * }
 * ```
 */
fun Tree.randomSpreadFoliagePlacer(
	radius: Int,
	offset: Int = 0,
	foliageHeight: Int = 0,
	leafPlacementAttempts: Int = 0,
	block: RandomSpreadFoliagePlacer.() -> Unit = {},
) = randomSpreadFoliagePlacer(
	ConstantIntProvider(radius),
	ConstantIntProvider(offset),
	ConstantIntProvider(foliageHeight),
	leafPlacementAttempts,
	block,
)

/**
 * Sets [RandomSpreadFoliagePlacer.foliageHeight] to [value], the height of the foliage.
 *
 * ```kotlin
 * randomSpreadFoliagePlacer(radius = 3) { foliageHeight(uniform(2, 3)) }
 * ```
 */
fun RandomSpreadFoliagePlacer.foliageHeight(value: IntProvider) {
	foliageHeight = value
}

/** Sets [RandomSpreadFoliagePlacer.foliageHeight] to the constant [value]. */
fun RandomSpreadFoliagePlacer.foliageHeight(value: Int) = foliageHeight(ConstantIntProvider(value))
