---
root: .components.layouts.MarkdownLayout
title: Entities & Players
nav-title: Entities & Players
description: Create and manage entities and players with the Kore OOP module - selectors, execute helpers, batch commands, entity commands, and entity effects.
keywords: minecraft, datapack, kore, oop, entity, player, commands, execute, batch, effects, teleport, kill, damage
date-created: 2026-03-03
date-modified: 2026-08-14
routeOverride: /docs/oop/entities-and-players
---

# Entities & Players

The OOP module models Minecraft entities and players as Kotlin objects with selectors and context-aware
extension functions.

## Creating entities

```kotlin
val player = player("Steve") {
	gamemode = Gamemode.SURVIVAL
	team = "red"
}

val arenaMobs = entity("ArenaMob", limitToOne = false) {
	tag = "arena"
}

val zombie = entity {
	type = EntityTypes.ZOMBIE
}
```

`player()` creates a `Player` instance (subclass of `Entity`) with `type = minecraft:player`, `limit = 1`,
and the given name. `entity()` creates a generic `Entity` with custom selector arguments, and can also start from a
named entity selector when you already know the exact entity name to target.

Use `player(...)` when you want a selector already scoped to players, and `entity { ... }` when you need a reusable
selector for mobs, armor stands, projectiles, or a more generic execute target. Use `entity("Name", ...)` when you
want the same convenience as `player("Name")` without forcing the selector to `minecraft:player`.

### Creating entities from an EntityType

You can also construct an `Entity` handle directly from an `EntityTypes` value using `toEntity()` or the typed
overload of `entity()`:

```kotlin
// extension on EntityTypeArgument, the selector type is set for you
val creeper = EntityTypes.CREEPER.toEntity()
val skeletons = EntityTypes.SKELETON.toEntity(limitToOne = false) { team = "arena" }

// same result, written like the other entity(...) overloads
val spider = entity(EntityTypes.SPIDER)
val endermen = entity(EntityTypes.ENDERMAN, limitToOne = false) { tag = "target" }
```

Both forms set `selector.type` automatically so you never need to write `type = EntityTypes.X` by hand.
Use `toEntity()` when calling from an `EntityTypeArgument` receiver; use `entity(type, ...)` when you want a form
that reads like the other `entity(...)` overloads.

### Handles for spawned displays and mannequins

Four `Entity` subclasses target one specific spawned entity by UUID rather than by selector filters:
`BlockDisplayEntity`, `ItemDisplayEntity`, `TextDisplayEntity`, and `MannequinEntity`. Each pins
`selector.type` and a `UUID` NBT filter, so it always resolves to exactly that entity.

You do not construct them directly in normal use - the [`helpers`](/docs/helpers/utilities) module returns them
when it spawns something. `DisplayEntityInterpolable.toEntity()` picks the matching display subclass, and
`Mannequin.summon(position)` returns a `MannequinEntity`:

```kotlin
function("spawn_display") {
	val display = blockDisplay {
		blockState(Blocks.DIAMOND_BLOCK)
	}.interpolable(vec3(0, 64, 0))

	display.summon()
	display.toEntity().addTag("arena_decor")

	val statue = mannequin { pose = MannequinPose.STANDING }.summon(vec3(0, 64, 0))
	statue.swing(SwingHand.MAINHAND)
}
```

Both spawn calls embed a random UUID in the summon NBT, which is what the returned handle filters on - so the
handle resolves to that one instance even if several are spawned. Once you hold one it behaves like any other
`Entity`: every extension on this page applies.

```mcfunction
kill @e[limit=1,nbt={UUID:[I;...]},type=minecraft:block_display]
```

See [Display Entities](/docs/helpers/display-entities) and [Mannequins](/docs/helpers/mannequins) for building and
spawning them.

## Reading and reshaping a handle

An `Entity` is a thin wrapper around `SelectorArguments`, exposed through these members:

