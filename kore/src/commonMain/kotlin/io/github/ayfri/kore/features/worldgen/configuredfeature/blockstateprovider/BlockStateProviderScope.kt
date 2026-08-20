package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

/**
 * Builder scope for block state providers, the block state pickers shared by the configured features, the tree
 * decorators and the enchantment effects.
 *
 * Every block state provider builder (e.g. [simpleStateProvider], [weightedStateProvider]) is an extension on this
 * interface, so they only resolve inside a block that actually accepts a block state provider, such as
 * `simpleBlock("...") { }`, `tree("...") { }` or `weightedStateProvider { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider
 */
interface BlockStateProviderScope
