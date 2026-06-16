---
root: .components.layouts.MarkdownLayout
title: Villager Trades
nav-title: Villager Trades
description: Define custom villager trades and trade sets using Kore's type-safe Kotlin DSL for Minecraft Java Edition data packs.
keywords: minecraft, datapack, kore, villager trade, trade set, villager_trade, trade_set, custom trades, merchant
date-created: 2026-06-15
date-modified: 2026-06-15
routeOverride: /docs/data-driven/villager-trades
---

# Villager Trades

Villager trades are data-driven JSON files introduced in Minecraft Java Edition 26.1 that define
what individual trades a villager can offer and how they are grouped into trade sets per profession level.
This system fully replaces the old hardcoded trade lists and lets data packs control everything about
villager economies - what items are bought and sold, at what quantities, for how many uses, with what
item modifier functions applied to the output, and under what conditions.

## Overview

The trade system uses two file types that work together:

- **`villager_trade`** - a single trade offer: inputs the villager wants, the output it gives, limits, and rewards.
- **`trade_set`** - a pool of `villager_trade` references that Minecraft samples from when a villager gains a level.

A villager profession's trade list is built from a stack of trade sets, one per level. Each time a
villager levels up, Minecraft draws `amount` trades at random from the appropriate trade set.

## File Structure

```
data/<namespace>/villager_trade/<name>.json
data/<namespace>/trade_set/<name>.json
```

For the full JSON specification see:

