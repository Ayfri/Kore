---
root: .components.layouts.MarkdownLayout
title: Recipes
nav-title: Recipes
description: A guide for using recipes in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, recipes
date-created: 2024-01-08
date-modified: 2024-01-08
routeOverride: /docs/recipes
---

# Recipes

To create recipes with **Kore**, you can utilize the framework's built-in functions to define various types of recipes for your Minecraft data packs. Here's a
comprehensive guide on how to create different recipes using Kore:

## Set Up Your Data Pack Function

Begin by creating a function within your `DataPack` where you'll define your recipes:

```kotlin
fun DataPack.createRecipes() {
    // Your recipe definitions will go here
}
```

## Initialize the Recipes Block

Use the `recipes` block to start defining your recipes:

```kotlin
recipes {
    // Define individual recipes here
}
```

## Creating a Blasting Recipe

To create a blasting recipe, use the `blasting` function:

```kotlin
blasting("unique_recipe_name") {
    ingredient {
        // Define the ingredient item or tag
        item = Items.IRON_ORE
        // or
        tag = Tags.Item.ORES
    }
    result = itemStack(Items.IRON_INGOT) {
        // Optionally modify the result item (e.g., set damage)
        damage(0)
    }
    experience = 0.7  // Set the experience rewarded
    cookingTime = 100 // Set the cooking time in ticks
}
```

Note that `campfire`, `smelting`, and `smoking` recipes can be defined similarly using the respective functions.

## Crafting Shaped Recipes

For crafting recipes with a specific shape:

```kotlin
craftingShaped("unique_recipe_name") {
    pattern(
        " A ", // It's a common pattern to format recipe patterns like this, to make them more readable
        " B ",
        " C "
    )
    keys {
        "A" to Items.DIAMOND
        "B" to Items.STICK
        "C" to Items.GOLD_INGOT
    }
    result(Items.DIAMOND_SWORD)
}
```

## Crafting Shapeless Recipes

For recipes where the arrangement doesn't matter:

```kotlin
craftingShapeless("unique_recipe_name") {
    ingredient(Items.WHEAT)
    ingredient(Items.WHEAT)
    ingredient(Items.WHEAT)
    ingredient(Items.SUGAR)
    ingredient(Items.EGG)
    ingredient(Items.EGG)

    result(Items.CAKE)
}
```

## Special Crafting Recipes

In addition to standard crafting recipes, crafting special recipes leverage built-in game logic to handle complex crafting operations that regular
data-driven recipes cannot manage. These special recipes are essential for functionalities that require handling NBT (Named Binary Tag) data, multiple
inputs, or specific item interactions. They are particularly useful when the "vanilla" data pack is disabled, allowing you to re-enable and customize desired
built-in crafting behaviors.

### Available Special Recipe Types

Here is a list of available special recipe types and their functionalities:

-   `armordye`: Dyeing armor with multiple dyes.
-   `bannerduplicate`: Copying NBT data from one banner to another.
-   `bookcloning`: Cloning written books, including their NBT data.
-   `firework_rocket`: Crafting firework rockets with flexible inputs and NBT data from firework stars.
-   `firework_star`: Crafting firework stars and adding fade colors.
-   `firework_star_fade`: Adding fade colors to firework stars.
-   `mapcloning`: Copying maps along with their NBT data.
-   `mapextending`: Zooming maps by updating their NBT data.
-   `repairitem`: Repairing items by updating their damage data.
-   `shielddecoration`: Applying shield patterns using banner NBT data.
-   `shulkerboxcoloring`: Dyeing shulker boxes while preserving their NBT data.
-   `tippedarrow`: Crafting tipped arrows with NBT data from lingering potions.
-   `suspiciousstew`: Creating suspicious stew with specific status effects based on flower types.

### Defining a Special Recipe

To define a special crafting recipe, use the craftingSpecial function with the specific type and relevant parameters. Below are examples of how to define some
of the special recipes:

```kotlin
craftingSpecial("custom_armor_dye", CraftingSpecialArmorDye) {}
```

## Smithing Transform Recipes

To create a smithing transformation recipe:

