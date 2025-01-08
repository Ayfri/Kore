---
root: .components.layouts.MarkdownLayout
title: Advancements
nav-title: Advancements
description: A guide for using advancements in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, advancements
date-created: 2024-01-08
date-modified: 2024-01-08
routeOverride: /docs/advancements
---

# Advancements

Advancements in Kore can be created using the `advancement` function. This documentation will show you how to create and customize
advancements.

## Basic Usage

To create a basic advancement:

```kotlin
dataPack("my_datapack") {
	advancement("my_advancement") {
		display(Items.DIAMOND_SWORD, "Title", "Description") {
			frame = AdvancementFrameType.TASK
		}

		criteria {
			// Define criteria here
		}
	}
}
```

## Parent Advancements

You can specify a parent advancement for your advancement. This is done by setting the `parent` property to an `Advancement` object or an
`AdvancementArgument` object. Here's an example:

```kotlin
advancement("my_advancement") {
	parent = Advancements.Story.ROOT  // Reference to vanilla advancement
	// or
	parent = AdvancementArgument("custom_advancement", "my_namespace")
}
```

## Display

The display block allows you to customize how the advancement appears in-game:

```kotlin
advancement("my_advancement") {
	display(Items.DIAMOND_SWORD) {
		icon {
			item(Items.DIAMOND_SWORD)
		}
		title = textComponent("Title")
		description = textComponent("Description", Color.GRAY)
		frame = AdvancementFrameType.TASK
		background = "minecraft:textures/gui/advancements/backgrounds/adventure.png"
		showToast = true
		announceToChat = true
		hidden = false
	}
}
```

-   `icon`: The icon of the advancement, defined by an `AdvancementIcon` object which is an item + nbt data.
-   `title`: The title of the advancement, defined by a `ChatComponents` object.
-   `description`: The description of the advancement, defined by a `ChatComponents` object.
-   `frame`: The frame type of the advancement, which can be `CHALLENGE`, `GOAL`, or `TASK`, defaults to `TASK`.
-   `background`: The background texture of the advancement, specified as a string (optional).
-   `showToast`: Whether to show a toast notification when the advancement is achieved (optional).
-   `announceToChat`: Whether to announce the advancement in chat when it is achieved (optional).
-   `hidden`: Whether the advancement is hidden until it is achieved (optional).

## Criteria

Criteria define the conditions that must be met to earn the advancement.<br>
Each criterion can have predicate conditions that must be met for the criterion to be completed on top of its properties.<br>
You can add multiple criteria:

```kotlin
advancement("my_advancement") {
	criteria {
		// Simple item consumption
		consumeItem("eat_golden_apple") {
			item {
				item(Items.GOLDEN_APPLE)
			}
		}

		// With conditions
		sleptInBed("sleep_in_bed") {
			conditions { // Predicates
				randomChance(0.8f)
				timeCheck(5f..15f)
			}
		}
	}
}
```

## Available Triggers

Check the [Triggers](./advancements-triggers) page for a list of available triggers.

## Requirements

Requirements define which criteria must be completed to earn the advancement:

```kotlin
advancement("my_advancement") {
	// Single requirement
	requirements("criterion1")

	// Multiple requirements (AND)
	requirements(listOf("criterion1", "criterion2"))

	// Multiple requirement groups (OR between groups)
	requirements(
		listOf("criterion1", "criterion2"),
		listOf("criterion3")
	)
}
```

## Rewards

You can define rewards for completing the advancement:

```kotlin
advancement("my_advancement") {
	rewards {
		experience = 10
		function = function("reward_function") {
			say("Congratulations!")
		}
		loots(LootTables.Chests.IGLOO_CHEST)
		recipes(Recipes.SOME_RECIPE)
	}
}
```

-   `experience`: The amount of experience to give the player.
-   `function`: A function to run when the advancement is completed.
-   `loots`: A list of loot tables to give the player.
-   `recipes`: A list of recipes to unlock for the player.

## Telemetry

You can enable or disable telemetry for the advancement, defaults to `false`:

```kotlin
advancement("my_advancement") {
	sendsTelemetryEvent = true
}
```

## Complete Example

Here's a complete example combining various features:

```kotlin
advancement("complex_advancement") {
	display(Items.DIAMOND_SWORD, "Master Craftsman", "Craft a special item") {
		frame = AdvancementFrameType.CHALLENGE
		announceToChat = true
	}

	parent = Advancements.Story.ROOT

	criteria {
		crafterRecipeCrafted("craft_special", Recipes.SPECIAL_RECIPE) {
			ingredient(Items.DIAMOND) {
				components {
					damage(0)
				}
			}
		}
	}

	requirements("craft_special")

	rewards {
		experience = 100
		function = function("reward") {
			say("Congratulations on becoming a Master Craftsman!")
		}
		loots(LootTables.Chests.IGLOO_CHEST)
	}

	sendsTelemetryEvent = false
}
```
