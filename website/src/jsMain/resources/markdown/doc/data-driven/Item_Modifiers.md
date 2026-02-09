---
root: .components.layouts.MarkdownLayout
title: Item Modifiers
nav-title: Item Modifiers
description: Transform item stacks using Kore's type-safe DSL for loot functions - set counts, add enchantments, copy data, and more.
keywords: minecraft, datapack, kore, item modifiers, loot functions, /item modify, components
date-created: 2025-08-11
date-modified: 2026-02-03
routeOverride: /docs/data-driven/item-modifiers
---

# Item Modifiers

Item modifiers (also called loot functions) transform item stacks by adjusting counts, adding enchantments, copying data, setting components, and more. They can be defined as standalone JSON files referenced by commands, or used inline within loot tables.

## Overview

Item modifiers have several key characteristics:

- **Composable**: Chain multiple functions together for complex transformations
- **Conditional**: Each function can have predicate conditions
- **Context-aware**: Access loot context for killer, tool, block entity, etc.
- **Reusable**: Define once as a file, reference anywhere

### Common Use Cases

| Use Case             | Functions                                                 |
|----------------------|-----------------------------------------------------------|
| Set stack count      | `setCount`                                                |
| Add enchantments     | `enchantRandomly`, `enchantWithLevels`, `setEnchantments` |
| Modify durability    | `setDamage`                                               |
| Copy NBT/components  | `copyComponents`, `copyCustomData`, `copyName`            |
| Set name/lore        | `setName`, `setLore`                                      |
| Create explorer maps | `explorationMap`                                          |
| Fill containers      | `setContents`, `setLootTable`                             |
| Apply formulas       | `applyBonus`, `enchantedCountIncrease`                    |

## File Structure

Item modifiers are stored as JSON files in data packs at:

```
data/<namespace>/item_modifier/<name>.json
```

