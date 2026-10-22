---
root: .components.layouts.MarkdownLayout
title: Minecraft Datapack Conversations - Per-Player Dialogue with Kore
nav-title: Conversations
description: Script NPC dialogue in Minecraft datapacks with Kore, timed lines, click pauses and dialog or chat choices, each player in their own conversation.
keywords: minecraft conversation, datapack dialogue, npc dialogue, quest giver datapack, dialog choices, clickable chat, per-player timer, datapack cutscene, kore conversation
date-created: 2026-10-22
date-modified: 2026-10-22
routeOverride: /docs/helpers/conversations
---

# Conversations

A `Conversation` is a scripted exchange played to one player: lines in the chat, pauses, choices and any command, in the
order you write them. The progress is stored on each player, so ten players talk to the same NPC at the same time without
sharing a timer, a line or a choice.

```kotlin
import io.github.ayfri.kore.helpers.conversations.conversation

val greet = conversation("greet") {
	speaker = textComponent("Bob")
	line("Hello!")
	wait(2.seconds)
	line("Bye!")
}

function("talk") { greet.start() }
```

```mcfunction
# conversation_greet_2
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",text:"Bob"},{type:"text",text:"Bye!"}]}
scoreboard players reset @s my_pack.conversation.greet
scoreboard players reset @s my_pack.conversation.greet.wait

# conversation_greet_1
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",text:"Bob"},{type:"text",text:"Hello!"}]}
scoreboard players set @s my_pack.conversation.greet 2
scoreboard players set @s my_pack.conversation.greet.wait 40
advancement revoke @s only my_pack:conversation/greet

# conversation_greet_dispatch
execute if score @s my_pack.conversation.greet matches 2 run return run function my_pack:generated_scopes/conversation_greet_2

# conversation_greet_tick
execute unless score @s my_pack.conversation.greet matches 1.. run return fail
scoreboard players remove @s my_pack.conversation.greet.wait 1
execute if score @s my_pack.conversation.greet.wait matches 1.. run return run advancement revoke @s only my_pack:conversation/greet
function my_pack:generated_scopes/conversation_greet_dispatch

# conversation_greet_load
scoreboard objectives add my_pack.conversation.greet dummy
scoreboard objectives add my_pack.conversation.greet.wait dummy
```

The steps are compiled into functions when the `conversation(name) { }` block returns, so they only count when declared
inside it. A name is unique in the pack: declaring `greet` twice throws `Conversation 'greet' is already declared in this pack.`

> [!FOOTPRINT]
> - **Objectives:** `<pack>.conversation.<name>` (`dummy`, the step of each player), `<pack>.conversation.<name>.wait` (`dummy`, only with `wait` or `waitUntil`) and `<pack>.conversation.<name>.choice` (`trigger`, only with a `choice`).
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** nothing while nobody is waiting, the tick function is not in `#minecraft:tick` but the reward of the advancement, so a player waiting on a timer or a choice costs about 4 commands per tick. A conversation made of lines and `waitFor` only has no tick at all.
> - **Load and files:** `conversation_<name>_load` creates the objectives. One advancement `conversation/<name>` fires `conversation_<name>_tick` for the player, the other functions are one per segment, `_dispatch` (only with a timed pause), `_show_<n>` and `_reenable` (only with a `choice`), `_resume` and `_advance` (only when `resume()` or `advance()` is called).

## Steps

| Step                      | What it does                                                                                       |
|---------------------------|----------------------------------------------------------------------------------------------------|
| `line(text)`              | Says `text` in the chat, as `<Bob> text` when a `speaker` is set                                   |
| `show(dialog)`            | Shows a dialog to the player and goes on right away                                                |
| `step { }`                | Runs any commands as the player                                                                    |
| `wait(duration)`          | Pauses for a duration, at least one tick                                                           |
| `waitFor()`               | Pauses until `advance()` or `resume()` runs for the player                                         |
| `waitUntil { condition }` | Pauses until an `execute` condition holds, checked every tick                                      |
| `choice(title) { }`       | Pauses until the player picks an option, in a dialog or in the chat                                |

`line(text, color)` colors the text, a `ChatComponents` works too. `speaker` applies to the lines declared after it.

```kotlin
conversation("miner") {
	speaker = textComponent("Bob")
	line("Mine ten stones.")
	line("Hurry!", Color.RED)
	step { give(self(), Items.IRON_PICKAXE) }
	waitUntil { score(self(), "my_pack.mined") greaterThanOrEqualTo 10 }
	line("Well done.")
}
```

