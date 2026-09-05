package io.github.ayfri.kore.features.enchantments.values

import kotlinx.serialization.Serializable

/**
 * [value] restricted to the `[min, max]` range.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#clamped
 *
 * @property value The clamped expression.
 * @property min The lowest value returned.
 * @property max The highest value returned.
 */
@Serializable
data class Clamped(
	var value: LevelBased,
	var min: Float,
	var max: Float,
) : LevelBased(), LevelBasedScope

/** Creates a [Clamped] value restricting [value] to the `[min, max]` range. */
fun LevelBasedScope.clampedLevelBased(value: LevelBased, min: Float, max: Float) = Clamped(value, min, max)

/** Creates a [Clamped] value restricting the constant [value] to the `[min, max]` range. */
fun LevelBasedScope.clampedLevelBased(value: Float, min: Float, max: Float) = Clamped(Constant(value), min, max)

/** Creates a [Clamped] value restricting the constant [value] to the `[min, max]` range. */
fun LevelBasedScope.clampedLevelBased(value: Int, min: Float, max: Float) = Clamped(Constant(value.toFloat()), min, max)
