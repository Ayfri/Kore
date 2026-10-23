---
root: .components.layouts.MarkdownLayout
title: Clickable Minecraft Interaction Entities
nav-title: Interactions
description: Make clickable Minecraft interaction entities with Kore, with right and left click callbacks generated as advancements and functions and no tick function.
keywords: minecraft interaction entity, interaction entity, clickable entity, datapack click detection, right click, left click, player_interacted_with_entity, player_hurt_entity, datapack button, minecraft npc, clickable npc, shopkeeper npc, kore interaction
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
builds identically every time. The interaction builder isn't reachable inside a callback of a plain interaction,
`onRightClick { width = 2f }` doesn't compile. The click callbacks of an NPC are the exception, they reach the NPC
builder to declare an `objective(key)`.

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

An NPC sets it the same way, `npc("bob") { interaction { searchRadius = 40 } }`.

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

## NPCs

An NPC is a [mannequin](/docs/helpers/mannequins) you see and an interaction you click, summoned at the same position.
`npc(name) { }` declares both: `mannequin { }` configures the body, `onRightClick` and `onLeftClick` add the callbacks, and
`summon` places the pair.

```kotlin
import io.github.ayfri.kore.helpers.npcs.npc

val greeter = npc("greeter") {
	mannequin {
		customName("Bob", Color.GOLD)
		playerProfile("Ayfri")
	}
	onRightClick { say("Welcome!") }
}

function("place_greeter") { greeter.summon(vec3(0, 64, 0)) }
```

```mcfunction
summon minecraft:mannequin 0 64 0 {CustomName:{text:Bob,color:gold},Invulnerable:1b,Tags:["my_pack.npc.greeter"],immovable:1b,profile:{name:Ayfri},UUID:[I;698108564,1678478208,-1383230621,-2104385281]}
summon minecraft:interaction 0 64 0 {Tags:["my_pack.interaction.npc_greeter"],height:1.9f,width:0.7f,UUID:[I;-1579629773,-2071100508,-114521633,248843919]}
```

