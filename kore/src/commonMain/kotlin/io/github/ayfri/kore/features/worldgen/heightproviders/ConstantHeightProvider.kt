package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.AboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.BelowTop
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Always returns the same Y level.
 *
 * It is inlined to its anchor when serialized, so it produces `{ "absolute": 64 }` rather than an object with a
 * `type` field.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 *
 * @property value The anchor resolved on every call.
 */
@Serializable
@SerialName("minecraft:constant")
data class ConstantHeightProvider(
	var value: VerticalAnchor,
) : HeightProvider

/**
 * Creates a `constant` height provider always returning [value].
 *
 * ```kotlin
 * heightRange(constantHeightProvider(aboveBottom(8)))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.constantHeightProvider(value: VerticalAnchor) = ConstantHeightProvider(value)

/**
 * Creates a `constant` height provider always returning the absolute Y coordinate [absolute].
 *
 * ```kotlin
 * heightRange(constantAbsolute(32))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.constantAbsolute(absolute: Int) = ConstantHeightProvider(Absolute(absolute))

/**
 * Creates a `constant` height provider always returning the level [aboveBottom] blocks above the bottom of the
 * dimension.
 *
 * ```kotlin
 * heightRange(constantAboveBottom(8))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.constantAboveBottom(aboveBottom: Int) = ConstantHeightProvider(AboveBottom(aboveBottom))

/**
 * Creates a `constant` height provider always returning the level [belowTop] blocks below the top of the dimension.
 *
 * ```kotlin
 * heightRange(constantBelowTop(8))
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
fun HeightProviderScope.constantBelowTop(belowTop: Int) = ConstantHeightProvider(BelowTop(belowTop))
