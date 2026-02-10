---
root: .components.layouts.MarkdownLayout
title: Predicates
nav-title: Predicates
description: Learn how to use predicates in your Kore datapacks
keywords: minecraft, datapack, kore, predicates, conditions, entity properties
date-created: 2024-01-08
date-modified: 2026-02-03
routeOverride: /docs/data-driven/predicates
---

# Predicates

Predicates are JSON structures used in data packs to check conditions within the world. They return a pass or fail result to the invoker, which acts differently based on the result. In practical terms, predicates are a flexible way for data packs to encode "if this, then that" logic without needing custom code.

Predicates can be used in:

- **Commands**: Via `/execute if predicate` or target selector argument `predicate=`
- **Loot tables**: As conditions for loot entries
- **Advancements**: As trigger conditions
- **Other predicates**: Via the `reference` condition

Kore provides a type-safe DSL to create predicates, eliminating the need to write raw JSON.

## Basic Usage

Here's a simple example of creating a predicate that checks if a player is holding a diamond pickaxe:

```kotlin
val myPredicate = predicate("test") {
	matchTool {
		item(Items.DIAMOND_PICKAXE)
	}
}
```

The `predicate` function creates and registers a predicate in your DataPack. It produces a file at
`data/<namespace>/predicate/<fileName>.json` and returns a `PredicateArgument` that can be used in commands.

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

Conditions are categorized by their **loot context requirements
**. Some conditions can be invoked from any context, while others require specific data to be available.

#### Universal Conditions (invokable from any context)

| Condition          | Description                                                                        |
|--------------------|------------------------------------------------------------------------------------|
| `allOf`            | Evaluates a list of predicates and passes if **all** of them pass                  |
| `anyOf`            | Evaluates a list of predicates and passes if **any one** of them passes            |
| `entityProperties` | Checks properties of an entity                                                     |
| `inverted`         | Inverts another predicate condition                                                |
| `randomChance`     | Generates a random number between 0.0 and 1.0, passes if less than specified value |
| `reference`        | Invokes another predicate file and returns its result (cannot be cyclic)           |
| `timeCheck`        | Compares the current day time against given values (supports `period` for modulo)  |
| `valueCheck`       | Compares a number against another number or range                                  |
| `weatherCheck`     | Checks the current game weather (raining, thundering)                              |

#### Context-Dependent Conditions

These conditions require specific loot context data and will **always fail** if that data is not provided:

| Condition                        | Required Context            | Description                                                                           |
|----------------------------------|-----------------------------|---------------------------------------------------------------------------------------|
| `blockStateProperty`             | Block state                 | Checks the mined block and its block states                                           |
| `damageSourceProperties`         | Origin + damage source      | Checks properties of the damage source                                                |
| `enchantmentActiveCheck`         | Enchantment active status   | Checks if an enchantment is active (only usable from `enchanted_location` context)    |
| `entityScores`                   | Specified entity            | Checks the scoreboard scores of an entity                                             |
| `killedByPlayer`                 | `attacking_player` entity   | Checks if there is an attacking player entity                                         |
| `locationCheck`                  | Origin                      | Checks the current location against location criteria (supports offsets)              |
| `matchTool`                      | Tool                        | Checks tool used to mine the block                                                    |
| `randomChanceWithEnchantedBonus` | Attacker entity (optional)  | Random chance modified by enchantment level (level 0 if no attacker)                  |
| `survivesExplosion`              | Explosion radius (optional) | Returns success with 1 ÷ explosion radius probability (always passes if no explosion) |
| `tableBonus`                     | Tool (optional)             | Passes with probability from a list indexed by enchantment power (level 0 if no tool) |

## Entity Properties

The
`entityProperties` condition allows you to check various properties of an entity. You must specify which entity to check using the
`entity` parameter:

### Entity Context Options

| Value                | Description                                               |
|----------------------|-----------------------------------------------------------|
| `this`               | The entity that invoked the predicate (default)           |
| `attacker`           | The entity that attacked                                  |
| `direct_attacker`    | The direct cause of damage (e.g., arrow, not the shooter) |
| `attacking_player`   | The attacking player specifically                         |
| `target_entity`      | The targeted entity                                       |
| `interacting_entity` | The entity interacting with something                     |

### Entity Predicate Example

