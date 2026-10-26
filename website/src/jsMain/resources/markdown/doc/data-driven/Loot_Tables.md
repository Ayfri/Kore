---
root: .components.layouts.MarkdownLayout
title: Loot Tables
nav-title: Loot Tables
description: Create and customize Minecraft loot tables using Kore's type-safe Kotlin DSL for drops, container contents, fishing, and more.
keywords: minecraft, datapack, kore, loot tables, pools, entries, item modifiers, drops
date-created: 2025-08-11
date-modified: 2026-10-26
routeOverride: /docs/data-driven/loot-tables
---

# Loot Tables

Loot tables are JSON files that dictate what items should generate in various game situations.
They control drops from mobs and blocks, contents of naturally generated containers (chests, barrels, dispensers),
fishing rewards, archaeology brushing results, bartering exchanges, and more.

The [loot table example](/playground#example=loot-table) of the playground runs in your browser and shows the JSON it
generates.

## Overview

Loot tables have several key characteristics:

- **Context-dependent**: Different loot contexts provide different parameters (killer entity, tool, luck level, etc.)
- **Randomized**: Use [context providers](#context-providers) for rolls and weighted entries for varied results
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
			rolls(3)

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

{{{ .components.mc.LootChestMockup }}}

## Table Structure

A loot table consists of:

| Property         | Type                     | Description                                         |
|------------------|--------------------------|-----------------------------------------------------|
| `type`           | `LootTableType`          | Optional context type for validation                |
| `pools`          | `List<LootPool>`         | One or more pools that generate items               |
| `modifier`       | `InlinableItemModifier?` | Optional item modifier applied to all drops         |
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

Available types: `ADVANCEMENT_ENTITY`, `ADVANCEMENT_LOCATION`, `ADVANCEMENT_REWARD`, `ARCHAEOLOGY`, `BARTER`, `BLOCK`,
`BLOCK_INTERACT`, `BLOCK_USE`, `CHEST`, `COMMAND`, `COMMAND_COMPUTE_DEFAULT`, `COMMAND_COMPUTE_ENTITY`,
`COMMAND_COMPUTE_POSITION`, `COMMAND_SLOT_SOURCE`, `CONTAINER_PROCESS`, `EMPTY`,
`ENCHANTED_DAMAGE`, `ENCHANTED_ENTITY`, `ENCHANTED_ITEM`, `ENCHANTED_LOCATION`, `ENTITY`, `ENTITY_INTERACT`,
`EQUIPMENT`, `FISHING`, `GENERIC`, `GIFT`, `HIT_BLOCK`, `SELECTOR`, `SHEARING`, `VAULT`, `VILLAGER_TRADE`.

The types used by [enchantment](/docs/data-driven/enchantments) effects, by container processing and by the
[`compute`](/docs/commands/commands#compute-command) command provide these parameters:

| Type                       | Parameters                                                                                              |
|----------------------------|---------------------------------------------------------------------------------------------------------|
| `COMMAND_COMPUTE_DEFAULT`  | origin, optionally this entity                                                                          |
| `COMMAND_COMPUTE_ENTITY`   | origin, optionally this entity, target entity                                                           |
| `COMMAND_COMPUTE_POSITION` | origin, optionally this entity, block entity, block state                                               |
| `CONTAINER_PROCESS`        | block entity, block state, container, origin                                                            |
| `ENCHANTED_DAMAGE`         | this entity, enchantment level, origin, damage source, optionally the direct attacker and the attacker   |
| `ENCHANTED_ENTITY`         | this entity, enchantment level, origin                                                                  |
| `ENCHANTED_ITEM`           | tool, enchantment level                                                                                 |
| `ENCHANTED_LOCATION`       | this entity, enchantment level, origin, whether the enchantment is active                               |
| `HIT_BLOCK`                | this entity, enchantment level, origin, block state                                                     |

### Global Modifier

Apply an item modifier to all items dropped by the table, one function being written bare and several ones as a list:

```kotlin
lootTable("enchanted_loot") {
	modifier {
		enchantRandomly(Enchantments.LOOTING)
	}

	pool {
		entries {
			item(Items.DIAMOND_SWORD)
		}
	}
}
```

Each `modifier` (table, pool and entry) also takes the ID of an [item modifier file](/docs/data-driven/item-modifiers#reference) or an item modifier tag, written as a bare string:

```kotlin
val sharpen = itemModifier("sharpen") {
	enchantRandomly(Enchantments.SHARPNESS)
}

lootTable("sharp_loot") {
	modifier { reference(sharpen) }  // "modifier": "my_pack:sharpen"
}
```

## Pools

Each pool represents an independent set of rolls. A table can have multiple pools that all contribute to the final loot.

### Pool Properties

| Property     | Type                     | Description                                                          |
|--------------|--------------------------|----------------------------------------------------------------------|
| `rolls`      | `ContextIntProvider`     | How many times to select from entries                                |
| `bonusRolls` | `ContextFloatProvider?`  | Additional rolls per luck level                                      |
| `condition`  | `PredicateCondition?`    | Condition that must pass for the pool to activate, several are AND-ed |
| `entries`    | `List<LootEntry>`        | Possible entries to select from                                      |
| `modifier`   | `InlinableItemModifier?` | Item modifier applied to items from this pool                        |

### Basic Pool

```kotlin
lootTable("simple_pool") {
	pool {
		rolls(2)
		bonusRolls(1f)

		entries {
			item(Items.EMERALD)
		}
	}
}
```

A pool with only a number of rolls takes it as its first argument, `pool(2) { ... }` writing `"rolls": 2`. `pool { }` rolls once.

### Conditional Pool

```kotlin
lootTable("weather_dependent") {
	pool {
		rolls(1)

		condition {
			weatherCheck(raining = true)
		}

		entries {
			item(Items.WATER_BUCKET)
		}
	}
}
```

### Pool with a Modifier

```kotlin
lootTable("modified_drops") {
	pool {
		rolls(1)

		entries {
			item(Items.DIAMOND_PICKAXE)
		}

		modifier {
			setDamage(0.5f)
			enchantWithLevels(levels = 30)
		}
	}
}
```

## Context Providers

Context providers determine dynamic numeric values for rolls, counts, and other quantities. The game splits them in two families by the number they produce, and each field takes exactly one of them:

| Family | Kotlin type            | Number          | Constant | Fields                                                                                                                                                           |
|--------|------------------------|-----------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| int    | `ContextIntProvider`   | 32-bit integer  | `5`      | `rolls`, item function counts, levels and durations, trade `maxUses` and `xp`, trade set `amount`, `intValueCheck`, `timeCheck` and `entityScores` ranges        |
| float  | `ContextFloatProvider` | 32-bit float    | `5.0`    | `bonusRolls`, `setDamage`, attribute amounts, `randomChance`, trade `reputationDiscount`, `floatValueCheck`                                                      |

The builders are extensions on `NumberProviderScope`, so they only resolve inside the blocks that accept a context provider: loot tables and pools, item modifiers and their functions, predicates, villager trades and trade sets. The parameter type picks the family: `constant(5)` and `uniform(1, 5)` build int providers, `constant(5f)` and `uniform(1f, 5f)` float ones. A builder that has the same parameters in both families has one name per family (`intStorage` and `floatStorage`, for instance).

Most fields take a plain number for a fixed value instead of `constant(...)`: `rolls(3)`, `bonusRolls(0.5f)`, `setCount(2)`, `enchantWithLevels(levels = 30)` and `potionEffect(Effects.NIGHT_VISION, 200)` write the bare number. `constant(...)` stays for a fixed value inside an aggregate (`add`, `max`, `min`, `mul`) and for a single bound such as `limitCount(max = constant(64))`. The `fallback` of `score`, `intStorage` and `floatStorage` takes a plain number too, `score("kills", EntityTarget.THIS, 0)` writing `"fallback": 0`.

| Builder                                               | Family       | Description                                               | Example                                                                                  |
|-------------------------------------------------------|--------------|-----------------------------------------------------------|------------------------------------------------------------------------------------------|
| `abs(input)`                                          | int, float   | Absolute value                                            | `abs(sub(constant(1), constant(5)))`                                                     |
| `add(...)`                                            | int, float   | Sum of several providers, or of a tag of providers        | `add(constant(1), uniform(1, 3))`                                                        |
| `avg(...)`                                            | int, float   | Mean of several providers, or of a tag of providers       | `avg(constant(2), constant(4))`                                                          |
| `binomial(n, p)`                                      | int          | Binomial distribution, `n` an int and `p` a float provider | `binomial(5, 0.5f)`                                                                     |
| `ceil(input)`, `floor(input)`, `round(input)`, `truncate(input)` | float | Rounds to a whole number                              | `round(uniform(0.5f, 2.5f))`                                                             |
| `conditional(...)`                                    | int, float   | One value when a condition passes, another otherwise      | `conditional(5, 2) { killedByPlayer() }`                                                 |
| `constant(value)`                                     | int, float   | Fixed value                                               | `constant(5)`, `constant(5f)`                                                            |
| `cos(input)`, `sin(input)`                            | float        | Cosine and sine, in radians                               | `sin(constant(1f))`                                                                      |
| `div(left, right)`                                    | int, float   | Division, an int one rounds toward zero                   | `div(constant(7), constant(2))`                                                          |
| `enchantmentLevel(...)`                               | float        | Based on enchantment level (requires enchantment context) | `enchantmentLevel(5)`                                                                    |
| `intEnvironmentAttribute(attribute)`, `floatEnvironmentAttribute(attribute)` | int, float | Current value of a numeric env attribute | `floatEnvironmentAttribute(EnvironmentAttributes.Visual.FOG_START_DISTANCE)`             |
| `floorDiv(left, right)`, `floorMod(left, right)`      | int          | Division rounded down, and its remainder                  | `floorMod(score("kills", EntityTarget.THIS), constant(10))`                              |
| `fromFloat(input)`                                    | int          | Float provider converted to an int, truncated toward zero | `fromFloat(constant(2.7f))`                                                              |
| `fromInt(input)`                                      | float        | Int provider converted to a float                         | `fromInt(score("kills", EntityTarget.THIS))`                                             |
| `intNumberDispatcher(...) { }`, `floatNumberDispatcher(...) { }` | int, float | Value of the first matching case, or a default | `intNumberDispatcher(default = 0) { case(1) { killedByPlayer() } }`                      |
| `intStorage(storage, path)`, `floatStorage(storage, path)` | int, float | Value from command storage, with an optional `fallback` | `intStorage("my_pack:data", "player.health", 0)`                                         |
| `intWeightedList { }`, `floatWeightedList { }`        | int, float   | Value of one entry, picked by weight                      | `intWeightedList { entry(30, 1); entry(70, 0) }`                                         |
| `length(...)`                                         | float        | Euclidean length of a vector, or of a tag of providers    | `length(constant(3f), constant(4f))`                                                     |
| `max(...)`                                            | int, float   | Largest of several providers, or of a tag of providers    | `max(constant(1), score("kills", EntityTarget.THIS))`                                    |
| `min(...)`                                            | int, float   | Smallest of several providers, or of a tag of providers   | `min(constant(5), score("kills", EntityTarget.THIS))`                                    |
| `mod(left, right)`                                    | int, float   | Remainder of a division                                   | `mod(constant(7), constant(2))`                                                          |
| `mul(...)`                                            | int, float   | Product of several providers, or of a tag of providers    | `mul(reference(ContextIntProviders.Cooking.TIME_COAL), constant(2))`                     |
| `negate(input)`                                       | int, float   | Opposite value                                            | `negate(constant(1))`                                                                    |
| `pow(base, exponent)`                                 | int, float   | Power                                                     | `pow(constant(2), constant(3))`                                                          |
| `reference(id)`                                       | int, float   | Context provider file, written as its ID                  | `reference(ContextIntProviders.Cooking.TIME_COAL)`                                       |
| `score(score, target)`                                | int          | Value from a scoreboard objective, with an optional `fallback` | `score("kills", EntityTarget.THIS, 0)`                                              |
| `sqrt(input)`                                         | float        | Square root                                               | `sqrt(constant(4f))`                                                                     |
| `sub(left, right)`                                    | int, float   | Difference                                                | `sub(constant(5), score("deaths", EntityTarget.THIS))`                                   |
| `uniform(min, max)`                                   | int, float   | Random value between min and max                          | `uniform(1, 5)`, `uniform(0.5f, 1f)`                                                     |

Only the int family has `binomial`, `floorDiv`, `floorMod`, `fromFloat` and `score`, only the float family has `ceil`, `cos`, `enchantmentLevel`, `floor`, `fromInt`, `length`, `round`, `sin`, `sqrt` and `truncate`. `score` has no float form because the game only reads an int from a scoreboard: wrap it in `fromInt(...)` to use it in a float field.

`conditional`, the number dispatchers and the weighted lists nest other providers, and the conditions are the ones of [predicates](/docs/data-driven/predicates). A condition is either passed as a value (`conditional(SurvivesExplosion, 1, 0)`), as a predicate ID (`conditional(fastCooking, 800, 1600)`) or declared in a block holding exactly one condition. `onFalse` and `default` are `0` when omitted. A dispatcher case is written `{ "condition": ..., "value": ... }`.

A constant is the bare number (`5` for an int provider, `5.0` for a float one), and every other provider is an object that always carries its `type`: the game rejects `{"min": 1, "max": 2}`, it has to be `{"type": "minecraft:uniform", "min": 1, "max": 2}`.

`intEnvironmentAttribute` reads an attribute holding an integer, and no vanilla attribute is representable as one. Wrap the float one in `fromFloat(...)` to read it as an int:

```kotlin
fromFloat(floatEnvironmentAttribute(EnvironmentAttributes.Visual.FOG_START_DISTANCE))
```

### Context provider IDs

Wherever a context provider is expected (`rolls`, `min`, `max`, `on_true`, the item modifier counts...), the game also accepts the ID of a [context provider file](#context-provider-files) of the same family, written as a bare string. `reference(id)` builds it, from a `ContextIntProviderArgument` for an int provider and a `ContextFloatProviderArgument` for a float one, and `contextIntProvider(...)` and `contextFloatProvider(...)` return the ID to pass:

```kotlin
val bonus = contextIntProvider("bonus") { uniform(1, 3) }

pool {
	rolls = reference(bonus)  // "rolls": "my_pack:bonus"
	bonusRolls = uniform(reference(ContextFloatProviders.Cooking.SPEED_DEFAULT), constant(5f))
}
```

The `inputs` of `add`, `avg`, `max`, `min` and `mul` (and of `length` for float providers) take one provider or ID, a list mixing IDs and inline providers, or a whole `#tag` of providers of the family: `add(ContextIntProviderTagArgument("bonuses", "my_pack"))` writes `"inputs": "#my_pack:bonuses"`. A tag is only valid as the whole `inputs`, not as one element of a list: the game reads `inputs` as a set of providers, and Kore throws an `IllegalArgumentException` when a list of several elements holds a tag. The list can't be empty either: the game reports an aggregate without any input, and Kore throws.

`min` caps a value, here the roll count at 5 whatever the score is:

```kotlin
pool {
	rolls = min(score("kills", EntityTarget.THIS), constant(5))
}
```

A provider that references itself, directly or through other files, makes the pack fail to load.

### Expressions

Kotlin operators build the same providers as the builders, so an expression reads like the maths it computes. The right operand is a provider or a number of the same family (an `Int` for an int provider, a `Float` for a float one):

| Expression                             | Family     | Builds                                                                    |
|----------------------------------------|------------|---------------------------------------------------------------------------|
| `a + b`                                | int, float | `add`                                                                     |
| `a - b`                                | int, float | `sub`                                                                     |
| `a * b`                                | int, float | `mul`                                                                     |
| `a / b`                                | int, float | `div`, an int one rounds toward zero                                      |
| `a % b`                                | int, float | `mod`, with the sign of `a` for an int and of `b` for a float             |
| `-a`                                   | int, float | `negate`                                                                  |
| `a floorDiv b`, `a floorMod b`         | int        | `floor_div`, `floor_mod`                                                  |
| `a pow b`                              | int, float | `pow`                                                                     |
| `a.toFloat()`, `a.toInt()`             | int, float | `from_int`, `from_float`                                                  |
| `a.coerceIn(0, 10)`                    | int, float | `max([min([a, 10]), 0])`, with `coerceAtLeast(0)` and `coerceAtMost(10)`  |
| `min(a, 5)`, `max(a, 5)`               | int, float | `min` and `max` with a constant                                           |

A number can lead an expression inside the blocks that accept providers (`2 + score(...)`, `1.5f * speed`), and nowhere else. The two families don't mix, convert with `toFloat()` or `toInt()`:

```kotlin
pool {
	rolls = 2 + score("kills", EntityTarget.THIS)
}
```

```json
"rolls": {
	"type": "minecraft:add",
	"inputs": [
		{ "type": "minecraft:score", "score": "kills", "target": { "type": "minecraft:context", "target": "this" } },
		2
	]
}
```

The operators keep the tree small, and only on what they build (a builder like `add(...)` stays as written):

- **Int sums and products** become one `add` or `mul`: `score + 1 + 2` is `add([score, 3])`, and `(score + 1) + (deaths + 2)` is `add([score, deaths, 3])`. The constants of the list are merged into one, put last. The game adds all the inputs on 64 bits and checks the result once, so a chain only fails when its final total overflows. A merged constant that doesn't fit an int isn't merged: `score + Int.MAX_VALUE + 1` stays `add([score, 2147483647, 1])`. A `+ 0` and a `* 1` disappear, and two constants fold into one (`constant(2) + constant(3)` is `5`).
- **Int subtraction and negation** fold two constants when the result fits an int (`constant(5) - constant(3)` is `2`, `-constant(5)` is `-5`). `-constant(Int.MIN_VALUE)` and an overflowing difference stay as providers, since the game fails on them.
- **Int `/`, `%`, `floorDiv`, `floorMod` and `pow`** are never folded, so `score % 0` still reaches the game.
- **Float expressions** flatten the left operand only: `(a + b) + c` is one `add`, `a + (b + c)` keeps its nesting, because float sums aren't associative. Constants are never folded, since that could change the rounding. A `+ 0f` and a `* 1f` disappear (dropping `+ 0f` can keep a `-0.0` that the game, whose float `add` starts from `0.0f`, would turn into `0.0`).
- An `add` or `mul` made of a `#tag` is never merged into a bigger list: the game only accepts a tag as the whole `inputs`.

`<kills>` stands for the `score` provider of `kills`. `toFloat()` moves an expression to float maths, and `toInt()` brings it back:

```kotlin
compute(self()) { (score("kills", EntityTarget.TARGET_ENTITY) + 3 + 4) % 7 }
// compute entity @s integer {type:"minecraft:mod",left:{type:"minecraft:add",inputs:[<kills>,7]},right:7}

compute(self()) { sqrt(score("kills", EntityTarget.TARGET_ENTITY).toFloat() * 1.5f + 2f) }
// compute entity @s float {type:"minecraft:sqrt",input:{type:"minecraft:add",inputs:[{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:<kills>},1.5d]},2]}}

score("kills", EntityTarget.THIS).coerceIn(0, 10)
// {"type": "minecraft:max", "inputs": [{"type": "minecraft:min", "inputs": [<kills>, 10]}, 0]}
```

### Evaluation

- Outside commands, a provider that hits an error (a division by zero, an int overflow) or gives Infinity or NaN returns `0`. The [`compute`](/docs/commands/commands#compute-command) command fails instead, and `data modify ... compute` stores `0`.
- Int `add` and `mul` are computed on 64 bits and fail when the result doesn't fit in 32 bits. `avg` can't overflow.
- `fromFloat` truncates toward zero (`-2.7` gives `-2`) and fails when the float isn't finite or doesn't fit in an int.
- `uniform` returns `min` when `min` is at or above `max`.
- Float providers are 32-bit floats: whole numbers are exact up to 16,777,216 (2²⁴), past it neighboring values read as the same one. Int providers are exact 32-bit integers, so `score` in an int field and the ranges of `entityScores` stay exact on the whole range of a score. `fromInt(score(...))` is a float again and loses the same precision past 2²⁴.

### Examples

```kotlin
pool {
	rolls = conditional(3, 1) { killedByPlayer() }

	bonusRolls = floatNumberDispatcher(default = floatWeightedList {
		entry(30, 1f)
		entry(70, 0f)
	}) {
		case(2f) { killedByPlayer() }
	}
}
```

This writes `weighted_list` as `{"type": "minecraft:weighted_list", "distribution": [{"data": 1.0, "weight": 30}, {"data": 0.0, "weight": 70}]}`.

```kotlin
pool {
	rolls = uniform(2, 5)  // 2-5 rolls randomly
	bonusRolls(1f)  // +1 roll per luck level

	entries {
		item(Items.GOLD_INGOT)
	}
}
```

### Context provider files

A context provider can also live in its own file, `data/<namespace>/context_int_provider/<fileName>.json` for an int provider and `data/<namespace>/context_float_provider/<fileName>.json` for a float one, so the game components that take a context provider by ID can point at it. `contextIntProvider` and `contextFloatProvider` take a block returning the provider and give back the reference to use:

```kotlin
val layers = contextIntProvider("my_layers") {
	intNumberDispatcher(default = intWeightedList {
		entry(30, 1)
		entry(70, 0)
	}) {
		case(1) {
			matchBlock(Blocks.COMPOSTER) {
				state("level", "0")
			}
		}
	}
}
```

```json
{
	"type": "minecraft:number_dispatcher",
	"cases": [
		{
			"condition": {
				"type": "minecraft:match_block",
				"blocks": "minecraft:composter",
				"state": {
					"level": "0"
				}
			},
			"value": 1
		}
	],
	"default": {
		"type": "minecraft:weighted_list",
		"distribution": [
			{ "data": 1, "weight": 30 },
			{ "data": 0, "weight": 70 }
		]
	}
}
```

A constant is written as a bare number, the way vanilla's `compostable/always_add_one.json` is:

```kotlin
contextIntProvider("always_add_one", 1)               // 1
contextFloatProvider("always_one_and_a_half", 1.5f)   // 1.5
```

The vanilla files are listed in `ContextIntProviders` and `ContextFloatProviders`, e.g. `ContextIntProviders.Compostable.LOW` for `minecraft:compostable/low`. Context providers, including the ones of other provider files, can reference a file of their family by its ID with `reference(id)`, see [Context provider IDs](#context-provider-ids). The item components `cooking_fuel`, `brewing_fuel` and `compostable` take a plain number or such an ID, see [Components](/docs/concepts/components#compostable). A file itself can't be a bare reference because the game reads it as an inline provider, so Kore throws when generating one.

## Entries

Entries define what can be selected during a pool roll.
There are singleton entries (yield items) and composite entries (combine other entries).

### Singleton Entries

#### Item Entry

Drops a specific item:

```kotlin
entries {
	item(Items.DIAMOND) {
		weight = 1
		quality = 2
		condition {
			randomChance(0.5f)
		}
		modifier {
			setCount(uniform(1, 3))
		}
	}
}
```

The entry's `condition` and `modifier` live in the `features.loottables.entries` package, the pool's and table's in
`features.loottables`. The loot table, item modifier and predicate builders are DSL-scoped, so calling a builder that
only an enclosing pool or table has fails to compile rather than silently adding to that pool: import the entry's
version when the compiler reports it.

#### Loot Table Entry

Drops the items of other loot tables, given by ID or by loot table tag. The entry's `value` is one ID, a list of IDs or a tag:

```kotlin
entries {
	lootTable(LootTables.Gameplay.PIGLIN_BARTERING) {
		weight = 1
		modifier {
			setCount(2)
		}
	}
}
```

```json
{
	"type": "minecraft:loot_table",
	"modifier": {
		"type": "minecraft:set_count",
		"count": 2
	},
	"value": "minecraft:gameplay/piglin_bartering",
	"weight": 1
}
```

With several loot tables, `expand = true` picks one of them at random, while the default drops all of them. A loot table tag is written `#my_pack:my_tag`:

```kotlin
entries {
	lootTable(LootTables.Equipment.TRIAL_CHAMBER, LootTables.Equipment.TRIAL_CHAMBER_MELEE, expand = true)
	lootTable(LootTableTagArgument("trial_loot", "my_pack"))
}
```

The game also accepts a loot table written in place of an ID, which isn't possible in Kore because `value` only takes loot table IDs and tags. Declare the table with `lootTable("...")` and point at it.

#### Tag Entry

Drops items from an item tag, or from a list of items. `items` is written as a bare string when it holds one element:

```kotlin
entries {
	tag(Tags.Item.ARROWS, expand = true) {  // "items": "#minecraft:arrows", one arrow picked at random
		weight = 1
	}

	tag(Items.APPLE, Items.CARROT)          // "items": ["minecraft:apple", "minecraft:carrot"], drops both
}
```

`expand = true` picks one item at random, while the default drops all of them.

#### Dynamic Entry

For block-specific drops (decorated pot sherds or shulkers contents):

```kotlin
entries {
	dynamic(LootEntryDynamicName.SHERDS) {
		// Drops contents of decorated pot sherds
	}
}
```

#### Empty Entry

A weighted entry that drops nothing (useful for rarity):

```kotlin
entries {
	empty(weight = 10) // 10x more likely than weight=1 entries
}
```

### Composite Entries

#### Alternatives

Selects the first entry whose condition passes:

```kotlin
entries {
	alternatives {
		children {
			item(Items.DIAMOND) {
				condition {
					randomChance(0.1f)
				}
			}
			item(Items.GOLD_INGOT) {
				condition {
					randomChance(0.3f)
				}
			}
			item(Items.IRON_INGOT)  // Fallback
		}
	}
}
```

#### Group

All children are added to the pool if the condition passes. Composite entries take a `modifier` like singleton entries:

```kotlin
entries {
	group {
		condition {
			weatherCheck(raining = true)
		}
		modifier {
			setCount(2)
		}
		children {
			item(Items.WATER_BUCKET)
			item(Items.COD)
		}
	}
}
```

#### Sequence

Children are added until one fails its condition:

```kotlin
entries {
	sequence {
		children {
			item(Items.DIAMOND) {
				condition { randomChance(0.5f) }
			}
			item(Items.EMERALD) {
				condition { randomChance(0.5f) }
			}
			item(Items.GOLD_INGOT)
		}
	}
}
```

#### Slots Entry

Selects items from inventory slots specified by a [slot source](/docs/data-driven/slot-sources):

```kotlin
entries {
	slots {
		slotSources {
			slotRange(SlotSourceOrigin.THIS, HOTBAR)
		}
	}
}
```

### Entry Properties

Singleton entries share these properties:

| Property    | Type                     | Description                                              |
|-------------|--------------------------|----------------------------------------------------------|
| `weight`    | `Int`                    | Selection weight (higher = more likely)                  |
| `quality`   | `Int`                    | Modifies weight based on luck: `weight + quality × luck` |
| `condition` | `PredicateCondition?`    | Entry only available if the condition passes             |
| `modifier`  | `InlinableItemModifier?` | Item modifier applied to this entry's items              |

`alternatives`, `group` and `sequence` entries have `condition` and `modifier` too. `loot_table` and `tag` entries also take `expand` (`Boolean?`): `true` picks one loot table or item at random, the default drops all of them.

## Conditions (Predicates)

A pool or an entry holds one `condition`. Several conditions declared in its block are wrapped in an `all_of`, the same way as in a [predicate](/docs/data-driven/predicates) file.

```kotlin
pool {
	condition {
		// Multiple conditions are AND-ed in an all_of
		weatherCheck(raining = true)
		randomChance(0.5f)

		// Check entity properties
		entityProperties {
			entityType(EntityTypes.PLAYER)
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

A pool or an entry can also point at a predicate file by ID, written as the bare ID string:

```kotlin
val isNight = predicate("is_night") { timeCheck(WorldClocks.OVERWORLD, 13000..23000) }

pool {
	condition(isNight)  // "condition": "my_pack:is_night"
}
```

See [Predicates](/docs/data-driven/predicates) for the complete list of available conditions.

## Modifiers (Item Modifiers)

A `modifier` transforms the generated items. It can be set at table, pool, or entry level.

### Common Functions

```kotlin
modifier {
	// Set stack count
	setCount(uniform(1, 5))

	// Apply enchantments
	enchantRandomly(Enchantments.FORTUNE)

	// Set damage (durability)
	setDamage(0.8f)

	// Add custom name
	setName("Legendary Sword")

	// Conditional function
	setCount(10) {
		condition {
			killedByPlayer()
		}
	}
}
```

A single function is written as an object, several ones as a list. See [Item Modifiers](/docs/data-driven/item-modifiers) for the complete list of available functions.

## Full Example

```kotlin
dataPack("treasure_hunt") {
	// Custom boss drop table
	lootTable("boss_drops") {
		type = LootTableType.ENTITY

		// Global enchantment on all drops
		modifier {
			enchantRandomly()
		}

		// Guaranteed drops
		pool {
			rolls(1)

			entries {
				item(Items.NETHER_STAR)
			}
		}

		// Rare equipment drops
		pool {
			rolls(1)
			bonusRolls(0.5f)

			condition {
				killedByPlayer()
			}

			entries {
				item(Items.NETHERITE_SWORD) {
					weight = 1
					modifier {
						enchantWithLevels(levels = 30)
						setName("Boss Slayer")
					}
				}
				item(Items.DIAMOND_SWORD) {
					weight = 5
					modifier {
						enchantWithLevels(levels = uniform(15, 25))
					}
				}
				empty(weight = 10)
			}
		}

		// Bonus loot from existing table
		pool {
			rolls = uniform(1, 3)

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
	"modifier": {
		"type": "minecraft:enchant_randomly"
	},
	"pools": [
		{
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:nether_star"
				}
			],
			"rolls": 1
		},
		{
			"bonus_rolls": 0.5,
			"condition": {
				"type": "minecraft:killed_by_player"
			},
			"entries": [
				{
					"type": "minecraft:item",
					"name": "minecraft:netherite_sword",
					"modifier": [
						{
							"type": "minecraft:enchant_with_levels",
							"levels": 30
						},
						{
							"type": "minecraft:set_name",
							"name": "Boss Slayer"
						}
					],
					"weight": 1
				},
				{
					"type": "minecraft:item",
					"name": "minecraft:diamond_sword",
					"modifier": {
						"type": "minecraft:enchant_with_levels",
						"levels": {
							"type": "minecraft:uniform",
							"max": 25,
							"min": 15
						}
					},
					"weight": 5
				},
				{
					"type": "minecraft:empty",
					"quality": 1,
					"weight": 10
				}
			],
			"rolls": 1
		},
		{
			"entries": [
				{
					"type": "minecraft:loot_table",
					"value": "minecraft:chests/end_city_treasure"
				}
			],
			"rolls": {
				"type": "minecraft:uniform",
				"max": 3,
				"min": 1
			}
		}
	],
	"type": "entity"
}
```

## Using with Commands

Spawn loot from a table using the `/loot` command. The surrounding command builders are documented
in [Commands](/docs/commands/commands), while reusable conditions usually come
from [Predicates](/docs/data-driven/predicates):

```kotlin
load {
	// Give loot directly to player
	loot(self(), myLootTable)

	// Spawn loot at position
	loot {
		target { spawn(vec3(0, 64, 0)) }
		source { loot(myLootTable) }
	}

	// Insert loot into the container at 0 64 0
	loot {
		target { insert(vec3(0, 64, 0)) }
		source { loot(myLootTable) }
	}
}
```

## Overriding Vanilla Tables

To modify vanilla loot tables, create a file with the same path in your datapack. You can combine that
with [Item Modifiers](/docs/data-driven/item-modifiers) when you want reusable post-processing logic instead of
duplicating functions inside every pool:

```kotlin
dataPack("better_zombies") {
	// This overrides minecraft:entities/zombie
	lootTable("entities/zombie") {
		namespace = "minecraft"

		pool {
			rolls(1)

			entries {
				item(Items.ROTTEN_FLESH) {
					modifier {
						setCount(uniform(0, 2))
						enchantedCountIncrease(Enchantments.LOOTING, uniform(0f, 1f))
					}
				}
			}
		}

		// Add custom drops
		pool {
			rolls(1)

			condition {
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

- [Item Modifiers](/docs/data-driven/item-modifiers) - Functions applied to loot table items
- [Advancements](/docs/data-driven/advancements) - Rewards can reference loot tables
- [Tags](/docs/data-driven/tags) - Use item tags for tag entries

### External Resources

- [Minecraft Wiki: Loot table](https://minecraft.wiki/w/Loot_table) - Official JSON format reference
- [Minecraft Wiki: Loot context](https://minecraft.wiki/w/Loot_context) - Understanding loot contexts