Inside `step { }` and `waitUntil { }` the conversation builder isn't reachable, `step { line("x") }` doesn't compile: the
block only holds commands, and the conversation steps go beside it.

## Pauses

`wait(duration)` truncates to whole ticks and refuses less than one: `wait(0.5.seconds)` stores 10, `wait(0.ticks)` throws.

```kotlin
wait(0.5.seconds)  // scoreboard players set @s my_pack.conversation.miner.wait 10
```

The countdown loses one point per tick of the player, and the next step runs on its last point: `wait(2.seconds)` runs it
40 ticks after the previous step, or 39 when the advancement already fires again in the tick that set the countdown,
depending on where the previous step ran in the game tick. It is never later than 40. `waitUntil` runs its condition every tick,
the condition is the first line of the steps after it, the `miner` conversation above gives:

```mcfunction
# conversation_miner_2
execute unless score @s my_pack.mined matches 10.. run return run advancement revoke @s only my_pack:conversation/miner
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",text:"Bob"},{type:"text",text:"Well done."}]}
scoreboard players reset @s my_pack.conversation.miner
scoreboard players reset @s my_pack.conversation.miner.wait
```

`waitFor()` costs nothing while waiting, there is no advancement and no tick: the conversation goes on when a function
calls `advance()` or `resume()`, typically from the click on an NPC.

## Choices

`choice(title) { }` pauses on options, `option(label) { }` declares what runs when one is picked. After the steps of the
picked option, the conversation goes on with the steps after the `choice`. Options can hold any step, even another choice.

```kotlin
conversation("offer") {
	line("A gift for you?")
	choice("Take the bread?") {
		option("Yes", Color.GREEN) { step { give(self(), Items.BREAD) } }
		option("No", Color.RED) { line("Suit yourself.") }
	}
	line("See you.")
}
```

A choice has two styles, both setting the same `trigger` objective, `trigger <pack>.conversation.<name>.choice set <n>`,
which any player can run, so the rest of the mechanism is shared. The options are numbered in order across the whole
conversation, each choice's options together: two choices of two options give 1 and 2 to the first and 3 and 4 to the
second, and a choice nested in an option comes after the choice holding it. So an old chat option scrolled up can't pick an option of
a later choice. The game disables a trigger after each use, so when such a stale option is clicked, the tick function
re-enables the trigger and sets it back to 0 (`conversation_<name>_reenable`), and the options of the pending choice keep working.

| Style                          | Shows                                        |
|--------------------------------|----------------------------------------------|
| `ChoiceStyle.DIALOG` (default) | A dialog with one button per option          |
| `ChoiceStyle.CHAT`             | Clickable options in the chat, `[Yes]  [No]` |

`choiceStyle` sets the style of the choices declared after it, and a choice gives its own as second argument:

```kotlin
conversation("mixed") {
	choiceStyle = ChoiceStyle.CHAT
	choice("Chat choice") { option("A") { } }
	choice("Dialog choice", ChoiceStyle.DIALOG) { option("B") { } }
}
```

### Chat choices

The options are one `tellraw` line with a click event, after the title said by the speaker. `hover` is the tooltip of an
option:

```kotlin
conversation("help") {
	speaker = textComponent("Mira", Color.GOLD)
	choice("Will you help?", ChoiceStyle.CHAT) {
		option("Accept", Color.GREEN, hover = "Take the pickaxe") { line("Thank you.") }
		option("Decline", Color.RED) { line("Maybe later.") }
	}
}
```

```mcfunction
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",color:"gold",text:"Mira"},{type:"text",text:"Will you help?"}]}
tellraw @s [{type:"text",text:""},{type:"text",click_event:{action:"run_command",command:"trigger my_pack.conversation.help.choice set 1"},extra:[{type:"text",color:"green",text:"["},{type:"text",color:"green",text:"Accept"},{type:"text",color:"green",text:"]"}],hover_event:{action:"show_text",value:"Take the pickaxe"},text:""},{type:"text",text:"  "},{type:"text",click_event:{action:"run_command",command:"trigger my_pack.conversation.help.choice set 2"},extra:[{type:"text",color:"red",text:"["},{type:"text",color:"red",text:"Decline"},{type:"text",color:"red",text:"]"}],text:""}]
```

`ChatChoiceFormat` customizes the look. It is set for the conversation with `chatFormat`, and overridden for one choice with
`chat { }`:

