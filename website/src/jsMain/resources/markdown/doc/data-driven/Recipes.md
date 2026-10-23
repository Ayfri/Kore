---
root: .components.layouts.MarkdownLayout
title: Recipes
nav-title: Recipes
description: Create custom Minecraft recipes using Kore's type-safe Kotlin DSL for crafting, smelting, smithing, and more.
keywords: minecraft, datapack, kore, recipes, crafting, smelting, smithing, stonecutting
date-created: 2024-01-08
date-modified: 2026-10-22
routeOverride: /docs/data-driven/recipes
---

# Recipes

Recipes define how items are transformed through crafting tables, furnaces, smithing tables, stonecutters, and other workstations. Kore provides a type-safe DSL to create all vanilla recipe types programmatically.

The [recipes example](/playground#example=recipes) of the playground runs in your browser and shows the JSON it generates.

## Overview

Recipes have several key characteristics:

- **Type-specific**: Each workstation has its own recipe format
- **Discoverable**: Recipes can be unlocked via advancements
- **Customizable results**: Output items can have custom components
- **Tag-based ingredients**: Use item tags for flexible ingredient matching

### Recipe Types

| Type                                  | Workstation                                                     | Description                         |
|---------------------------------------|-----------------------------------------------------------------|-------------------------------------|
| `blasting`                            | ![Blast Furnace](mc:block/blast_furnace_front) Blast Furnace    | Faster ore smelting                 |
| `brewing`                             | ![Brewing Stand](mc:item/brewing_stand) Brewing Stand           | Brew potions from a reagent         |
| `campfire_cooking`                    | ![Campfire](mc:item/campfire) Campfire                          | Slow food cooking                   |
| `crafting_decorated_pot`              | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Craft a decorated pot from sherds   |
| `crafting_dye`                        | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Dye an item with a dye              |
| `crafting_imbue`                      | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Imbue items (e.g. tip arrows)       |
| `crafting_shaped`                     | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Pattern-based crafting              |
| `crafting_shapeless`                  | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Order-independent crafting          |
| `crafting_special_bannerduplicate`    | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Copy a banner pattern               |
| `crafting_special_bookcloning`        | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Copy a written book                 |
| `crafting_special_firework_rocket`    | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Craft a firework rocket             |
| `crafting_special_firework_star`      | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Craft a firework star               |
| `crafting_special_firework_star_fade` | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Add a fade colour to a star         |
| `crafting_special_mapextending`       | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Extend a map with paper             |
| `crafting_special_shielddecoration`   | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Apply a banner to a shield          |
| `crafting_special_*`                  | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Remaining hardcoded special recipes |
| `crafting_transmute`                  | ![Crafting Table](mc:block/crafting_table_front) Crafting Table | Transform item with material        |
| `smelting`                            | ![Furnace](mc:block/furnace_front) Furnace                      | Standard smelting                   |
| `smithing_transform`                  | ![Smithing Table](mc:block/smithing_table_front) Smithing Table | Upgrade items                       |
| `smithing_trim`                       | ![Smithing Table](mc:block/smithing_table_front) Smithing Table | Apply armor trims                   |
| `smoking`                             | ![Smoker](mc:block/smoker_front) Smoker                         | Faster food cooking                 |
| `stonecutting`                        | ![Stonecutter](mc:block/stonecutter_saw) Stonecutter            | Cut blocks                          |

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

### Banner Duplicate

Copy a banner's pattern onto a blank banner:

```kotlin
recipes {
	craftingSpecialBannerDuplicate("banner_copy") {
		banner(Tags.Item.BANNERS)
		result(Items.WHITE_BANNER)
	}
}
```

### Book Cloning

Copy a written book, with an optional generation limit:

```kotlin
recipes {
	craftingSpecialBookCloning("book_clone") {
		source(Items.WRITTEN_BOOK)
		material(Items.WRITABLE_BOOK)
		result(Items.WRITTEN_BOOK)
		allowedGenerations = rangeOrInt(0..2)
	}
}
```

### Decorated Pot

Craft a decorated pot from sherds:

```kotlin
recipes {
	craftingDecoratedPot("decorated_pot") {
		back(Tags.Item.DECORATED_POT_INGREDIENTS)
		front(Tags.Item.DECORATED_POT_INGREDIENTS)
		left(Tags.Item.DECORATED_POT_INGREDIENTS)
		right(Tags.Item.DECORATED_POT_INGREDIENTS)
		result(Items.DECORATED_POT)
	}
}
```

### Dye Crafting

Dye an item using a dye ingredient. _Replaces the old `crafting_special_armordye` recipe_:

```kotlin
recipes {
	craftingDye("red_wool_dye") {
		dye(Items.RED_DYE)
		target(Tags.Item.WOOL)
		result(Items.RED_WOOL)
	}
}
```

### Firework Rocket

Craft a firework rocket from its component ingredients:

```kotlin
recipes {
	craftingSpecialFireworkRocket("firework_rocket") {
		fuel(Items.GUNPOWDER)
		shell(Items.PAPER)
		star(Items.FIREWORK_STAR)
		result(Items.FIREWORK_ROCKET)
	}
}
```

### Firework Star

Craft a firework star with dye, fuel, optional shape modifiers, trail, and twinkle effects:

```kotlin
recipes {
	craftingSpecialFireworkStar("red_burst_star") {
		dye(Items.RED_DYE)
		fuel(Items.GUNPOWDER)
		shape("burst", Items.FIRE_CHARGE)
		trail(Items.DIAMOND)
		twinkle(Items.GLOWSTONE_DUST)
		result(Items.FIREWORK_STAR)
	}
}
```

### Firework Star Fade

Add a fade colour to an existing firework star:

```kotlin
recipes {
	craftingSpecialFireworkStarFade("blue_fade_star") {
		dye(Items.BLUE_DYE)
		target(Items.FIREWORK_STAR)
		result(Items.FIREWORK_STAR)
	}
}
```

### Imbue Crafting

Imbue items with properties from a source (e.g. tip arrows with lingering potions). _Replaces the
old `crafting_special_tippedarrow` recipe_:

```kotlin
recipes {
	craftingImbue("tipped_arrow") {
		material(Tags.Item.ARROWS)
		source(Items.LINGERING_POTION)
		result(Items.TIPPED_ARROW)
	}
}
```

### Map Extending

Extend a map by combining it with paper:

```kotlin
recipes {
	craftingSpecialMapExtending("extended_map") {
		map(Items.FILLED_MAP)
		material(Items.PAPER)
	}
}
```

The `result` is optional: without it the result is the input map and the recipe writes `"result": {}`. Like
`craftingTransmute`, `result` also takes `count` and `components`, see [Transmute Crafting](#transmute-crafting).

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
	}
}
```

{{{ .components.mc.ShapedRecipeMockup }}}

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
	result(Items.TORCH, count = 4)
}

// Using tags as ingredients
craftingShaped("planks") {
	pattern("L")
	key("L", Tags.Item.LOGS)
	result(Items.OAK_PLANKS, count = 4)
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

	result(Items.MAGENTA_DYE, count = 2)
}
```

