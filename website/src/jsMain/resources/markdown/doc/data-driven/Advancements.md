---
root: .components.layouts.MarkdownLayout
title: Advancements
nav-title: Advancements
description: A guide for using advancements in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, advancements
date-created: 2024-01-08
date-modified: 2026-02-03
routeOverride: /docs/data-driven/advancements
---

# Advancements

Advancements in Minecraft are a way to gradually guide new players into the game and give them challenges to complete. In Kore, advancements can be created using the
`advancement` builder.

> **Note:
** For more detailed information about advancements in Minecraft, see the [Minecraft Wiki - Advancement](https://minecraft.wiki/w/Advancement) page.

Advancements can be completed in any game mode and are obtained and saved per world. They are independent of each other, an advancement can be completed without having completed the advancements "before" it. There are many advancements in vanilla Minecraft across 5 tabs: Minecraft, Nether, The End, Adventure, and Husbandry.

## Advancement Structure

Advancements are stored as JSON files in data packs. For the complete technical specification, see the [Minecraft Wiki - Advancement Definition](https://minecraft.wiki/w/Advancement_definition) page.

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

You can specify a parent advancement for your advancement. This is done by setting the `parent` property to an
`Advancement` object or an `AdvancementArgument` object.

Here's an example:

```kotlin
advancement("my_advancement") {
	parent = Advancements.Story.ROOT  // Reference to vanilla advancement
	// or
	parent = AdvancementArgument("custom_advancement", "my_namespace")
}
```

## Display

The display block allows you to customize how the advancement appears in-game. When an advancement is completed, a sliding toast notification appears in the top right corner, and a chat message is sent if the
`announceAdvancements` game rule is enabled.

### Frame Types and Notifications

Different frame types produce different notification behaviors:

- **`TASK`** (Normal): Shows "Advancement Made!" with yellow header text
- **`GOAL`**: Shows "Goal Reached!" with yellow header text
- **`CHALLENGE`**: Shows "Challenge Complete!" with pink header text and plays music

> **Note:** Root advancements (the leftmost advancement in each tab) do not cause notifications or chat messages to appear.

```kotlin
advancement("my_advancement") {
	display(Items.DIAMOND_SWORD) {
		icon {
			item(Items.DIAMOND_SWORD)
		}
		title = textComponent("Title")
		description = textComponent("Description", Color.GRAY)
		frame = AdvancementFrameType.TASK
		background = Textures.Gui.Advancements.Backgrounds.ADVENTURE
		showToast = true
		announceToChat = true
		hidden = false
	}
}
```

- `icon`: The icon of the advancement, defined by an `AdvancementIcon` object which is an item + nbt data.
- `title`: The title of the advancement, defined by a `ChatComponents` object.
- `description`: The description of the advancement, defined by a `ChatComponents` object.
- `frame`: The frame type of the advancement, which can be `CHALLENGE`, `GOAL`, or `TASK`, defaults to `TASK`.
- `background`: The background texture of the advancement, specified as a string (optional).
- `showToast`: Whether to show a toast notification when the advancement is achieved (optional).
- `announceToChat`: Whether to announce the advancement in chat when it is achieved (optional).
- `hidden`: Whether the advancement is hidden until it is achieved (optional).

## Criteria

Criteria define the conditions that must be met to earn the advancement.<br>
Each criterion can have predicate conditions that must be met for the criterion to be completed on top of its properties.<br>
For a complete guide on predicates and their conditions, see the [Predicates](./predicates) documentation.<br>
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

Check the [Triggers](./advancements/triggers) page for a list of available triggers.

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
		function("reward_function") {
			say("Congratulations!")
		}
		loots(LootTables.Chests.IGLOO_CHEST)
		recipes(Recipes.SOME_RECIPE)
	}
}
```

- `experience`: The amount of experience to give the player.
- `function`: A function to run when the advancement is completed.
- `loots`: A list of loot tables to give the player.
- `recipes`: A list of recipes to unlock for the player.

### Reward function

You can define a function to be executed when the advancement is completed in 3 ways:

1. A generated function with a random name:

```kotlin
rewards {
	function {
		say("Congratulations!")
	}
}
```

2. A new function with a custom name:

```kotlin
rewards {
	function("reward_function") {
		say("Congratulations!")
	}
}
```

3. A referenced function

```kotlin
rewards {
	function = myFunction
}
```

## Telemetry

You can enable or disable telemetry for the advancement, defaults to `false`:

```kotlin
advancement("my_advancement") {
	sendsTelemetryEvent = true
}
```

## Advancement Tabs and Organization

Advancements in Minecraft are organized into 5 tabs, each with a specific theme:

- **Minecraft**: Basic gameplay mechanics and progression (16 advancements)
- **Nether**: Nether-related challenges and exploration (23 advancements)
- **The End**: End dimension content and ender dragon (9 advancements)
- **Adventure**: Combat, exploration, and interaction challenges (46 advancements)
- **Husbandry**: Farming, breeding, and food-related tasks (30 advancements)

When creating custom advancements, you can organize them into existing tabs by setting a parent advancement from that tab, or create entirely new tabs by making root advancements.

### Creating a Root Advancement (New Tab)

To create a new advancement tab, create an advancement without a parent and ensure it has display properties:

```kotlin
advancement("my_custom_tab") {
	display(Items.COMPASS, "Custom Adventures", "A new set of challenges") {
		frame = AdvancementFrameType.TASK
		background = Textures.Gui.Advancements.Backgrounds.STONE
	}

	criteria {
		tick("start") // Simple tick trigger to auto-complete
	}
}
```

## Managing Advancements with Commands

Advancements can be granted, revoked, and tested using the
`/advancement` command. For complete command documentation, see the [Minecraft Wiki - /advancement command](https://minecraft.wiki/w/Commands/advancement).

In Kore, you can use advancement commands within functions in two ways:

### Method 1: Using the `advancement` block

```kotlin
function("manage_advancements") {
	advancement {
		// Grant all advancements to a player
		grantEverything(self())
		revokeEverything(self())

		// Grant/revoke specific advancements
		grant(self(), AdvancementRoute.ONLY, Advancements.Adventure.KILL_A_MOB, "test")
		revoke(self(), AdvancementRoute.ONLY, Advancements.Adventure.KILL_A_MOB)

		// Simplified grant/revoke (defaults to ONLY route)
		grant(self(), Advancements.Adventure.KILL_A_MOB)
		revoke(self(), Advancements.Adventure.KILL_A_MOB)
	}
}
```

### Method 2: Using target-specific `advancement` blocks

```kotlin
function("manage_player_advancements") {
	advancement(self()) {
		// All operations target the specified player
		grantEverything()
		revokeEverything()

		// Grant/revoke with specific routes
		grant(AdvancementRoute.ONLY, Advancements.Adventure.KILL_A_MOB, "test")
		grant(AdvancementRoute.ONLY, Advancements.Adventure.KILL_A_MOB)
		
		// Simplified operations (defaults to ONLY route)
		revoke(Advancements.Adventure.KILL_A_MOB)
	}
}
```

### Available Routes

Kore supports different advancement routes corresponding to the vanilla command options:

- **`ONLY`**: Grant/revoke only the specified advancement
- **`FROM`**: Grant/revoke advancement and all its children
- **`THROUGH`**: Grant/revoke advancement, all parents, and all children
- **`UNTIL`**: Grant/revoke advancement and all its parents

## Best Practices

### 1. Logical Progression

Structure your advancements to follow a logical progression path, even though they're technically independent:

```kotlin
// Root advancement for your custom content
val rootAdvancement = advancement("my_mod_root") {
	display(Items.BOOK, "Getting Started", "Begin your custom journey") {
		frame = AdvancementFrameType.TASK
		background = Textures.Gui.Advancements.Backgrounds.ADVENTURE
	}
	// ...criteria
}