| Member                    | Description                                                        |
|---------------------------|--------------------------------------------------------------------|
| `selector`                | The underlying `SelectorArguments`, mutable                        |
| `type`                    | The constrained entity type, or `null`                             |
| `isPlayer`                | Whether the handle currently resolves to `minecraft:player`        |
| `team`                    | Read/write the team filter on the selector                         |
| `limitToOne`              | Whether `asSelector()` defaults to a single-entity selector        |
| `asSelector(limitToOne)`  | Build the `@e[...]` selector, optionally overriding the limit and refining the arguments |

`asSelector()` is the escape hatch: every OOP command ultimately calls it, and you can call it yourself to drop back
into the core command DSL at any point.

```kotlin
function("inspect") {
	val mobs = entity(EntityTypes.ZOMBIE, limitToOne = false) { tag = "arena" }

	kill(mobs.asSelector(limitToOne = false) { tag = "marked" })
}
```

`toEntity<T>()` and `toEntityOrNull<T>()` narrow a generic `Entity` to a subtype such as `Player` - the first throws
when the conversion is impossible, the second returns `null`.

## Targeting `@s`

`Entity` always renders as `@e[...]`, because `asSelector()` builds an `allEntities` selector from the stored
`SelectorArguments`. There is no `Entity` handle that renders as `@s`, and `self()` returns a `SelectorArgument`,
not an `Entity`, so it cannot be passed to `Entity`-only helpers such as `getScoreEntity`, `batch`, or `executeAs`.

When the executing entity is already `@s` - inside an `execute as` block, an [event](/docs/oop/events) handler, or a
`batch` body - use the core command DSL with `self()` instead of trying to wrap it in an `Entity`:

```kotlin
function("charge_up") {
	val lastCharge = scoreboard.objective(self(), "last_crystal_charge")
	lastCharge += 10
}
```

