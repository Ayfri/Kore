---
root: .components.layouts.MarkdownLayout
title: Recipes
nav-title: Recipes
description: Create custom Minecraft recipes using Kore's type-safe Kotlin DSL for crafting, smelting, smithing, and more.
keywords: minecraft, datapack, kore, recipes, crafting, smelting, smithing, stonecutting
date-created: 2024-01-08
date-modified: 2026-02-03
routeOverride: /docs/data-driven/recipes
---

# Recipes

Recipes define how items are transformed through crafting tables, furnaces, smithing tables, stonecutters, and other workstations. Kore provides a type-safe DSL to create all vanilla recipe types programmatically.

## Overview

Recipes have several key characteristics:

- **Type-specific**: Each workstation has its own recipe format
- **Discoverable**: Recipes can be unlocked via advancements
- **Customizable results**: Output items can have custom components
- **Tag-based ingredients**: Use item tags for flexible ingredient matching

### Recipe Types

| Type                 | Workstation    | Description                  |
|----------------------|----------------|------------------------------|
| `crafting_shaped`    | Crafting Table | Pattern-based crafting       |
| `crafting_shapeless` | Crafting Table | Order-independent crafting   |
| `crafting_transmute` | Crafting Table | Transform item with material |
| `crafting_special_*` | Crafting Table | Built-in special recipes     |
| `smelting`           | Furnace        | Standard smelting            |
| `blasting`           | Blast Furnace  | Faster ore smelting          |
| `smoking`            | Smoker         | Faster food cooking          |
| `campfire_cooking`   | Campfire       | Slow food cooking            |
| `smithing_transform` | Smithing Table | Upgrade items                |
| `smithing_trim`      | Smithing Table | Apply armor trims            |
| `stonecutting`       | Stonecutter    | Cut blocks                   |

## File Structure

Recipes are stored as JSON files in data packs at:

```
data/<namespace>/recipe/<name>.json
```