The mannequin is `immovable` and `invulnerable` by default and carries the tag `<pack>.npc.<name>`. The interaction is the
definition `npc_<name>`, so it generates what any [interaction](#right-and-left-clicks) does. They share a position instead of
riding each other, since a passenger sits at the attachment point and would shift the hitbox.

Kore has no hook run when a block ends, so the NPC is registered when the `npc(name) { }` block returns: the callbacks,
objectives and sizes only count when declared inside it, and a name is unique in the pack.

### Hitbox

The interaction is as wide and tall as the mannequin's `pose` plus 0.1, so a click reaches it before the mannequin. Setting
`width` or `height` in `interaction { }` keeps your value for that side and sizes the other one from the pose.

| Pose                         | Mannequin hitbox (width x height) | Interaction |
|------------------------------|-----------------------------------|-------------|
| `STANDING` (default)         | 0.6 x 1.8                         | 0.7 x 1.9   |
| `CROUCHING`                  | 0.6 x 1.5                         | 0.7 x 1.6   |
| `SWIMMING` and `FALL_FLYING` | 0.6 x 0.6                         | 0.7 x 0.7   |
| `SLEEPING`                   | 0.2 x 0.2                         | 0.3 x 0.3   |

{{{ .components.doc.NpcHitboxes }}}

A mannequin in a lying pose is drawn well outside its hitbox, the model of a swimming or sleeping one covers more than the
interaction. Set `interaction { width = 1.8f; height = 0.6f }` to cover the drawing.

```kotlin
val sleeper = npc("sleeper") {
	mannequin { pose = MannequinPose.SLEEPING }
	interaction { width = 1.8f }
}
```

Here the interaction is 1.8 wide and 0.3 tall.

### Inside an NPC callback

`@s` is the player and the position is the NPC's, like in any interaction callback, but `clicked` selects the mannequin,
the entity a player sees, instead of the interaction. The selector is sorted by distance, so with two instances standing
close together the one at the clicked position wins:

```kotlin
npc("bob") {
	onLeftClick { clicked -> clicked.kill() }
}
```

```mcfunction
execute at @s on attacker run kill @e[distance=..0.5,limit=1,sort=nearest,tag=my_pack.npc.bob,type=minecraft:mannequin]
```

### State

`objective(key)` declares the dummy objective `<pack>.npc.<name>.<key>`, creates it in the load function `npc_<name>_load`
and returns its name. The callback picks who holds the score, with the usual scoreboard commands:

| Scope                  | Holder                               | Example                                  |
|------------------------|--------------------------------------|------------------------------------------|
| Per player and per NPC | the clicking player, `self()`        | visits of each player to this NPC        |
| Per NPC instance       | the mannequin, `clicked`             | how many times this very NPC was greeted |
| Per definition         | a fake player, `fakePlayer("total")` | clicks on all the instances of this NPC  |

```kotlin
npc("counter") {
	val visits = objective("visits")
	val greeted = objective("greeted")
	val total = objective("total")

	onRightClick { clicked ->
		scoreboard.players.add(self(), visits, 1)
		scoreboard.players.add(clicked.asScoreHolder(), greeted, 1)
		scoreboard.players.add(fakePlayer("total").asScoreHolder(), total, 1)
	}
}
```

```mcfunction
scoreboard players add @s my_pack.npc.counter.visits 1
scoreboard players add @e[distance=..0.5,limit=1,sort=nearest,tag=my_pack.npc.counter,type=minecraft:mannequin] my_pack.npc.counter.greeted 1
scoreboard players add #total my_pack.npc.counter.total 1
```

```mcfunction
# npc_counter_load
scoreboard objectives add my_pack.npc.counter.visits dummy
scoreboard objectives add my_pack.npc.counter.greeted dummy
scoreboard objectives add my_pack.npc.counter.total dummy
```

For a value that is read back as a Kotlin property, see [State Delegates](/docs/helpers/state-delegates).

An NPC that talks, with timed lines and choices per player, calls a [conversation](/docs/helpers/conversations) from
`onRightClick`.

### NpcEntity

`summon` returns an `NpcEntity` holding the two handles, `mannequin` and `interaction`, each targeting its instance by UUID.
`kill()` and `teleportTo(position, rotation)` act on both, the rotation only turns the mannequin.

```kotlin
function("move_greeter") {
	val bob = greeter.summon(vec3(0, 64, 0))
	bob.teleportTo(vec3(0, 64, 5))
	bob.kill()
}
```

```mcfunction
teleport @e[limit=1,nbt={UUID:[I;698108564,1678478208,-1383230621,-2104385281]},type=minecraft:mannequin] 0 64 5
teleport @e[limit=1,nbt={UUID:[I;-1579629773,-2071100508,-114521633,248843919]},type=minecraft:interaction] 0 64 5
kill @e[limit=1,nbt={UUID:[I;698108564,1678478208,-1383230621,-2104385281]},type=minecraft:mannequin]
kill @e[limit=1,nbt={UUID:[I;-1579629773,-2071100508,-114521633,248843919]},type=minecraft:interaction]
```

Any other entity command goes through `bob.mannequin` or `bob.interaction`. `NpcEntity` isn't an `Entity` because it stands for
two entities.

The two UUIDs are fixed per `summon` call, so a function that runs at several places (`execute at @a run function place_npc`)
creates only the first NPC, and the next ones fail because the UUIDs are already taken. Placing one NPC at several positions
from one line isn't possible with `summon`, since the handle needs the UUIDs. The vanilla `summon` with the `toNbt()` of
both models has no handle and works there, with the interaction tag added to `tags` so the clicks still match:

```kotlin
greeter.interaction.tags = greeter.interaction.tags.orEmpty() + greeter.interaction.definitionTag!!

function("place_npc") {
	summon(EntityTypes.MANNEQUIN, vec3(), greeter.mannequin.toNbt())
	summon(EntityTypes.INTERACTION, vec3(), greeter.interaction.toNbt())
}
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {CustomName:{text:Bob,color:gold},Invulnerable:1b,Tags:["my_pack.npc.greeter"],immovable:1b,profile:{name:Ayfri}}
summon minecraft:interaction ~ ~ ~ {Tags:["my_pack.interaction.npc_greeter"],height:1.9f,width:0.7f}
```

### A Shopkeeper that Opens a Menu

A shopkeeper by hand is a mannequin, an interaction, the advancement and functions of [the click](#generated-output), a
trigger objective for the dialog and a score per player. With Kore it is one declaration: a right click opens a
[menu](/docs/helpers/menus) and counts the visit of the player, a left click answers in the chat.

```kotlin
val shop = menu("shop", "Bob's Shop", Color.GOLD) {
	text("Everything is for sale.")
	button("Heal me") { say("healed") }
}

val shopkeeper = npc("shopkeeper") {
	val visits = objective("visits")

	mannequin {
		customName("Bob", Color.GOLD)
		customNameVisible = true
		playerProfile("Ayfri")
		pose = MannequinPose.CROUCHING
	}

	onRightClick {
		scoreboard.players.add(self(), visits, 1)
		shop.open()
	}
	onLeftClick { tellraw(self(), textComponent("Bob: Right click to trade.", Color.GRAY)) }
}

function("place_bob") { shopkeeper.summon(vec3(0, 64, 0)) }
```

```mcfunction
# place_bob
summon minecraft:mannequin 0 64 0 {CustomName:{text:Bob,color:gold},CustomNameVisible:1b,Invulnerable:1b,Tags:["my_pack.npc.shopkeeper"],immovable:1b,pose:crouching,profile:{name:Ayfri},UUID:[I;1986223431,821987320,-494237298,-530125415]}
summon minecraft:interaction 0 64 0 {Tags:["my_pack.interaction.npc_shopkeeper"],height:1.6f,width:0.7f,UUID:[I;-725450195,-1726051519,-1557154389,87209692]}

# generated_460f5ccb
scoreboard players add @s my_pack.npc.shopkeeper.visits 1
dialog show @s my_pack:shop

# interaction_npc_shopkeeper_left_click_hit
tag @s add my_pack.interaction.clicked
execute at @s on attacker run tellraw @s {type:text,color:gray,text:"Bob: Right click to trade."}
data remove entity @s attack
tag @s remove my_pack.interaction.clicked

# npc_shopkeeper_load
scoreboard objectives add my_pack.npc.shopkeeper.visits dummy
```

> [!FOOTPRINT]
> - **Objectives:** one dummy objective per `objective(key)`, named `<pack>.npc.<name>.<key>`.
> - **Entities:** two per `summon`, a mannequin tagged `<pack>.npc.<name>` and an interaction tagged `<pack>.interaction.npc_<name>`.
> - **Load and files:** `npc_<name>_load` creates the objectives, only when there are some. The rest is the interaction's, see the footprint above.
