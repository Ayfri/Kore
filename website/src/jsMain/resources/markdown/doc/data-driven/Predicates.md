---
root: .components.layouts.MarkdownLayout
title: Minecraft Predicates - Type-Safe Condition DSL in Kore
nav-title: Predicates
description: Create Minecraft predicates with Kore's type-safe Kotlin DSL. Covers entity properties, location, weather, time, enchantments, damage, and NBT checks. Use in execute if/unless, loot tables, and advancements.
keywords: minecraft predicates, datapack conditions, execute if predicate, entity properties check, location check, weather check, time check, damage predicate, kore predicates, minecraft condition dsl
date-created: 2024-01-08
date-modified: 2026-10-23
routeOverride: /docs/data-driven/predicates
---

# Predicates

Predicates are JSON structures used in data packs to check conditions within the world. They return a pass or fail result to the invoker, which acts differently based on the result. In practical terms, predicates are a flexible way for data packs to encode "if this, then that" logic without needing custom code.

Predicates can be used in:

- **Commands**: Via [`execute if predicate`](/docs/commands/execute) or target selector argument `predicate=`
- **Loot tables**: As conditions for loot entries
- **Advancements**: As trigger conditions
- **Other predicates**: By their ID, see [Referencing Other Predicates](#referencing-other-predicates)

Kore provides a type-safe DSL to create predicates, eliminating the need to write raw JSON.

## Basic Usage

Here's a simple example of creating a predicate that checks if a player is holding a diamond pickaxe:

```kotlin
val myPredicate = predicate("test") {
	matchTool {
		items(Items.DIAMOND_PICKAXE)
	}
}
```

The `predicate` function creates and registers a predicate in your DataPack. It produces a file at
`data/<namespace>/predicate/<fileName>.json` and returns a `PredicateArgument` that can be used in commands.

## Conditions

A predicate file holds **one** condition, whose JSON object names its kind in the `type` key. Declaring several
conditions in a `predicate { }` block writes them as the terms of one `minecraft:all_of`, so both predicates below
produce the same file:

```kotlin
predicate("two_conditions") {
	randomChance(0.5f)
	weatherCheck(raining = true)
}

predicate("explicit_all_of") {
	allOf {
		randomChance(0.5f)
		weatherCheck(raining = true)
	}
}
```

```json
{
	"type": "minecraft:all_of",
	"terms": [
		{
			"type": "minecraft:random_chance",
			"chance": 0.5
		},
		{
			"type": "minecraft:weather_check",
			"raining": true
		}
	]
}
```

A block with one condition writes that condition alone, and an empty block writes an `all_of` without terms, which
always passes. `terms` of `allOf` and `anyOf` is written inline, without the array, when it holds a single condition.

You can combine conditions using `allOf` or `anyOf`:

```kotlin
predicate("complex_test") {
	allOf {
		enchantmentActiveCheck(true)
		randomChance(0.5f)
		randomChanceWithEnchantedBonus(
           unenchantedChance = 0.3f,
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

Conditions are categorized by their **loot context requirements**. Some conditions can be invoked from any context, while others require specific data to be available.

#### Universal Conditions (invokable from any context)

| Condition                   | Description                                                                                                                                                                                                             |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `allOf`                     | Evaluates its conditions and passes if **all** of them pass                                                                                                                                                             |
| `anyOf`                     | Evaluates its conditions and passes if **any one** of them passes                                                                                                                                                       |
| `entityProperties`          | Checks properties of an entity                                                                                                                                                                                          |
| `environmentAttributeCheck` | Passes if the specified environment attribute currently matches the given value                                                                                                                                         |
| `floatValueCheck`           | Compares a float provider value against a float provider (exact match) or a `{ min, max }` range of float providers                                                                                                    |
| `intValueCheck`             | Compares an int provider value against an int provider (exact match) or a `{ min, max }` range of int providers                                                                                                         |
| `inverted`                  | Inverts another predicate condition                                                                                                                                                                                     |
| `randomChance`              | Passes if a random float between 0.0 and 1.0 is below the given float provider                                                                                                                                          |
| `reference`                 | Invokes another predicate by ID (cannot be cyclic), written as the bare ID string                                                                                                                                       |
| `timeCheck`                 | Compares a world clock's time against an int provider or an int provider range (mandatory `clock` to select the clock, optional `period` for modulo) - see [World Clocks](/docs/data-driven/world-clocks#timecheck-predicate-condition) |
| `weatherCheck`              | Checks the current game weather (raining, thundering)                                                                                                                                                                   |

> `intValueCheck`, `floatValueCheck`, `timeCheck` and `entityScores` take [int providers](/docs/data-driven/loot-tables#context-providers) for their
> values and bounds, `randomChance` and `floatValueCheck` take float providers, so you can use dynamic values like
> scoreboard scores, enchantment levels, or environment attributes instead of plain numbers.

### Value Checks

`intValueCheck` and `floatValueCheck` compare the `value` provider with `test`. `test` is either one provider, which
`value` must equal exactly, or a range whose `min` and `max` are providers and either of which can be left out:

```kotlin
predicate("kills") {
	intValueCheck(score("kills", EntityTarget.THIS), 0..10)                    // "test": { "max": 10, "min": 0 }
	intValueCheck(score("kills", EntityTarget.THIS), intValue(3))              // "test": 3
	intValueCheck(score("kills", EntityTarget.THIS), min = constant(5))        // "test": { "min": 5 }
	floatValueCheck(enchantmentLevel(1), 0f..1f)                               // "test": { "max": 1.0, "min": 0.0 }
}
```

`intRange(min, max)` and `floatRange(min, max)` build the same ranges with a `null` bound leaving that side open, and
`intValue(...)` and `floatValue(...)` build the exact match.

`entityScores` takes one int range per objective and `timeCheck` one for the clock time. Int providers are exact 32-bit
integers, so scores and bounds stay exact on their whole range:

```kotlin
predicate("veteran") {
	entityScores {
		this["kills"] = intRange(10, 100)  // { "max": 100, "min": 10 }
		this["deaths"] = intValue(0)       // 0
	}
}

predicate("daytime") {
	timeCheck(WorldClocks.OVERWORLD, 0..12000, period = 24000)  // "value": { "max": 12000, "min": 0 }, "period": 24000
}
```

A context provider file can also be used by ID through `reference(...)`, in `randomChance`, in `value` and in the bounds of
the `test` ranges of `intValueCheck`, `floatValueCheck`, `timeCheck` and `entityScores`:

```kotlin
val chance = ContextFloatProviderArgument("drop_chance", "my_pack")
val minimum = ContextIntProviderArgument("min_kills", "my_pack")

predicate("lucky") {
	randomChance(reference(chance))
	intValueCheck(score("kills", EntityTarget.THIS), intRange(reference(minimum), constant(10)))
}
```

```json
{
  "type": "minecraft:all_of",
  "terms": [
    {
      "type": "minecraft:random_chance",
      "chance": "my_pack:drop_chance"
    },
    {
      "type": "minecraft:int_value_check",
      "test": {
        "max": 10,
        "min": "my_pack:min_kills"
      },
      "value": {
        "type": "minecraft:score",
        "score": "kills",
        "target": {
          "type": "minecraft:context",
          "target": "this"
        }
      }
    }
  ]
}
```

### Environment Attribute Check

`environmentAttributeCheck` passes when the specified environment attribute equals the given value.
The value type is inferred from the attribute - booleans for toggle attributes, floats for numeric ones, strings for
enum-like ones (moon phase, villager activity), and objects for compound ones (ambient sounds, background music).

```kotlin
predicate("is_daytime") {
   // Boolean attribute - convenience overload, no wrapping needed
   environmentAttributeCheck(EnvironmentAttributes.Gameplay.MONSTERS_BURN, true)
}

predicate("dim_sky") {
   // Float attribute - convenience overload, no wrapping needed
   environmentAttributeCheck(EnvironmentAttributes.Visual.SKY_LIGHT_FACTOR, 0.5f)
}

predicate("full_moon") {
   // Builder block: call exactly one typed helper from EnvironmentAttributesScope.
   // It sets both the attribute ID and the expected value automatically.
   environmentAttributeCheck {
      moonPhase(Textures.Environment.Celestial.Moon.FULL_MOON)
   }
}
```

The builder block accepts any number of calls from the same scope helpers used in dimension types and biomes (
`moonPhase`, `beesStayInHive`, `fogColor`, `ambientSounds`, etc.).
Each attribute set in the block produces one `environment_attribute_check` condition, so multiple attributes are an
implicit AND - all must match for the predicate to pass.

#### Context-Dependent Conditions

Most of these conditions require specific loot context data and will **always fail** if not provided. Three exceptions
have optional context with graceful fallback behavior (noted in the table):

| Condition                        | Required Context            | Description                                                                           |
|----------------------------------|-----------------------------|---------------------------------------------------------------------------------------|
| `damageSourceProperties`         | Origin + damage source      | Checks properties of the damage source                                                |
| `enchantmentActiveCheck`         | Enchantment active status   | Checks if an enchantment is active (only usable from `enchanted_location` context)    |
| `entityScores`                   | Specified entity            | Checks scoreboard scores of an entity against int provider ranges                     |
| `killedByPlayer`                 | `attacking_player` entity   | Checks if there is an attacking player entity                                         |
| `locationCheck`                  | Origin                      | Checks the current location against location criteria (supports offsets)              |
| `matchBlock`                     | Block state                 | Checks the mined block, its block states and its block entity                         |
| `matchTool`                      | Tool                        | Checks tool used to mine the block                                                    |
| `randomChanceWithEnchantedBonus` | Attacker entity (optional)  | Random chance modified by enchantment level (level 0 if no attacker)                  |
| `survivesExplosion`              | Explosion radius (optional) | Returns success with 1 ÷ explosion radius probability (always passes if no explosion) |
| `tableBonus`                     | Tool (optional)             | Passes with probability from a list indexed by enchantment power (level 0 if no tool) |

## Match Block

`matchBlock` checks the block of the loot context, such as the block a loot table drops from. It takes the blocks or
block tags to match, and a block with the same checks as the `can_place_on` and `can_break` item components: `state`,
`states`, `nbt`, `components` and `predicates`. A single block or tag is written as a string, several as a list.

```kotlin
predicate("mature_wheat") {
	matchBlock(Blocks.WHEAT) { state("age", "7") }
}

predicate("furnaces") {
	matchBlock(Blocks.SMOKER, Blocks.BLAST_FURNACE)
}

predicate("lit_logs") {
	matchBlock(Tags.Block.LOGS) {
		states {
			this["axis"] = "y"
			this["lit"] = "true"
		}
	}
}
```

```json
{
	"type": "minecraft:match_block",
	"blocks": "minecraft:wheat",
	"state": {
		"age": "7"
	}
}
```

## Entity Properties

The
`entityProperties` condition allows you to check various properties of an entity. You must specify which entity to check using the
`entity` parameter:

### Entity Context Options

The `entity` parameter takes an `EntityTarget`:

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
			this[Effects.INVISIBILITY] = mobEffectPredicate {
				amplifier = rangeOrInt(1)
			}
		}

		// Check equipment, one entry per equipment slot
		equipment {
			mainHand = itemStackPredicate(Items.DIAMOND_SWORD)
			body = itemStackPredicate(Items.SADDLE)
		}

		// Check entity flags
		flags {
			isBaby = true
		}

		// Check location
		location {
			block(Blocks.STONE)
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
			team("foo")
		}

		// Check scoreboard tags set through /tag
		entityTags {
			allOf("boss")
			noneOf("tamed")
		}

		// Check the entity this one's AI is targeting
		targetedEntity {
			entityType(EntityTypes.VILLAGER)
		}

		// Check custom data predicates
		predicates {
			customData {
				this["foo"] = "bar"
			}
		}

		// Check specific inventory slots
		slots {
			this[WEAPON.MAINHAND] = itemStackPredicate(Items.DIAMOND_SWORD)
		}

		// Check block the entity is standing on
		steppingOn {
			block(Blocks.STONE) {
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
		}

		// Check entity type
		entityType(EntityTypes.MARKER)

		// Check player-specific properties
		typeSpecific {
			player {
				gamemodes(Gamemode.SURVIVAL)
			}
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

### Damage Source Tags

`damageSourceProperties` (and the `type` of an advancement trigger's `damage`) matches the damage type against damage
types and damage type tags. `tag(...)` takes any number of damage types or tags and writes a tag or a single type as a
bare string and several as a list. `expected = false` requires the damage type to match none of them.

```kotlin
predicate("fire_damage") {
	damageSourceProperties {
		tag(Tags.DamageType.IS_FIRE)
		tag(Tags.DamageType.BYPASSES_INVULNERABILITY, expected = false)
		tag(DamageTypes.LAVA, DamageTypes.IN_FIRE)
	}
}
```

```json
{
  "type": "minecraft:damage_source_properties",
  "predicate": {
    "tags": [
      {
        "expected": true,
        "id": "#minecraft:is_fire"
      },
      {
        "expected": false,
        "id": "#minecraft:bypasses_invulnerability"
      },
      {
        "expected": true,
        "id": ["minecraft:lava", "minecraft:in_fire"]
      }
    ]
  }
}
```

### Entity Sub-Predicates

The `entityProperties` condition supports various sub-predicates to check different aspects of an entity. Each one is
serialized as its own identifier-keyed entry (`minecraft:<name>`) rather than a flat object:

| Sub-Predicate          | JSON key                         | Description                              | Example                                                                              |
|------------------------|----------------------------------|------------------------------------------|--------------------------------------------------------------------------------------|
| `components`           | `minecraft:components`           | Check entity data components             | `components { axolotlVariant(AxolotlVariants.CYAN) }`                                |
| `distance`             | `minecraft:distance`             | Check distance between entities          | `distance { x(1f..4f) }`                                                             |
| `effects`              | `minecraft:effects`              | Check potion effects                     | `effects { this[Effects.SPEED] = mobEffectPredicate { amplifier = rangeOrInt(1) } }` |
| `entityTags`           | `minecraft:entity_tags`          | Check scoreboard tags set through `/tag` | `entityTags { allOf("boss") }`                                                       |
| `entityType`           | `minecraft:entity_type`          | Check entity type                        | `entityType(EntityTypes.MARKER)`                                                     |
| `equipment`            | `minecraft:equipment`            | Check equipped items                     | `equipment { mainHand = itemStackPredicate(Items.DIAMOND_SWORD) }`                   |
| `flags`                | `minecraft:flags`                | Check entity flags (baby, on fire, etc.) | `flags { isBaby = true }`                                                            |
| `location`             | `minecraft:location`             | Check entity location                    | `location { block(Blocks.STONE) }`                                                   |
| `movement`             | `minecraft:movement`             | Check entity movement                    | `movement { x(1.0, 4.0); horizontalSpeed(1.0) }`                                     |
| `movementAffectedBy`   | `minecraft:movement_affected_by` | Check what affects entity movement       | `movementAffectedBy { canSeeSky = true }`                                            |
| `nbt`                  | `minecraft:nbt`                  | Check entity NBT data                    | `nbt { this["foo"] = "bar" }`                                                        |
| `passenger`            | `minecraft:passenger`            | Check entity passenger                   | `passenger { team("foo") }`                                                          |
| `periodicTick`         | `minecraft:periodic_tick`        | Check entity periodic ticks              | `periodicTick(20)`                                                                   |
| `predicates`           | `minecraft:predicates`           | Check custom data predicates             | `predicates { customData { this["key"] = "value" } }`                                |
| `slots`                | `minecraft:slots`                | Check specific inventory slots           | `slots { this[WEAPON.MAINHAND] = itemStackPredicate(Items.DIAMOND_SWORD) }`          |
| `steppingOn`           | `minecraft:stepping_on`          | Check block the entity is standing on    | `steppingOn { block(Blocks.STONE) }`                                                 |
| `targetedEntity`       | `minecraft:targeted_entity`      | Check entity being targeted              | `targetedEntity { entityType(EntityTypes.ZOMBIE) }`                                  |
| `team`                 | `minecraft:team`                 | Check entity team                        | `team("my_team")`                                                                    |
| `typeSpecific { ... }` | `minecraft:type_specific/<name>` | Check type-specific properties           | See [Type-Specific Properties](#entity-type-specific-properties)                     |
| `vehicle`              | `minecraft:vehicle`              | Check entity vehicle                     | `vehicle { distance { x(1f..4f) } }`                                                 |

The `EntityPredicate` class provides all the functions for these sub-predicates, backed by a single
`EntitySubPredicate` sealed family - each call appends one entry to `EntityPredicate.subPredicates`.

### Entity Type-Specific Properties

Entities expose a handful of hard-coded type-specific predicates, covering the state that data components do not:
fishing hooks, lightning bolts, players, raiders, sheep and cube mobs. Every visual *variant* check lives in the
components system instead, so it is matched through the `components` block.

#### Component-based variant checks

Query an entity's data components directly from `entityProperties` with the `components` block:

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

Any component you can put on an **item** can be matched on an **entity** in exactly the same way - just call the
corresponding extension inside the `components {}` scope.

#### Built-in `typeSpecific` helpers

These helpers cover information that is **not** represented by components. They're grouped under a `typeSpecific { }`
scope, and each keys under `minecraft:type_specific/<name>`:

##### Fishing Hook

Check if a fishing hook is in open water:

```kotlin
predicate("fishing_hook_check") {
	entityProperties {
		typeSpecific {
			fishingHook(inOpenWater = true)
		}
	}
}
```

##### Lightning

Check lightning bolt properties like blocks set on fire:

```kotlin
predicate("lightning_check") {
	entityProperties {
		typeSpecific {
			lightning {
				blocksSetOnFire = rangeOrInt(1..5)
			}
		}
	}
}
```

##### Player

Check player-specific properties including gamemode, experience level, food stats, unlocked recipes, statistics, what
the player is looking at, and input state:

```kotlin
predicate("player_check") {
	entityProperties {
		typeSpecific {
			player {
				gamemodes(Gamemode.CREATIVE)
				level = rangeOrInt(1..5)
				food {
					level = rangeOrInt(5..15)
					saturation = rangeOrDouble(1.0, 10.0)
				}
				recipes {
					this[Recipes.BOW] = true
				}
				lookingAt {
					entityType(EntityTypes.CREEPER)
				}
				stats {
					statistic(StatisticTypes.CUSTOM, CustomStats.JUMP, 10)
					statistic(StatisticTypes.MINED, Blocks.STONE, 1..5)
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
}
```

##### Raider

Check raider properties like raid participation and captain status:

```kotlin
predicate("raider_check") {
	entityProperties {
		typeSpecific {
			raider(hasRaid = true, isCaptain = false)
		}
	}
}
```

##### Sheep

Check if a sheep has been sheared:

```kotlin
predicate("sheep_check") {
	entityProperties {
		typeSpecific {
			sheep(sheared = true)
		}
	}
}
```

##### Cube Mob (slimes and magma cubes)

Check cube mob size, keyed under `minecraft:type_specific/cube_mob`:

```kotlin
predicate("cube_mob_check") {
	entityProperties {
		typeSpecific {
			cubeMob(rangeOrInt(2))
		}
	}
}
```

> **Note**   Variant checks (axolotl, cat, fox, frog, horse, llama, mooshroom, painting, parrot, pig, rabbit, salmon,
> tropical fish, villager, wolf) are component matches, not `typeSpecific` helpers.

### Item Sub-Predicates

When using `matchTool` or checking equipment, you can use item sub-predicates. There are two main ways to check item properties:

1. Basic item properties:

```kotlin
predicate("basic_item_check") {
	matchTool {
		items(Items.DIAMOND_SWORD)
		count(1..64)
	}
}
```

2. Component Matchers - A powerful system to check component properties:

```kotlin
predicate("component_check") {
	matchTool {
		items(Items.DIAMOND_SWORD)
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

Each matcher corresponds to a component type in Minecraft and provides type-safe ways to check their properties. See the
[Available Component Matchers](/docs/concepts/components#available-component-matchers) table in the Components guide for
the full list.

## Using Predicates in Commands

Predicates can be invoked in commands in two ways:

### Execute If Predicate

Use `/execute if predicate` to conditionally run commands. If you need a refresher on the surrounding execution DSL,
the [Commands](/docs/commands/commands) page covers the broader command surface:

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

Use the `predicate=` selector argument to filter entities. This pairs naturally with Kore's
typed [Selectors](/docs/concepts/selectors):

```kotlin
function("filter_entities") {
	// Kill all entities matching the predicate
	kill(allEntities { predicate = myPredicate })
}
```

### Pairing with Inventory Manager

Predicates excel at validating complex item properties. When you need to both validate and actively manage inventories (
e.g., keep a GUI slot populated with an item matching specific [Components](/docs/concepts/components)), use them
alongside the [Inventory Manager](/docs/helpers/inventory-manager).

## Item Predicates

Item predicates check the item involved in a predicate context - most commonly the tool used to mine a block via
`matchTool`, but the same shape is used for `equipment` slots and `slots` checks inside `entityProperties` (see
[Entity Predicate Example](#entity-predicate-example) above).

A basic item predicate matches on the item type plus optional `count`/`durability` ranges:

```kotlin
predicate("enchanted_tool") {
	matchTool {
		items(Items.DIAMOND_PICKAXE)
		predicates {
			enchantments(Enchantments.EFFICIENCY)
		}
	}
}
```

The `predicates { }` block accepts
any [component matcher](/docs/concepts/components#component-matchers-item-predicates)

- `damage`, `enchantments`, `storedEnchantments`, `customData`, `container`, and more - so you can gate a predicate on
  arbitrary component state, not just enchantments. If you instead need the inline command-syntax form
  (`minecraft:diamond_sword[damage=10]`) for use outside a predicate file - e.g. in `/give`, `/clear`, or the `items`
  selector - see [Item Predicates](/docs/concepts/components#item-predicates) and
  [Component Matchers (Sub-Predicates)](/docs/concepts/components#component-matchers-sub-predicates) in the Components
  guide, which cover both forms side by side with more examples (existence checks, partial matching, negation, OR).

## Referencing Other Predicates

Use `reference` to invoke another predicate by ID, which keeps larger predicate sets composable in the same spirit as
helper extraction in the [Cookbook](/docs/guides/cookbook). A reference is written as the bare ID string wherever the
game expects a condition:

```kotlin
val basePredicate = predicate("base_check") {
	weatherCheck(raining = true)
}

predicate("combined_check") {
	allOf {
		reference(basePredicate)
		reference("my_pack:other_check")
	}
}
```

```json
{
	"type": "minecraft:all_of",
	"terms": [
		"my_pack:base_check",
		"my_pack:other_check"
	]
}
```

`inverted(basePredicate)` writes `"term": "my_pack:base_check"`, and item modifier `condition(basePredicate)`, loot
pool and entry `condition(basePredicate)`, `conditional(basePredicate, 800, 1600)` and number dispatcher
`case(basePredicate, 2)` take the ID the same way.

A predicate tag is only accepted as the whole terms of an `allOf` or an `anyOf`:

```kotlin
predicate("any_night_like") {
	anyOf { reference(PredicateTagArgument("night_like", "my_pack")) }
}
```

```json
{
	"type": "minecraft:any_of",
	"terms": "#my_pack:night_like"
}
```

A list can mix IDs and inline conditions, but a tag can't be one of its terms, only the whole list.

A predicate file can't be a bare reference because the game reads the file as an inline condition: Kore throws when it
generates one. Cyclic references (predicate A references B, which references A) make the pack fail to load.

## Best Practices

1. **Descriptive names**: Give your predicates names that reflect their purpose (e.g., `is_holding_sword`, `in_rain_at_night`)
2. **Logical composition**: Use `allOf` and `anyOf` to combine multiple conditions clearly
3. **Reusability**: Keep predicates focused on a single concern and use `reference` to compose them (inside `allOf` or `anyOf`)
4. **Context awareness
   **: Be mindful of which loot context your predicate will be invoked from. Context-dependent conditions will silently fail if required data is missing
5. **Testing**: Test your predicates in-game using `/execute if predicate <name>` to verify they work as expected

Predicates are powerful tools for creating complex conditions in your datapack. They enable sophisticated game mechanics and enhance player experience without requiring custom code.

## See Also

- [Loot Tables](/docs/data-driven/loot-tables) - Use predicates as conditions for loot entries
- [Advancements](/docs/data-driven/advancements) - Use predicates as trigger conditions
- [Item Modifiers](/docs/data-driven/item-modifiers) - Modify items conditionally with predicates
- [Components](/docs/concepts/components#component-matchers-item-predicates) - Item predicates and component matchers
  in depth: command-syntax predicates, sub-predicate matchers, existence checks, and a complete tool-upgrade example
- [Inventory Manager](/docs/helpers/inventory-manager) - Pair predicates with inventory management
- [Villager Trades](/docs/data-driven/villager-trades) - Gate trade availability via `merchantPredicate`

### External Resources

- [Minecraft Wiki: Predicate](https://minecraft.wiki/w/Predicate) - Official JSON format reference
- [Minecraft Wiki: Loot context](https://minecraft.wiki/w/Loot_context) - Understanding loot contexts for conditions