| Field       | Default                              | Effect                                                                      |
|-------------|--------------------------------------|-----------------------------------------------------------------------------|
| `decorate`  | the label in brackets of its color   | Wraps the label, the result is what is clickable and carries the hover text |
| `prompt`    | `true`                               | Says the title of the choice first, as a line from the speaker              |
| `separator` | two spaces                           | Written between options on a line, `null` writes one option per line        |

```kotlin
conversation("menu") {
	chatFormat = chatFormat.copy(prompt = false)
	choice("Pick one", ChoiceStyle.CHAT) {
		chat {
			decorate = { label -> textComponent("> ") + label }
			separator = null
		}
		option("Sword") { }
		option("Bow") { }
	}
}
```

`option(label: ChatComponents, hover: ChatComponents?)` takes full components when the label needs more than a color.

### Dialog choices

The options are the buttons of a [`multi_action` dialog](/docs/data-driven/dialogs), generated at
`data/<pack>/dialog/conversation_<name>_choice_<n>.json`. `hover` is the tooltip of a button. `dialog { }` tweaks the
generated `MultiAction` once its title and buttons are set: `body`, `columns`, `canCloseWithEscape`, the `width` of each
`actions`...

```kotlin
conversation("help") {
	choice("Will you help?") {
		dialog {
			columns = 1
			canCloseWithEscape = false
		}
		option("Accept", Color.GREEN) { line("Thank you.") }
		option("Decline", Color.RED) { line("Maybe later.") }
	}
}
```

Closing the dialog with Escape leaves the choice pending, and `advance()` or `resume()` shows it again. With
`canCloseWithEscape = false` the player has to pick.

## Start, advance, cancel and resume

These run as the player the conversation is played to, `@s`.

| Call        | Does                                                                                                   |
|-------------|--------------------------------------------------------------------------------------------------------|
| `start()`   | Starts the conversation unless it is already running                                                   |
| `advance()` | Starts it when idle, continues a `waitFor`, shows a pending choice again, nothing during a timed pause |
| `resume()`  | Continues a `waitFor` or shows a pending choice again, nothing in any other state                      |
| `cancel()`  | Resets the scores, the conversation is idle again                                                      |

```kotlin
function("talk") {
	greet.start()      // execute unless score @s my_pack.conversation.greet matches 1.. run function my_pack:generated_scopes/conversation_greet_1
	greet.advance()    // function my_pack:generated_scopes/conversation_greet_advance
	greet.cancel()     // scoreboard players reset @s my_pack.conversation.greet, then the same for its .wait objective
}
```

`advance()` is made for clicks: a player clicking an NPC twice during a timed pause doesn't skip it, and a player who closed
a choice gets it back. `cancel()` leaves an open choice dialog on screen, its buttons no longer do anything.

`start()`, `advance()` and `resume()` can't be called from the steps of the conversation itself, a `step { }` runs while
the conversation compiles and they throw `Conversation 'greet' is used before its declaration block returned.`. Call them
from a function or a click callback declared after the conversation.

## Players and state

Running a conversation as a non-player isn't possible because its timers run on the advancements of the player. Each
player owns their state in the scores of the three objectives above, nothing else is stored, and none of the generated
functions uses a selector other than `@s`.

A player who logs out during a pause stops its countdown, since only the tick of the player advances it, and the
conversation goes on when they join again. An advancement fires for a player who just joined once, then the tick function
ends with `return fail` and the advancement stays granted, costing nothing.

## A Quest Giver

Without a helper, a quest giver is a mannequin, an interaction, a click advancement, a score per player for the quest, a
timer per player for the lines, a trigger objective for the choice and a function per branch. With Kore it is one
declaration: the first click plays three timed lines then a choice in the chat, accepting gives a pickaxe and remembers it
for that player, and the next click says something else.

```kotlin
val questGiver = npc("quest_giver") {
	val quest = objective("quest")

	mannequin {
		customName("Elder Mira", Color.GOLD)
		playerProfile("Ayfri")
	}

	val intro = conversation("quest_intro") {
		speaker = textComponent("Elder Mira", Color.GOLD)
		choiceStyle = ChoiceStyle.CHAT

		line("Traveler, the mines have gone quiet.")
		wait(2.seconds)
		line("Something old woke up down there.")
		wait(2.seconds)
		choice("Will you take this pickaxe and look?") {
			option("Accept", Color.GREEN, hover = "Take the pickaxe") {
				step {
					give(self(), Items.IRON_PICKAXE)
					scoreboard.players.set(self(), quest, 1)
				}
				line("Good luck, and come back alive.")
			}
			option("Decline", Color.RED) { line("I understand. The offer stays open.") }
		}
	}

	onRightClick {
		execute {
			ifCondition { score(self(), quest, rangeOrInt(1)) }
			run { tellraw(self(), "Elder Mira: How is the pickaxe treating you?", Color.GOLD) }
		}
		execute {
			unlessCondition { score(self(), quest, rangeOrInt(1)) }
			run { intro.advance() }
		}
	}
}

function("place_mira") { questGiver.summon(vec3(0, 64, 0)) }
```

