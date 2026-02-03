---
root: .components.layouts.MarkdownLayout
title: Tags
nav-title: Tags
description: Create and manage tags for grouping game elements with Kore's type-safe DSL
keywords: minecraft, datapack, kore, tags, grouping, blocks, items, entities, functions
date-created: 2026-02-03
date-modified: 2026-02-03
routeOverride: /docs/data-driven/tags
---

# Tags

Tags are JSON structures used in data packs to group related game elements together. They allow you to reference multiple items, blocks, entities, or other resources as a single unit. Tags are extensively used in commands, loot tables, advancements, recipes, and other data-driven features.

## Basic Usage

Kore provides a generic `tag` function as well as specialized helper functions for each tag type:

```kotlin
// Generic tag with explicit type
tag<BlockTagArgument>("my_blocks", "block") {
	add(Blocks.STONE)
	add(Blocks.GRANITE)
	add(Blocks.DIORITE)
}

// Specialized helper function
blockTag("my_blocks") {
	add(Blocks.STONE)
	add(Blocks.GRANITE)
	add(Blocks.DIORITE)
}
```

This creates a tag file at `data/<namespace>/tags/block/my_blocks.json`.

## Adding Values

There are several ways to add values to a tag:

### Simple Addition

```kotlin
blockTag("building_blocks") {
	// Add a single block
	add(Blocks.STONE)
	
	// Using operators
	this += Blocks.COBBLESTONE
	this += "minecraft:granite"
	
	// Add with namespace
	add("custom_stone", namespace = "mymod")
}
```

### Adding Other Tags

You can include other tags within a tag by prefixing with `#`:

```kotlin
blockTag("all_stones") {
	// Reference another tag
	add("#minecraft:stone_bricks", tag = true)
	
	// Or using the name/namespace form
	add("base_stone_overworld", namespace = "minecraft", tag = true)
}
```

### Optional Entries

Mark entries as optional with the `required` parameter. Optional entries won't cause errors if they don't exist:

```kotlin
itemTag("optional_items") {
	add(Items.DIAMOND, required = true)  // Must exist
	add("mymod:custom_gem", required = false)  // Optional
	
	// Using TagEntry directly
	this += TagEntry("minecraft:emerald", required = false)
}
```

### Replace Mode

By default, tags merge with existing tags of the same name. Set `replace = true` to completely override:

```kotlin
// This will replace the vanilla tag entirely
blockTag("logs", namespace = "minecraft", replace = true) {
	add(Blocks.OAK_LOG)
	add(Blocks.BIRCH_LOG)
	// Other logs won't be included
}
```

## Tag Types

Kore provides specialized helper functions for all tag types. Here are the most commonly used ones:

### Block Tags

```kotlin
blockTag("fragile_blocks") {
	add(Blocks.GLASS)
	add(Blocks.ICE)
	add(Blocks.GLOWSTONE)
}
```

### Item Tags

```kotlin
itemTag("valuable_gems") {
	add(Items.DIAMOND)
	add(Items.EMERALD)
	add(Items.AMETHYST_SHARD)
}
```

### Entity Type Tags

```kotlin
entityTypeTag("friendly_mobs") {
	add(EntityTypes.VILLAGER)
	add(EntityTypes.IRON_GOLEM)
	add(EntityTypes.CAT)
}
```

### Function Tags

Function tags are special - they define groups of functions that can be called together or triggered by game events:

```kotlin
// Create functions to be tagged
function("on_load") {
	say("Datapack loaded!")
}

function("setup_scores") {
	scoreboard.objectives.add("kills", ScoreboardCriteria.PLAYER_KILL_COUNT)
}

// Tag them to run on load
functionTag("load", namespace = "minecraft") {
	add("on_load", namespace = name)
	add("setup_scores", namespace = name)
}
```

The `minecraft:load` and `minecraft:tick` function tags are special:

- `minecraft:load` - Functions run once when the datapack loads
- `minecraft:tick` - Functions run every game tick

### Biome Tags

```kotlin
biomeTag("hot_biomes") {
	add(Biomes.DESERT)
	add(Biomes.BADLANDS)
	add(Biomes.SAVANNA)
}
```

### Damage Type Tags

```kotlin
damageTypeTag("magic_damage") {
	add(DamageTypes.MAGIC)
	add(DamageTypes.INDIRECT_MAGIC)
	add(DamageTypes.DRAGON_BREATH)
}
```

### Enchantment Tags

```kotlin
enchantmentTag("combat_enchants") {
	add(Enchantments.SHARPNESS)
	add(Enchantments.SMITE)
	add(Enchantments.BANE_OF_ARTHROPODS)
}
```

### Fluid Tags

```kotlin
fluidTag("dangerous_fluids") {
	add(Fluids.LAVA)
	add(Fluids.FLOWING_LAVA)
}
```

## Complete List of Tag Helpers

Kore provides helpers for all vanilla tag types:

| Function                      | Tag Type             | Path                                        |
|-------------------------------|----------------------|---------------------------------------------|
| `bannerPatternTag`            | Banner patterns      | `tags/banner_pattern`                       |
| `biomeTag`                    | Biomes               | `tags/worldgen/biome`                       |
| `blockTag`                    | Blocks               | `tags/block`                                |
| `catVariantTag`               | Cat variants         | `tags/cat_variant`                          |
| `configuredCarverTag`         | World carvers        | `tags/worldgen/configured_carver`           |
| `configuredFeatureTag`        | World features       | `tags/worldgen/configured_feature`          |
| `configuredStructureTag`      | Structures           | `tags/worldgen/structure`                   |
| `damageTypeTag`               | Damage types         | `tags/damage_type`                          |
| `enchantmentTag`              | Enchantments         | `tags/enchantment`                          |
| `entityTypeTag`               | Entity types         | `tags/entity_type`                          |
| `flatLevelGeneratorPresetTag` | Flat world presets   | `tags/worldgen/flat_level_generator_preset` |
| `fluidTag`                    | Fluids               | `tags/fluid`                                |
| `frogVariantTag`              | Frog variants        | `tags/frog_variant`                         |
| `functionTag`                 | Functions            | `tags/function`                             |
| `gameEventTag`                | Game events          | `tags/game_event`                           |
| `instrumentTag`               | Goat horns           | `tags/instrument`                           |
| `itemTag`                     | Items                | `tags/item`                                 |
| `noiseSettingsTag`            | Noise settings       | `tags/worldgen/noise_settings`              |
| `noiseTag`                    | Noise                | `tags/worldgen/noise`                       |
| `paintingVariantTag`          | Paintings            | `tags/painting_variant`                     |
| `pigVariantTag`               | Pig variants         | `tags/pig_variant`                          |
| `placedFeatureTag`            | Placed features      | `tags/worldgen/placed_feature`              |
| `pointOfInterestTypeTag`      | POI types            | `tags/point_of_interest_type`               |
| `processorListTag`            | Structure processors | `tags/worldgen/processor_list`              |
| `structureTag`                | Structures           | `tags/worldgen/structure`                   |
| `templatePoolTag`             | Jigsaw pools         | `tags/worldgen/template_pool`               |
| `trimMaterialTag`             | Trim materials       | `tags/trim_material`                        |
| `trimPatternTag`              | Trim patterns        | `tags/trim_pattern`                         |
| `wolfVariantTag`              | Wolf variants        | `tags/wolf_variant`                         |
| `worldPresetTag`              | World presets        | `tags/worldgen/world_preset`                |

## Modifying Existing Tags

Use `addToTag` to append to an existing tag without creating duplicates:

```kotlin
// First creation
blockTag("my_ores") {
	add(Blocks.IRON_ORE)
	add(Blocks.GOLD_ORE)
}

// Later in code, add more entries
addToTag<BlockTagArgument>("my_ores", "block") {
	add(Blocks.DIAMOND_ORE)
	add(Blocks.EMERALD_ORE)
}
```

## Using Tags in Commands

Tags can be referenced in commands with the `#` prefix:

```kotlin
function("clear_valuables") {
	// Clear all items matching a tag
	clear(allPlayers(), tag = ItemTagArgument("valuable_gems", name))
}

function("kill_hostiles") {
	// Kill entities matching a tag
	kill(allEntities {
		type = "#minecraft:raiders"
	})
}
```

## Using Tags in Predicates

Tags work seamlessly with [Predicates](./predicates):

```kotlin
predicate("holding_tool") {
	matchTool {
		items(Tags.Item.PICKAXES)
	}
}

predicate("in_hot_biome") {
	locationCheck {
		biomes(BiomeTagArgument("hot_biomes", name))
	}
}
```

## Using Tags in Recipes

Tags are commonly used in [Recipes](./recipes) for flexible ingredient matching:

```kotlin
craftingShapeless("any_planks_to_sticks") {
	ingredient(Tags.Item.PLANKS)
	ingredient(Tags.Item.PLANKS)
	result(Items.STICK, 4)
}
```

## Generated JSON

A basic tag generates JSON like this:

```json
{
	"replace": false,
	"values": [
		"minecraft:stone",
		"minecraft:granite",
		"minecraft:diorite"
	]
}
```

With optional entries:

```json
{
	"replace": false,
	"values": [
		"minecraft:diamond",
		{
			"id": "mymod:custom_gem",
			"required": false
		}
	]
}
```

With tag references:

```json
{
	"replace": false,
	"values": [
		"#minecraft:stone_bricks",
		"minecraft:cobblestone"
	]
}
```

## Best Practices

1. **Use meaningful names**: Name tags based on their purpose (e.g., `mineable/pickaxe` not `pickaxe_blocks`)
2. **Leverage existing tags**: Reference vanilla tags when appropriate instead of duplicating entries
3. **Keep tags focused**: Each tag should serve a single, clear purpose
4. **Use optional entries**: Mark modded content as `required = false` for cross-mod compatibility
5. **Avoid replace when possible**: Merging with existing tags maintains compatibility with other datapacks

## See Also

- [Predicates](./predicates) - Use tags in predicate conditions
- [Recipes](./recipes) - Use tags for flexible recipe ingredients
- [Loot Tables](./loot-tables) - Use tags in loot conditions
- [Functions](../commands/functions) - Creating functions to use with function tags
- [Trims](./trims) - Trim material and pattern tags

### External Resources

- [Minecraft Wiki: Tag (Java Edition)](https://minecraft.wiki/w/Tag_(Java_Edition)) - Complete reference for all tag types and vanilla tags
