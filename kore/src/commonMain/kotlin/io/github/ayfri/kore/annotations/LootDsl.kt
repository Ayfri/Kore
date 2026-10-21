package io.github.ayfri.kore.annotations

/**
 * Marks the loot table, item modifier and predicate builders, which nest into each other and share builder names.
 *
 * Inside `item(Items.DIAMOND) { modifier { } }`, a `modifier` or `condition` of the entry that is not imported would
 * otherwise bind to the enclosing pool or table and land there silently. With the marker it fails to compile instead.
 */
@DslMarker
annotation class LootDsl
