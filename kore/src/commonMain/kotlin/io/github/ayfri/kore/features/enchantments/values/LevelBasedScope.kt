package io.github.ayfri.kore.features.enchantments.values

/**
 * Receiver of the [LevelBased] builders, implemented by everything that accepts a level-based value.
 *
 * Scoping them this way keeps `constantLevelBased`, `linearLevelBased` and friends out of the global completion
 * list: they only resolve inside a block that actually takes a level-based value, such as `explode { }` or
 * `damageEntity(...) { }`.
 *
 * Outside such a block, [LevelBased.Companion] is a scope of its own, so `LevelBased.linearLevelBased(1, 1)` builds
 * a value anywhere.
 */
interface LevelBasedScope
