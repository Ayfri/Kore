package io.github.ayfri.kore.features.predicates.providers

/**
 * Builder scope for [number providers][NumberProvider], the number pickers shared by the loot tables, the item
 * modifiers, the predicates, the villager trades and the trade sets.
 *
 * Every number provider builder (e.g. [constant], [uniform]) is an extension on this interface, so they only resolve
 * inside a block that actually accepts a number provider, such as `lootTable("...") { }` or `predicate("...") { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table#Number_provider
 */
interface NumberProviderScope
