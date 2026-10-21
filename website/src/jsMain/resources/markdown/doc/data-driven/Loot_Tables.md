---
root: .components.layouts.MarkdownLayout
title: Loot Tables
nav-title: Loot Tables
description: Create and customize Minecraft loot tables using Kore's type-safe Kotlin DSL for drops, container contents, fishing, and more.
keywords: minecraft, datapack, kore, loot tables, pools, entries, item modifiers, drops
date-created: 2025-08-11
date-modified: 2026-10-21
routeOverride: /docs/data-driven/loot-tables
---

# Loot Tables

Loot tables are JSON files that dictate what items should generate in various game situations.
They control drops from mobs and blocks, contents of naturally generated containers (chests, barrels, dispensers),
fishing rewards, archaeology brushing results, bartering exchanges, and more.

The [loot table example](/playground#example=loot-table) of the playground runs in your browser and shows the JSON it
generates.

## Overview

Loot tables have several key characteristics:

- **Context-dependent**: Different loot contexts provide different parameters (killer entity, tool, luck level, etc.)
- **Randomized**: Use number providers for rolls and weighted entries for varied results
- **Conditional**: Apply predicates to pools, entries, and functions
- **Composable**: Reference other loot tables as entries for modularity
- **Transformable**: Apply item modifier functions to modify generated items

## File Structure

Loot tables are stored as JSON files in data packs at:

```
data/<namespace>/loot_table/<path>.json
```

For complete JSON specification, see the [Minecraft Wiki - Loot table](https://minecraft.wiki/w/Loot_table).

## Creating Loot Tables

Use the `lootTable` builder function to create loot tables in Kore:

```kotlin
dataPack("my_datapack") {
	lootTable("custom_chest") {
		pool {
			rolls = constant(3f)

			entries {
				item(Items.DIAMOND) {
					weight = 1
				}
				item(Items.GOLD_INGOT) {
					weight = 5
				}
				item(Items.IRON_INGOT) {
					weight = 10
				}
			}
		}
	}
}
```

This generates `data/my_datapack/loot_table/custom_chest.json`.

{{{ .components.mc.LootChestMockup }}}

## Table Structure

A loot table consists of:

| Property         | Type                     | Description                                         |
|------------------|--------------------------|-----------------------------------------------------|
| `type`           | `LootTableType`          | Optional context type for validation                |
| `pools`          | `List<LootPool>`         | One or more pools that generate items               |
| `modifier`       | `InlinableItemModifier?` | Optional item modifier applied to all drops         |
| `randomSequence` | `RandomSequenceArgument` | Optional deterministic random sequence              |

### Setting the Type

The type validates that the loot table uses appropriate context parameters:

```kotlin
lootTable("entity_drops") {
	type = LootTableType.ENTITY

	pool {
		// Entity context allows accessing killer, damage source, etc.
	}
}
```

Available types: `ADVANCEMENT_ENTITY`, `ADVANCEMENT_LOCATION`, `ADVANCEMENT_REWARD`, `ARCHEOLOGY`, `BARTER`, `BLOCK`,
`BLOCK_USE`, `CHEST`, `COMMAND`, `EMPTY`, `ENTITY`, `EQUIPMENT`, `FISHING`, `GIFT`, `GENERIC`, `SELECTOR`, `SHEARING`,
`VAULT`, `VILLAGER_TRADE`.

### Global Modifier

Apply an item modifier to all items dropped by the table, one function being written bare and several ones as a list:

```kotlin
lootTable("enchanted_loot") {
	modifier {
		enchantRandomly(Enchantments.LOOTING)
	}

	pool {
		entries {
			item(Items.DIAMOND_SWORD)
		}
	}
}
```

Each `modifier` (table, pool and entry) also takes the ID of an [item modifier file](/docs/data-driven/item-modifiers#reference) or an item modifier tag, written as a bare string:

```kotlin
val sharpen = itemModifier("sharpen") {
	enchantRandomly(Enchantments.SHARPNESS)
}

lootTable("sharp_loot") {
	modifier { reference(sharpen) }  // "modifier": "my_pack:sharpen"
}
```

## Pools

Each pool represents an independent set of rolls. A table can have multiple pools that all contribute to the final loot.

### Pool Properties

| Property     | Type                     | Description                                                          |
|--------------|--------------------------|----------------------------------------------------------------------|
| `rolls`      | `NumberProvider`         | How many times to select from entries                                |
| `bonusRolls` | `NumberProvider`         | Additional rolls per luck level                                      |
| `condition`  | `PredicateCondition?`    | Condition that must pass for the pool to activate, several are AND-ed |
| `entries`    | `List<LootEntry>`        | Possible entries to select from                                      |
| `modifier`   | `InlinableItemModifier?` | Item modifier applied to items from this pool                        |

### Basic Pool

```kotlin
lootTable("simple_pool") {
	pool {
		rolls = constant(2f)
		bonusRolls = constant(1f)

		entries {
			item(Items.EMERALD)
		}
	}
}
```

### Conditional Pool

```kotlin
lootTable("weather_dependent") {
	pool {
		rolls = constant(1f)

		condition {
			weatherCheck(raining = true)
		}

		entries {
			item(Items.WATER_BUCKET)
		}
	}
}
```

### Pool with a Modifier

```kotlin
lootTable("modified_drops") {
	pool {
		rolls = constant(1f)

		entries {
			item(Items.DIAMOND_PICKAXE)
		}

		modifier {
			setDamage(0.5f)
			enchantWithLevels(levels = constant(30f))
		}
	}
}
```

## Number Providers

Number providers determine dynamic numeric values for rolls, counts, and other quantities.

The builders are extensions on `NumberProviderScope`, so they only resolve inside the blocks that accept a number provider: loot tables and pools, item modifiers and their functions, predicates, villager trades and trade sets.

| Provider                          | Description                                               | Example                                                                 |
|-----------------------------------|-----------------------------------------------------------|-------------------------------------------------------------------------|
| `binomial(n, p)`                  | Binomial distribution                                     | `binomial(5f, 0.5f)`                                                    |
| `conditionalNumber(...)`          | One value when a condition passes, another otherwise      | `conditionalNumber(5f, 2f) { killedByPlayer() }`                        |
| `constant(value)`                 | Fixed value                                               | `constant(5f)`                                                          |
| `enchantmentLevel(...)`           | Based on enchantment level (requires enchantment context) | `enchantmentLevel(5)`                                                   |
| `environmentAttribute(attribute)` | Current value of a numeric env attribute                  | `environmentAttribute(EnvironmentAttributes.Visual.FOG_START_DISTANCE)` |
| `numberDispatcher(...) { }`       | Value of the first matching case, or a default            | `numberDispatcher(default = 0f) { case(1f) { killedByPlayer() } }`      |
| `reference(id)`                   | Number provider file, written as its ID                   | `reference(NumberProviders.Cooking.TIME_COAL)`                          |
| `scoreNumber(...)`                | Value from a scoreboard objective                         | `scoreNumber("kills", EntityTarget.THIS)`                               |
| `storageNumber(storage, path)`    | Value from command storage                                | `storageNumber("my_pack:data", "player.health")`                        |
| `sum(...)`                        | Sum of multiple providers, or of a tag of providers       | `sum(constant(1f), uniform(1f, 3f))`                                    |
| `uniform(min, max)`               | Random value between min and max                          | `uniform(1f, 5f)`                                                       |
| `weightedListNumber { }`          | Value of one entry, picked by weight                      | `weightedListNumber { entry(30, 1f); entry(70, 0f) }`                   |

`conditionalNumber`, `numberDispatcher` and `weightedListNumber` nest other providers, and the conditions are the ones of [predicates](/docs/data-driven/predicates). A condition is either passed as a value (`conditionalNumber(SurvivesExplosion, 1f)`), as a predicate ID (`conditionalNumber(fastCooking, 800f, 1600f)`) or declared in a block holding exactly one condition. `onFalse` and `default` are `0` when omitted.

A constant is the bare number (`5.0`), and every other provider is an object that always carries its `type`: the game rejects `{"min": 1, "max": 2}`, it has to be `{"type": "minecraft:uniform", "min": 1, "max": 2}`.

### Number provider IDs

Wherever a number provider is expected (`rolls`, `min`, `max`, `on_true`, the item modifier counts...), the game also accepts the ID of a [number provider file](#number-provider-files), written as a bare string. `reference(id)` builds it, and `numberProvider(...)` returns the ID to pass:

```kotlin
val bonus = numberProvider("bonus") { uniform(1f, 3f) }

pool {
	rolls = reference(bonus)  // "rolls": "my_pack:bonus"
	bonusRolls = uniform(reference(NumberProviders.Cooking.TIME_COAL), constant(5f))
}
```

The `summands` of a `sum` also take a tag of number providers, `sum(NumberProviderTagArgument("bonuses", "my_pack"))`, which writes `"summands": "#my_pack:bonuses"`. A tag is only valid there. A list can't mix references and inline values, the game rejects it.

A provider that references itself, directly or through other files, makes the pack fail to load.

Every number provider computes a 32-bit float, and an integer field rounds it. `scoreNumber` and the ranges of `entityScores` therefore stay exact only up to 16,777,216 (2²⁴): past it, neighboring scores read as the same value ([MC-254370](https://bugs.mojang.com/browse/MC-254370)). To compare larger scores exactly, test them with `execute if score`, or store the high and low parts on two objectives and check each one. A `sum` of the parts is a float again and loses the same precision.

```kotlin
pool {
	rolls = conditionalNumber(3f, 1f) { killedByPlayer() }

	bonusRolls = numberDispatcher(default = weightedListNumber {
		entry(30, 1f)
		entry(70, 0f)
	}) {
		case(2f) { killedByPlayer() }
	}
}
```

This writes `weighted_list` as `{"type": "minecraft:weighted_list", "distribution": [{"data": 1.0, "weight": 30}, {"data": 0.0, "weight": 70}]}`.

```kotlin
pool {
	rolls = uniform(2f, 5f)  // 2-5 rolls randomly
	bonusRolls = constant(1f)  // +1 roll per luck level

	entries {
		item(Items.GOLD_INGOT)
	}
}
```

### Number provider files

A number provider can also live in its own file, `data/<namespace>/number_provider/<fileName>.json`, so the game components that take a number provider by ID can point at it. `numberProvider` takes a block returning the provider and gives back the reference to use:

```kotlin
val layers = numberProvider("my_layers") {
	numberDispatcher(default = weightedListNumber {
		entry(30, 1f)
		entry(70, 0f)
	}) {
		case(1f) {
			matchBlock(Blocks.COMPOSTER) {
				state("level", "0")
			}
		}
	}
}
```

```json
{
	"type": "minecraft:number_dispatcher",
	"cases": [
		{
			"condition": {
				"type": "minecraft:match_block",
				"blocks": "minecraft:composter",
				"state": {
					"level": "0"
				}
			},
			"number_provider": 1.0
		}
	],
	"default": {
		"type": "minecraft:weighted_list",
		"distribution": [
			{ "data": 1.0, "weight": 30 },
			{ "data": 0.0, "weight": 70 }
		]
	}
}
```

A constant is written as a bare number, the way vanilla's `compostable/always_add_one.json` is:

```kotlin
numberProvider("always_add_one", 1f) // 1.0
```

The vanilla files are listed in `NumberProviders`, e.g. `NumberProviders.Compostable.LOW` for `minecraft:compostable/low`. Number providers, including the ones of other number provider files, can reference a file by its ID with `reference(id)`, see [Number provider IDs](#number-provider-ids). A file itself can't be a bare reference because the game reads it as an inline provider, so Kore throws when generating one.

## Entries

Entries define what can be selected during a pool roll.
There are singleton entries (yield items) and composite entries (combine other entries).

### Singleton Entries

#### Item Entry

Drops a specific item:

```kotlin
entries {
	item(Items.DIAMOND) {
		weight = 1
		quality = 2
		condition {
			randomChance(0.5f)
		}
		modifier {
			setCount(uniform(1f, 3f))
		}
	}
}
```

The entry's `condition` and `modifier` live in the `features.loottables.entries` package, the pool's and table's in
`features.loottables`. The loot table, item modifier and predicate builders are DSL-scoped, so calling a builder that
only an enclosing pool or table has fails to compile rather than silently adding to that pool: import the entry's
version when the compiler reports it.

#### Loot Table Entry

References another loot table:

```kotlin
entries {
	lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
		weight = 1
		modifier {
			setCount(2f)
		}
	}
}
```

#### Tag Entry

Drops items from an item tag:

```kotlin
entries {
	tag(Tags.Item.ARROWS) {
		expand = true  // Each item becomes a separate entry
		weight = 1
	}
}
```

#### Dynamic Entry

For block-specific drops (decorated pot sherds or shulkers contents):

```kotlin
entries {
	dynamic(LootEntryDynamicName.SHERDS) {
		// Drops contents of decorated pot sherds
	}
}
```

#### Empty Entry

A weighted entry that drops nothing (useful for rarity):

```kotlin
entries {
	empty(weight = 10) // 10x more likely than weight=1 entries
}
```

### Composite Entries

#### Alternatives

Selects the first entry whose condition passes:

```kotlin
entries {
	alternatives {
		children {
			item(Items.DIAMOND) {
				condition {
					randomChance(0.1f)
				}
			}
			item(Items.GOLD_INGOT) {
				condition {
					randomChance(0.3f)
				}
			}
			item(Items.IRON_INGOT)  // Fallback
		}
	}
}
```

#### Group

All children are added to the pool if the condition passes. Composite entries take a `modifier` like singleton entries:

```kotlin
entries {
	group {
		condition {
			weatherCheck(raining = true)
		}
		modifier {
			setCount(2f)
		}
		children {
			item(Items.WATER_BUCKET)
			item(Items.COD)
		}
	}
}
```

#### Sequence

Children are added until one fails its condition:

```kotlin
entries {
	sequence {
		children {
			item(Items.DIAMOND) {
				condition { randomChance(0.5f) }
			}
			item(Items.EMERALD) {
				condition { randomChance(0.5f) }
			}
			item(Items.GOLD_INGOT)
		}
	}
}
```

#### Slots Entry

Selects items from inventory slots specified by a [slot source](/docs/data-driven/slot-sources):

```kotlin
entries {
	slots {
		slotSources {
			slotRange(SlotSourceOrigin.THIS, HOTBAR)
		}
	}
}
```

### Entry Properties

Singleton entries share these properties:

| Property    | Type                     | Description                                              |
|-------------|--------------------------|----------------------------------------------------------|
| `weight`    | `Int`                    | Selection weight (higher = more likely)                  |
| `quality`   | `Int`                    | Modifies weight based on luck: `weight + quality × luck` |
| `condition` | `PredicateCondition?`    | Entry only available if the condition passes             |
| `modifier`  | `InlinableItemModifier?` | Item modifier applied to this entry's items              |

`alternatives`, `group` and `sequence` entries have `condition` and `modifier` too.

## Conditions (Predicates)

A pool or an entry holds one `condition`. Several conditions declared in its block are wrapped in an `all_of`, the same way as in a [predicate](/docs/data-driven/predicates) file.

```kotlin
pool {
	condition {
		// Multiple conditions are AND-ed in an all_of
		weatherCheck(raining = true)
		randomChance(0.5f)

		// Check entity properties
		entityProperties {
			entityType(EntityTypes.PLAYER)
		}

		// Check killer
		killedByPlayer()

		// Check tool
		matchTool {
			items = listOf(Items.DIAMOND_PICKAXE)
		}
	}
}
```

A pool or an entry can also point at a predicate file by ID, written as the bare ID string:

```kotlin
val isNight = predicate("is_night") { timeCheck(WorldClocks.OVERWORLD, 13000f..23000f) }

pool {
	condition(isNight)  // "condition": "my_pack:is_night"
}
```

See [Predicates](/docs/data-driven/predicates) for the complete list of available conditions.

## Modifiers (Item Modifiers)

A `modifier` transforms the generated items. It can be set at table, pool, or entry level.

### Common Functions

```kotlin
modifier {
	// Set stack count
	setCount(uniform(1f, 5f))

	// Apply enchantments
	enchantRandomly(Enchantments.FORTUNE)

	// Set damage (durability)
	setDamage(0.8f)

	// Add custom name
	setName("Legendary Sword")

	// Conditional function
	setCount(10f) {
		condition {
			killedByPlayer()
		}
	}
}
```

A single function is written as an object, several ones as a list. See [Item Modifiers](/docs/data-driven/item-modifiers) for the complete list of available functions.

## Full Example

```kotlin
dataPack("treasure_hunt") {
	// Custom boss drop table
	lootTable("boss_drops") {
		type = LootTableType.ENTITY

		// Global enchantment on all drops
		modifier {
			enchantRandomly()
		}

		// Guaranteed drops
		pool {
			rolls = constant(1f)

			entries {
				item(Items.NETHER_STAR)
			}
		}

		// Rare equipment drops
		pool {
			rolls = constant(1f)
			bonusRolls = constant(0.5f)

			condition {
				killedByPlayer()
			}

			entries {
				item(Items.NETHERITE_SWORD) {
					weight = 1
					modifier {
						enchantWithLevels(levels = constant(30f))
						setName("Boss Slayer")
					}
				}
				item(Items.DIAMOND_SWORD) {
					weight = 5
					modifier {
						enchantWithLevels(levels = uniform(15f, 25f))
					}
				}
				empty(weight = 10)
			}
		}

		// Bonus loot from existing table
		pool {
			rolls = uniform(1f, 3f)

			entries {
				lootTable(LootTables.Chests.END_CITY_TREASURE)
			}
		}
	}
}
```

### Generated JSON

```json
{
	"type": "minecraft:entity",
	"modifier": {
		"type": "minecraft:enchant_randomly"
	},
	"pools": [
		{
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:nether_star"
				}
			],
			"rolls": 1.0
		},
		{
			"bonus_rolls": 0.5,
			"condition": {
				"type": "minecraft:killed_by_player"
			},
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:netherite_sword",
					"weight": 1,
					"modifier": [
						{
							"type": "minecraft:enchant_with_levels",
							"levels": 30.0
						},
						{
							"type": "minecraft:set_name",
							"name": "Boss Slayer"
						}
					]
				},
				{
					"type": "minecraft:item",
					"name": "minecraft:diamond_sword",
					"weight": 5,
					"modifier": {
						"type": "minecraft:enchant_with_levels",
						"levels": {
							"type": "minecraft:uniform",
							"min": 15.0,
							"max": 25.0
						}
					}
				},
				{
					"type": "minecraft:empty",
					"weight": 10
				}
			],
			"rolls": 1.0
		},
		{
			"entries": [
				{
					"type": "minecraft:loot_table",
					"name": "minecraft:chests/end_city_treasure"
				}
			],
			"rolls": {
				"type": "minecraft:uniform",
				"min": 1.0,
				"max": 3.0
			}
		}
	]
}
```

## Using with Commands

Spawn loot from a table using the `/loot` command. The surrounding command builders are documented
in [Commands](/docs/commands/commands), while reusable conditions usually come
from [Predicates](/docs/data-driven/predicates):

```kotlin
load {
	// Give loot directly to player
	loot(self(), myLootTable)

	// Spawn loot at position
	loot {
		target { spawn(vec3(0, 64, 0)) }
		source { loot(myLootTable) }
	}

	// Insert loot into the container at 0 64 0
	loot {
		target { insert(vec3(0, 64, 0)) }
		source { loot(myLootTable) }
	}
}
```

## Overriding Vanilla Tables

To modify vanilla loot tables, create a file with the same path in your datapack. You can combine that
with [Item Modifiers](/docs/data-driven/item-modifiers) when you want reusable post-processing logic instead of
duplicating functions inside every pool:

```kotlin
dataPack("better_zombies") {
	// This overrides minecraft:entities/zombie
	lootTable("entities/zombie") {
		namespace = "minecraft"

		pool {
			rolls = constant(1f)

			entries {
				item(Items.ROTTEN_FLESH) {
					modifier {
						setCount(uniform(0f, 2f))
						enchantedCountIncrease(Enchantments.LOOTING, uniform(0f, 1f))
					}
				}
			}
		}

		// Add custom drops
		pool {
			rolls = constant(1f)

			condition {
				randomChance(0.1f)
			}

			entries {
				item(Items.DIAMOND)
			}
		}
	}
}
```

## Best Practices

1. **Use appropriate types** - Set the `type` field to catch invalid context parameter usage early
2. **Organize with multiple pools** - Use separate pools for different drop categories (guaranteed, rare, conditional)
3. **Leverage composition** - Reference other loot tables instead of duplicating entries
4. **Weight appropriately** - Use meaningful weights (e.g., 1/5/10/50) for clear rarity tiers
5. **Apply conditions at the right level** - Pool conditions for entire categories, entry conditions for specific items

## See Also

- [Item Modifiers](/docs/data-driven/item-modifiers) - Functions applied to loot table items
- [Advancements](/docs/data-driven/advancements) - Rewards can reference loot tables
- [Tags](/docs/data-driven/tags) - Use item tags for tag entries

### External Resources

- [Minecraft Wiki: Loot table](https://minecraft.wiki/w/Loot_table) - Official JSON format reference
- [Minecraft Wiki: Loot context](https://minecraft.wiki/w/Loot_context) - Understanding loot contexts
