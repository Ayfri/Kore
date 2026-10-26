---
root: .components.layouts.MarkdownLayout
title: Item Modifiers
nav-title: Item Modifiers
description: Transform item stacks using Kore's type-safe DSL for loot functions - set counts, add enchantments, copy data, and more.
keywords: minecraft, datapack, kore, item modifiers, loot functions, /item modify, components
date-created: 2025-08-11
date-modified: 2026-10-26
routeOverride: /docs/data-driven/item-modifiers
---

# Item Modifiers

Item modifiers (also called loot functions) transform item stacks by adjusting counts, adding enchantments, copying data, setting components, and more. They can be defined as standalone JSON files referenced by commands, or used inline within loot tables.

## Overview

Item modifiers have several key characteristics:

- **Composable**: Chain multiple functions together for complex transformations
- **Conditional**: Each function can have a predicate condition
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

A file holds one function object (`{ "type": "minecraft:set_count", ... }`) whose `type` names its kind. Several functions, or a lone reference to another item modifier (`"my_pack:base"`) or an item modifier tag (`"#my_pack:buffs"`), are written as a `minecraft:sequence`, which Kore does for you. An `itemModifier { }` without any function throws when the pack is generated.

For complete JSON specification, see the [Minecraft Wiki - Item modifier](https://minecraft.wiki/w/Item_modifier).

## Creating Item Modifiers

Use the `itemModifier` builder function to create item modifiers in Kore:

```kotlin
dataPack("my_datapack") {
	val modifier = itemModifier("fortune_bonus") {
		enchantRandomly(Enchantments.FORTUNE)
		setCount(1..5)
	}
}
```

This generates `data/my_datapack/item_modifier/fortune_bonus.json`.

## Using Item Modifiers

### With Commands

Apply modifiers to items using the `/item modify` command:

```kotlin
load {
	// Modify item in player's mainhand
	items.modify(self(), WEAPON.MAINHAND, modifier)

	// Modify item in the first slot of the container at 0 64 0
	items.modify(vec3(0, 64, 0), CONTAINER[0], modifier)

	// Inline modifier, no file needed
	items.modify(self(), WEAPON.MAINHAND) {
		setCount(2)
	}
}
```

An inline modifier is written in the command as SNBT and keeps the short forms a file doesn't have: one function as an object, several as a list, or an ID.

### In Loot Tables

Use functions directly in loot tables at table, pool, or entry level:

```kotlin
lootTable("treasure") {
	// Table-level modifier (applied to all drops)
	modifier {
		enchantRandomly()
	}

	pool {
		// Pool-level modifier
		modifier {
			setCount(1..3)
		}

		entries {
			item(Items.DIAMOND) {
				// Entry-level modifier
				modifier {
					setName("Lucky Diamond")
				}
			}
		}
	}
}
```

Each `modifier` also takes an item modifier ID, `modifier { reference(baseModifier) }`, see [reference](#reference).

## Function Reference

### Count and Damage

#### setCount

Sets or modifies the stack count:

```kotlin
itemModifier("set_count") {
	// Exact count
	setCount(5)

	// Random range, both ends included
	setCount(1..10)

	// Add to current count
	setCount(5, add = true)

	// With condition
	setCount(10) {
		condition {
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

	// Random damage, the end itself never being returned
	setDamage(0.5f..1.0f)

	// Add to current damage
	setDamage(-0.1f, add = true)  // Repair 10%
}
```

The numeric fields of item functions are [context providers](/docs/data-driven/loot-tables#context-providers): an int provider (`1`, `uniform(1, 5)`) or a float provider (`1f`, `uniform(0.5f, 1f)`) depending on the field, a fixed value being a plain number. Each also accepts the ID of a [context provider file](/docs/data-driven/loot-tables#context-provider-files) through `reference(...)`:

| Function                    | Field                         | Provider |
|-----------------------------|-------------------------------|----------|
| `setCount`                  | `count`                       | int      |
| `limitCount`                | `min`, `max`                  | int      |
| `enchantWithLevels`         | `levels`                      | int      |
| `setEnchantments`           | the level of each enchantment | int      |
| `setOminousBottleAmplifier` | `amplifier`                   | int      |
| `setRandomDyes`             | `numberOfDyes`                | int      |
| `setStewEffect`             | `duration` of each effect     | int      |
| `setCustomModelData`        | `colors`                      | int      |
| `setCustomModelData`        | `floats`                      | float    |
| `setDamage`                 | `damage`                      | float    |
| `setAttributes`             | `amount` of each modifier     | float    |
| `enchantedCountIncrease`    | `count`                       | float    |

A convenience overload takes a plain `Int` for an int field and a plain `Float` for a float field.

```kotlin
itemModifier("count_from_provider") {
	setCount(reference(ContextIntProviderArgument("n", "my_pack")))  // "count": "my_pack:n"
}
```

#### limitCount

Clamps stack count to a range. The game only reads the `{ min, max }` object, each bound being an int provider and either one optional:

```kotlin
itemModifier("limit") {
	// At most 64, no lower bound
	limitCount(max = constant(64))

	// Between 1 and 32
	limitCount(1..32)

	// Same, with two Int bounds
	limitCount(1, 32)

	// Bounds from providers
	limitCount(min = constant(1), max = score("cap", EntityTarget.THIS))
}
```

`limitCount(1..32)` clamps the count between the two bounds and draws nothing, unlike `setCount(1..32)`, which draws a uniform count. `limitCount(max = constant(64))` writes `"limit": { "max": 64 }`. A single `Int` isn't accepted, so `limitCount(64)` can't silently mean a lower bound: pass both bounds, or name the provider bound you set.

### Enchantments

#### enchantRandomly

Adds a random enchantment:

```kotlin
itemModifier("random_enchant") {
	// Any enchantment
	enchantRandomly()

	// From specific list
	enchantRandomly {
		enchantments {
			add(Enchantments.SHARPNESS)
			add(Enchantments.SMITE)
			add(Enchantments.BANE_OF_ARTHROPODS)
		}
	}

	// Only compatible enchantments
	enchantRandomly(onlyCompatible = true)

	// Include the additional cost component from trade costs
	enchantRandomly(Enchantments.SHARPNESS) {
		includeAdditionalCostComponent = true
	}
}
```

An empty enchantment list leaves `options` out of the JSON: the game reads `"options": []` as an empty candidate set and
leaves the item unenchanted.

#### enchantWithLevels

Enchants as if using an enchanting table:

```kotlin
itemModifier("table_enchant") {
	// Fixed level
	enchantWithLevels(levels = 30)

	// Random level range
	enchantWithLevels(levels = 20..39)

	// Limit to specific enchantments
	enchantWithLevels(Enchantments.PROTECTION, levels = 30)

	// Include the additional cost component from trade costs
	enchantWithLevels(levels = 30) {
		includeAdditionalCostComponent = true
	}
}
```

#### setEnchantments

Sets specific enchantments and levels:

```kotlin
itemModifier("specific_enchants") {
	setEnchantments {
		this[Enchantments.SHARPNESS] = 5
		this[Enchantments.UNBREAKING] = 3
		this[Enchantments.MENDING] = 1
	}
}
```

A level is a plain number, a range drawn uniformly (`this[Enchantments.SHARPNESS] = 1..3`) or an int provider (`this[Enchantments.SHARPNESS] = uniform(1, 3)`). The plain number and range forms need `import io.github.ayfri.kore.features.itemmodifiers.functions.set`, otherwise the standard library `set` rejects them with a type mismatch.

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
	setName(text("Legendary Sword", Color.GOLD) {
		bold = true
	})

	// Several components joined with +
	setName(textComponent("Legendary ", Color.GOLD) + textComponent("Sword"))

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
		unbreakable()

		// Remove component with !
		!ItemComponentTypes.FOOD
	}
}
```

#### copyComponents

Copy components from a source:

```kotlin
itemModifier("copy_from_block") {
	copyComponents {
		source = CopyComponentsSource.BLOCK_ENTITY

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
	copyName(Source.ATTACKER)
}
```

#### setCustomData

Set custom NBT data on an item:

```kotlin
itemModifier("custom_data") {
	setCustomData {
		this["test"] = 1
	}
}
```

#### setCustomModelData

Set custom model data on an item:

```kotlin
itemModifier("custom_model") {
	setCustomModelData(
		colors = listOf(Color.RED, Color.BLUE),
		flags = listOf(true, false),
		floats = listOf(1.0f, 2.0f),
		strings = listOf("test1", "test2")
	)
}
```

Each float is a [float provider](/docs/data-driven/loot-tables#context-providers) or a plain number, so `floats(0.5f, 1f)` writes fixed floats and `floats(...)` can also read one from a score (`fromInt` turns the int score into a float) or add
several sources with `add`:

```kotlin
itemModifier("model_from_score") {
	setCustomModelData {
		floats(add(fromInt(score("charge", "#gun")), constant(0.5f)))
	}
}
```

Each color is a fixed `Color`, a decimal RGB number, or an int provider computing that value:

```kotlin
itemModifier("model_colors") {
	setCustomModelData {
		colors(Color.RED, Color.BLUE)                // "values": [16733525, 5592575]
		colors(0xFF5555)                             // "values": [16733525]
		colors(score("tint", "#shirt"))              // "values": [{ "type": "minecraft:score", ... }]
	}
}
```

Each `colors(...)` call replaces the whole list. To mix fixed colors and providers in one list, set `colors` to a `ListOperation` of `CustomModelDataColor`.

#### copyCustomData

Copy NBT data to custom_data component:

```kotlin
itemModifier("copy_nbt") {
	copyCustomData(Source.BLOCK_ENTITY) {
		replace("Items", "BlockItems")
		merge("Lock", "OriginalLock")
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
				modifier {
					setCount(16)
				}
			}
			item(Items.EMERALD) {
				modifier {
					setCount(32)
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
	setLootTable(LootTables.Chests.SIMPLE_DUNGEON, seed = 42)
}
```

The `containerLoot` item component sets the same loot table.

#### modifyContents

Apply modifiers to items inside a container. `modifier { }` takes inline functions, or an item modifier ID with `reference(...)`:

```kotlin
itemModifier("enchant_bundle_contents") {
	modifyContents(ContentComponentTypes.BUNDLE_CONTENTS) {
		modifier {
			enchantRandomly()
		}
	}
}

itemModifier("modify_by_id") {
	modifyContents(ContentComponentTypes.CONTAINER) {
		modifier { reference(ItemModifierArgument("my_modifier", "my_pack")) }
	}
}
```

The second one writes `"modifier": "my_pack:my_modifier"`.

### Maps and Exploration

#### explorationMap

Add map data leading to a structure to the item the modifier applies to. The loot entry or trade picks the item (vanilla
uses `minecraft:buried_treasure_map`, `minecraft:desert_village_map`...). The destination is required: one or several
structures or structure tags. The `decoration` is the map decoration type, the icon marking the destination.

```kotlin
itemModifier("treasure_map") {
	explorationMap(
		Tags.Worldgen.Structure.ON_TREASURE_MAPS,
		decoration = MapDecorationTypes.RED_X,
		zoom = 1,
		searchRadius = 50,
		skipExistingChunks = false,
	)
}
```

A tag is written with its `#` (`"destination": "#minecraft:on_treasure_maps"`) and several structures as a list:

```kotlin
itemModifier("two_structures") {
	explorationMap(ConfiguredStructures.MINESHAFT, ConfiguredStructures.MONUMENT)
}
```

When no structure is found the item has no `map_id`, so `discard` removes it. Vanilla chest loot does it with a `filtered`
step:

```kotlin
itemModifier("treasure_map_or_nothing") {
	explorationMap(Tags.Worldgen.Structure.ON_TREASURE_MAPS, decoration = MapDecorationTypes.RED_X, zoom = 1)
	filtered {
		itemFilter { predicates { exists(ItemComponentTypes.MAP_ID) } }
		onFail { discard() }
	}
}
```

### Special Items

#### setInstrument

Set a goat horn instrument using a tag, a single ID, or multiple IDs/tags:

```kotlin
// tag
itemModifier("horn_tag") {
	setInstrument(Tags.Instrument.GOAT_HORNS)
}

// single ID
itemModifier("horn_id") {
  setInstrument(Instruments.ADMIRE_GOAT_HORN)
}

// list of IDs/tags
itemModifier("horn_list") {
  setInstrument(Instruments.ADMIRE_GOAT_HORN, Instruments.SING_GOAT_HORN)
}
```

#### setItem

Change the item type:

```kotlin
itemModifier("change_item") {
	setItem(Items.APPLE)
}
```

#### setOminousBottleAmplifier

Set the amplifier of an ominous bottle:

```kotlin
itemModifier("ominous_bottle") {
	// Fixed amplifier, written as the int provider 5
	setOminousBottleAmplifier(5)

	// Amplifier drawn from an int provider
	setOminousBottleAmplifier(uniform(2, 4))
}
```

#### setPotion

Set potion type:

```kotlin
itemModifier("potion") {
	setPotion(Potions.HEALING)
}
```

#### setRandomPotion

Set a random potion from a list of options (or tags):

```kotlin
itemModifier("random_potion") {
  // Random from specific list
  setRandomPotion(Potions.HEALING, Potions.SWIFTNESS)
}
```

#### setRandomDyes

Set a random number of dyes on the item using an int provider:

```kotlin
itemModifier("random_dyes") {
  // Fixed count
  setRandomDyes(3)

  // Dynamic count
  setRandomDyes(1..5)
}
```

#### setStewEffect

Set suspicious stew effects:

```kotlin
itemModifier("stew") {
	setStewEffect {
		potionEffect(Effects.NIGHT_VISION, 200)
	}
}

itemModifier("single_stew") {
	setStewEffect(Effects.JUMP_BOOST, 160)  // "duration": 160
}
```

#### setFireworkExplosion

Set a single firework explosion:

```kotlin
itemModifier("firework_explosion") {
	setFireworkExplosion(FireworkExplosionShape.STAR) {
		colors = listOf(Color.RED.toRGB())
		fadeColors = listOf(Color.BLUE.toRGB())
		hasTwinkle = true
		hasTrail = true
	}
}
```

#### setFireworks

Configure firework rocket:

```kotlin
itemModifier("firework") {
	setFireworks {
		flightDuration = 5
		explosions {
			explosion(FireworkExplosionShape.BURST) {
				colors(Color.RED)
				fadeColors(Color.BLUE)
				hasTrail = true
				hasTwinkle = true
			}

			mode(Mode.REPLACE_ALL)
		}
	}
}
```

#### setBookCover

Set written book cover. The title is plain text up to 32 characters, not a text component, pass
`WritablePage("Adventure Log", filtered = "...")` to also give the version shown with chat filtering:

```kotlin
itemModifier("book") {
	setBookCover(
		title = "Adventure Log",
		author = "Player",
		generation = 0
	)
}
```

#### setWritableBookPages

Set pages of a writable book:

```kotlin
itemModifier("writable_book") {
	setWritableBookPages {
		page("test", filtered = "filtered")
		mode(Mode.INSERT, 1)
	}
}
```

#### setWrittenBookPages

Set pages of a written book:

```kotlin
itemModifier("written_book") {
	setWrittenBookPages {
		page("test", filtered = "test2")
	}
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
			amount = 5f,
			id = "bonus_damage",
			slot = listOf(EquipmentSlot.MAINHAND)
		)
		attribute(
			attribute = Attributes.MOVEMENT_SPEED,
			operation = AttributeModifierOperation.ADD_MULTIPLIED_BASE,
			amount = 0.1f,
			id = "speed_boost",
			slot = listOf(EquipmentSlot.FEET)
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
		bannerPattern(BannerPatterns.CREEPER, FormattingColor.BLACK)
	}
}
```

### Block State

#### copyState

Copy block state to item:

```kotlin
itemModifier("block_state") {
	copyState(Blocks.FURNACE, "facing", "lit")
}
```

### Bonus Formulas

#### applyBonus

Apply enchantment-based bonus formulas:

```kotlin
itemModifier("fortune") {
	// Ore drops formula
	applyBonus(Enchantments.FORTUNE, OreDrops)

	// Uniform bonus
	applyBonus(Enchantments.FORTUNE, UniformBonusCount(bonusMultiplier = 1f))

	// Binomial distribution
	applyBonus(Enchantments.FORTUNE, BinomialWithBonusCount(extra = 3, probability = 0.5f))
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
	fillPlayerHead(Source.ATTACKER)
}
```

### Tooltips

#### toggleTooltips

Show/hide tooltip sections:

```kotlin
itemModifier("clean_tooltip") {
	toggleTooltips {
		toggle(true, ItemComponentTypes.TRIM, ItemComponentTypes.CAN_PLACE_ON)
		toggles(ItemComponentTypes.DYED_COLOR to false)
	}
}
```

### Composition

#### sequence

Run multiple functions in sequence:

```kotlin
itemModifier("complex") {
	sequence {
		setCount(1)
		enchantRandomly()
		setName("Mystery Item")
	}
}
```

A `sequence` holding a single function writes it as an object, several ones as a list. `sequence { }` returns the function, so a `condition` guards the whole group:

```kotlin
itemModifier("sometimes_mystery") {
	sequence {
		setCount(1)
		setName("Mystery Item")
	}.condition { randomChance(0.5f) }
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
			setCount(1..5)
		}
	}
}
```

#### reference

Wherever the game expects item functions, it also accepts the ID of an item modifier file, or a `#tag` of item modifiers, written as a bare string. `reference(id)` builds it, and `itemModifier(...)` returns the ID to pass:

```kotlin
val baseModifier = itemModifier("base") {
	setCount(1)
}

itemModifier("combined") {
	reference(baseModifier)
	setCount(2)  // { "type": "minecraft:sequence", "functions": ["my_pack:base", {"type": "minecraft:set_count", "count": 2}] }
}

itemModifier("shortcut") {
	reference(baseModifier)  // { "type": "minecraft:sequence", "functions": "my_pack:base" }
}

lootTable("tagged") {
	modifier { reference(ItemModifierTagArgument("buffs", "my_pack")) }  // "modifier": "#my_pack:buffs"
}
```

A file is one function object, so the `combined` and `shortcut` files are written as a `sequence`. A list can mix IDs and inline values, but a tag can't be one of its elements, only the whole `functions` value.

A reference has no `condition` of its own: wrap it in a `sequence { }` function to guard it. An item modifier that references itself, directly or through other files, makes the pack fail to load.

## Conditions

Every function can have one `condition` that must pass:

```kotlin
itemModifier("conditional") {
	setCount(10) {
		condition {
			// Several conditions are written as one all_of, so they are AND-ed
			killedByPlayer()
			randomChance(0.5f)
		}
	}

	enchantRandomly {
		condition {
			weatherCheck(raining = true)
		}
	}
}
```

The first function is written as:

```json
{
	"type": "minecraft:set_count",
	"condition": {
		"type": "minecraft:all_of",
		"terms": [
			{
				"type": "minecraft:killed_by_player"
			},
			{
				"type": "minecraft:random_chance",
				"chance": 0.5
			}
		]
	},
	"count": 10
}
```

A function can also point at a predicate file by ID: `condition(isNight)` writes `"condition": "my_pack:is_night"`.

See [Predicates](/docs/data-driven/predicates) for all available conditions.

## Full Example

```kotlin
dataPack("legendary_items") {
	val legendaryModifier = itemModifier("legendary_weapon") {
		// High-level enchantments
		enchantWithLevels(levels = 30) {
			condition {
				randomChance(0.3f)
			}
		}

		// Guaranteed enchantments
		setEnchantments {
			this[Enchantments.UNBREAKING] = 3
		}

		// Custom name with formatting
		setName(text("Legendary Weapon", Color.GOLD) {
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
				amount = 5f,
				id = "legendary_damage",
				slot = listOf(EquipmentSlot.MAINHAND)
			)
		}

		// Full durability
		setDamage(1.0f)
	}

	// Use in loot table
	lootTable("boss_weapon") {
		pool {
			rolls(1)

			entries {
				item(Items.DIAMOND_SWORD) {
					modifier {
						reference(legendaryModifier)
					}
				}
			}
		}
	}

	// Use with command
	load {
		items.modify(self(), WEAPON.MAINHAND, legendaryModifier)
	}
}
```

### Generated JSON

```json
{
	"type": "minecraft:sequence",
	"functions": [
		{
			"type": "minecraft:enchant_with_levels",
			"condition": {
				"type": "minecraft:random_chance",
				"chance": 0.3
			},
			"levels": 30
		},
		{
			"type": "minecraft:set_enchantments",
			"enchantments": {
				"minecraft:unbreaking": 3
			}
		},
		{
			"type": "minecraft:set_name",
			"name": {
				"text": "Legendary Weapon",
				"color": "gold",
				"bold": true
			}
		},
		{
			"type": "minecraft:set_lore",
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
			"type": "minecraft:set_attributes",
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
			"type": "minecraft:set_damage",
			"damage": 1.0
		}
	]
}
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
- [Commands](/docs/commands/commands) - Using the `/item` command
- [Villager Trades](/docs/data-driven/villager-trades) - Apply item modifiers to villager trade outputs via
  `givenItemModifier`

### External Resources

- [Minecraft Wiki: Item modifier](https://minecraft.wiki/w/Item_modifier) - Official JSON format reference