For complete JSON specification, see the [Minecraft Wiki - Item modifier](https://minecraft.wiki/w/Item_modifier).

## Creating Item Modifiers

Use the `itemModifier` builder function to create item modifiers in Kore:

```kotlin
dataPack("my_datapack") {
	val modifier = itemModifier("fortune_bonus") {
		enchantRandomly {
			options += Enchantments.FORTUNE
		}
		setCount(uniform(1f, 5f))
	}
}
```

This generates `data/my_datapack/item_modifier/fortune_bonus.json`.

## Using Item Modifiers

### With Commands

Apply modifiers to items using the `/item modify` command:

```kotlin
load {
	items {
		// Modify item in player's mainhand
		modify(self(), WEAPON.MAINHAND, modifier)

		// Modify item in container
		modify(block(0, 64, 0), slot(0), modifier)
	}
}
```

### In Loot Tables

Use functions directly in loot tables at table, pool, or entry level:

```kotlin
lootTable("treasure") {
	// Table-level functions (applied to all drops)
	functions {
		enchantRandomly()
	}

	pool {
		// Pool-level functions
		functions {
			setCount(uniform(1f, 3f))
		}

		entries {
			item(Items.DIAMOND) {
				// Entry-level functions
				functions {
					setName("Lucky Diamond")
				}
			}
		}
	}
}
```

## Function Reference

### Count and Damage

#### setCount

Sets or modifies the stack count:

```kotlin
itemModifier("set_count") {
	// Exact count
	setCount(5f)

	// Random range
	setCount(uniform(1f, 10f))

	// Add to current count
	setCount(5f, add = true)

	// With condition
	setCount(10f) {
		conditions {
			killedByPlayer()
		}
	}
}
```

#### setDamage

Sets item durability (1.0 = full, 0.0 = broken):

```kotlin
itemModifier("damage") {
	// Set to 80% durability
	setDamage(0.8f)

	// Random damage
	setDamage(uniform(0.5f, 1.0f))

	// Add to current damage
	setDamage(-0.1f, add = true)  // Repair 10%
}
```

#### limitCount

Clamps stack count to a range:

```kotlin
itemModifier("limit") {
	// Exact limit
	limitCount(64)

	// Range
	limitCount(providersRange(min = constant(1f), max = constant(32f)))
}
```

### Enchantments

#### enchantRandomly

Adds a random enchantment:

```kotlin
itemModifier("random_enchant") {
	// Any enchantment
	enchantRandomly()

	// From specific list
	enchantRandomly {
		options += Enchantments.SHARPNESS
		options += Enchantments.SMITE
		options += Enchantments.BANE_OF_ARTHROPODS
	}

	// Only compatible enchantments
	enchantRandomly(onlyCompatible = true)
}
```

#### enchantWithLevels

Enchants as if using an enchanting table:

```kotlin
itemModifier("table_enchant") {
	// Fixed level
	enchantWithLevels(levels = constant(30f))

	// Random level range
	enchantWithLevels(levels = uniform(20f, 39f))

	// Limit to specific enchantments
	enchantWithLevels(Enchantments.PROTECTION, levels = constant(30f))
}
```

#### setEnchantments

Sets specific enchantments and levels:

```kotlin
itemModifier("specific_enchants") {
	setEnchantments {
		enchantment(Enchantments.SHARPNESS, 5)
		enchantment(Enchantments.UNBREAKING, 3)
		enchantment(Enchantments.MENDING, 1)
	}
}
```

#### enchantedCountIncrease

Increases count based on enchantment level (like Looting):

```kotlin
itemModifier("looting_bonus") {
	enchantedCountIncrease(Enchantments.LOOTING, count = 1f, limit = 5)
}
```

### Names and Lore

#### setName

Sets the item's display name:

```kotlin
itemModifier("named") {
	// Simple string
	setName("Legendary Sword")

	// Text component with formatting
	setName(textComponent("Legendary Sword") {
		color = Color.GOLD
		bold = true
	})

	// Set item name vs custom name
	setName("Base Name") {
		target = SetNameTarget.ITEM_NAME  // or CUSTOM_NAME
	}
}
```

#### setLore

Sets or modifies item lore:

```kotlin
itemModifier("lore") {
	setLore {
		lore("First line", Color.GRAY)
		lore("Second line", Color.DARK_GRAY)

		// Insert at specific position
		mode(Mode.INSERT, offset = 0)
	}
}
```

### Components

#### setComponents

Directly set item components:

```kotlin
itemModifier("components") {
	setComponents {
		customName(textComponent("Custom Item", Color.GOLD))
		damage(10)
		unbreakable(showInTooltip = false)

		// Remove component with !
		!food {}
	}
}
```

#### copyComponents

Copy components from a source:

```kotlin
itemModifier("copy_from_block") {
	copyComponents {
		source = Source.BLOCK_ENTITY

		// Include specific components
		include(ItemComponentTypes.CUSTOM_NAME, ItemComponentTypes.LORE)

		// Or exclude specific components
		exclude(ItemComponentTypes.DAMAGE)
	}
}
```

#### copyName

Copy entity/block name to item:

```kotlin
itemModifier("named_drop") {
	copyName(Source.BLOCK_ENTITY)
	// Or from entity
	copyName(Source.THIS)
	copyName(Source.KILLER)
}
```

#### copyCustomData

Copy NBT data to custom_data component:

```kotlin
itemModifier("copy_nbt") {
	copyCustomData {
		source(Source.BLOCK_ENTITY)

		operations {
			operation("Items", "BlockItems", CopyOperation.REPLACE)
			operation("Lock", "OriginalLock", CopyOperation.MERGE)
		}
	}
}
```

### Container Contents

#### setContents

Fill container items (bundles, shulker boxes):

```kotlin
itemModifier("filled_bundle") {
	setContents(ContentComponentTypes.BUNDLE_CONTENTS) {
		entries {
			item(Items.DIAMOND) {
				functions {
					setCount(16f)
				}
			}
			item(Items.EMERALD) {
				functions {
					setCount(32f)
				}
			}
		}
	}
}
```

#### setLootTable

Set a container's loot table:

```kotlin
itemModifier("chest_loot") {
	setLootTable(
		LootTableType.CHEST,
		LootTables.Chests.SIMPLE_DUNGEON,
		seed = 12345L
	)
}
```

#### modifyContents

Apply modifiers to items inside a container:

```kotlin
itemModifier("enchant_bundle_contents") {
	modifyContents(ContentComponentTypes.BUNDLE_CONTENTS) {
		modifiers {
			enchantRandomly()
		}
	}
}
```

### Maps and Exploration

#### explorationMap

Convert empty map to explorer map:

```kotlin
itemModifier("treasure_map") {
	explorationMap {
		destination = Tags.Worldgen.Structure.BURIED_TREASURE
		decoration = MapDecorationTypes.RED_X
		zoom = 2
		searchRadius = 50
		skipExistingChunks = true
	}
}
```

### Special Items

#### setInstrument

Set goat horn instrument:

```kotlin
itemModifier("horn") {
	setInstrument(Tags.Instrument.GOAT_HORNS)
}
```

#### setPotion

Set potion type:

```kotlin
itemModifier("potion") {
	setPotion(Potions.STRONG_HEALING)
}
```

#### setStewEffect

Set suspicious stew effects:

```kotlin
itemModifier("stew") {
	setStewEffect {
		potionEffect(Effects.REGENERATION, duration = 100)
		potionEffect(Effects.SATURATION, duration = 200)
	}
}
```

#### setFireworks

Configure firework rocket:

```kotlin
itemModifier("firework") {
	setFireworks(flightDuration = 2) {
		explosions {
			explosion(FireworkExplosionShape.LARGE_BALL) {
				colors(Color.RED, Color.ORANGE)
				fadeColors(Color.YELLOW)
				hasTrail = true
				hasTwinkle = true
			}
		}
	}
}
```

#### setBookCover

Set written book cover:

```kotlin
itemModifier("book") {
	setBookCover(
		title = "Adventure Log",
		author = "Player",
		generation = 0
	)
}
```

### Attributes

#### setAttributes

Add attribute modifiers:

```kotlin
itemModifier("buffed") {
	setAttributes {
		attribute(
			attribute = Attributes.ATTACK_DAMAGE,
			operation = AttributeModifierOperation.ADD_VALUE,
			amount = constant(5f),
			id = "bonus_damage",
			slot = EquipmentSlot.MAINHAND
		)
		attribute(
			attribute = Attributes.MOVEMENT_SPEED,
			operation = AttributeModifierOperation.ADD_MULTIPLIED_BASE,
			amount = constant(0.1f),
			id = "speed_boost",
			slot = EquipmentSlot.FEET
		)
	}
}
```

### Banners and Patterns

#### setBannerPattern

Add banner patterns:

```kotlin
itemModifier("banner") {
	setBannerPattern(append = true) {
		pattern(BannerPatterns.STRIPE_TOP, DyeColors.RED)
		pattern(BannerPatterns.STRIPE_BOTTOM, DyeColors.BLUE)
	}
}
```

### Block State

#### copyState

Copy block state to item:

```kotlin
itemModifier("block_state") {
	copyState(Blocks.FURNACE) {
		properties("facing", "lit")
	}
}
```

### Bonus Formulas

#### applyBonus

Apply enchantment-based bonus formulas:

```kotlin
itemModifier("fortune") {
	// Ore drops formula
	applyBonus(Enchantments.FORTUNE) {
		formula = OreDrops()
	}

	// Uniform bonus
	applyBonus(Enchantments.FORTUNE) {
		formula = UniformBonusCount(bonusMultiplier = 1f)
	}

	// Binomial distribution
	applyBonus(Enchantments.FORTUNE) {
		formula = BinomialWithBonusCount(extra = 3, probability = 0.5f)
	}
}
```

### Smelting and Decay

#### furnaceSmelt

Smelt the item as if in a furnace:

```kotlin
itemModifier("auto_smelt") {
	furnaceSmelt()
}
```

#### explosionDecay

Random chance to destroy items based on explosion:

```kotlin
itemModifier("explosion") {
	explosionDecay()
}
```

### Player Heads

#### fillPlayerHead

Set player head skin:

```kotlin
itemModifier("head") {
	fillPlayerHead(Source.KILLER)
}
```

### Tooltips

#### toggleTooltips

Show/hide tooltip sections:

```kotlin
itemModifier("clean_tooltip") {
	toggleTooltips {
		enchantments = false
		modifiers = false
		canBreak = false
		canPlaceOn = false
	}
}
```

### Composition

#### sequence

Run multiple functions in sequence:

```kotlin
itemModifier("complex") {
	sequence {
		setCount(1f)
		enchantRandomly()
		setName("Mystery Item")
	}
}
```

#### discard

Replaces the produced item stack with an empty one:

```kotlin
itemModifier("discard_example") {
	discard()
}
```

#### filtered

Apply functions depending on whether the item matches a filter:

```kotlin
itemModifier("filter") {
	filtered {
		itemFilter(Items.DIAMOND, Items.EMERALD)

		onFail {
			discard()
		}

		onPass {
			setCount(uniform(1f, 5f))
		}
	}
}
```

#### reference

Reference another item modifier:

```kotlin
val baseModifier = itemModifier("base") {
	setCount(1f)
}

itemModifier("extended") {
	reference(baseModifier)
	enchantRandomly()
}
```

## Conditions

Every function can have conditions that must pass:

```kotlin
itemModifier("conditional") {
	setCount(10f) {
		conditions {
			// Multiple conditions are AND-ed
			killedByPlayer()
			randomChance(0.5f)
		}
	}

	enchantRandomly {
		conditions {
			weatherCheck(raining = true)
		}
	}
}
```

See [Predicates](./predicates) for all available conditions.

## Full Example

```kotlin
dataPack("legendary_items") {
	val legendaryModifier = itemModifier("legendary_weapon") {
		// High-level enchantments
		enchantWithLevels(levels = constant(30f)) {
			conditions {
				randomChance(0.3f)
			}
		}

		// Guaranteed enchantments
		setEnchantments {
			enchantment(Enchantments.UNBREAKING, 3)
		}

		// Custom name with formatting
		setName(textComponent("Legendary Weapon") {
			color = Color.GOLD
			bold = true
		})

		// Lore
		setLore {
			lore("Forged in ancient flames", Color.GRAY)
			lore("", Color.WHITE)
			lore("▸ +5 Attack Damage", Color.GREEN)
			mode(Mode.REPLACE_ALL)
		}

		// Attributes
		setAttributes(replace = false) {
			attribute(
				attribute = Attributes.ATTACK_DAMAGE,
				operation = AttributeModifierOperation.ADD_VALUE,
				amount = constant(5f),
				id = "legendary_damage",
				slot = EquipmentSlot.MAINHAND
			)
		}

		// Full durability
		setDamage(1.0f)
	}

	// Use in loot table
	lootTable("boss_weapon") {
		pool {
			rolls = constant(1f)

			entries {
				item(Items.DIAMOND_SWORD) {
					functions {
						reference(legendaryModifier)
					}
				}
			}
		}
	}

	// Use with command
	load {
		items {
			modify(self(), WEAPON.MAINHAND, legendaryModifier)
		}
	}
}
```

### Generated JSON

```json
[
	{
		"function": "minecraft:enchant_with_levels",
		"levels": 30.0,
		"conditions": [
			{
				"condition": "minecraft:random_chance",
				"chance": 0.3
			}
		]
	},
	{
		"function": "minecraft:set_enchantments",
		"enchantments": {
			"minecraft:unbreaking": 3
		}
	},
	{
		"function": "minecraft:set_name",
		"name": {
			"text": "Legendary Weapon",
			"color": "gold",
			"bold": true
		}
	},
	{
		"function": "minecraft:set_lore",
		"lore": [
			{
				"text": "Forged in ancient flames",
				"color": "gray"
			},
			{
				"text": ""
			},
			{
				"text": "▸ +5 Attack Damage",
				"color": "green"
			}
		],
		"mode": "replace_all"
	},
	{
		"function": "minecraft:set_attributes",
		"modifiers": [
			{
				"attribute": "minecraft:attack_damage",
				"id": "minecraft:legendary_damage",
				"amount": 5.0,
				"operation": "add_value",
				"slot": "mainhand"
			}
		],
		"replace": false
	},
	{
		"function": "minecraft:set_damage",
		"damage": 1.0
	}
]
```

## Best Practices

1. **Keep modifiers focused** - Create small, reusable modifiers and compose with `reference()`
2. **Use conditions wisely** - Guard expensive operations with appropriate conditions
3. **Prefer components** - Use `setComponents` for direct component manipulation when possible
4. **Consider context** - Some functions require specific loot contexts (killer, tool, etc.)
5. **Test thoroughly** - Verify modifiers work in all intended contexts (loot, commands, etc.)

## See Also

- [Predicates](/docs/data-driven/predicates) - Conditions for item functions
- [Components](/docs/concepts/components) - Understanding item components
- [Loot Tables](/docs/data-driven/loot-tables) - Use item modifiers in loot tables
- [Commands](/docs/data-driven/commands/commands) - Using the `/item` command

### External Resources

- [Minecraft Wiki: Item modifier](https://minecraft.wiki/w/Item_modifier) - Official JSON format reference
