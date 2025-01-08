---
root: .components.layouts.MarkdownLayout
title: Predicates
nav-title: Predicates
description: Learn how to use predicates in your Kore datapacks
keywords: minecraft, datapack, kore, predicates, conditions, entity properties
date-created: 2024-01-08
date-modified: 2024-01-08
routeOverride: /docs/predicates
---

# Predicates

Predicates in Minecraft are used to check if certain conditions are met. They can be used in various features like advancements, loot
tables, and commands. Kore provides a type-safe way to create predicates with a simple DSL.

## Basic Usage

Here's a simple example of creating a predicate that checks if a player is holding a diamond pickaxe:

```kotlin
val myPredicate = predicate("test") {
	matchTool {
		item(Items.DIAMOND_PICKAXE)
	}
}
```

## Conditions

Predicates can have multiple conditions that must be met. You can combine them using `allOf` or `anyOf`:

```kotlin
predicate("complex_test") {
	allOf {
		enchantmentActiveCheck(true)
		randomChance(0.5f)
		randomChanceWithEnchantedBonus(
			unenchantedChance = 3f,
			enchantedChance = 2,
			Enchantments.EFFICIENCY
		)
		weatherCheck(raining = true, thundering = false)
	}
}
```

You can also use the `inverted` condition to invert the result of a predicate:

```kotlin
predicate("inverted_test") {
	inverted {
		randomChance(0.5f)
	}
}
```

### Available Conditions

-   `allOf` - Evaluates a list of predicates and passes if all of them pass
-   `anyOf` - Evaluates a list of predicates and passes if any one of them passes
-   `blockStateProperty` - Checks the mined block and its block states
-   `damageSourceProperties` - Checks properties of the damage source
-   `enchantmentActiveCheck` - Checks if an enchantment is active
-   `entityProperties` - Checks properties of an entity
-   `entityScores` - Checks the scoreboard scores of an entity
-   `inverted` - Inverts another predicate condition
-   `killedByPlayer` - Checks if there is an attacking player entity
-   `locationCheck` - Checks the current location against location criteria
-   `matchTool` - Checks tool used to mine the block
-   `randomChance` - Generates a random number between 0.0 and 1.0
-   `randomChanceWithEnchantedBonus` - Random chance with enchantment bonus
-   `reference` - Invokes another predicate file and returns its result
-   `survivesExplosion` - Returns success with probability based on explosion radius
-   `tableBonus` - Passes with probability picked from a list, indexed by enchantment power
-   `timeCheck` - Compares the current day time against given values
-   `valueCheck` - Compares a number against another number or range of numbers
-   `weatherCheck` - Checks the current game weather

Each condition has specific requirements and contexts where it can be used. Some conditions require specific loot context data to be available, while others can be invoked from any context.

## Entity Properties

The `entityProperties` condition allows you to check various properties of an entity:

```kotlin
predicate("entity_check") {
	entityProperties {
		// Check effects
		effects {
			this[Effects.INVISIBILITY] = effect {
				amplifier = rangeOrInt(1)
			}
		}

		// Check equipment
		equipment {
			mainHand = itemStack(Items.DIAMOND_SWORD)
		}

		// Check entity flags
		flags {
			isBaby = true
		}

		// Check location
		location {
			block {
				blocks(Blocks.STONE)
			}
		}

		// Check movement
		movement {
			x(1.0, 4.0)
			horizontalSpeed(1.0)
		}

		// Check NBT data
		nbt {
			this["foo"] = "bar"
		}

		// Check player-specific properties
		playerTypeSpecific {
			gamemodes(Gamemode.SURVIVAL)
		}

		// many more...
	}
}
```

## Sub-Predicates

Sub-predicates are nested data structures that allow you to define specific properties to check within a predicate condition. Each condition type can have its own set of sub-predicates.

### Entity Sub-Predicates

The `entityProperties` condition supports various sub-predicates to check different aspects of an entity:

-   `distance` - Check distance between entities
-   `effects` - Check potion effects
-   `equipment` - Check equipped items
-   `flags` - Check entity flags (baby, on fire, etc.)
-   `location` - Check entity location
-   `movement` - Check entity movement
-   `movementAffectedBy` - Check what affects entity movement
-   `nbt` - Check entity NBT data
-   `passenger` - Check entity passenger
-   `periodicTicks` - Check entity periodic ticks
-   `slots` - Check specific inventory slots
-   `steppingOn` - Check block the entity is standing on
-   `targetedEntity` - Check entity being targeted
-   `team` - Check entity team
-   `type` - Check entity type
-   `typeSpecific` - Check type-specific properties
-   `vehicle` - Check entity vehicle