```kotlin
predicate("entity_check") {
	entityProperties {
		// Check entity components (e.g., axolotl variant)
		components {
			axolotlVariant(AxolotlVariants.CYAN)
			damage(12)
			!unbreakable()  // Negated component check
		}

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

		// Check what affects entity movement
		movementAffectedBy {
			canSeeSky = true
		}

		// Check NBT data
		nbt {
			this["foo"] = "bar"
		}

		// Check entity passenger
		passenger {
			team = "foo"
		}

		// Check custom data predicates
		predicates {
			customData {
				this["foo"] = "bar"
			}
		}

		// Check specific inventory slots
		slots {
			this[WEAPON.MAINHAND] = itemStack(Items.DIAMOND_SWORD)
		}

		// Check block the entity is standing on
		steppingOn {
			blocks(Blocks.STONE)
			components {
				damage(5)
			}
			predicates {
				customData {
					this["foo"] = "bar"
				}
			}
			state("up", "bottom")
		}

		// Check entity type
		type(EntityTypes.MARKER)

		// Check player-specific properties
		playerTypeSpecific {
			gamemodes(Gamemode.SURVIVAL)
		}

		// Check entity vehicle with distance
		vehicle {
			distance {
				x(1f..4f)
				z(1f)
			}
		}
	}
}
```

## Sub-Predicates

Sub-predicates are nested data structures that allow you to define specific properties to check within a predicate condition. Each condition type can have its own set of sub-predicates.

### Entity Sub-Predicates

The `entityProperties` condition supports various sub-predicates to check different aspects of an entity:

