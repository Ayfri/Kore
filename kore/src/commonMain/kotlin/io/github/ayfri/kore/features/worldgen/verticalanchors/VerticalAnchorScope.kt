package io.github.ayfri.kore.features.worldgen.verticalanchors

/**
 * Builder scope for [vertical anchors][VerticalAnchor], the single Y levels shared by the height providers and the
 * material rule conditions.
 *
 * Every vertical anchor builder ([absolute], [aboveBottom], [belowTop]) is an extension on this interface, so they
 * only resolve inside a block that actually accepts a vertical anchor, such as `cave("...") { }`, `materialRules { }`
 * or any height provider builder.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
interface VerticalAnchorScope

/**
 * Creates an [Absolute] anchor at the absolute Y coordinate [absolute], the one shown on the F3 screen.
 *
 * ```kotlin
 * cave("my_cave") {
 *     y = uniformHeightProvider(absolute(-54), absolute(64))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
fun VerticalAnchorScope.absolute(absolute: Int) = Absolute(absolute)

/**
 * Creates an [AboveBottom] anchor [aboveBottom] blocks above the bottom of the dimension, `0` being `min_y` itself.
 *
 * ```kotlin
 * cave("my_cave") {
 *     y = uniformHeightProvider(aboveBottom(8), absolute(180))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
fun VerticalAnchorScope.aboveBottom(aboveBottom: Int) = AboveBottom(aboveBottom)

/**
 * Creates a [BelowTop] anchor [belowTop] blocks below the top of the dimension, larger values moving the anchor
 * further down.
 *
 * ```kotlin
 * cave("my_cave") {
 *     y = uniformHeightProvider(absolute(0), belowTop(10))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
fun VerticalAnchorScope.belowTop(belowTop: Int) = BelowTop(belowTop)