The `Entity` class will provide all the functions for these sub-predicates.

### Entity Type-Specific Properties

Entities can have type-specific properties that can be checked using specialized sub-predicates. Here are some examples:

```kotlin
// Check axolotl variant
predicate("axolotl_check") {
	entityProperties {
		axolotlTypeSpecific(AxolotlVariants.LUCY)
	}
}

// Check cat variant
predicate("cat_check") {
	entityProperties {
		catTypeSpecific(CatVariants.WHITE)
	}
}

// Check player properties
predicate("player_check") {
	entityProperties {
		playerTypeSpecific {
			gamemodes(Gamemode.CREATIVE)
			recipes {
				this[Recipes.BOW] = true
			}
			input {
				forward = true
				backward = false
				sprint = true
			}
		}
	}
}

// Check villager type
predicate("villager_check") {
	entityProperties {
		villagerTypeSpecific(VillagerTypes.JUNGLE)
	}
}
```

Available type-specific checks include:

-   `axolotlTypeSpecific` - Axolotl variants
-   `catTypeSpecific` - Cat variants
-   `fishingHookTypeSpecific` - Fishing hook properties
-   `foxTypeSpecific` - Fox variants
-   `frogTypeSpecific` - Frog variants
-   `horseTypeSpecific` - Horse variants
-   `lightningTypeSpecific` - Lightning properties
-   `llamaTypeSpecific` - Llama variants
-   `mooshroomTypeSpecific` - Mooshroom variants
-   `paintingTypeSpecific` - Painting variants
-   `parrotTypeSpecific` - Parrot variants
-   `playerTypeSpecific` - Player properties (gamemode, recipes, input)
-   `rabbitTypeSpecific` - Rabbit variants
-   `raiderTypeSpecific` - Raider properties
-   `salmonTypeSpecific` - Salmon variants
-   `sheepTypeSpecific` - Sheep properties
-   `slimeTypeSpecific` - Slime size
-   `tropicalFishTypeSpecific` - Tropical fish variants
-   `villagerTypeSpecific` - Villager types
-   `wolfTypeSpecific` - Wolf variants

### Item Sub-Predicates

When using `matchTool` or checking equipment, you can use item sub-predicates. There are two main ways to check item properties:

1. Basic item properties:

```kotlin
predicate("basic_item_check") {
	matchTool {
		item(Items.DIAMOND_SWORD)
		count = rangeOrInt(1..64)
		durability = rangeOrInt(0..100)
	}
}
```

2. Component Matchers - A powerful system to check component properties:

```kotlin
predicate("component_check") {
	matchTool {
		item(Items.DIAMOND_SWORD)
		predicates {
			// Check damage and durability
			damage {
				durability(1)
				damage = rangeOrInt(4..5)
			}

			// Check enchantments
			enchantments {
				enchantment(Enchantments.SHARPNESS, level = 3)
			}
		}
	}
}
```

Component Matchers allow you to check various item components like:

-   Attribute modifiers
-   Container contents (bundles, shulker boxes)
-   Damage and durability
-   Enchantments
-   Firework properties
-   Book contents
-   And many more

Each matcher corresponds to a component type in Minecraft and provides type-safe ways to check their properties. For a complete list of available matchers, refer to the `arguments.components.matchers` package in the source code.

## Using Predicates in Commands

You can use predicates in commands using the execute command:

```kotlin
function("test") {
	execute {
		ifCondition {
			predicate(myPredicate)
		}
		run {
			debug("predicate validated!")
		}
	}
}
```

## Item Predicates

You can also create predicates for items with enchantments:

```kotlin
predicate("enchanted_tool") {
	matchTool {
		item(Items.DIAMOND_PICKAXE)
		predicates {
			enchantments(enchantment(Enchantments.EFFICIENCY))
		}
	}
}
```

## Best Practices

1. Give your predicates descriptive names that reflect their purpose
2. Use `allOf` and `anyOf` to combine multiple conditions logically
3. Keep predicates focused and reusable
4. Test your predicates in-game to ensure they work as expected

Remember that predicates are powerful tools for creating complex conditions in your datapack. They can be used to create sophisticated game
mechanics and enhance player experience.