### Shield Decoration

Apply a banner pattern to a shield:

```kotlin
recipes {
	craftingSpecialShieldDecoration("shield_decor") {
		banner(Tags.Item.BANNERS)
		target(Items.SHIELD)
		result(Items.SHIELD)
	}
}
```

### Special Crafting

Remaining hardcoded special recipes with no configurable ingredients:

```kotlin
recipes {
	craftingSpecial("repair", CraftingSpecialRepairItem)
	craftingSpecial("shulker_color", CraftingSpecialShulkerBoxColoring)
}
```

These are useful when the vanilla datapack is disabled and you need to re-enable specific special recipes.

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

The `result` is optional. Without it the result is the input item itself, and the recipe writes `"result": {}`. This is
how vanilla clones maps:

```kotlin
recipes {
	craftingTransmute("map_cloning") {
		input(Items.FILLED_MAP)
		material(Items.MAP)
	}
}
```

```json
{
	"type": "minecraft:crafting_transmute",
	"input": "minecraft:filled_map",
	"material": "minecraft:map",
	"result": {}
}
```

The result is a `TransmuteResult` with optional `id`, `count` and `components`. Setting only the item writes the bare
item ID (`"result": "minecraft:red_bundle"`), any other field writes an object:

```kotlin
recipes {
	craftingTransmute("red_bundle_pair") {
		input(Items.BLACK_BUNDLE)
		material(Items.RED_DYE)
		result(Items.RED_BUNDLE, count = 2)
	}
}
```

```json
{
	"type": "minecraft:crafting_transmute",
	"input": "minecraft:black_bundle",
	"material": "minecraft:red_dye",
	"result": {
		"count": 2,
		"id": "minecraft:red_bundle"
	}
}
```

### Recipe Book Category

Shaped, shapeless, dye, imbue and transmute recipes take an optional `category` (`BUILDING`, `EQUIPMENT`, `MISC` or
`REDSTONE`) choosing the recipe book tab they show up in:

```kotlin
recipes {
	craftingShapeless("quick_torch") {
		category = RecipeCategory.EQUIPMENT
		ingredient(Items.COAL)
		ingredient(Items.STICK)
		result(Items.TORCH, count = 4)
	}
}
```

## Brewing Recipes

A `brewing` recipe turns an `input` potion and a `reagent` into the `output` item. `input` and `reagent` take items or
item tags, and can be restricted to a `potion_contents` component predicate with the `potionContents` block:

```kotlin
recipes {
	brewing("lingering_potion_awkward_blaze_powder") {
		input(Items.LINGERING_POTION) { potions(Potions.AWKWARD) }
		reagent(Items.BLAZE_POWDER)
		output(Items.LINGERING_POTION) {
			potionContents(Potions.STRENGTH)
		}
	}
}
```

This generates:

```json
{
	"type": "minecraft:brewing",
	"input": {
		"item": "minecraft:lingering_potion",
		"potion_contents": {
			"potions": "minecraft:awkward"
		}
	},
	"output": {
		"id": "minecraft:lingering_potion",
		"components": {
			"potion_contents": "minecraft:strength"
		}
	},
	"reagent": {
		"item": "minecraft:blaze_powder"
	}
}
```

