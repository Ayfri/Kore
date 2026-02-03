---
root: .components.layouts.MarkdownLayout
title: Dialogs
nav-title: Dialogs
description: Create interactive dialog screens in Minecraft with Kore's comprehensive dialog system.
keywords: minecraft, datapack, kore, dialogs, ui, interactive, forms, confirmation, notice
date-created: 2025-09-18
date-modified: 2025-09-18
routeOverride: /docs/data-driven/dialogs
---

# Dialogs

Minecraft includes a powerful dialog system that allows creating interactive modal windows for displaying information and receiving player
input. Dialogs are native Minecraft features introduced in Java Edition 1.21.6 that enable sophisticated user interfaces within the game.
With **Kore**, you can easily create and manage these dialogs using a comprehensive Kotlin DSL that maps directly to Minecraft's dialog
format.

Minecraft's dialog system supports various interaction types including:

- Displaying rich text with formatting and clickable elements
- Receiving player input through text fields, toggles, sliders, and option selections
- Executing commands via action buttons (with appropriate permissions)
- Navigating between multiple dialogs using nested structures
- Integration with the pause menu and quick actions hotkey

For the vanilla reference, see the [Minecraft Wiki – Dialog](https://minecraft.wiki/w/Dialog)
and [Commands/dialog](https://minecraft.wiki/w/Commands/dialog).

## Minecraft Dialog System Overview

Minecraft dialogs consist of three main elements:

- **Header**: Contains the title and warning button
- **Body elements**: Labels, inputs, buttons, and submit actions (scrollable if needed)
- **Optional footer**: Confirmation buttons and submit actions

When a dialog opens, player controls are temporarily disabled until the user exits through an action button, the Escape key, or the warning
button. In single-player mode, dialogs can be configured to pause the game and trigger an autosave.

## Set Up Your Data Pack Function

Begin by creating a function within your `DataPack` where you'll define your dialogs:

```kotlin
fun DataPack.createDialogs() {
	// Your dialog definitions will go here
}
```

## Initialize the Dialogs Block

Use the `dialogBuilder` to start defining your dialogs:

```kotlin
val myDialog = dialogBuilder.confirmation("welcome", "Welcome!") {
	// Define dialog properties here
}
```

Or use the `dialogs` DSL:

```kotlin
dialogs {
	confirmation("my-dialog", "Title!") {
		// Define dialog properties here
	}
}
```

## Dialog Types

Minecraft currently supports five different dialog types:

### Confirmation Dialog

A dialog with two action buttons (yes/no) for binary choices:

```kotlin
val confirmDialog = dialogBuilder.confirmation("delete_world", "Delete World?") {
	afterAction = AfterAction.WAIT_FOR_RESPONSE
	externalTitle("Delete", Color.RED)
	pause = true

	bodies {
		plainMessage("Are you sure you want to delete this world? This action cannot be undone.")
	}

	yes("Delete") {
		action {
			runCommand {
				say("World deleted!")
			}
		}
	}

	no("Cancel") {
		action {
			suggestChatMessage("Cancelled deletion")
		}
	}
}
```

### Notice Dialog

A simple dialog with a single action button for displaying information:

```kotlin
val noticeDialog = dialogBuilder.notice("achievement", "Achievement Unlocked!") {
	bodies {
		item(Items.DIAMOND_SWORD) {
			description = ItemDescription(textComponent("Your first diamond tool!"))
			showTooltip = true
		}
		plainMessage("You've crafted your first diamond sword!")
	}

	action("Awesome!") {
		tooltip("Click to continue")
		action {
			dynamicCustom("celebrate") {
				this["achievement"] = "first_diamond_tool"
			}
		}
	}
}
```

### Multi Action Dialog

A dialog with multiple action buttons arranged in columns, perfect for menus:

```kotlin
val menuDialog = dialogBuilder.multiAction("main_menu", "Server Menu") {
	columns = 3

	inputs {
		text("player_name", "Your Name") {
			maxLength = 16
			initial = "Steve"
		}

		numberRange("difficulty", "Difficulty", range = 1..10, initial = 5) {
			step = 1f
		}

		boolean("pvp_enabled", "Enable PVP") {
			initial = false
			onTrue = "PVP On"
			onFalse = "PVP Off"
		}
	}

	actions {
		action("Start Game") {
			action {
				runCommand {
					say("Game starting with settings!")
				}
			}
		}

		action("Settings") {
			action {
				openUrl("https://example.com/settings")
			}
		}

		action("Quit") {
			action {
				dynamicRunCommand {
					kick(allPlayers(), textComponent("Thanks for playing!"))
				}
			}
		}
	}
}
```

### Dialog List

A dialog that displays a scrollable list of other dialogs:

```kotlin
val listDialog = dialogBuilder.dialogList("dialog_menu", "Available Dialogs") {
	columns = 2
	buttonWidth = 200

	dialogs(confirmDialog, noticeDialog, menuDialog)
	// or use a tag:
	// dialogs(Tags.Dialog.PAUSE_SCREEN_ADDITIONS)

	exitAction("Back to Game") {
		action {
			dynamicRunCommand {
				say("Returning to game...")
			}
		}
	}
}
```

### Server Links Dialog

A specialized dialog for displaying server links (configured server-side):

```kotlin
val linksDialog = dialogBuilder.serverLinks("server_links", "Server Links") {
	buttonWidth = 150
	columns = 3

	exitAction("Close") {
		action {
			dynamicRunCommand {
				say("Links closed")
			}
		}
	}
}
```

## Dialog Properties

All dialogs share common properties that can be customized:

### Basic Properties

```kotlin
dialogBuilder.confirmation("example", "Title") {
	// External title shown on buttons leading to this dialog
	externalTitle("Custom Button Text", Color.AQUA)

	// Action performed after dialog interactions
	afterAction = AfterAction.WAIT_FOR_RESPONSE // or CLOSE

	// Whether dialog can be dismissed with Escape key
	canCloseWithEscape = true

	// Whether to pause the game in single-player
	pause = true
}
```

### After Actions

Control what happens after dialog interactions:

- `AfterAction.NONE` - Do nothing
- `AfterAction.CLOSE` - Close the dialog (default)
- `AfterAction.WAIT_FOR_RESPONSE` - Keep dialog open awaiting response

## Body Elements

Dialogs can contain rich content between the title and action buttons:

### Plain Messages

Display text content:

```kotlin
bodies {
	plainMessage("Welcome to our server!") {
		width = 300
	}

	plainMessage(textComponent("Colored text", Color.GREEN))
}
```

### Items

Display items with descriptions:

```kotlin
bodies {
	item(Items.ENCHANTED_BOOK) {
		description = ItemDescription(textComponent("A mysterious tome"))
		showTooltip = true
		showDecorations = false
		height = 64
		width = 64
	}
}
```

## Input Controls

Multi-action dialogs can include various input controls for user interaction:

### Text Input

Single-line or multi-line text input:

```kotlin
inputs {
	text("username", "Username") {
		maxLength = 20
		initial = "Player"
		width = 200
		labelVisible = true
	}

	text("bio", "Biography") {
		multiline(maxLines = 5, height = 100)
		maxLength = 500
	}
}
```

### Number Range

Slider controls for numeric input:

```kotlin
inputs {
	numberRange("volume", "Volume", range = 0..100, initial = 50) {
		step = 5f
		labelFormat = "Volume: %d%%"
		width = 250
	}

	// Float ranges also supported
	numberRange("speed", "Speed", range = 0.1f..2.0f, initial = 1.0f) {
		step = 0.1f
	}
}
```

### Boolean Toggle

Checkbox-style boolean input:

```kotlin
inputs {
	boolean("notifications", "Enable Notifications") {
		initial = true
		onTrue = "✓ Enabled"
		onFalse = "✗ Disabled"
	}
}
```

### Single Option

Dropdown-style selection:

```kotlin
inputs {
	singleOption("gamemode", "Game Mode") {
		width = 200
		labelVisible = true

		option("survival", "Survival", initial = true)
		option("creative", "Creative")
		option("adventure", "Adventure")
		option("spectator", "Spectator")
	}
}
```

## Actions

Dialog actions define what happens when buttons are clicked:

### Available Action Types

```kotlin
action {
	// Run a command
	runCommand {
		say("Hello world!")
	}

	// Suggest a chat message
	suggestChatMessage("/gamemode creative")

	// Open a URL
	openUrl("https://minecraft.net")

	// Copy text to clipboard  
	copyToClipboard("Server IP: mc.example.com")

	// Change page in a book
	changePage(5)

	// Dynamic commands with macros named after the inputs
	dynamicRunCommand {
		say("Player name is ${macro('username')}")
	}

	// Custom dynamic actions, for sending custom packets for server plugins/mods
	dynamicCustom("custom_action") {
		this["data"] = "value"
		this["count"] = 42
	}
}
```

### Action Properties

Enhance actions with labels, tooltips, and sizing:

```kotlin
action("My Button") {
	tooltip("Click me for awesome results!")
	width = 150

	action {
		runCommand { say("Button clicked!") }
	}
}
```

## Using Dialogs in Commands

Commands can show and clear dialogs to players, using a reference or an inline dialog:

```kotlin
load {
	// Show dialog to specific players
	dialogShow(allPlayers(), myDialog)

	// Create and show dialog inline
	dialogShow(allPlayers()) {
		confirmation("inline_dialog", "Quick Confirmation") {
			yes("Yes") {
				action { runCommand { say("Yes selected") } }
			}
			no("No") {
				action { runCommand { say("No selected") } }
			}
		}
	}

	// Clear dialogs
	dialogClear(allPlayers())
}
```

## Advanced Examples

### Complex Form Dialog

```kotlin
val registrationForm = dialogBuilder.multiAction("register", "Player Registration") {
	columns = 1

	bodies {
		plainMessage("Welcome! Please fill out your information:")
	}

	inputs {
		text("display_name", "Display Name") {
			maxLength = 32
			labelVisible = true
		}

		text("email", "Email Address") {
			maxLength = 100
		}

		numberRange("age", "Age", range = 13..99, initial = 18) {
			step = 1f
		}

		singleOption("region", "Region") {
			option("na", "North America")
			option("eu", "Europe")
			option("as", "Asia")
			option("other", "Other")
		}

		boolean("newsletter", "Subscribe to Newsletter") {
			initial = false
		}

		text("comments", "Additional Comments") {
			multiline(maxLines = 3, height = 80)
			maxLength = 200
		}
	}

	actions {
		action("Register") {
			action {
				dynamicRunCommand {
					say("Registration submitted!")
					give(allPlayers(), Items.WRITTEN_BOOK)
				}
			}
		}

		action("Cancel") {
			action {
				suggestChatMessage("Registration cancelled")
			}
		}
	}
}
```

### Interactive Tutorial System

```kotlin
val tutorialDialog = dialogBuilder.dialogList("tutorials", "Tutorial Menu") {
	columns = 2
	buttonWidth = 180

	bodies {
		plainMessage("Choose a tutorial to begin:")
		item(Items.BOOK) {
			description = ItemDescription(textComponent("Learn the basics"))
		}
	}

	// Reference other tutorial dialogs
	dialogs(
		basicTutorial,
		advancedTutorial,
		pvpTutorial
	)

	exitAction("Skip Tutorials") {
		action {
			runCommand {
				advancement.grant(allPlayers(), AdvancementArgument("tutorial:skipped"))
			}
		}
	}
}
```

## Best Practices

1. **Keep dialogs focused**: Each dialog should serve a single, clear purpose
2. **Use appropriate dialog types**:
	- Confirmation for yes/no decisions
	- Notice for information display
	- Multi-action for complex forms or menus
3. **Provide clear labels**: Make button and input labels descriptive
4. **Include tooltips**: Add helpful tooltips for complex actions
5. **Handle edge cases**: Provide cancel/exit options where appropriate

## Integration with Other Systems

Dialogs work seamlessly with other Kore features:

```kotlin
val conditionalDialog = dialogBuilder.confirmation("weather_change", "Change Weather?") {
	// Only show if it's currently raining
	yes("Make Sunny") {
		action {
			runCommand {
				weatherClear()
			}
		}
	}

	no("Keep Current") {
		action {
			suggestChatMessage("Weather unchanged")
		}
	}
}

val firstDeathScoreboard = "first_death"
advancement("first_death") {
	criteria {
		tick("check_each_ticks") {
			conditions {
				entityProperties {
					nbt {
						this["Health"] = 0f
					}
				}
			}
		}
	}

	display(Items.AIR) {
		announceToChat = false
		hidden = true
	}

	rewards {
		function {
			scoreboard.objective(self(), firstDeathScoreboard).set(1)
			execute {
				ifCondition {
					score(self(), firstDeathScoreboard) equalTo 1
				}
				run {
					dialogShow(self(), conditionalDialog)
					scoreboard.objective(self(), firstDeathScoreboard).set(2) // Prevent re-triggering
				}
			}
		}
	}
}

// Integration with advancements
load {
	// Define the objective
	scoreboard.objective(firstDeathScoreboard).create(ScoreboardCriteria.DUMMY)
}
```

## See also

- [Advancements](./advancements)
- [Components](./components)
- [Predicates](./predicates)