```kotlin
smithingTransform("unique_recipe_name") {
    template(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
    base(Items.DIAMOND_SWORD)
    addition(Items.NETHERITE_INGOT)
    result(Items.NETHERITE_SWORD)
}
```

## Smithing Trim Recipes

For smithing trim recipes:

```kotlin
smithingTrim("unique_recipe_name") {
    template(Items.SMITHING_TEMPLATE)
    base(Items.IRON_CHESTPLATE)

    addition(Items.GOLD_INGOT)
}
```

## Stonecutting Recipes

To define a stonecutting recipe:

```kotlin
stoneCutting("unique_recipe_name") {
    ingredient(Items.STONE)

    result = Items.STONE_SLAB
    count = 2
}
```

## Defining Ingredients

Ingredients can be items or tags, and you can define them as follows:

```kotlin
ingredient {
    item = Items.COBBLESTONE
    // or
    tag = Tags.Item.COBBLESTONE
}
```

You also have a shorter form for defining an ingredient:

```kotlin
ingredient(Items.COBBLESTONE)
// or
ingredient(tag = Tags.Item.COBBLESTONE)
```

## Setting the Result

When specifying the result of a recipe:

```kotlin
result = Items.ENCHANTED_BOOK {
    enchantments {
        enchantment(Enchantments.SHARPNESS, 5)
    }
}
```

## Using Keys in Shaped Recipes

Map characters in your pattern to specific items:

```kotlin
keys {
    "S" to Items.STICK
    "D" to Items.DIAMOND
}
```

## Adding Experience and Cooking Time

For recipes that require cooking:

```kotlin
experience = 0.35  // The amount of experience the recipe yields
cookingTime = 200  // The time in ticks it takes to cook
```

## Loading and Giving Recipes

To load the recipes and make them available:

```kotlin
load {
    recipeGive(allPlayers(), yourRecipeArgument)
}
```

## Utilizing Built-in Recipe Types

Kore provides a variety of built-in recipe types through `RecipeTypes`:

-   `RecipeTypes.BLASTING`
-   `RecipeTypes.CAMPFIRE_COOKING`
-   `RecipeTypes.CRAFTING_SHAPED`
-   `RecipeTypes.CRAFTING_SHAPELESS`
-   `RecipeTypes.CRAFTING_SPECIAL`
-   `RecipeTypes.SMELTING`
-   `RecipeTypes.SMITHING_TRANSFORM`
-   `RecipeTypes.SMITHING_TRIM`
-   `RecipeTypes.SMOKING`
-   `RecipeTypes.STONECUTTING`

## Custom Components

You can customize items using components, but for now Minecraft only allows components on results:

```kotlin
Items.DIAMOND_SWORD {
    damage(10)
    enchantments {
        enchantment(Enchantments.UNBREAKING, 3)
    }
}
```

## Full Example

Here's a full example putting it all together:

```kotlin
fun DataPack.createRecipes() {
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
                "S" to Items.NETHERITE_INGOT
            }
            result = Items.NETHERITE_SWORD {
                enchantments {
                    enchantment(Enchantments.SHARPNESS, 5)
                }
            }
        }
    }
}
```

## Reference Recipes in Commands

To reference recipes in commands, you can use the `recipeBuilder` property and store the recipe inside a variable:

```kotlin
val myRecipe = recipeBuilder.craftingShaped("experience_bottle") {
    pattern(
        " G ",
        "GBG",
        " G "
    )
    keys {
        "G" to Items.GOLD_BLOCK
        "B" to Items.GLASS_BOTTLE
    }
    result(Items.EXPERIENCE_BOTTLE)
}

load {
    // Give the recipe to all players
    recipeGive(allPlayers(), myRecipe)
}
```

## Additional Tips

-   **Grouping Recipes**: You can assign a group to recipes, prefer using it when recipes have the same result.
-   **Tags**: Use tags to reference multiple items sharing the same tag.
-   **Components**: Modify result items with components like `damage`, `enchantments`, etc.

All recipes are stored inside `Datapack.recipes` list, if you ever need to access them programmatically.

By following this guide, you can create complex and customized recipes in your Minecraft data pack using the Kore framework. Be sure to explore the Kore
documentation and source code further to take full advantage of its capabilities.
