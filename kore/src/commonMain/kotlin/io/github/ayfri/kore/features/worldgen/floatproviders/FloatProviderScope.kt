package io.github.ayfri.kore.features.worldgen.floatproviders

/**
 * Builder scope for [float providers][FloatProvider], the float pickers shared by the carvers, the configured
 * features and the enchantment effects.
 *
 * Every float provider builder (e.g. [constant], [uniform]) is an extension on this interface, so they only resolve
 * inside a block that actually accepts a float provider, such as `cave("...") { }` or `largeDripstone("...") { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 */
interface FloatProviderScope
