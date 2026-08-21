package io.github.ayfri.kore.features.worldgen.heightproviders

import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchorScope

/**
 * Builder scope for [height providers][HeightProvider], the Y level pickers shared by the carvers, the placement
 * modifiers and the structures.
 *
 * Every height provider builder (e.g. [constantAbsolute], [uniformHeightProvider]) is an extension on this
 * interface, so they only resolve inside a block that actually accepts a height provider, such as `cave("...") { }`,
 * `jigsaw("...") { }` or `placedFeature("...", ...) { }`.
 *
 * It extends [VerticalAnchorScope], so the anchors the providers take as arguments are available in the same block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/height_provider
 */
interface HeightProviderScope : VerticalAnchorScope
