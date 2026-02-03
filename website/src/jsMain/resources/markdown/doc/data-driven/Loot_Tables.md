---
root: .components.layouts.MarkdownLayout
title: Loot Tables
nav-title: Loot Tables
description: Create and customize Minecraft loot tables using Kore's type-safe Kotlin DSL for drops, container contents, fishing, and more.
keywords: minecraft, datapack, kore, loot tables, pools, entries, item modifiers, drops
date-created: 2025-08-11
date-modified: 2026-02-03
routeOverride: /docs/data-driven/loot-tables
---

# Loot Tables

Loot tables are JSON files that dictate what items should generate in various game situations. They control drops from mobs and blocks, contents of naturally generated containers (chests, barrels, dispensers), fishing rewards, archaeology brushing results, bartering exchanges, and more.

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

## Table Structure

A loot table consists of:

| Property         | Type                     | Description                                         |
|------------------|--------------------------|-----------------------------------------------------|
| `type`           | `LootTableType`          | Optional context type for validation                |
| `pools`          | `List<LootPool>`         | One or more pools that generate items               |
| `functions`      | `ItemModifier`           | Optional global item functions applied to all drops |
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

Available types: `ADVANCEMENT_ENTITY`, `ADVANCEMENT_LOCATION`, `ADVANCEMENT_REWARD`, `ARCHEOLOGY`, `BARTER`, `BLOCK`, `BLOCK_USE`,
`CHEST`, `COMMAND`, `EMPTY`, `ENTITY`, `EQUIPMENT`, `FISHING`, `GIFT`, `GENERIC`, `SELECTOR`, `SHEARING`, `VAULT`.

### Global Functions

Apply item modifier functions to all items dropped by the table:

```kotlin
lootTable("enchanted_loot") {
	functions {
		enchantRandomly {
			options += Enchantments.LOOTING
		}
	}

	pool {
		entries {
			item(Items.DIAMOND_SWORD)
		}
	}
}
```

## Pools

Each pool represents an independent set of rolls. A table can have multiple pools that all contribute to the final loot.

### Pool Properties