| Sub-Predicate        | Description                              | Example                                                                  |
|----------------------|------------------------------------------|--------------------------------------------------------------------------|
| `components`         | Check entity data components             | `components { axolotlVariant(AxolotlVariants.CYAN) }`                    |
| `distance`           | Check distance between entities          | `distance { x(1f..4f) }`                                                 |
| `effects`            | Check potion effects                     | `effects { this[Effects.SPEED] = effect { amplifier = rangeOrInt(1) } }` |
| `equipment`          | Check equipped items                     | `equipment { mainHand = itemStack(Items.DIAMOND_SWORD) }`                |
| `flags`              | Check entity flags (baby, on fire, etc.) | `flags { isBaby = true }`                                                |
| `location`           | Check entity location                    | `location { block { blocks(Blocks.STONE) } }`                            |
| `movement`           | Check entity movement                    | `movement { x(1.0, 4.0); horizontalSpeed(1.0) }`                         |
| `movementAffectedBy` | Check what affects entity movement       | `movementAffectedBy { canSeeSky = true }`                                |
| `nbt`                | Check entity NBT data                    | `nbt { this["foo"] = "bar" }`                                            |
| `passenger`          | Check entity passenger                   | `passenger { team = "foo" }`                                             |
| `periodicTicks`      | Check entity periodic ticks              | `periodicTicks = 20`                                                     |
| `predicates`         | Check custom data predicates             | `predicates { customData { this["key"] = "value" } }`                    |
| `slots`              | Check specific inventory slots           | `slots { this[WEAPON.MAINHAND] = itemStack(Items.DIAMOND_SWORD) }`       |
| `steppingOn`         | Check block the entity is standing on    | `steppingOn { blocks(Blocks.STONE) }`                                    |
| `targetedEntity`     | Check entity being targeted              | `targetedEntity { type(EntityTypes.ZOMBIE) }`                            |
| `team`               | Check entity team                        | `team = "my_team"`                                                       |
| `type`               | Check entity type                        | `type(EntityTypes.MARKER)`                                               |
| `typeSpecific`       | Check type-specific properties           | See [Type-Specific Properties](#entity-type-specific-properties)         |
| `vehicle`            | Check entity vehicle                     | `vehicle { distance { x(1f..4f) } }`                                     |

The `Entity` class provides all the functions for these sub-predicates.

### Entity Type-Specific Properties

Entities can still expose a handful of hard-coded type-specific predicates (mainly utility ones such as fishing hooks, lightning, player, raider, sheep and slime). All the visual
*variant* checks that existed before snapshot **25w04a** were migrated by Mojang to the new **components
** system. Kore therefore removed the dedicated helpers (`axolotlTypeSpecific`,
`catTypeSpecific`, …) in favor of component matching.

#### Component-based variant checks (25w04a +)

You can now query an entity’s data components directly from `entityProperties` with the `components` block:

```kotlin
// Check axolotl variant via its component
predicate("axolotl_component_check") {
    entityProperties {
        components {
            axolotlVariant(AxolotlVariants.LUCY)
        }
    }
}
```

Any component you can put on an **item** can be matched on an **entity
** in exactly the same way – just call the corresponding extension inside the `components {}` scope.

#### Remaining built-in `typeSpecific` helpers

These helpers are still available because they cover information that is **not** represented by components:

##### Fishing Hook

Check if a fishing hook is in open water:

```kotlin
predicate("fishing_hook_check") {
	entityProperties {
		fishingHookTypeSpecific(inOpenWater = true)
	}
}
```

##### Lightning

Check lightning bolt properties like blocks set on fire:

```kotlin
predicate("lightning_check") {
	entityProperties {
		lightningTypeSpecific {
			blocksSetOnFire = rangeOrInt(1..5)
		}
	}
}
```

##### Player

Check player-specific properties including gamemode, unlocked recipes, and input state:

```kotlin
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
				left = true
				right = false
				jump = true
				sneak = false
				sprint = true
			}
		}
	}
}
```

##### Raider

Check raider properties like raid participation and captain status:

```kotlin
predicate("raider_check") {
	entityProperties {
		raiderTypeSpecific(hasRaid = true, isCaptain = false)
	}
}
```

##### Sheep

Check if a sheep has been sheared:

```kotlin
predicate("sheep_check") {
	entityProperties {
		sheepTypeSpecific(sheared = true)
	}
}
```

##### Slime

Check slime size:

```kotlin
predicate("slime_check") {
	entityProperties {
		slimeTypeSpecific(rangeOrInt(2))
	}
}
```

> **Note**   All former
`*TypeSpecific` helpers that dealt with variants (axolotl, cat, fox, frog, horse, llama, mooshroom, painting, parrot, pig, rabbit, salmon, tropical fish, villager, wolf) have been removed. Update your predicates to use component matching instead.

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

- Attribute modifiers
- Container contents (bundles, shulker boxes)
- Damage and durability
- Enchantments
- Firework properties
- Book contents
- And many more

Each matcher corresponds to a component type in Minecraft and provides type-safe ways to check their properties. For a complete list of available matchers, refer to the
`arguments.components.matchers` package in the source code.

## Using Predicates in Commands

Predicates can be invoked in commands in two ways:

### Execute If Predicate

Use `/execute if predicate` to conditionally run commands:

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

### Target Selector Argument

Use the `predicate=` selector argument to filter entities:

```kotlin
function("filter_entities") {
	// Kill all entities matching the predicate
	kill(allEntities { predicate = myPredicate })
}
```

### Pairing with Inventory Manager

Predicates excel at validating complex item properties. When you need to both validate and actively manage inventories (e.g., keep a GUI slot populated with an item matching specific components), use them alongside the [Inventory Manager](./helpers/inventory-manager).

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

## Referencing Other Predicates

Use the `reference` condition to invoke another predicate file:

```kotlin
val basePredicate = predicate("base_check") {
	weatherCheck(raining = true)
}

predicate("combined_check") {
	allOf {
		reference(basePredicate)
		randomChance(0.5f)
	}
}
```

> **Warning**: Cyclic references (predicate A references B, which references A) will cause a parsing failure.

## Best Practices

1. **Descriptive names**: Give your predicates names that reflect their purpose (e.g., `is_holding_sword`, `in_rain_at_night`)
2. **Logical composition**: Use `allOf` and `anyOf` to combine multiple conditions clearly
3. **Reusability**: Keep predicates focused on a single concern and use `reference` to compose them
4. **Context awareness
   **: Be mindful of which loot context your predicate will be invoked from. Context-dependent conditions will silently fail if required data is missing
5. **Testing**: Test your predicates in-game using `/execute if predicate <name>` to verify they work as expected

Predicates are powerful tools for creating complex conditions in your datapack. They enable sophisticated game mechanics and enhance player experience without requiring custom code.

## See Also

- [Loot Tables](/docs/data-driven/loot-tables) - Use predicates as conditions for loot entries
- [Advancements](/docs/data-driven/advancements) - Use predicates as trigger conditions
- [Item Modifiers](/docs/data-driven/item-modifiers) - Modify items conditionally with predicates
- [Components](/docs/concepts/components) - Item and entity data components used in predicate checks
- [Commands](/docs/commands/commands) - Using predicates with `/execute if predicate`
- [Inventory Manager](/docs/helpers/inventory-manager) - Pair predicates with inventory management
- [Tags](/docs/data-driven/tags) - Use tags in predicate conditions

### External Resources

- [Minecraft Wiki: Predicate](https://minecraft.wiki/w/Predicate) - Official JSON format reference
- [Minecraft Wiki: Loot context](https://minecraft.wiki/w/Loot_context) - Understanding loot contexts for conditions
