package io.github.ayfri.kore.features.worldgen.intproviders

/**
 * Builder scope for [int providers][IntProvider], the integer pickers shared by the configured features, the placed
 * feature modifiers, the processors and the enchantment providers.
 *
 * Every int provider builder (e.g. [constant], [uniform]) is an extension on this interface, so they only resolve
 * inside a block that actually accepts an int provider, such as `ore("...") { }` or `placedFeature("...", ...) { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 */
interface IntProviderScope
