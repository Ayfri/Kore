---
root: .components.layouts.MarkdownLayout
title: Clickable Minecraft Interaction Entities
nav-title: Interactions
description: Make clickable Minecraft interaction entities with Kore, with right and left click callbacks generated as advancements and functions and no tick function.
keywords: minecraft interaction entity, interaction entity, clickable entity, datapack click detection, right click, left click, player_interacted_with_entity, player_hurt_entity, datapack button, kore interaction
date-created: 2026-10-22
date-modified: 2026-10-22
routeOverride: /docs/helpers/interactions
---

# Interactions

An interaction (`minecraft:interaction`) is an invisible hitbox the game reports clicks on. It is the usual base for
buttons, levers and clickable NPC areas in a datapack, since it needs no item, block or mob. Vanilla creates one with
[`/summon`](https://minecraft.wiki/w/Interaction), and the game then only records who clicked it last:

```mcfunction
summon minecraft:interaction ~ ~ ~ {width:0.5f,height:0.5f,response:1b}
```

Kore turns the click into a callback. `interaction(name) { }` declares the interaction, `onRightClick` and `onLeftClick`
add what runs, and `summon` places one:

```kotlin
import io.github.ayfri.kore.helpers.interactions.interaction
import io.github.ayfri.kore.helpers.interactions.onRightClick
import io.github.ayfri.kore.helpers.interactions.summon

val lever = interaction("lever") {
	height = 0.5f
	width = 0.5f
	onRightClick { say("Pulled") }
}

function("place_lever") { lever.summon(vec3(0, 64, 0)) }
```

```mcfunction
summon minecraft:interaction 0 64 0 {Tags:["my_pack.interaction.lever"],height:0.5f,width:0.5f,UUID:[I;-522001395,-648367109,-1705678205,-267231968]}
```

> [!FOOTPRINT]
> - **Objectives:** none.
> - **Storage:** none.
> - **Entities:** only the interactions you summon, each tagged `<pack>.interaction.<name>`.
> - **Forceloaded chunks:** none.
> - **Every tick:** nothing, the game fires the advancement when a click happens.
> - **Load and files:** each click kind a definition uses (`right_click`, `left_click`) generates one advancement at `interaction/<name>_<click>` and three functions: `interaction_<name>_<click>` (reward), `interaction_<name>_<click>_hit` (callbacks) and `interaction_<click>_target`, shared by every definition of the pack. Callbacks briefly tag the player `<pack>.interaction.source` and the interaction `<pack>.interaction.clicked`.

## Right and left clicks

`onRightClick { }` runs when a player uses the interaction, `onLeftClick { }` when a player attacks it. A definition can
use both, and several callbacks of the same kind run in declaration order.

```kotlin
interaction("gate_button") {
	height = 0.5f
	response = true
	width = 0.5f

	onRightClick { fill(vec3(10, 65, 5), vec3(10, 66, 5), Blocks.AIR) }
	onLeftClick { fill(vec3(10, 65, 5), vec3(10, 66, 5), Blocks.IRON_BARS) }
}
```

The callbacks are generated when the `interaction(name) { }` block returns, so they only count when added inside it. An
interaction built with the plain `interaction { }` (no name) is only a model: `summon` refuses it when it has callbacks,
because nothing was generated for them.

## Inside a callback

`@s` is the player who clicked and the position is the clicked interaction's, so relative coordinates and `positioned`
work from the button. The lambda parameter, `clicked` by convention, is an `Entity` selecting that interaction:

```kotlin
interaction("crate") {
	onLeftClick { clicked -> clicked.kill() }
}
```

```mcfunction
execute at @s on attacker run kill @e[limit=1,tag=my_pack.interaction.clicked,type=minecraft:interaction]
```

A callback with several commands is run through a generated function, named after a hash of its commands so the pack
builds identically every time.

## Generated output

For the `lever` above, a right click produces this advancement at
`data/my_pack/advancement/interaction/lever_right_click.json`:

```json
{
	"criteria": {
		"right_click": {
			"trigger": "minecraft:player_interacted_with_entity",
			"conditions": {
				"entity": {
					"type": "minecraft:entity_properties",
					"entity": "this",
					"predicate": {
						"minecraft:entity_type": "minecraft:interaction",
						"minecraft:entity_tags": {
							"all_of": [
								"my_pack.interaction.lever"
							]
						}
					}
				}
			}
		}
	},
	"rewards": {
		"function": "my_pack:generated_scopes/interaction_lever_right_click"
	}
}
```

and these functions:

```mcfunction
# interaction_right_click_target
execute on target if entity @s[tag=my_pack.interaction.source] run return 1
return fail

# interaction_lever_right_click_hit
tag @s add my_pack.interaction.clicked
execute at @s on target run say Pulled
data remove entity @s interaction
tag @s remove my_pack.interaction.clicked

# interaction_lever_right_click
advancement revoke @s only my_pack:interaction/lever_right_click
tag @s add my_pack.interaction.source
execute as @e[distance=..24,tag=my_pack.interaction.lever,type=minecraft:interaction] if function my_pack:generated_scopes/interaction_right_click_target run function my_pack:generated_scopes/interaction_lever_right_click_hit
tag @s remove my_pack.interaction.source
```

The advancement fires for the clicking player and revokes itself so the next click fires it again. The game stores the last
clicker on each interaction, not who clicked now, so the reward function tags the player and the `target` function
(`attacker` for a left click) keeps the nearby interactions of the definition whose last clicker is that player. Their
stored click is cleared after the callbacks ran, since `on target` and `on attacker` read it, so it can't fire twice.

A left click is the same with `player_hurt_entity`, `on attacker` and `data remove entity @s attack`.

## Hitbox and response

`width` and `height` size the hitbox, in blocks: the box is centered on the position horizontally and grows upwards, and
the game defaults to 1 for both. `response = true` makes the game react to a click, the attack sound for a left click
and an arm swing for a right click.

| NBT        | Kore       | Default | Effect                                              |
|------------|------------|---------|-----------------------------------------------------|
| `height`   | `height`   | `1`     | Height of the hitbox, upwards from the position     |
| `response` | `response` | `false` | Plays the attack sound and swings the arm on clicks |
| `Tags`     | `tags`     | none    | Extra scoreboard tags, the definition tag is added  |
| `width`    | `width`    | `1`     | Width of the hitbox, centered on the position       |

`tags` is one of the entity fields every interaction shares with other entities, from the
[`EntitySummonData`](/docs/oop/items#shared-entity-fields) base: `customName`, `glowing`, `rotation(yaw, pitch)` and the others are
available too, and written before `height`.

## Search radius

The reward function only looks at instances of the interaction within `searchRadius` blocks of the player, 24 by default.
A click on an instance farther away runs no callback and leaves its stored click in place, which happens with a wide
hitbox or a boosted `entity_interaction_range`. `searchRadius` is set per interaction, inside its block, and isn't part of
the entity NBT:

```kotlin
interaction("gate") {
	searchRadius = 40
	onRightClick { say("Open") }
}
```

```mcfunction
execute as @e[distance=..40,tag=my_pack.interaction.gate,type=minecraft:interaction] if function my_pack:generated_scopes/interaction_right_click_target run function my_pack:generated_scopes/interaction_gate_right_click_hit
```

## Hover

Hover callbacks aren't available because the `type_specific/player.looking_at` predicate needs a per-player check every
tick and only tests an entity, it can't tell which one is hovered. A tick function with the predicate does the job when
the pack accepts the cost:

```kotlin
val lookingAtStatue = predicate("looking_at_statue") {
	entityProperties {
		typeSpecific {
			player {
				lookingAt {
					entityType(EntityTypes.INTERACTION)
					entityTags { allOf("my_pack.interaction.statue") }
				}
			}
		}
	}
}

tick("hover_check") {
	execute {
		asTarget(allPlayers { predicate = lookingAtStatue })
		run { say("Looking") }
	}
}
```

```mcfunction
execute as @a[predicate=my_pack:looking_at_statue] run say Looking
```

## OOP handle

`summon` returns an `InteractionEntity`, which targets that instance by UUID and takes every `Entity` extension. The UUID
comes from `DataPack.entityUUID()`, a hash of the pack name, the interaction and its position, so builds stay identical.

```kotlin
function("setup") {
	val button = lever.summon(vec3(0, 64, 0))
	button.kill()
}
```

```mcfunction
kill @e[limit=1,nbt={UUID:[I;...]},type=minecraft:interaction]
```

One `summon` call writes one `summon` line with one fixed UUID, so a function that runs at several places
(`execute at @a run function place_lever`) creates only the first entity, and the next ones fail because the UUID is
already taken. Placing one interaction at several positions from one line isn't possible with the handle, since it needs
the UUID. The vanilla `summon` with the model's `toNbt()` has no UUID and no handle, so it works there, with the
definition tag added to `tags` so the clicks still match:

```kotlin
lever.tags = lever.tags.orEmpty() + lever.definitionTag!!

function("place_lever") { summon(lever.entityType, vec3(), lever.toNbt()) }
```

```mcfunction
summon minecraft:interaction ~ ~ ~ {Tags:["my_pack.interaction.lever"],height:0.5f,width:0.5f}
```

`InteractionEntity` lives in the `oop` module, see [Entities & Players](/docs/oop/entities-and-players).

## A Wall Button for a Gate

Without a helper, a clickable gate button is an advancement filtering on the interaction, a reward function that revokes it
and finds the clicking player among the interactions, and a function per click. With Kore it is one declaration:

```kotlin
val gateButton = interaction("gate_button") {
	height = 0.5f
	response = true
	width = 0.5f

	onRightClick {
		fill(vec3(10, 65, 5), vec3(10, 66, 5), Blocks.AIR)
		playSound(SoundEvents.Block.Lever.CLICK)
	}
	onLeftClick {
		fill(vec3(10, 65, 5), vec3(10, 66, 5), Blocks.IRON_BARS)
		playSound(SoundEvents.Block.Lever.CLICK)
	}
}

function("build_gate") { gateButton.summon(vec3(11, 65, 5)) }
```

```mcfunction
# interaction_gate_button_right_click_hit
tag @s add my_pack.interaction.clicked
execute at @s on target run function my_pack:generated_scopes/generated_16d41647
data remove entity @s interaction
tag @s remove my_pack.interaction.clicked

# generated_16d41647
fill 10 65 5 10 66 5 minecraft:air
playsound minecraft:block.lever.click
```

Right clicking the button opens the gate, left clicking it closes it again.
