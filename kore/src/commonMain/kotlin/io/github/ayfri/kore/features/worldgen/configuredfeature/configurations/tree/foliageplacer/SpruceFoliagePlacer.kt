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

/**
 * Sets [Tree.foliagePlacer] to a [SpruceFoliagePlacer] of the constant [radius], [offset] and [trunkHeight].
 *
 * ```kotlin
 * tree("spruce") {
 *     spruceFoliagePlacer(radius = 2, offset = 0, trunkHeight = 1)
 * }
 * ```
 */
fun Tree.spruceFoliagePlacer(
	radius: Int,
	offset: Int = 0,
	trunkHeight: Int = 0,
	block: SpruceFoliagePlacer.() -> Unit = {},
) = spruceFoliagePlacer(ConstantIntProvider(radius), ConstantIntProvider(offset), ConstantIntProvider(trunkHeight), block)

/**
 * Sets [SpruceFoliagePlacer.trunkHeight] to [value], the height of the trunk left bare under the leaves.
 *
 * ```kotlin
 * spruceFoliagePlacer(radius = 2) { trunkHeight(uniform(1, 2)) }
 * ```
 */
fun SpruceFoliagePlacer.trunkHeight(value: IntProvider) {
	trunkHeight = value
}

/** Sets [SpruceFoliagePlacer.trunkHeight] to the constant [value]. */
fun SpruceFoliagePlacer.trunkHeight(value: Int) = trunkHeight(ConstantIntProvider(value))