`scoreboard.objective(selector, objective)` returns a `PlayerObjective` supporting the same operations as
`ScoreboardEntity` (`set`, `add`, `remove`, `reset`, `+=`, `-=`, `min`, `max`, `operation`), so nothing is lost by
dropping the OOP wrapper here. See
[Scoreboards → Raw selectors and `@s`](/docs/oop/scoreboards#raw-selectors-and-s).

The same applies to every other command: pass `self()` directly to `kill(...)`, `tellraw(...)`, `give(...)` and
friends rather than looking for an `Entity` equivalent.

## Execute helpers

Entity-scoped execute shortcuts emit `/execute as`, `/execute at`, or both:

```kotlin
function("teleport_self") {
	player.executeAs {
		run { it.teleportTo(it) }
	}

	player.executeAt {
		run { it.giveItem(Items.DIAMOND) }
	}

	player.executeAsAt {
		run { it.sendMessage("Hello from my location!") }
	}
}
```

These helpers are especially valuable when you would otherwise repeat the same `execute as`, `execute at`, or
`execute as ... at ...` boilerplate around several commands.

## Batch

`batch()` creates a named sub-function that groups multiple commands under a single entity context:

```kotlin
function("setup") {
	player.batch("init_player") {
		giveItem(Items.DIAMOND)
		giveEffect(Effects.SPEED, duration = 200)
		sendMessage("Welcome!")
	}
}
```

`batch()` is a good fit for onboarding flows, class kits, respawn setup, or any repeated multi-command routine that
should stay grouped under one entity context.

## Entity Commands

Extension functions on `Entity` for common [Minecraft commands](https://minecraft.wiki/w/Commands):

```kotlin
function("commands_demo") {
	player.kill()
	player.damage(5f)
	player.addTag("vip")
	player.removeTag("vip")
	player.giveXp(10.levels)
	player.setXp(0.points)
	player.setGamemode(Gamemode.CREATIVE)
	player.sendMessage("Hello!")
	player.showTitle(textComponent("Title"), textComponent("Subtitle"))
	player.showActionBar(textComponent("Action bar text"))
	player.playSound(Sounds.ENTITY_EXPERIENCE_ORB_PICKUP)
	player.mount(zombie)
	player.dismount()
	player.clearItems()
	player.giveItem(Items.DIAMOND)
	player.replaceItem(ItemSlotType.MAINHAND, itemStack(Items.NETHERITE_SWORD))
}
```

| Function        | Description                               |
|-----------------|-------------------------------------------|
| `addTag`        | Add a scoreboard tag                      |
| `clearItems`    | Clear inventory (optionally filtered)     |
| `damage`        | Deal damage with optional damage type     |
| `dismount`      | Dismount from current vehicle             |
| `giveItem`      | Give an item stack                        |
| `giveXp`        | Add experience (levels or points)         |
| `kill`          | Kill the entity                           |
| `mount`         | Mount another entity                      |
| `playSound`     | Play a sound at the entity                |
| `removeTag`     | Remove a scoreboard tag                   |
| `replaceItem`   | Replace an item in a specific slot        |
| `sendMessage`   | Send a tellraw message                    |
| `setGamemode`   | Change the player's gamemode              |
| `setXp`         | Set experience to an exact value          |
| `showActionBar` | Display text on the action bar            |
| `showTitle`     | Display a title and optional subtitle     |
| `swing`         | Swing the left or right hand              |
| `teleportTo`    | Teleport to coordinates or another entity |

## Scores, teams, and counts

These extensions bridge an `Entity` handle to the other OOP systems:

```kotlin
function("bridge_demo") {
	val kills = player.getScoreEntity("kills")   // -> ScoreboardEntity
	player.setScore("kills", 0)                  // one-shot write, no handle

	player.joinTeam("red")                       // by name, or joinTeam(redTeam)
	player.leaveAnyTeam()

	// counts how many entities match right now, then writes that number to a score or NBT path
	val mobs = entity(EntityTypes.ZOMBIE, limitToOne = false)
	mobs.storeCountIn(kills)
	mobs.storeCountIn(player, "kore.mob_count")
	mobs.storeCountIn(storage("kore", "stats"), "mob_count")
}
```

| Function          | Description                                                        |
|-------------------|--------------------------------------------------------------------|
| `getScoreEntity`  | Get a [`ScoreboardEntity`](/docs/oop/scoreboards) for an objective |
| `joinTeam`        | Join a [team](/docs/oop/teams) by name or `Team` handle             |
| `leaveAnyTeam`    | Leave the current team                                              |
| `setScore`        | Set a score without building a handle                               |
| `storeCountIn`    | Store how many entities match this selector into a score, entity NBT, or storage NBT |

## Entity Effects

Extension functions on `Entity` for giving, clearing, and managing
[mob effects](https://minecraft.wiki/w/Effect):

```kotlin
function("buff_player") {
	player.giveEffect(Effects.SPEED, duration = 200, amplifier = 1)
	player.giveInfiniteEffect(Effects.NIGHT_VISION, hideParticles = true)
	player.clearEffect(Effects.SPEED)
	player.clearAllEffects()
}
```

| Function             | Description                           |
|----------------------|---------------------------------------|
| `giveEffect`         | Give a timed effect with optional amp |
| `giveInfiniteEffect` | Give an infinite-duration effect      |
| `clearEffect`        | Remove a specific effect              |
| `clearAllEffects`    | Remove all effects                    |
| `effects { ... }`    | Builder block for multiple operations |

## Putting it together

```kotlin
dataPack("arena") {
	val player = player("Steve")
	val mobs = entity(EntityTypes.ZOMBIE, limitToOne = false) { tag = "arena" }

	function("round_start") {
		player.joinTeam("red")

		player.batch("round_start_player") {
			giveItem(Items.DIAMOND)
			giveEffect(Effects.SPEED, duration = 200)
			showActionBar(textComponent("Fight!"))
		}

		mobs.storeCountIn(player.getScoreEntity("mobs_left"))
	}
}
```

One reusable handle per gameplay concept, declared once at datapack level, then used across as many functions as
needed - that is the core value of the OOP entity API.

## See also

- [Items](/docs/oop/items) - Reuse item stacks with `giveItem`, `replaceItem`, or summon-based reward flows.
- [Events](/docs/oop/events) - Attach gameplay reactions directly to the entity and player handles you define here.
- [Spawners](/docs/oop/spawners) - Pair selectors and entity utilities with reusable spawning entry points.