// Child advancement that builds on the root
advancement("my_mod_advanced") {
	parent = rootAdvancement
	display(Items.DIAMOND, "Advanced Techniques", "Master the advanced features") {
		frame = AdvancementFrameType.GOAL
	}
	// ...criteria
}
```

### 2. Meaningful Rewards

Consider giving meaningful rewards for challenging advancements:

```kotlin
advancement("difficult_challenge") {
	display(Items.NETHERITE_INGOT, "Ultimate Challenge", "Complete the impossible") {
		frame = AdvancementFrameType.CHALLENGE
	}

	rewards {
		experience = 500
		function("give_special_items") {
			give(self(), Items.DIAMOND, 64)
			effect(self(), Effects.RESISTANCE, 6000, 4)
		}
		recipes(Recipes.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
	}
	// ...criteria
}
```

### 3. Hidden Advancements

Use hidden advancements for surprise discoveries or secret content:

```kotlin
advancement("secret_discovery") {
	display(Items.ENDER_EYE, "???", "A mysterious discovery awaits") {
		frame = AdvancementFrameType.CHALLENGE
		hidden = true  // Won't show until completed
	}
	// ...criteria
}
```

## See Also

- [Predicates](./predicates) - Conditions used in advancement criteria
- [Triggers](./advancements/triggers) - Complete list of available triggers
- [Loot Tables](./loot-tables) - Loot rewards for advancements
- [Functions](../commands/functions) - Run functions as advancement rewards

### External Resources

- [Minecraft Wiki: Advancement](https://minecraft.wiki/w/Advancement) - Overview of advancements
- [Minecraft Wiki: Advancement definition](https://minecraft.wiki/w/Advancement_definition) - Technical JSON specification
