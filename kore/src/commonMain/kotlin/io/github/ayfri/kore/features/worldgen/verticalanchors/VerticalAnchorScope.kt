package io.github.ayfri.kore.features.worldgen.verticalanchors

/**
 * Builder scope for [vertical anchors][VerticalAnchor], the single Y levels shared by the height providers, the
 * carver configurations and the surface rule conditions.
 *
 * Every vertical anchor builder ([absolute], [aboveBottom], [belowTop]) is an extension on this interface, so they
 * only resolve inside a block that actually accepts a vertical anchor, such as `cave("...") { }`, `surfaceRules { }`
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
 *     lavaLevel = absolute(-54)
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
 *     lavaLevel = aboveBottom(31)
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
 *     lavaLevel = belowTop(10)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
fun VerticalAnchorScope.belowTop(belowTop: Int) = BelowTop(belowTop)