| Property     | Type              | Description                                    |
|--------------|-------------------|------------------------------------------------|
| `rolls`      | `NumberProvider`  | How many times to select from entries          |
| `bonusRolls` | `NumberProvider`  | Additional rolls per luck level                |
| `conditions` | `Predicate`       | Conditions that must pass for pool to activate |
| `entries`    | `List<LootEntry>` | Possible entries to select from                |
| `functions`  | `ItemModifier`    | Functions applied to items from this pool      |

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

		conditions {
			weatherCheck(raining = true)
		}

		entries {
			item(Items.WATER_BUCKET)
		}
	}
}
```

### Pool with Functions

```kotlin
lootTable("modified_drops") {
	pool {
		rolls = constant(1f)

		entries {
			item(Items.DIAMOND_PICKAXE)
		}

		functions {
			setDamage(0.5f)
			enchantWithLevels(levels = constant(30f))
		}
	}
}
```

## Number Providers

Number providers determine dynamic numeric values for rolls, counts, and other quantities.

| Provider                | Description                | Example                                 |
|-------------------------|----------------------------|-----------------------------------------|
| `constant(value)`       | Fixed value                | `constant(5f)`                          |
| `uniform(min, max)`     | Random between min and max | `uniform(1f, 5f)`                       |
| `binomial(n, p)`        | Binomial distribution      | `binomial(5, 0.5f)`                     |
| `scoreNumber(...)`      | Value from scoreboard      | `scoreNumber("kills", EntityType.THIS)` |
| `enchantmentLevel(...)` | Based on enchantment level | `enchantmentLevel(5)`                   |

```kotlin
pool {
	rolls = uniform(2f, 5f)  // 2-5 rolls randomly
	bonusRolls = constant(1f)  // +1 roll per luck level

	entries {
		item(Items.GOLD_INGOT)
	}
}
```

## Entries

Entries define what can be selected during a pool roll. There are singleton entries (yield items) and composite entries (combine other entries).

### Singleton Entries

#### Item Entry

Drops a specific item:

```kotlin
entries {
	item(Items.DIAMOND) {
		weight = 1
		quality = 2
		conditions {
			randomChance(0.5f)
		}
		functions {
			setCount(uniform(1f, 3f))
		}
	}
}
```

#### Loot Table Entry

References another loot table:

```kotlin
entries {
	lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
		weight = 1
		functions {
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

For block-specific drops (shulker box contents, decorated pot sherds):

```kotlin
entries {
	dynamic("contents") {
		// Drops contents of shulker boxes
	}
}
```

#### Empty Entry

A weighted entry that drops nothing (useful for rarity):

```kotlin
entries {
	empty {
		weight = 10  // 10x more likely than weight=1 entries
	}
}
```

### Composite Entries

#### Alternatives

Selects the first entry whose conditions pass:

```kotlin
entries {
	alternatives {
		children {
			item(Items.DIAMOND) {
				conditions {
					randomChance(0.1f)
				}
			}
			item(Items.GOLD_INGOT) {
				conditions {
					randomChance(0.3f)
				}
			}
			item(Items.IRON_INGOT)  // Fallback
		}
	}
}
```

#### Group

All children are added to the pool if conditions pass:

```kotlin
entries {
	group {
		conditions {
			weatherCheck(raining = true)
		}
		children {
			item(Items.WATER_BUCKET)
			item(Items.FISH)
		}
	}
}
```

#### Sequence

Children are added until one fails its conditions:

```kotlin
entries {
	sequence {
		children {
			item(Items.DIAMOND) {
				conditions { randomChance(0.5f) }
			}
			item(Items.EMERALD) {
				conditions { randomChance(0.5f) }
			}
			item(Items.GOLD_INGOT)
		}
	}
}
```

### Entry Properties

Singleton entries share these properties:

| Property     | Type           | Description                                              |
|--------------|----------------|----------------------------------------------------------|
| `weight`     | `Int`          | Selection weight (higher = more likely)                  |
| `quality`    | `Int`          | Modifies weight based on luck: `weight + quality × luck` |
| `conditions` | `Predicate`    | Entry only available if conditions pass                  |
| `functions`  | `ItemModifier` | Functions applied to this entry's items                  |

## Conditions (Predicates)

Conditions control when pools, entries, or functions apply. They use the same Predicate system as advancements.

```kotlin
pool {
	conditions {
		// Multiple conditions are AND-ed
		weatherCheck(raining = true)
		randomChance(0.5f)

		// Check entity properties
		entityProperties {
			type(EntityTypes.PLAYER)
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

See [Predicates](./predicates) for the complete list of available conditions.

## Functions (Item Modifiers)

Functions transform the generated items. They can be applied at table, pool, or entry level.

### Common Functions

```kotlin
functions {
	// Set stack count
	setCount(uniform(1f, 5f))

	// Apply enchantments
	enchantRandomly {
		options += Enchantments.FORTUNE
	}

	// Set damage (durability)
	setDamage(0.8f)

	// Add custom name
	setName("Legendary Sword")

	// Conditional function
	setCount(10f) {
		conditions {
			killedByPlayer()
		}
	}
}
```

See [Item Modifiers](./item-modifiers) for the complete list of available functions.

## Full Example

```kotlin
dataPack("treasure_hunt") {
	// Custom boss drop table
	lootTable("boss_drops") {
		type = LootTableType.ENTITY

		// Global enchantment on all drops
		functions {
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

			conditions {
				killedByPlayer()
			}

			entries {
				item(Items.NETHERITE_SWORD) {
					weight = 1
					functions {
						enchantWithLevels(levels = constant(30f))
						setName("Boss Slayer")
					}
				}
				item(Items.DIAMOND_SWORD) {
					weight = 5
					functions {
						enchantWithLevels(levels = uniform(15f, 25f))
					}
				}
				empty {
					weight = 10
				}
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
  "functions": [
	  {
		  "function": "minecraft:enchant_randomly"
	  }
  ],
  "pools": [
    {
	    "rolls": 1.0,
	    "entries": [
		    {
			    "type": "minecraft:item",
			    "name": "minecraft:nether_star"
		    }
	    ]
    },
	  {
		  "rolls": 1.0,
		  "bonus_rolls": 0.5,
      "conditions": [
	      {
		      "condition": "minecraft:killed_by_player"
	      }
      ],
      "entries": [
        {
	        "type": "minecraft:item",
	        "name": "minecraft:netherite_sword",
	        "weight": 1,
          "functions": [
	          {
		          "function": "minecraft:enchant_with_levels",
		          "levels": 30.0
	          },
	          {
		          "function": "minecraft:set_name",
		          "name": "Boss Slayer"
	          }
          ]
        },
	      {
		      "type": "minecraft:item",
		      "name": "minecraft:diamond_sword",
		      "weight": 5,
		      "functions": [
			      {
				      "function": "minecraft:enchant_with_levels",
				      "levels": {
					      "type": "minecraft:uniform",
					      "min": 15.0,
					      "max": 25.0
				      }
			      }
		      ]
	      },
	      {
		      "type": "minecraft:empty",
		      "weight": 10
	      }
      ]
	  },
	  {
		  "rolls": {
			  "type": "minecraft:uniform",
			  "min": 1.0,
			  "max": 3.0
		  },
		  "entries": [
			  {
				  "type": "minecraft:loot_table",
				  "name": "minecraft:chests/end_city_treasure"
			  }
      ]
    }
  ]
}
```

## Using with Commands

Spawn loot from a table using the `/loot` command:

```kotlin
load {
	// Give loot directly to player
	loot(self(), myLootTable)

	// Spawn loot at position
	loot(vec3(0, 64, 0), myLootTable)

	// Insert loot into container
	loot(block(0, 64, 0), myLootTable)
}
```

## Overriding Vanilla Tables

To modify vanilla loot tables, create a file with the same path in your datapack:

```kotlin
dataPack("better_zombies") {
	// This overrides minecraft:entities/zombie
	lootTable("entities/zombie") {
		namespace = "minecraft"

		pool {
			rolls = constant(1f)

			entries {
				item(Items.ROTTEN_FLESH) {
					functions {
						setCount(uniform(0f, 2f))
						lootingEnchant(uniform(0f, 1f))
					}
				}
			}
		}

		// Add custom drops
		pool {
			rolls = constant(1f)

			conditions {
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

- [Predicates](./predicates) - Conditions used in loot table pools and entries
- [Item Modifiers](./item-modifiers) - Functions applied to loot table items
- [Advancements](./advancements) - Rewards can reference loot tables
- [Commands](../commands/commands) - Using the `/loot` command
- [Tags](./tags) - Use item tags for tag entries

### External Resources

- [Minecraft Wiki: Loot table](https://minecraft.wiki/w/Loot_table) - Official JSON format reference
- [Minecraft Wiki: Loot context](https://minecraft.wiki/w/Loot_context) - Understanding loot contexts
