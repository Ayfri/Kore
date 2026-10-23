package io.github.ayfri.kore.features.predicates.providers

/**
 * Builder scope for [context int providers][ContextIntProvider] and [context float providers][ContextFloatProvider], the
 * number pickers shared by the loot tables, the item modifiers, the predicates, the villager trades, the trade sets and
 * the `compute` command.
 *
 * Every provider builder (e.g. [constant], [uniform]) is an extension on this interface, so they only resolve inside a
 * block that actually accepts a context provider, such as `lootTable("...") { }` or `predicate("...") { }`. A builder
 * taking an `Int` or an int provider (`constant(1)`) builds an int provider, one taking a `Float` or a float provider
 * (`constant(1f)`) builds a float provider. A builder whose parameters are the same for both families has one name per
 * family: [intStorage] and [floatStorage], [intEnvironmentAttribute] and [floatEnvironmentAttribute], [intWeightedList]
 * and [floatWeightedList], [intNumberDispatcher] and [floatNumberDispatcher].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
interface NumberProviderScope
