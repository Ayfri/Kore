---
root: .components.layouts.MarkdownLayout
title: Advancements
nav-title: Advancements
description: A comprehensive guide for creating and managing advancements in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, advancements, triggers, criteria, rewards
date-created: 2024-01-08
date-modified: 2026-02-10
routeOverride: /docs/data-driven/advancements
---

# Advancements

Advancements are a system in Minecraft Java Edition that guides players through the game by setting goals and challenges to complete. They serve as in-game achievements that track player progress across various activities.

## Overview

Advancements have several key characteristics:

- **World-specific**: Progress is saved per world, not globally
- **Game mode independent**: Can be completed in any game mode (Survival, Creative, Adventure, Spectator)
- **Non-linear**: Can be completed in any order, regardless of parent-child relationships
- **Notification system**: Completed advancements trigger toast notifications and chat messages
- **Customizable**: Data packs can add custom advancements with unique criteria and rewards

## File Structure

Advancements are stored as JSON files in data packs at:

```
data/<namespace>/advancement/<path>.json
```

For complete JSON specification, see the [Minecraft Wiki - Advancement Definition](https://minecraft.wiki/w/Advancement_definition).

## Creating Advancements

Use the `advancement` builder function to create advancements in Kore:

```kotlin
dataPack("my_datapack") {
	advancement("my_first_advancement") {
		display(Items.DIAMOND, "My First Advancement", "Complete this challenge!")

		criteria {
			inventoryChanged("get_diamond", Items.DIAMOND)
		}
	}
}
```

This generates `data/my_datapack/advancement/my_first_advancement.json`.

## Display Properties

The display configuration controls how the advancement appears in the advancement screen and notifications.

### Basic Display

```kotlin
advancement("example") {
	display(Items.DIAMOND_SWORD, "Title", "Description") {
		frame = AdvancementFrameType.TASK
	}
}
```

### Display with Chat Components

For styled text with colors and formatting:

```kotlin
advancement("styled_advancement") {
	display(
		icon = Items.GOLDEN_APPLE,
		title = textComponent("Golden Achievement") { color = Color.GOLD },
		description = textComponent("Eat a golden apple") { color = Color.GRAY }
	) {
		frame = AdvancementFrameType.GOAL
	}
}
```

### Display Properties Reference

| Property         | Type                   | Default  | Description                                 |
|------------------|------------------------|----------|---------------------------------------------|
| `icon`           | `AdvancementIcon`      | Required | Item displayed as the advancement icon      |
| `title`          | `ChatComponents`       | Required | Title shown in the advancement screen       |
| `description`    | `ChatComponents`       | Required | Description text below the title            |
| `frame`          | `AdvancementFrameType` | `TASK`   | Frame style: `TASK`, `GOAL`, or `CHALLENGE` |
| `background`     | `ModelArgument?`       | `null`   | Background texture (root advancements only) |
| `showToast`      | `Boolean?`             | `true`   | Show toast notification on completion       |
| `announceToChat` | `Boolean?`             | `true`   | Announce completion in chat                 |
| `hidden`         | `Boolean?`             | `false`  | Hide until completed (and hide children)    |

### Frame Types

Each frame type produces different visual feedback:

| Frame       | Notification Header   | Header Color | Sound         |
|-------------|-----------------------|--------------|---------------|
| `TASK`      | "Advancement Made!"   | Yellow       | Standard      |
| `GOAL`      | "Goal Reached!"       | Yellow       | Standard      |
| `CHALLENGE` | "Challenge Complete!" | Pink         | Special music |

> **Note:** Root advancements (without a parent) don't trigger notifications or chat messages.

### Icon with Components

Customize the icon with item components like enchantments:

```kotlin
advancement("enchanted_icon") {
	display(Items.DIAMOND_SWORD, "Master Swordsman", "Wield a legendary blade") {
		icon(Items.DIAMOND_SWORD, count = 1) {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
				enchantment(Enchantments.UNBREAKING, 3)
			}
			customName(textComponent("Legendary Sword", Color.GOLD))
		}
		frame = AdvancementFrameType.CHALLENGE
	}
}
```

### Hidden Advancements

Hidden advancements remain invisible (along with their children) until completed:

```kotlin
advancement("secret_discovery") {
	display(Items.ENDER_EYE, "???", "A mysterious discovery") {
		hidden = true
		frame = AdvancementFrameType.CHALLENGE
	}
	// ...criteria
}
```

## Parent Advancements

Set a parent to position the advancement in an existing tree:

```kotlin
advancement("child_advancement") {
	// Reference vanilla advancement
	parent = Advancements.Story.ROOT

	display(Items.IRON_PICKAXE, "Mining Progress", "Continue your journey")
	// ...
}
```

Or reference a custom advancement:

```kotlin
advancement("child_advancement") {
	parent = AdvancementArgument("my_root", "my_namespace")
	// ...
}
```

### Creating a New Tab

To create a new advancement tab, create a root advancement (no parent) with display and a background:

```kotlin
advancement("my_custom_tab") {
	display(Items.COMPASS, "Custom Adventures", "Begin your journey") {
		frame = AdvancementFrameType.TASK
		background = Textures.Gui.Advancements.Backgrounds.STONE
	}

	criteria {
		tick("auto_grant") // Grants immediately
	}
}
```

## Criteria

Criteria define the conditions that must be met to complete the advancement. Each criterion has a **trigger
** that activates when specific game events occur.

### Basic Criteria

```kotlin
advancement("eat_apple") {
	criteria {
		consumeItem("eat_golden_apple") {
			item {
				items = listOf(Items.GOLDEN_APPLE)
			}
		}
	}
}
```

### Multiple Criteria

Add multiple criteria to an advancement:

```kotlin
advancement("multi_criteria") {
	criteria {
		inventoryChanged("get_diamond", Items.DIAMOND)
		inventoryChanged("get_emerald", Items.EMERALD)
		enterBlock("enter_water") {
			block = Blocks.WATER
		}
	}
}
```

### Criteria with Predicate Conditions

Add predicate conditions to criteria for additional checks:

```kotlin
advancement("conditional_criteria") {
	criteria {
		consumeItem("eat_apple_lucky") {
			item {
				items = listOf(Items.GOLDEN_APPLE)
			}
			conditions {
				randomChance(0.5f)  // 50% chance
				timeCheck(6000f..18000f)  // Daytime only
			}
		}
	}
}
```

For a complete guide on predicates, see the [Predicates](./predicates) documentation.

## Triggers

Triggers are the events that activate criteria. Kore supports all vanilla triggers:

| Trigger                        | Description                               |
|--------------------------------|-------------------------------------------|
| `allayDropItemOnBlock`         | Allay drops an item on a block            |
| `anyBlockUse`                  | Player uses any block                     |
| `avoidVibration`               | Player avoids a vibration while sneaking  |
| `beeNestDestroyed`             | Player breaks a bee nest/beehive          |
| `bredAnimals`                  | Player breeds two animals                 |
| `brewedPotion`                 | Player takes item from brewing stand      |
| `changedDimension`             | Player travels between dimensions         |
| `channeledLightning`           | Player uses Channeling enchantment        |
| `constructBeacon`              | Beacon structure is updated               |
| `consumeItem`                  | Player consumes an item                   |
| `crafterRecipeCrafted`         | Crafter crafts a recipe                   |
| `curedZombieVillager`          | Player cures a zombie villager            |
| `defaultBlockUse`              | Player uses a block (default interaction) |
| `effectsChanged`               | Player's effects change                   |
| `enchantedItem`                | Player enchants an item                   |
| `enterBlock`                   | Player enters a block                     |
| `entityHurtPlayer`             | Entity hurts the player                   |
| `entityKilledPlayer`           | Entity kills the player                   |
| `fallAfterExplosion`           | Player falls after an explosion           |
| `fallFromHeight`               | Player falls from a height                |
| `filledBucket`                 | Player fills a bucket                     |
| `fishingRodHooked`             | Player hooks something with fishing rod   |
| `heroOfTheVillage`             | Player becomes Hero of the Village        |
| `impossible`                   | Never triggers (manual grant only)        |
| `inventoryChanged`             | Player's inventory changes                |
| `itemDurabilityChanged`        | Item durability changes                   |
| `itemUsedOnBlock`              | Player uses item on a block               |
| `killedByArrow`                | Player kills entities with arrows         |
| `killMobNearSculkCatalyst`     | Kill a mob near sculk catalyst            |
| `levitation`                   | Player has Levitation effect              |
| `lightningStrike`              | Lightning strikes near player             |
| `location`                     | Player is at a specific location          |
| `netherTravel`                 | Player travels via Nether                 |
| `placedBlock`                  | Player places a block                     |
| `playerGeneratesContainerLoot` | Player generates container loot           |
| `playerHurtEntity`             | Player hurts an entity                    |
| `playerInteractedWithEntity`   | Player interacts with entity              |
| `playerKilledEntity`           | Player kills an entity                    |
| `playerShearedEquipment`       | Player shears equipment from entity       |
| `recipeCrafted`                | Player crafts a recipe                    |
| `recipeUnlocked`               | Player unlocks a recipe                   |
| `rideEntityInLava`             | Player rides entity in lava               |
| `shotCrossbow`                 | Player shoots a crossbow                  |
| `sleptInBed`                   | Player sleeps in bed                      |
| `slideDownBlock`               | Player slides down a block                |
| `spearMobs`                    | Player spears mobs                        |
| `startedRiding`                | Player starts riding                      |
| `summonedEntity`               | Player summons an entity                  |
| `tameAnimal`                   | Player tames an animal                    |
| `targetHit`                    | Player hits a target block                |
| `thrownItemPickedUpByEntity`   | Entity picks up thrown item               |
| `thrownItemPickedUpByPlayer`   | Player picks up thrown item               |
| `tick`                         | Every game tick (use for auto-grant)      |
| `usedEnderEye`                 | Player uses Eye of Ender                  |
| `usedTotem`                    | Player uses Totem of Undying              |
| `usingItem`                    | Player is using an item                   |
| `villagerTrade`                | Player trades with villager               |
| `voluntaryExile`               | Player gets Bad Omen                      |

For detailed trigger documentation, see the [Triggers](./advancements/triggers) page.

### Trigger Examples

```kotlin
advancement("trigger_examples") {
	criteria {
		// Dimension travel
		changedDimension("enter_nether") {
			from = Dimensions.OVERWORLD
			to = Dimensions.THE_NETHER
		}

		// Block interaction
		enterBlock("step_on_pressure_plate") {
			block = Blocks.STONE_PRESSURE_PLATE
		}

		// Entity interaction
		playerKilledEntity("kill_zombie") {
			entity {
				type(EntityTypes.ZOMBIE)
			}
		}

		// Effect-based
		effectsChanged("get_speed") {
			effect(Effects.SPEED) {
				amplifier = rangeOrInt(1..3)
				duration = rangeOrInt(100..200)
			}
		}

		// Never triggers - for manual grant via commands
		impossible("manual_only")
	}
}
```

## Requirements

Requirements define how criteria combine to complete the advancement. By default, **all criteria must be completed** (AND logic).

### Simple Requirements

Require specific criteria by name:

```kotlin
advancement("single_requirement") {
	criteria {
		inventoryChanged("get_diamond", Items.DIAMOND)
		inventoryChanged("get_emerald", Items.EMERALD)
	}

	// Only diamond is required (emerald is optional)
	requirements("get_diamond")
}
```

### AND Logic (All Required)

Require multiple criteria (all must be met):

```kotlin
advancement("and_requirements") {
	criteria {
		inventoryChanged("get_diamond", Items.DIAMOND)
		inventoryChanged("get_emerald", Items.EMERALD)
	}

	// Both required
	requirements("get_diamond", "get_emerald")
}
```

### OR Logic (Any Required)

Use nested lists for OR groups:

```kotlin
advancement("or_requirements") {
	criteria {
		inventoryChanged("get_diamond", Items.DIAMOND)
		inventoryChanged("get_emerald", Items.EMERALD)
		inventoryChanged("get_gold", Items.GOLD_INGOT)
	}

	// Need diamond OR emerald, AND gold
	requirements(
		listOf("get_diamond", "get_emerald"),  // Either diamond or emerald
		listOf("get_gold")  // AND gold
	)
}
```

## Rewards

Define rewards granted when the advancement is completed:

```kotlin
advancement("rewarding_advancement") {
	// ...display and criteria

	rewards {
		experience = 100
		loots(LootTables.Chests.IGLOO_CHEST)
		recipes(Recipes.DIAMOND_SWORD)
	}
}
```

### Reward Properties

| Property     | Type                       | Description                       |
|--------------|----------------------------|-----------------------------------|
| `experience` | `Int?`                     | Experience points awarded         |
| `function`   | `FunctionArgument?`        | Function to execute on completion |
| `loot`       | `List<LootTableArgument>?` | Loot tables to roll               |
| `recipes`    | `List<RecipeArgument>?`    | Recipes to unlock                 |

### Function Rewards

Execute commands when the advancement is completed:

```kotlin
// Anonymous generated function
rewards {
	function {
		say("Congratulations!")
	}
}

// Named function
rewards {
	function("celebration") {
		say("You did it!")
		playsound(Sounds.UI_TOAST_CHALLENGE_COMPLETE, PlaySoundMixer.MASTER, self())
	}
}

// Reference existing function
rewards {
	function = myExistingFunction
}
```

### Multiple Rewards

```kotlin
advancement("full_rewards") {
	rewards {
		experience = 500
		function("reward_function") {
			give(self(), Items.DIAMOND, 10)
			effect(self(), Effects.REGENERATION, 200, 2)
		}
		loots(
			LootTables.Chests.END_CITY_TREASURE,
			LootTables.Chests.STRONGHOLD_CORRIDOR
		)
		recipes(
			Recipes.DIAMOND_PICKAXE,
			Recipes.DIAMOND_SWORD
		)
	}
}
```

## Telemetry

Control whether completing this advancement sends telemetry data:

```kotlin
advancement("tracked_advancement") {
	sendsTelemetryEvent = true  // Default is false
}
```

## Managing Advancements with Commands

Use the `/advancement` command to grant, revoke, or test advancements.

### Using the Advancement Command Block

```kotlin
function("manage_advancements") {
	advancement {
		// Grant/revoke everything
		grantEverything(self())
		revokeEverything(self())

		// Specific advancement
		grant(self(), Advancements.Adventure.KILL_A_MOB)
		revoke(self(), Advancements.Adventure.KILL_A_MOB)

		// With route and criterion
		grant(self(), AdvancementRoute.ONLY, Advancements.Story.ROOT, "criterion_name")
	}
}
```

### Target-Specific Block

```kotlin
function("player_advancements") {
	advancement(self()) {
		grantEverything()
		grant(Advancements.Story.IRON_TOOLS)
		revoke(AdvancementRoute.FROM, Advancements.Nether.ROOT)
	}
}
```

### Advancement Routes

| Route     | Description                                |
|-----------|--------------------------------------------|
| `ONLY`    | Only the specified advancement             |
| `FROM`    | Advancement and all its children           |
| `THROUGH` | Advancement, all parents, and all children |
| `UNTIL`   | Advancement and all its parents            |

## Complete Example

Here's a comprehensive example demonstrating multiple features:

```kotlin
dataPack("adventure_pack") {
	// Create a custom tab
	val customRoot = advancement("custom/root") {
		display(Items.COMPASS, "Custom Adventures", "Begin your custom journey") {
			frame = AdvancementFrameType.TASK
			background = Textures.Gui.Advancements.Backgrounds.ADVENTURE
		}
		criteria {
			tick("start")
		}
	}

	// Child advancement with multiple criteria
	advancement("custom/explorer") {
		parent = customRoot
		display(Items.MAP, "Explorer", "Visit multiple biomes") {
			frame = AdvancementFrameType.GOAL
			showToast = true
			announceToChat = true
		}

		criteria {
			location("visit_forest") {
				location {
					biome = Biomes.FOREST
				}
			}
			location("visit_desert") {
				location {
					biome = Biomes.DESERT
				}
			}
			location("visit_ocean") {
				location {
					biome = Biomes.OCEAN
				}
			}
		}

		// Any two biomes complete the advancement
		requirements(
			listOf("visit_forest", "visit_desert"),
			listOf("visit_forest", "visit_ocean"),
			listOf("visit_desert", "visit_ocean")
		)

		rewards {
			experience = 50
		}
	}

	// Challenge advancement
	advancement("custom/master") {
		parent = customRoot
		display(Items.NETHERITE_SWORD, "Master Adventurer", "Complete the ultimate challenge") {
			icon(Items.NETHERITE_SWORD) {
				enchantments {
					enchantment(Enchantments.SHARPNESS, 5)
				}
			}
			frame = AdvancementFrameType.CHALLENGE
			hidden = true
		}

		criteria {
			playerKilledEntity("kill_dragon") {
				entity {
					type(EntityTypes.ENDER_DRAGON)
				}
			}
			playerKilledEntity("kill_wither") {
				entity {
					type(EntityTypes.WITHER)
				}
			}
		}

		rewards {
			experience = 1000
			function("master_reward") {
				title(self(), textComponent("MASTER ADVENTURER", Color.GOLD), textComponent(""))
			}
		}
	}
}
```

## Best Practices

### 1. Logical Progression

Structure advancements to guide players naturally, even though completion order is flexible.

### 2. Meaningful Rewards

Match reward value to advancement difficulty - challenging advancements should have worthwhile rewards.

### 3. Clear Descriptions

Write descriptions that clearly explain what players need to do.

### 4. Use Hidden Sparingly

Reserve hidden advancements for genuine surprises or easter eggs.

### 5. Test Criteria

Verify criteria trigger correctly in-game before releasing your data pack.

## See Also

- [Triggers](./advancements/triggers) - Complete trigger reference
- [Predicates](./predicates) - Conditions for advancement criteria
- [Loot Tables](./loot-tables) - Loot rewards
- [Functions](../commands/functions) - Function rewards
- [Tags](./tags) - Use tags in conditions

## External Resources

- [Minecraft Wiki: Advancement](https://minecraft.wiki/w/Advancement) - Game mechanics overview
- [Minecraft Wiki: Advancement Definition](https://minecraft.wiki/w/Advancement_definition) - JSON format specification
