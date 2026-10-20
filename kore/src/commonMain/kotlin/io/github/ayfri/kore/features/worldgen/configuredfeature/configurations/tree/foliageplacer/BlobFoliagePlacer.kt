package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import kotlinx.serialization.Serializable

/**
 * Places the rounded blob of leaves of the oak, birch, swamp oak and jungle trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the blob, `0` to `16`, `2` for vanilla trees (`3` for swamp oaks).
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 * @property height Height of the blob, `0` to `16`, `3` for vanilla trees.
 */
@Serializable
data class BlobFoliagePlacer(
	override var radius: IntProvider = ConstantIntProvider(0),
	override var offset: IntProvider = ConstantIntProvider(0),
	var height: Int = 0,
) : FoliagePlacer()

/**
 * Sets [Tree.foliagePlacer] to a [BlobFoliagePlacer].
 *
 * ```kotlin
 * tree("oak") {
 *     blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun Tree.blobFoliagePlacer(
	radius: IntProvider = ConstantIntProvider(0),
	offset: IntProvider = ConstantIntProvider(0),
	height: Int = 0,
	block: BlobFoliagePlacer.() -> Unit = {},
) {
	foliagePlacer = BlobFoliagePlacer(radius, offset, height).apply(block)
}