- [Minecraft Wiki: Villager trade definition](https://minecraft.wiki/w/Villager_trade_definition)
- [Minecraft Wiki: Trade set definition](https://minecraft.wiki/w/Trade_set_definition)

---

## Villager Trade

### Creating a Trade

Use `villagerTrade` on your `DataPack`. Call `wants` and `gives` inside the block to set the required
items - both accept an `ItemArgument`, an optional fixed `count`, and an optional component builder.

```kotlin
datapack.villagerTrade("wheat_for_emerald") {
    wants(Items.WHEAT, count = 20)
    gives(Items.EMERALD)
    maxUses = constant(12f)
    xp = constant(1f)
}
```

### Item Slots

`wants`, `gives`, and `additionalWants` all follow the same shape: an item id, an optional stack size,
and an optional block to set data components on the item.

```kotlin
datapack.villagerTrade("enchanted_sword") {
    wants(Items.EMERALD, count = 30)
    gives(Items.DIAMOND_SWORD) {
        enchantments {
            add(Enchantments.SHARPNESS, 5)
        }
    }
    maxUses = constant(3f)
    xp = constant(30f)
}
```

Two-input trades use `additionalWants` for the second slot:

```kotlin
datapack.villagerTrade("book_trade") {
    wants(Items.EMERALD, count = 5)
    additionalWants(Items.BOOK)
    gives(Items.ENCHANTED_BOOK)
    maxUses = constant(1f)
    xp = constant(15f)
}
```

### VillagerTrade Fields

All fields are optional except `wants` and `gives`, which must be set before the data pack is generated.

| Field                          | Type                                       | Description                                                                       |
|--------------------------------|--------------------------------------------|-----------------------------------------------------------------------------------|
| `additionalWants`              | `ItemStack?`                               | Second item slot the villager requests alongside `wants`.                         |
| `doubleTradePriceEnchantments` | `InlinableList<EnchantmentOrTagArgument>?` | Enchantments on the player's item that double the trade cost.                     |
| `givenItemModifiers`           | `ItemModifierAsList?`                      | Item modifier functions applied to the output item before delivery.               |
| `gives`                        | `ItemStack?`                               | Item the villager offers in return. **Required.**                                 |
| `maxUses`                      | `NumberProvider?`                          | Maximum uses before the trade locks. Unlocks when the villager restocks.          |
| `merchantPredicate`            | `PredicateCondition?`                      | Condition checked against the merchant entity; trade only appears when it passes. |
| `reputationDiscount`           | `NumberProvider?`                          | Price multiplier applied based on the player's village reputation.                |
| `wants`                        | `ItemStack?`                               | Primary item the villager requests. **Required.**                                 |
| `xp`                           | `NumberProvider?`                          | XP points awarded to the villager when the trade completes.                       |

### Applying Item Modifiers to the Output

Use `givenItemModifiers` to run item modifier functions on the item the villager gives.
This is how you add random enchantments, custom lore, or any other post-processing:

```kotlin
datapack.villagerTrade("random_enchanted_book") {
    wants(Items.EMERALD, count = 10)
    additionalWants(Items.BOOK)
    gives(Items.ENCHANTED_BOOK)
    givenItemModifiers {
        enchantRandomly()
    }
    maxUses = constant(1f)
    xp = constant(15f)
}
```

See [Item Modifiers](/docs/data-driven/item-modifiers) for all available functions.

### Gating a Trade with a Predicate

`merchantPredicate` is a condition evaluated against the villager entity. The trade only appears in
the merchant's offer list when the condition passes - useful for biome-locked or NBT-gated trades.

```kotlin
datapack.villagerTrade("desert_trade") {
    wants(Items.SAND, count = 8)
    gives(Items.GLASS)
    merchantPredicate = LocationCheck(predicate = LocationPredicate(biomes = listOf(Biomes.DESERT)))
    maxUses = constant(16f)
    xp = constant(2f)
}
```

See [Predicates](/docs/data-driven/predicates) for all available condition types.

### Double-Price Enchantments

`doubleTradePriceEnchantments` lists enchantments that, when present on the player's traded-in item,
cause the villager to charge twice as much. Vanilla uses this for the `Curse of Binding` and similar effects.

```kotlin
doubleTradePriceEnchantments = listOf(Enchantments.BINDING_CURSE)
```

---

## Trade Set

### Creating a Trade Set

A `TradeSet` groups references to `villager_trade` files and controls how many are offered per level.
Pass the list of trade references (returned by `villagerTrade`) and an `amount` provider:

```kotlin
val wheatTrade = datapack.villagerTrade("wheat_for_emerald") {
    wants(Items.WHEAT, count = 20)
    gives(Items.EMERALD)
    xp = constant(1f)
}

val potatoTrade = datapack.villagerTrade("potato_for_emerald") {
    wants(Items.POTATO, count = 26)
    gives(Items.EMERALD)
    xp = constant(1f)
}

datapack.tradeSet(
    "novice_farmer",
    trades = listOf(wheatTrade, potatoTrade),
    amount = constant(2f),
)
```

### Sampling with Tags

You can mix individual trade references with tag references in the same pool:

```kotlin
datapack.tradeSet(
    "apprentice_farmer",
    trades = listOf(
        VillagerTradeTagArgument("farmer_apprentice", "mymod"),
        myCustomTrade,
    ),
    amount = uniform(constant(1f), constant(3f)),
) {
    allowDuplicates = false
}
```

### TradeSet Fields

| Field             | Type                                        | Description                                                        |
|-------------------|---------------------------------------------|--------------------------------------------------------------------|
| `allowDuplicates` | `Boolean?`                                  | Whether the same trade can be drawn more than once per level-up.   |
| `amount`          | `NumberProvider`                            | How many trades are drawn from this set when a villager levels up. |
| `randomSequence`  | `RandomSequenceArgument?`                   | Named random sequence for reproducible sampling.                   |
| `trades`          | `InlinableList<VillagerTradeOrTagArgument>` | Trade references or tags to sample from.                           |

---

## Full Example: Custom Farmer Profession Level 1

```kotlin
datapack {
    val hayTrade = villagerTrade("hay_for_emerald") {
        wants(Items.HAY_BLOCK, count = 1)
        gives(Items.EMERALD)
        maxUses = constant(16f)
        xp = constant(2f)
    }

    val wheatTrade = villagerTrade("wheat_for_emerald") {
        wants(Items.WHEAT, count = 20)
        gives(Items.EMERALD)
        maxUses = constant(16f)
        xp = constant(1f)
    }

    val breadTrade = villagerTrade("emerald_for_bread") {
        wants(Items.EMERALD)
        gives(Items.BREAD, count = 6)
        maxUses = constant(16f)
        xp = constant(1f)
    }

    tradeSet(
        "custom_farmer_novice",
        trades = listOf(hayTrade, wheatTrade, breadTrade),
        amount = constant(2f),
    ) {
        allowDuplicates = false
    }
}
```

---

## See Also

- [Item Modifiers](/docs/data-driven/item-modifiers) - Apply functions to the item a villager gives
- [Predicates](/docs/data-driven/predicates) - Gate trade availability via `merchantPredicate`
- [Enchantments](/docs/data-driven/enchantments) - `doubleTradePriceEnchantments` and enchanting trade outputs
- [Tags](/docs/data-driven/tags) - Group trades into reusable `villager_trade` tags for trade sets
- [Loot Tables](/docs/data-driven/loot-tables) - Related data-driven item generation system

### External Resources

- [Minecraft Wiki: Villager trade definition](https://minecraft.wiki/w/Villager_trade_definition)
- [Minecraft Wiki: Trade set definition](https://minecraft.wiki/w/Trade_set_definition)
- [Minecraft Wiki: Trading](https://minecraft.wiki/w/Trading) - How the vanilla trading system works
- [Minecraft Wiki: Villager](https://minecraft.wiki/w/Villager) - Villager behavior, professions, and leveling