The conversation is declared inside the `npc { }` block to share the `quest` objective of the [NPC](/docs/helpers/interactions#npcs).
The choice of that conversation compiles to:

```mcfunction
# conversation_quest_intro_2
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",color:"gold",text:"Elder Mira"},{type:"text",text:"Something old woke up down there."}]}
scoreboard players set @s my_pack.conversation.quest_intro 3
scoreboard players set @s my_pack.conversation.quest_intro.wait 40
advancement revoke @s only my_pack:conversation/quest_intro

# conversation_quest_intro_3
scoreboard players set @s my_pack.conversation.quest_intro 4
scoreboard players enable @s my_pack.conversation.quest_intro.choice
scoreboard players set @s my_pack.conversation.quest_intro.choice 0
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",color:"gold",text:"Elder Mira"},{type:"text",text:"Will you take this pickaxe and look?"}]}
tellraw @s [{type:"text",text:""},{type:"text",click_event:{action:"run_command",command:"trigger my_pack.conversation.quest_intro.choice set 1"},extra:[{type:"text",color:"green",text:"["},{type:"text",color:"green",text:"Accept"},{type:"text",color:"green",text:"]"}],hover_event:{action:"show_text",value:"Take the pickaxe"},text:""},{type:"text",text:"  "},{type:"text",click_event:{action:"run_command",command:"trigger my_pack.conversation.quest_intro.choice set 2"},extra:[{type:"text",color:"red",text:"["},{type:"text",color:"red",text:"Decline"},{type:"text",color:"red",text:"]"}],text:""}]
advancement revoke @s only my_pack:conversation/quest_intro

# conversation_quest_intro_5
scoreboard players reset @s my_pack.conversation.quest_intro.choice
give @s minecraft:iron_pickaxe
scoreboard players set @s my_pack.npc.quest_giver.quest 1
tellraw @s {type:"translatable",translate:"chat.type.text",with:[{type:"text",color:"gold",text:"Elder Mira"},{type:"text",text:"Good luck, and come back alive."}]}
function my_pack:generated_scopes/conversation_quest_intro_4

# conversation_quest_intro_reenable
scoreboard players enable @s my_pack.conversation.quest_intro.choice
scoreboard players set @s my_pack.conversation.quest_intro.choice 0

# conversation_quest_intro_tick
execute unless score @s my_pack.conversation.quest_intro matches 1.. run return fail
execute if score @s my_pack.conversation.quest_intro matches 4 if score @s my_pack.conversation.quest_intro.choice matches 1 run return run function my_pack:generated_scopes/conversation_quest_intro_5
execute if score @s my_pack.conversation.quest_intro matches 4 if score @s my_pack.conversation.quest_intro.choice matches 2 run return run function my_pack:generated_scopes/conversation_quest_intro_6
execute if score @s my_pack.conversation.quest_intro matches 4 unless score @s my_pack.conversation.quest_intro.choice matches 0 run function my_pack:generated_scopes/conversation_quest_intro_reenable
execute if score @s my_pack.conversation.quest_intro matches 4 run return run advancement revoke @s only my_pack:conversation/quest_intro
scoreboard players remove @s my_pack.conversation.quest_intro.wait 1
execute if score @s my_pack.conversation.quest_intro.wait matches 1.. run return run advancement revoke @s only my_pack:conversation/quest_intro
function my_pack:generated_scopes/conversation_quest_intro_dispatch
```

The state of each player lives in two places: the step of the conversation in `my_pack.conversation.quest_intro` and
the quest in `my_pack.npc.quest_giver.quest`, both dummy scores held by the player. A click while the lines are playing
does nothing, a click while the choice is pending writes it again. While nobody talks, nothing runs every tick.

The same choice as a dialog is the same declaration without `choiceStyle`, with the buttons and an optional tweak:

```kotlin
choice("Will you take this pickaxe and look?") {
	dialog { columns = 1 }
	option("Accept", Color.GREEN, hover = "Take the pickaxe") { step { give(self(), Items.IRON_PICKAXE) } }
	option("Decline", Color.RED) { }
}
```

```mcfunction
dialog show @s my_pack:conversation_quest_intro_choice_4
```