For complete JSON specification, see the [Minecraft Wiki - Recipe](https://minecraft.wiki/w/Recipe).

## Creating Recipes

Use the `recipes` block inside a data pack to define recipes:

```kotlin
dataPack("my_datapack") {
	recipes {
		craftingShaped("diamond_sword_upgrade") {
			pattern(
				" E ",
				" D ",
				" S "
			)
			keys {
				"E" to Items.EMERALD
				"D" to Items.DIAMOND_SWORD
				"S" to Items.STICK
			}
			result(Items.DIAMOND_SWORD) {
				enchantments {
					enchantment(Enchantments.SHARPNESS, 5)
				}
			}
		}
	}
}
```

This generates `data/my_datapack/recipe/diamond_sword_upgrade.json`.

## Crafting Recipes

### Shaped Crafting

Pattern-based recipes where ingredient positions matter:

```kotlin
recipes {
	craftingShaped("my_pickaxe") {
		// Define the pattern (up to 3x3)
		pattern(
			"DDD",
			" S ",
			" S "
		)

		// Map characters to items
		key("D", Items.DIAMOND)
		key("S", Items.STICK)

		// Or use keys block
		keys {
			"D" to Items.DIAMOND
			"S" to Items.STICK
		}

		// Set the result
		result(Items.DIAMOND_PICKAXE)

		// Optional: set category for recipe book
		category = CraftingCategory.EQUIPMENT
	}
}
```

#### Pattern Rules

- Patterns can be 1x1 to 3x3
- Use space ` ` for empty slots
- Each character must be mapped in `key()` or `keys {}`
- Patterns are automatically trimmed (no need for padding)

```kotlin
// 2x2 recipe
craftingShaped("torch") {
    pattern(
	    "C",
	    "S"
    )
    keys {
	    "C" to Items.COAL
	    "S" to Items.STICK
    }
	result(Items.TORCH)
	count = 4
}

// Using tags as ingredients
craftingShaped("planks") {
	pattern("L")
	key("L", Tags.Item.LOGS)
	result(Items.OAK_PLANKS)
	count = 4
}
```

### Shapeless Crafting

Order-independent recipes:

```kotlin
recipes {
	craftingShapeless("mushroom_stew") {
		ingredient(Items.BOWL)
		ingredient(Items.BROWN_MUSHROOM)
		ingredient(Items.RED_MUSHROOM)

		result(Items.MUSHROOM_STEW)
	}
}
```

#### Multiple of Same Ingredient

```kotlin
craftingShapeless("book") {
	ingredient(Items.PAPER)
	ingredient(Items.PAPER)
	ingredient(Items.PAPER)
	ingredient(Items.LEATHER)

	result(Items.BOOK)
}
```

#### Using Tags

```kotlin
craftingShapeless("dye_mix") {
	ingredient(Tags.Item.DYES)
	ingredient(Tags.Item.DYES)

	result(Items.MAGENTA_DYE)
	count = 2
}
```

### Transmute Crafting

Transform an item while preserving its components:

```kotlin
recipes {
	craftingTransmute("dye_shulker") {
		input(Tags.Item.SHULKER_BOXES)
		material(Items.BLUE_DYE)
		result(Items.BLUE_SHULKER_BOX)
    }
}
```

The result item copies all components from the input item.

### Special Crafting

Special recipes use built-in game logic for complex crafting that can't be data-driven:

```kotlin
recipes {
	craftingSpecial("armor_dye", CraftingSpecialArmorDye)
	craftingSpecial("banner_copy", CraftingSpecialBannerDuplicate)
	craftingSpecial("book_clone", CraftingSpecialBookCloning)
	craftingSpecial("firework_rocket", CraftingSpecialFireworkRocket)
	craftingSpecial("firework_star", CraftingSpecialFireworkStar)
	craftingSpecial("map_clone", CraftingSpecialMapCloning)
	craftingSpecial("map_extend", CraftingSpecialMapExtending)
	craftingSpecial("repair", CraftingSpecialRepairItem)
	craftingSpecial("shield_decor", CraftingSpecialShieldDecoration)
	craftingSpecial("shulker_color", CraftingSpecialShulkerboxColoring)
	craftingSpecial("tipped_arrow", CraftingSpecialTippedArrow)
	craftingSpecial("suspicious_stew", CraftingSpecialSuspiciousStew)
}
```

These are useful when the vanilla datapack is disabled and you need to re-enable specific special recipes.

## Cooking Recipes

All cooking recipes share a similar structure:

| Property      | Description                       |
|---------------|-----------------------------------|
| `ingredient`  | Input item or tag                 |
| `result`      | Output item                       |
| `experience`  | XP awarded when collecting output |
| `cookingTime` | Time in ticks                     |

### Smelting (Furnace)

```kotlin
recipes {
	smelting("iron_ingot") {
		ingredient(Items.RAW_IRON)
		result(Items.IRON_INGOT)
		experience = 0.7
		cookingTime = 200  // 10 seconds (default)
	}

	// Using tags
	smelting("glass") {
		ingredient(Tags.Item.SMELTS_TO_GLASS)
		result(Items.GLASS)
		experience = 0.1
	}
}
```

### Blasting (Blast Furnace)

Twice as fast as smelting (100 ticks default):

```kotlin
recipes {
	blasting("iron_ingot_fast") {
		ingredient(Items.RAW_IRON)
		result(Items.IRON_INGOT)
		experience = 0.7
		cookingTime = 100  // 5 seconds (default)
	}
}
```

### Smoking (Smoker)

For food items, twice as fast as furnace:

```kotlin
recipes {
	smoking("cooked_beef") {
		ingredient(Items.BEEF)
		result(Items.COOKED_BEEF)
		experience = 0.35
		cookingTime = 100
	}
}
```

### Campfire Cooking

Slow cooking without fuel:

```kotlin
recipes {
	campfireCooking("baked_potato") {
		ingredient(Items.POTATO)
		result(Items.BAKED_POTATO)
		experience = 0.35
		cookingTime = 600  // 30 seconds (default)
	}
}
```

## Smithing Recipes

### Smithing Transform

Upgrade items at the smithing table:

```kotlin
recipes {
	smithingTransform("netherite_sword") {
		template(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
		base(Items.DIAMOND_SWORD)
		addition(Items.NETHERITE_INGOT)
		result(Items.NETHERITE_SWORD)
	}
}
```

The result item copies components from the base item.

#### Multiple Addition Options

```kotlin
smithingTransform("custom_upgrade") {
    template(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    base(Items.DIAMOND_SWORD)
	addition(Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP)  // Either works
    result(Items.NETHERITE_SWORD)
}
```

### Smithing Trim

Apply armor trims:

```kotlin
recipes {
	smithingTrim("sentry_trim") {
		template(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)
		base(Tags.Item.TRIMMABLE_ARMOR)
		addition(Tags.Item.TRIM_MATERIALS)
		pattern = TrimPatterns.SENTRY
	}
}
```

## Stonecutting

Single-item recipes for the stonecutter:

```kotlin
recipes {
	stoneCutting("stone_slab") {
		ingredient(Items.STONE)
		result(Items.STONE_SLAB)
		count = 2
	}

	stoneCutting("stone_stairs") {
		ingredient(Items.STONE)
		result(Items.STONE_STAIRS)
	}

	// Multiple outputs from same ingredient (separate recipes)
	stoneCutting("stone_bricks") {
		ingredient(Items.STONE)
		result(Items.STONE_BRICKS)
	}
}
```

## Result Items with Components

Add custom components to recipe results:

```kotlin
recipes {
	craftingShaped("enchanted_book") {
		pattern(
			"E E",
			" B ",
			"E E"
		)
		keys {
			"E" to Items.EMERALD
			"B" to Items.BOOK
		}

		result(Items.ENCHANTED_BOOK) {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
			}
		}
	}

	craftingShaped("damaged_sword") {
		pattern(
			" D",
			" D",
			" S"
		)
		keys {
			"D" to Items.DAMAGED_DIAMOND
			"S" to Items.STICK
		}

		result(Items.DIAMOND_SWORD) {
			damage(100)  // Partially damaged
		}
	}

	craftingShapeless("named_diamond") {
		ingredient(Items.DIAMOND)
		ingredient(Items.PAPER)

		result(Items.DIAMOND) {
			customName(textComponent("Certified Diamond", Color.AQUA))
			lore(textComponent("100% Genuine", Color.GRAY))
		}
	}
}
```

## Recipe Categories

Organize recipes in the recipe book:

```kotlin
craftingShaped("tool") {
	// ... pattern and keys
	result(Items.DIAMOND_PICKAXE)
	category = CraftingCategory.EQUIPMENT
}

smelting("food") {
	// ... ingredient and result
	category = SmeltingCategory.FOOD
}
```

### Crafting Categories

- `BUILDING` - Building blocks
- `REDSTONE` - Redstone components
- `EQUIPMENT` - Tools, weapons, armor
- `MISC` - Everything else

### Cooking Categories

- `FOOD` - Food items
- `BLOCKS` - Block transformations (sand → glass)
- `MISC` - Everything else

## Recipe Groups

Group similar recipes in the recipe book:

```kotlin
recipes {
	craftingShaped("oak_planks") {
		pattern("L")
		key("L", Items.OAK_LOG)
		result(Items.OAK_PLANKS)
		count = 4
		group = "planks"
	}

	craftingShaped("birch_planks") {
		pattern("L")
		key("L", Items.BIRCH_LOG)
		result(Items.BIRCH_PLANKS)
		count = 4
		group = "planks"
	}
}
```

Recipes with the same group appear together in the recipe book.

## Using Recipes in Commands

### Reference Recipes

Store a recipe reference for use in commands:

```kotlin
val myRecipe = recipesBuilder.craftingShaped("special_item") {
	pattern(
		" G ",
		"GBG",
		" G "
	)
	keys {
		"G" to Items.GOLD_BLOCK
		"B" to Items.DIAMOND_BLOCK
	}
	result(Items.BEACON)
}

load {
	// Give recipe to all players
	recipeGive(allPlayers(), myRecipe)
}
```

### Give/Take Recipes

```kotlin
load {
	// Give specific recipe
	recipeGive(self(), myRecipe)

	// Take recipe
	recipeTake(self(), myRecipe)

	// Give all recipes
	recipeGive(allPlayers(), "*")
}
```

## Overriding Vanilla Recipes

Override vanilla recipes by using the minecraft namespace:

```kotlin
dataPack("better_recipes") {
	recipes {
		// Override vanilla diamond sword recipe
		craftingShaped("diamond_sword") {
			namespace = "minecraft"

			pattern(
				" D ",
				" D ",
				" S "
			)
			keys {
				"D" to Items.DIAMOND
				"S" to Items.STICK
			}
			result(Items.DIAMOND_SWORD) {
				enchantments {
					enchantment(Enchantments.UNBREAKING, 1)
				}
			}
		}
    }
}
```

## Full Example

```kotlin
dataPack("custom_recipes") {
    recipes {
	    // Shaped crafting with components
	    craftingShaped("legendary_sword") {
            pattern(
	            " N ",
	            " N ",
	            " B "
            )
            keys {
	            "N" to Items.NETHERITE_INGOT
	            "B" to Items.BLAZE_ROD
            }
		    result(Items.NETHERITE_SWORD) {
			    customName(textComponent("Blade of Flames", Color.GOLD))
                enchantments {
	                enchantment(Enchantments.FIRE_ASPECT, 2)
                    enchantment(Enchantments.SHARPNESS, 5)
                }
			    unbreakable()
            }
		    category = CraftingCategory.EQUIPMENT
        }

	    // Shapeless recipe
	    craftingShapeless("quick_tnt") {
		    ingredient(Items.GUNPOWDER)
		    ingredient(Items.GUNPOWDER)
		    ingredient(Items.GUNPOWDER)
		    ingredient(Items.GUNPOWDER)
		    ingredient(Tags.Item.SAND)
		    result(Items.TNT)
	    }

	    // Transmute recipe
	    craftingTransmute("repaint_bed") {
		    input(Tags.Item.BEDS)
		    material(Items.WHITE_DYE)
		    result(Items.WHITE_BED)
	    }

	    // Smelting with experience
	    smelting("ancient_debris") {
		    ingredient(Items.ANCIENT_DEBRIS)
		    result(Items.NETHERITE_SCRAP)
		    experience = 2.0
		    cookingTime = 200
		    category = SmeltingCategory.MISC
	    }

	    // Smithing upgrade
	    smithingTransform("netherite_boots") {
		    template(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
		    base(Items.DIAMOND_BOOTS)
		    addition(Items.NETHERITE_INGOT)
		    result(Items.NETHERITE_BOOTS)
	    }

	    // Smithing trim
	    smithingTrim("ward_trim") {
		    template(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE)
		    base(Tags.Item.TRIMMABLE_ARMOR)
		    addition(Tags.Item.TRIM_MATERIALS)
		    pattern = TrimPatterns.WARD
	    }

	    // Stonecutting variants
	    stoneCutting("cut_copper_slab") {
		    ingredient(Items.COPPER_BLOCK)
		    result(Items.CUT_COPPER_SLAB)
		    count = 8
	    }
    }

	// Reference recipe in function
	val beaconRecipe = recipesBuilder.craftingShaped("easy_beacon") {
		pattern(
			"GGG",
			"GSG",
			"OOO"
		)
		keys {
			"G" to Items.GLASS
			"S" to Items.NETHER_STAR
			"O" to Items.OBSIDIAN
		}
		result(Items.BEACON)
	}

	load {
		recipeGive(allPlayers(), beaconRecipe)
	}
}
```

### Generated JSON (Shaped Recipe)

```json
{
	"type": "minecraft:crafting_shaped",
	"category": "equipment",
	"pattern": [
		" N ",
		" N ",
		" B "
	],
	"key": {
		"N": "minecraft:netherite_ingot",
		"B": "minecraft:blaze_rod"
	},
	"result": {
		"id": "minecraft:netherite_sword",
		"components": {
			"custom_name": {
				"text": "Blade of Flames",
				"color": "gold"
			},
			"enchantments": {
				"minecraft:fire_aspect": 2,
				"minecraft:sharpness": 5
			},
			"unbreakable": {}
		}
	}
}
```

## Best Practices

1. **Use meaningful names** - Recipe file names should describe the output
2. **Group related recipes** - Use the `group` field for recipe book organization
3. **Prefer tags** - Use item tags for flexible ingredient matching
4. **Set categories** - Help players find recipes in the recipe book
5. **Test in-game** - Verify recipes work as expected in all crafting interfaces
6. **Consider balance** - Ensure custom recipes maintain game balance

## See Also

- [Advancements](/docs/data-driven/advancements) - Unlock recipes as advancement rewards
- [Components](/docs/concepts/components) - Customize recipe result items
- [Commands](/docs/commands/commands) - Give or take recipes with commands
- [Tags](/docs/data-driven/tags) - Use item tags for flexible ingredient matching

### External Resources

- [Minecraft Wiki: Recipe](https://minecraft.wiki/w/Recipe) - Official JSON format reference
- [Minecraft Wiki: Crafting](https://minecraft.wiki/w/Crafting) - Crafting mechanics overview
