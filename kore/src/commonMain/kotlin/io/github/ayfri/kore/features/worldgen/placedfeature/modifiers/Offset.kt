package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

/**
 * Placement modifier moving the position by an offset drawn from each axis' provider.
 *
 * Vanilla uses it to place a feature next to the one before it, such as the pillars around a basalt pillar, or one
 * block lower.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#offset
 *
 * @property x Offset along the X axis, `-16` to `16`, a constant is written as a bare integer.
 * @property y Offset along the Y axis, `-16` to `16`, a constant is written as a bare integer.
 * @property z Offset along the Z axis, `-16` to `16`, a constant is written as a bare integer.
 */
@Serializable
data class Offset(
	var x: IntProvider = ConstantIntProvider(0),
	var y: IntProvider = ConstantIntProvider(0),
	var z: IntProvider = ConstantIntProvider(0),
) : PlacementModifier(), IntProviderScope

/**
 * Appends an `offset` placement modifier moving the position by [x], [y] and [z], each `0` when left out.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     offset(x = constant(1), y = uniform(-2, 2))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#offset
 */
fun PlacedFeature.offset(
	x: IntProvider = ConstantIntProvider(0),
	y: IntProvider = ConstantIntProvider(0),
	z: IntProvider = ConstantIntProvider(0),
) {
	placementModifiers += Offset(x, y, z)
}