The predicate also matches active effects through `effects { ... }`, see
[Components](/docs/concepts/components). `output` takes an item, an optional `count` and a component patch,
like `result` does on the other recipes.

## Cooking Recipes

All cooking recipes share a similar structure. `cookingTime` is required, so every builder takes it right after the recipe name:

| Property      | Description                                                           |
|---------------|-----------------------------------------------------------------------|
| `ingredient`  | Input item or tag                                                     |
| `result`      | Output item                                                           |
| `category`    | Recipe book tab, a `CookingRecipeCategory` (`BLOCKS`, `FOOD`, `MISC`) |
| `experience`  | XP awarded when collecting output                                     |
| `cookingTime` | Required time in ticks                                                |

The vanilla recipes use 200 ticks (10 seconds) for smelting, blasting and smoking, and 600 ticks (30 seconds) for campfire cooking.
The blast furnace and the smoker cook twice as fast as the furnace through their own speed.

### Blasting (Blast Furnace)

```kotlin
recipes {
	blasting("iron_ingot_fast", cookingTime = 200) {
		ingredient(Items.RAW_IRON)
		result(Items.IRON_INGOT)
		experience = 0.7
	}
}
```

### Campfire Cooking

Slow cooking without fuel:

```kotlin
recipes {
	campfireCooking("baked_potato", cookingTime = 600) {
		ingredient(Items.POTATO)
		result(Items.BAKED_POTATO)
		experience = 0.35
	}
}
```

### Smelting (Furnace)

```kotlin
recipes {
	smelting("iron_ingot", cookingTime = 200) {
		ingredient(Items.RAW_IRON)
		result(Items.IRON_INGOT)
		experience = 0.7
	}

	// Using tags
	smelting("glass", cookingTime = 200) {
		ingredient(Tags.Item.SMELTS_TO_GLASS)
		result(Items.GLASS)
		experience = 0.1
	}
}
```

{{{ .components.mc.SmeltingRecipeMockup }}}

### Smoking (Smoker)

For food items:

```kotlin
recipes {
	smoking("cooked_beef", cookingTime = 200) {
		ingredient(Items.BEEF)
		result(Items.COOKED_BEEF)
		experience = 0.35
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

{{{ .components.mc.SmithingRecipeMockup }}}

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
		result(Items.STONE_SLAB, count = 2)
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
			"D" to Items.DIAMOND
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

The recipe book tab of `craftingDye`, `craftingImbue` and `craftingTransmute` recipes is set with `category`, a
`RecipeCategory`:

```kotlin
craftingTransmute("dyed_bundle") {
	// ... input, material and result
	category = RecipeCategory.EQUIPMENT
}
```

- `BUILDING` - Building blocks
- `REDSTONE` - Redstone components
- `EQUIPMENT` - Tools, weapons, armor
- `MISC` - Everything else

Setting a category on shaped, shapeless or cooking recipes isn't possible yet because those Kore recipe types don't
expose the `category` field; the game then files them under its default tab.

## Recipe Groups

Group similar recipes in the recipe book:

```kotlin
recipes {
	craftingShaped("oak_planks") {
		pattern("L")
		key("L", Items.OAK_LOG)
		result(Items.OAK_PLANKS, count = 4)
		group = "planks"
	}

	craftingShaped("birch_planks") {
		pattern("L")
		key("L", Items.BIRCH_LOG)
		result(Items.BIRCH_PLANKS, count = 4)
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
	recipeGiveAll(allPlayers())
}
```

## Overriding Vanilla Recipes

A recipe overrides the vanilla one with the same id, so it has to live in the `minecraft` namespace. Every recipe
builder takes an optional `namespace` after the name, defaulting to the pack's:

```kotlin
dataPack("better_recipes") {
	recipes {
		// Override vanilla diamond sword recipe, returns RecipeArgument minecraft:diamond_sword
		craftingShaped("diamond_sword", namespace = "minecraft") {
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
		smelting("ancient_debris", cookingTime = 200) {
			ingredient(Items.ANCIENT_DEBRIS)
			result(Items.NETHERITE_SCRAP)
			experience = 2.0
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
			result(Items.CUT_COPPER_SLAB, count = 8)
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
4. **Test in-game** - Verify recipes work as expected in all crafting interfaces
5. **Consider balance** - Ensure custom recipes maintain game balance

## See Also

- [Advancements](/docs/data-driven/advancements) - Unlock recipes as advancement rewards
- [Components](/docs/concepts/components) - Customize recipe result items
- [Commands](/docs/commands/commands) - Give or take recipes with commands
- [Tags](/docs/data-driven/tags) - Use item tags for flexible ingredient matching

### External Resources

- [Minecraft Wiki: Recipe](https://minecraft.wiki/w/Recipe) - Official JSON format reference
- [Minecraft Wiki: Crafting](https://minecraft.wiki/w/Crafting) - Crafting mechanics overview
