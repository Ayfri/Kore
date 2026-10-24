---
root: .components.layouts.MarkdownLayout
title: Minecraft Commands in Kore - Type-Safe Command DSL
nav-title: Commands
description: Every Minecraft command in a type-safe Kotlin DSL. From /say, /teleport, and /give to /execute, /data, /scoreboard, and /summon -- all with code examples and generated mcfunction output.
keywords: minecraft commands, kore commands, kotlin commands dsl, mcfunction generator, execute command, data command, teleport command, summon command, minecraft command builder, type-safe commands
date-created: 2026-02-03
date-modified: 2026-10-24
routeOverride: /docs/commands/commands
---

# Commands

Kore provides type-safe builders for all Minecraft commands. This page covers both simple and complex command usage with examples.

Commands are used inside [Functions](/docs/commands/functions) to perform actions in the game. For dynamic command
arguments, see [Macros](/docs/commands/macros).

## Simple Commands

Simple commands are straightforward and take basic arguments like strings, numbers,
or [selectors](/docs/concepts/selectors).

### Say Command

The
`say` command broadcasts a message to all players in the chat. The message appears with the sender's name (the entity
executing the command). For more advanced chat formatting, see [Chat Components](/docs/concepts/chat-components).

```kotlin
function("greetings") {
	say("Hello, world!")
	say("Welcome to the server!")
}
```

Generated output:

```mcfunction
say Hello, world!
say Welcome to the server!
```

### Teleport Command

The `teleport` command instantly moves entities to a new location. You can teleport to absolute coordinates, relative positions, or another entity's location. Optionally specify rotation (yaw/pitch) for the entity to face after teleporting.

```kotlin
function("teleport_examples") {
	teleport(allPlayers(), vec3(100, 64, 100))

	// second argument is the destination, so this sends everyone to the command's executor
	teleport(allPlayers(), self())

	// rotation is yaw then pitch: yaw turns left/right, pitch looks up/down
	teleport(self(), vec3(0, 100, 0), rotation(0.rot, 90.rot))
}
```

Generated output:

```mcfunction
teleport @a 100 64 100
teleport @a @s
teleport @s 0 100 0 0 90
```

### Give Command

The
`give` command adds items directly to a player's inventory. If the inventory is full, items drop on the ground. You can
specify item count and use [Components](/docs/concepts/components) for custom item data.

```kotlin
function("give_items") {
	give(allPlayers(), Items.DIAMOND_SWORD)
	give(allPlayers(), Items.GOLDEN_APPLE, 64)
}
```

Generated output:

```mcfunction
give @a minecraft:diamond_sword
give @a minecraft:golden_apple 64
```

### Kill Command

The
`kill` command instantly removes entities from the world. Killed entities trigger death events (drops, death messages for players). Use selectors to target specific entity types.

```kotlin
function("cleanup") {
	kill(allEntities {
		type = EntityTypes.ZOMBIE
	})
	kill(self())
}
```

Generated output:

```mcfunction
kill @e[type=minecraft:zombie]
kill @s
```

### Kick Command

The `kick` command disconnects players from the server, with an optional reason that can contain target selectors. It
needs permission level 3, one above the default `function-permission-level` of 2, so a function only runs it on a server
that raises this setting in `server.properties`.

```kotlin
function("kick_examples") {
	kick(allPlayers())
	kick(allPlayers(), "Server restarting")
}
```

Generated output:

```mcfunction
kick @a
kick @a Server restarting
```

### Effect Command

The
`effect` command applies or removes status effects (like Speed, Regeneration, Poison) from entities. Effects have duration (in seconds or infinite) and amplifier levels (0 = level I, 1 = level II, etc.).

```kotlin
function("effects") {
	effect(allPlayers()) { give(Effects.SPEED, duration = 60, amplifier = 1) }
	effect(self()) { giveInfinite(Effects.REGENERATION) }
	effect(allPlayers()) { clear() }
	effect(self()) { clear(Effects.POISON) }
}
```

Generated output:

```mcfunction
effect give @a minecraft:speed 60 1
effect give @s minecraft:regeneration infinite
effect clear @a
effect clear @s minecraft:poison
```

### Gamemode Command

The
`gamemode` command changes a player's game mode (Survival, Creative, Adventure, Spectator). Each mode has different abilities and restrictions.

```kotlin
function("modes") {
	gamemode(Gamemode.CREATIVE, allPlayers())
	gamemode(Gamemode.SURVIVAL, player("Steve"))
}
```

Generated output:

```mcfunction
gamemode creative @a
gamemode survival @a[limit=1,name=Steve]
```

### Time Command

The `time` command controls world clocks. Time is measured in [ticks](/docs/concepts/time) (20 ticks = 1 second,
24 000 ticks = 1 Minecraft day). The `time` property on a `Function` returns a `Time` DSL scope.

For a full reference covering world clocks, timelines, time markers, and the `timeCheck` predicate, see
[World Clocks](/docs/data-driven/world-clocks).

#### Basic Time Operations

```kotlin
function("time_control") {
	time.add(6000)           // advance the clock, 6000 ticks = 5 minutes
	time.add(1.days)         // units spare you the maths, 1 day = 24000 ticks
	time.pause()             // freeze the clock, the world keeps running
	time.resume()
	time.set(TimePeriod.DAY) // jump to a named moment instead of a raw tick
	time.set(6000)
	time.query(TimeType.DAYS)
	time.queryTime()         // total ticks since the world was created
}
```

Generated output:

```mcfunction
time add 6000
time add 1d
time pause
time resume
time set day
time set 6000
time query day
time query time
```

#### Querying Timelines

Use `query(timeline)` to read a timeline's progress, and `queryRepetitions(timeline)` to read how many times
it has looped:

```kotlin
function("time_query_timeline") {
	time.query(Timelines.DAY)
	time.queryRepetitions(Timelines.DAY)
}
```

Generated output:

```mcfunction
time query minecraft:day
time query minecraft:day repetitions
```

#### Setting the Day-Night Cycle Rate

Use `rate(rate)` to control how fast the day-night cycle progresses. `1` is the default speed, `0` freezes the cycle,
and the maximum is `1000`. This is independent of the server tick rate:

```kotlin
function("time_rate") {
	time.rate(1.0f) // default speed
	time.rate(0.0f) // sun and moon stop moving, but the world still ticks
	time.rate(2.0f) // days pass twice as fast
	time.rate(0.5f) // days pass half as fast
}
```

Generated output:

```mcfunction
time rate 1
time rate 0
time rate 2
time rate 0.5
```

#### Setting to a Time Marker

`TimeMarkerArgument` (created with the `timeMarker()` factory) references a named tick position defined inside
a timeline. Pass it to `time.set()` to jump the clock to that position:

```kotlin
function("skip_to_noon") {
	time.set(timeMarker("noon", "mymod"))
}
```

Generated output:

```mcfunction
time set mymod:noon
```

#### Targeting a Specific Clock with `time.of(clock)`

When your datapack defines multiple [world clocks](/docs/data-driven/world-clocks), use `time.of(clock)` to
scope every subcommand to that clock. It returns a `TimeWithClock` instance that mirrors the full `Time` API:

```kotlin
val seasonClock = worldClock("season")

function("season_control") {
	time.of(seasonClock).add(6000)
	time.of(seasonClock).pause()
	time.of(seasonClock).resume()
	time.of(seasonClock).set(TimePeriod.DAY)
	time.of(seasonClock).set(timeMarker("summer", "mymod"))
	time.of(seasonClock).query(TimeType.DAYS)
	time.of(seasonClock).query(Timelines.DAY)
	time.of(seasonClock).queryRepetitions(Timelines.DAY)
	time.of(seasonClock).queryTime()
	time.of(seasonClock).rate(2.0f)
}
```

Generated output:

```mcfunction
time of my_datapack:season add 6000
time of my_datapack:season pause
time of my_datapack:season resume
time of my_datapack:season set day
time of my_datapack:season set mymod:summer
time of my_datapack:season query day
time of my_datapack:season query minecraft:day
time of my_datapack:season query minecraft:day repetitions
time of my_datapack:season query time
time of my_datapack:season rate 2
```

### Weather Command

The
`weather` command changes the world's weather state. Clear weather has full sunlight, rain reduces light and affects mob spawning, thunder enables lightning strikes and charged creeper creation.

```kotlin
function("weather_control") {
	weatherClear()
	weatherRain(6000)
	weatherThunder()
}
```

Generated output:

```mcfunction
weather clear
weather rain 6000
weather thunder
```

### Summon Command

The
`summon` command spawns a new entity at the specified location. You can provide NBT data to customize the entity's properties (name, AI, equipment, etc.).

```kotlin
function("spawn_mobs") {
	summon(EntityTypes.ZOMBIE, vec3(0, 64, 0))
	summon(EntityTypes.CREEPER, vec3()) {
		this["CustomName"] = "Boom"
		this["NoAI"] = true
	}
}
```

Generated output:

```mcfunction
summon minecraft:zombie 0 64 0
summon minecraft:creeper ~ ~ ~ {CustomName:Boom,NoAI:1b}
```

The trailing block is the raw [NBT builder](/docs/concepts/nbts), so entity tags are written by name and are not
type-checked against the entity type.

For item entities, the [`oop` module](/docs/oop/items) provides a typed alternative: `itemStack(...).summon { }`
builds the `Item` compound for you and exposes the common entity tags (`Motion`, `Rotation`, `Tags`, `Glowing`,
`NoGravity`, `PickupDelay`, `Age`, …) as named properties on `ItemEntitySummonData` instead of string keys. See
[Items → Configuring the spawned entity](/docs/oop/items#configuring-the-spawned-entity).

Higher-level spawning options:

- [Spawners](/docs/oop/spawners) - reusable named spawner handles with a fixed type and position.
- [Display Entities](/docs/helpers/display-entities) - block, item, and text displays with transformations and interpolation.
- [Mannequins](/docs/helpers/mannequins) - posable player-model entities.

### SetBlock Command

The `setblock` command places a single block at the specified coordinates. Use modes to control behavior: `destroy` (drops items),
`keep` (only if air), or `replace` (default).

```kotlin
function("build") {
	setBlock(vec3(0, 64, 0), Blocks.DIAMOND_BLOCK)
	setBlock(vec3(0, 65, 0), Blocks.STONE, SetBlockMode.REPLACE)
}
```

Generated output:

```mcfunction
setblock 0 64 0 minecraft:diamond_block
setblock 0 65 0 minecraft:stone replace
```

### Fill Command

The `fill` command fills a rectangular region with blocks. `FillOption` picks the mode: `HOLLOW` (outer shell, air
inside), `OUTLINE` (shell without clearing inside), `KEEP` (only air blocks) and `DESTROY` (drops items). Passing a
block filter instead emits `replace <filter>`, changing only the blocks that match it.

```kotlin
function("fill_area") {
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.STONE)
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.AIR, Blocks.STONE)
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.GLASS, FillOption.HOLLOW)
}
```

Generated output:

```mcfunction
fill 0 64 0 10 70 10 minecraft:stone
fill 0 64 0 10 70 10 minecraft:air replace minecraft:stone
fill 0 64 0 10 70 10 minecraft:glass hollow
```

### Enchant Command

The `enchant` command adds an enchantment to the item held by the target entity.
The enchantment must be compatible with the item type. For more control over enchantments,
see [Enchantments](/docs/data-driven/enchantments).

```kotlin
function("enchant_examples") {
	enchant(self(), Enchantments.MENDING)
	enchant(self(), Enchantments.SHARPNESS, 5)
}
```

Generated output:

```mcfunction
enchant @s minecraft:mending
enchant @s minecraft:sharpness 5
```

### Difficulty Command

The
`difficulty` command gets or sets the world's difficulty level (Peaceful, Easy, Normal, Hard). Difficulty affects mob
damage, hunger depletion, and whether hostile mobs spawn. Called without arguments it queries the current value.

```kotlin
function("difficulty_examples") {
	difficulty() // no argument reads the current difficulty instead of setting it
	difficulty(Difficulty.HARD)
}
```

Generated output:

```mcfunction
difficulty
difficulty hard
```

### SpawnPoint Command

The
`spawnpoint` command sets where a player respawns after death. Each player can have their own spawn point. Called
without a position it uses the executor's current one, and an optional rotation sets the facing direction on respawn.

```kotlin
function("spawnpoint_examples") {
	spawnPoint() // sets the executor's spawn to wherever the command runs from
	spawnPoint(self())
	spawnPoint(self(), vec3(100, 64, 100))
	spawnPoint(self(), vec3(100, 64, 100), rotation(90, 0))
}
```

Generated output:

```mcfunction
spawnpoint
spawnpoint @s
spawnpoint @s 100 64 100
spawnpoint @s 100 64 100 90 0
```

### SetWorldSpawn Command

The
`setworldspawn` command sets the default spawn point for all new players and players without a personal spawn point. This is where the world compass points to.

```kotlin
function("worldspawn_examples") {
	setWorldSpawn()
	setWorldSpawn(vec3(0, 64, 0))
	setWorldSpawn(vec3(0, 64, 0), rotation(0, 0))
}
```

Generated output:

```mcfunction
setworldspawn
setworldspawn 0 64 0
setworldspawn 0 64 0 0 0
```

### Place Command

The `place` command generates features, jigsaw structures, structures and templates. `placeFeature` takes a feature
ID, or builds a configured feature inline with the [worldgen features](/docs/data-driven/worldgen/features) builders, which writes it as
SNBT in the command instead of a file in the pack. The name given to the builder isn't used:

```kotlin
function("place_examples") {
	placeFeature(Features.BONUS_CHEST, vec3())
	placeFeature(vec3(0, 64, 0)) {
		simpleBlock("inline", scheduleTick = true) {
			toPlace = simpleStateProvider(blockState(Blocks.DIAMOND_BLOCK))
		}
	}
	placeStructure(ConfiguredStructures.MINESHAFT, vec3())
}
```

Generated output:

```mcfunction
place feature minecraft:bonus_chest ~ ~ ~
place feature {type:"minecraft:simple_block",to_place:{type:"minecraft:simple",state:"minecraft:diamond_block"},schedule_tick:1b} 0 64 0
place structure minecraft:mineshaft ~ ~ ~
```

### PlaySound Command

The `playsound` command plays a sound event (from `SoundEvents`) to players. Its arguments are positional, so when you
skip one but give a later one, Kore fills the skipped ones with the game defaults: `master` for the source, `@s` for the
target, `~ ~ ~` for the position and `1` for the volume and pitch.

```kotlin
function("playsound_examples") {
	playSound(SoundEvents.Entity.Bat.TAKEOFF)
	playSound(SoundEvents.Entity.Bat.TAKEOFF, target = allPlayers())
	playSound(SoundEvents.Entity.Bat.TAKEOFF, volume = 2.0)
	playSound(SoundEvents.Entity.Bat.TAKEOFF, PlaySoundMixer.PLAYER, allPlayers(), vec3(), 1.0, 2.0, 0.5)
}
```

Generated output:

```mcfunction
playsound minecraft:entity.bat.takeoff
playsound minecraft:entity.bat.takeoff master @a
playsound minecraft:entity.bat.takeoff master @s ~ ~ ~ 2
playsound minecraft:entity.bat.takeoff player @a ~ ~ ~ 1 2 0.5
```

### StopSound Command

The
`stopsound` command stops currently playing sounds for players. You can filter by sound source (master, music, weather, etc.) and specific sound event (from `SoundEvents`, not the sound files in `Sounds`). Useful for stopping looping sounds or music.

```kotlin
function("stopsound_examples") {
	stopSound(self())
	stopSound(self(), PlaySoundMixer.MASTER)
	stopSound(self(), PlaySoundMixer.MASTER, SoundEvents.Entity.Bat.TAKEOFF)
	stopSound(self(), sound = SoundEvents.Entity.Bat.TAKEOFF)
	stopSoundAllSources(self())
	stopSoundAllSources(self(), SoundEvents.Entity.Bat.TAKEOFF)
}
```

Generated output:

```mcfunction
stopsound @s
stopsound @s master
stopsound @s master minecraft:entity.bat.takeoff
stopsound @s * minecraft:entity.bat.takeoff
stopsound @s *
stopsound @s * minecraft:entity.bat.takeoff
```

### Stopwatch Command

The `stopwatch` command manages server-side timers that count game ticks. Stopwatches persist across sessions and can be queried in execute
conditions. Useful for cooldowns, timed events, and measuring durations.

```kotlin
function("stopwatch_examples") {
	val myStopwatch = stopWatch("my_timer")
	stopwatchCreate(myStopwatch)
	stopwatchQuery(myStopwatch)
	stopwatchRestart(myStopwatch)
	stopwatchRemove(myStopwatch)
}
```

Generated output:

```mcfunction
stopwatch my_datapack:my_timer create
stopwatch my_datapack:my_timer query
stopwatch my_datapack:my_timer restart
stopwatch my_datapack:my_timer remove
```

You can also use stopwatches in execute conditions:

```kotlin
function("stopwatch_condition") {
	execute {
		ifCondition {
			stopwatch(stopWatch("my_timer"), rangeOrIntStart(100))
		}
		run {
			say("Timer reached 100 seconds!")
		}
	}
}
```

Generated output:

```mcfunction
execute if stopwatch my_datapack:my_timer 100.. run say Timer reached 100 seconds!
```

### Message Commands

The `msg` command sends a private message to a specific player, and `teammsg` sends a message to all members of the
sender's team. `tell` and `w` are aliases of `msg`, `tm` is an alias of `teamMsg` - each pair emits the same command.
See [Scoreboards](/docs/concepts/scoreboards) for team management.

```kotlin
function("message_examples") {
	msg(self(), "Hello!")
	tell(self(), "Hello!") // alias for msg
	w(self(), "Hello!")    // alias for msg
	teamMsg("Hello team!")
	tm("Hello team!")      // alias for teamMsg
}
```

Generated output:

```mcfunction
msg @s Hello!
msg @s Hello!
msg @s Hello!
teammsg Hello team!
teammsg Hello team!
```

### Spectate Command

The
`spectate` command makes a player in Spectator mode view the game from another entity's perspective. Call it without
arguments to stop spectating, with one argument for the target to spectate, and with two to also name the spectator.

```kotlin
function("spectate_examples") {
	spectate()               // stop spectating and return to your own body
	spectate(self())         // the entity to look through
	spectate(self(), self()) // target first, then which player does the spectating
}
```

Generated output:

```mcfunction
spectate
spectate @s
spectate @s @s
```

### Compute Command

The `compute` command evaluates a [context provider](/docs/data-driven/loot-tables#context-providers) and returns the result as the
command result, so it can feed `execute store`. It needs permission level gamemasters. The game syntax is
`/compute <default | block <pos> | entity <target>> integer <provider>` and `... float <provider> [<scale>]`.

The first argument decides what the provider can read: nothing gives it no position and no entity, a position (`vec3(...)`) a block
entity and a block state, and an entity (`self()`) a target entity (a single entity, not a selector matching several).

The provider decides the mode. An `Int`, a context int provider ID or a lambda returning an int provider runs the `integer` mode,
which takes no scale. A `Float`, a context float provider ID or a lambda returning a float provider runs the `float` mode, which multiplies
the result by `scale` (`1.0` by default) and rounds it toward negative infinity (`2.5` gives `2`). A lambda is built on the same scope as the
other providers, so `compute { add(constant(1), constant(2)) }` is an int and `compute { sqrt(constant(4f)) }` a float.

The game reads a bare number as an ID (`5` is `minecraft:5`), so Kore writes a constant in its compound form,
`{type:"minecraft:constant",value:5}` for an int and `{type:"minecraft:constant",value:5.0f}` for a float.

```kotlin
function("compute_examples") {
	val bonus = ContextIntProviderArgument("bonus", "my_pack")

	compute(5)
	compute(bonus)
	compute(vec3(0, 64, 0), ContextIntProviders.Cooking.TIME_COAL)
	compute(5f, 2f)
	compute(self(), ContextFloatProviders.Cooking.SPEED_DEFAULT, 10f)
	compute { add(constant(1), constant(2)) }
	compute(2f) { sqrt(constant(4f)) }
	compute(self()) { add(reference(bonus), reference(bonus)) }
	compute(self()) { (score("kills", EntityTarget.TARGET_ENTITY) + 3 + 4) % 7 }
	compute(self()) { sqrt(score("kills", EntityTarget.TARGET_ENTITY).toFloat() * 1.5f + 2f) }
}
```

Generated output:

```mcfunction
compute default integer {type:"minecraft:constant",value:5}
compute default integer my_pack:bonus
compute block 0 64 0 integer minecraft:cooking/time_coal
compute default float {type:"minecraft:constant",value:5.0f} 2
compute entity @s float minecraft:cooking/speed_default 10
compute default integer {type:"minecraft:add",inputs:[1,2]}
compute default float {type:"minecraft:sqrt",input:4} 2
compute entity @s integer {type:"minecraft:add",inputs:["my_pack:bonus","my_pack:bonus"]}
compute entity @s integer {type:"minecraft:mod",left:{type:"minecraft:add",inputs:[{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}},7]},right:7}
compute entity @s float {type:"minecraft:sqrt",input:{type:"minecraft:add",inputs:[{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}},1.5d]},2]}}
```

`compute entity <selector>` puts that entity in `target_entity` and the executor in `this`, so a provider reads the entity with `EntityTarget.TARGET_ENTITY`.

The Kotlin [operators of context providers](/docs/data-driven/loot-tables#expressions) (`+`, `-`, `*`, `/`, `%`, `toFloat()`, `coerceIn`...) build the provider of a lambda: `score + 3 + 4` is one `add` with the constant `7`, and a `+ 0` or a `* 1` disappears.

`compute` fails when a provider gives Infinity, NaN or hits an error (a division by zero, an overflow), where the same provider read by
a loot table, a predicate or an item modifier gives `0`. `execute store result score <holder> <objective> run compute ...` keeps the result in a score:

```kotlin
function("compute_to_score") {
	execute {
		storeResult { score(self(), "bonus") }
		run { compute(ContextIntProviders.Cooking.TIME_COAL) }
	}
}
```

The same providers are also a source of [`data modify`](#data-modify): `data(storage("foo")).compute("bar", self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }` writes the result into a data path.

### Swing Command

The `swing` command plays the arm-swing animation of entities. An animation (`whack` by default, `stab`, or `none`) needs an explicit hand,
and a duration (at least 1 tick, 6 by default) needs an animation.

```kotlin
function("swing_examples") {
	swing(self(), SwingHand.MAINHAND)
	swing(self(), SwingHand.OFFHAND, SwingAnimationType.WHACK)
	swing(self(), SwingHand.MAINHAND, SwingAnimationType.STAB, 10.ticks)
	swing(self(), SwingHand.MAINHAND, SwingAnimationType.NONE)
}
```

Generated output:

```mcfunction
swing @s mainhand
swing @s offhand whack
swing @s mainhand stab 10
swing @s mainhand none
```

### PostEffect Command

The `posteffect` command applies, lists and removes client-side post-processing shaders on players. Vanilla ships the
`PostEffects` `BLUR`, `CREEPER`, `ENTITY_OUTLINE`, `INVERT` and `SPIDER`. Custom post effects are resource-pack shaders,
so a datapack references them through `PostEffectArgument`:

```kotlin
function("posteffect_examples") {
	postEffect {
		add(allPlayers(), PostEffects.CREEPER)
		add(self(), PostEffectArgument("glitch", "my_pack")) // my_pack:glitch from a resource pack
		list(self())
		remove(allPlayers(), PostEffects.INVERT)
		clear(allPlayers())
	}
}
```

Generated output:

```mcfunction
posteffect add @a minecraft:creeper
posteffect add @s my_pack:glitch
posteffect list @s
posteffect remove @a minecraft:invert
posteffect clear @a
```

### Debug Commands

These commands are server debugging utilities. `debug` starts/stops profiling and creates a report.
`perf` captures performance metrics for 10 seconds. `jfr` starts/stops Java Flight Recorder profiling.

```kotlin
function("debug_examples") {
	debugStart()
	debugStop()
	perfStart()
	perfStop()
	jfrStart()
	jfrStop()
}
```

Generated output:

```mcfunction
debug start
debug stop
perf start
perf stop
jfr start
jfr stop
```

## Complex Commands

Complex commands have nested structures and multiple sub-commands. Kore provides specialized builders for these.

### Execute Command

The `execute` command is one of the most powerful commands in Minecraft. It allows you to:

- Change the execution context (who/where the command runs)
- Add conditions (only run if criteria are met)
- Store command results in scores or NBT
- Chain multiple modifiers together

The examples below cover the basics. For the full subcommand, condition, store, and `run` reference, see the dedicated
[Execute](/docs/commands/execute) page. Use `execute` with [Predicates](/docs/data-driven/predicates) for complex
conditions.

#### Basic Execute

```kotlin
function("execute_basic") {
	execute {
		asTarget(allPlayers())

		run {
			say("Hello from execute!")
		}
	}
}
```

Generated output:

```mcfunction
execute as @a run say Hello from execute!
```

Conditions (`if`/`unless`), score comparisons, position/dimension/anchoring context, entity relations, and the full
subcommand list are documented on the dedicated [Execute](/docs/commands/execute) page. A quick conditional example:

```kotlin
function("execute_conditions") {
	execute {
		asTarget(allEntities {
			limit = 3
			sort = Sort.RANDOM
		})

		ifCondition {
			score(self(), "points") greaterThanOrEqualTo 10
		}

		run {
			say("You have enough points!")
		}
	}
}
```

Generated output:

```mcfunction
execute as @e[limit=3,sort=random] if score @s points matches 10.. run say You have enough points!
```

#### Execute Store

Store command results in scores or NBT:

```kotlin
function("execute_store") {
	execute {
		storeResult {
			score(self(), "my_score")
		}

		run {
			time.query(TimeType.DAYS)
		}
	}
}
```

Generated output:

```mcfunction
execute store result score @s my_score run time query day
```

### Data Command

The
`data` command reads and writes NBT (Named Binary Tag) data on entities, block entities (chests, signs, etc.), and command storage. NBT stores complex data like inventory contents, entity attributes, and custom tags. Operations include
`get` (read), `merge` (combine), `modify` (change specific paths), and `remove` (delete).

#### Basic Data Operations

```kotlin
function("data_basic") {
	data(self()) {
		get("Health")
		get("Inventory", 1.0)
	}
}
```

Generated output:

```mcfunction
data get entity @s Health
data get entity @s Inventory 1
```

#### Data Merge

```kotlin
function("data_merge") {
	data(self()) {
		merge {
			this["CustomName"] = "Hero"
			this["Invulnerable"] = true
		}
	}
}
```

This `merge { ... }` block uses the same NBT builder described in [NBTs](/docs/concepts/nbts), so you can reuse the same
assignment patterns in commands, predicates, and chat-related APIs.

Generated output:

```mcfunction
data merge entity @s {CustomName:Hero,Invulnerable:1b}
```

#### Data Modify

```kotlin
function("data_modify") {
	data(self()) {
		modify("Inventory") { append(Items.DIAMOND) }
		modify("Tags") { prepend("new_tag") }
		modify("Health") { set(20) }
		modify("Pos[0]") { set(self(), "Pos[0]") }
	}
}
```

Generated output:

```mcfunction
data modify entity @s Inventory append value "minecraft:diamond"
data modify entity @s Tags prepend value new_tag
data modify entity @s Health set value 20
data modify entity @s Pos[0] set from entity @s Pos[0]
```

`data modify ... <op> string ... [start] [end]` is also supported for every string-capable operation (`set`, `append`,
`insert`, `merge`, `prepend`):

```kotlin
function("data_modify_string_ranges") {
	data(self()) {
		modify("foo") { append(self(), "name", 1) }
		modify("foo") { insert(0, self(), "name", 0, 4) }
		modify("foo") { merge(self(), "name", -5) }
		modify("foo") { prepend(self(), "name", 0, 2) }
		modify("foo") { set(self(), "name", 0, 3) }
	}
}
```

Generated output:

```mcfunction
data modify entity @s foo append string entity @s name 1
data modify entity @s foo insert 0 string entity @s name 0 4
data modify entity @s foo merge string entity @s name -5
data modify entity @s foo prepend string entity @s name 0 2
data modify entity @s foo set string entity @s name 0 3
```

A context provider can also be the source of every operation: `compute(path, ...)` on the data builder writes the result of a provider
into a path, and `compute(...)` in a `modify { }` block is the source of `set`, `append`, `insert`, `merge` and `prepend`. Both take the
same target (none, a position or an entity) and provider (an ID, an `Int` or `Float` constant, or a lambda) as the [`compute`](#compute-command) command, with the same
mode choice (an int provider stores an int tag, a float provider a float tag), and have no scale. A provider that fails stores `0` (`0.0f`
in float mode) where `/compute` itself fails, and a float is stored as it is, never scaled or rounded down.

```kotlin
function("data_modify_compute") {
	val bonus = ContextIntProviderArgument("bonus", "my_pack")

	data(storage("foo")) {
		compute("bar", bonus)
		compute("baz", self()) { score("kills", EntityTarget.TARGET_ENTITY) + 1 }
		compute("qux", vec3(1, 2, 3)) { sqrt(constant(4f)) }
		compute("count", 5)
		modify("list") { append(compute(self(), bonus)) }
		modify("list") { insert(0, compute(vec3(1, 2, 3)) { constant(5) }) }
		modify("list") { append(compute(2.5f)) }
	}
}
```

Generated output:

```mcfunction
data modify storage minecraft:foo bar set compute default integer my_pack:bonus
data modify storage minecraft:foo baz set compute entity @s integer {type:"minecraft:add",inputs:[{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}},1]}
data modify storage minecraft:foo qux set compute block 1 2 3 float {type:"minecraft:sqrt",input:4}
data modify storage minecraft:foo count set compute default integer {type:"minecraft:constant",value:5}
data modify storage minecraft:foo list append compute entity @s integer my_pack:bonus
data modify storage minecraft:foo list insert 0 compute block 1 2 3 integer {type:"minecraft:constant",value:5}
data modify storage minecraft:foo list append compute default float {type:"minecraft:constant",value:2.5f}
```

#### Data Remove

```kotlin
function("data_remove") {
	data(self()) {
		remove("CustomName")
		remove("Tags[0]")
	}
}
```

Generated output:

```mcfunction
data remove entity @s CustomName
data remove entity @s Tags[0]
```

### Scoreboard Command

The
`scoreboard` command manages objectives (score types) and player/entity scores. Scoreboards are essential for tracking
game state, creating timers, and building game mechanics. See [Scoreboards](/docs/concepts/scoreboards) for detailed
usage.

```kotlin
function("scoreboard_examples") {
	scoreboard.objectives.add("kills", ScoreboardCriteria.PLAYER_KILL_COUNT, textComponent("Player Kills"))
	scoreboard.objectives.remove("old_objective")
	scoreboard.objectives.setDisplay(DisplaySlots.sidebar, "kills")

	scoreboard.players.set(allPlayers(), "kills", 0)
	scoreboard.players.add(self(), "kills", 1)
	scoreboard.players.remove(self(), "kills", 5)
	scoreboard.players.reset(self(), "kills")

	scoreboard.players.operation(self(), "total", Operation.ADD, self(), "kills")
}
```

Criteria come from the `ScoreboardCriteria` enum, or from the builders for compound criteria:
`criteriaKilled(EntityTypes.ZOMBIE)`, `criteriaMined(Blocks.STONE)`, `criteriaTeamKill(FormattingColor.RED)`,
`criteriaCustom(...)`, and friends. Passing a raw string does not compile - `ScoreboardCriterion` is an interface.

`scoreboard.objective(target, "objective")` returns a `PlayerObjective` handle when you want to run several
operations on the same score, including the `+=`, `-=`, `++`, `--` operators and the `min` / `max` infix operations:

```kotlin
function("score_handle") {
	val kills = scoreboard.objective(self(), "kills")
	kills += 1
	kills.reset()
}
```

Generated output:

```mcfunction
scoreboard players add @s kills 1
scoreboard players reset @s kills
```

### Bossbar Command

The
`bossbar` command creates and controls boss bars - the progress bars normally shown during boss fights. Boss bars can display custom text, colors, and progress values. They're useful for timers, progress indicators, and UI elements.

```kotlin
function("bossbar_examples") {
	bossBars.add("my_bar", displayName = textComponent("My Boss Bar"))

	val bar = bossBars.get("my_bar")
	bar.setColor(BossBarColor.RED)
	bar.setMax(100)
	bar.setValue(50)
	bar.setVisible(true)
	bar.setPlayers(allPlayers())
	bar.setStyle(BossBarStyle.NOTCHED_10)
	bar.remove()
}
```

### Team Command

The
`team` command creates and manages teams for players and entities. Teams control PvP (friendly fire), name tag
visibility, collision, and chat colors. See [Scoreboards](/docs/concepts/scoreboards) for more on teams.

```kotlin
function("team_examples") {
	teams.add("red_team", textComponent("Red Team"))
	teams.modify("red_team") {
		color(Color.RED)
		friendlyFire(false)
		seeFriendlyInvisibles(true)
	}
	teams.join("red_team", allPlayers())
	teams.leave(self())
}
```

### Attribute Command

The
`attribute` command reads and modifies entity attributes like max health, movement speed, attack damage, and armor. You can get/set base values or add temporary modifiers that stack.

```kotlin
function("attribute_examples") {
	val maxHealth = attributes(self(), Attributes.MAX_HEALTH)
	maxHealth.get()
	maxHealth.base.get()
	maxHealth.base.set(40.0)

	val speed = attributes(self(), Attributes.MOVEMENT_SPEED)
	speed.modifiers.add("speed_boost", value = 0.1, operation = AttributeModifierOperation.ADD_VALUE)
	speed.modifiers.remove("speed_boost")
}
```

### Schedule Command

The
`schedule` command delays function execution by a specified time. Useful for timers, cooldowns, and delayed effects.
Time can be specified in [ticks, seconds, or days](/docs/concepts/time). See [Scheduler Helper](/docs/helpers/scheduler)
for advanced
scheduling patterns.

```kotlin
function("schedule_examples") {
	val myFunction = function("delayed_action") {
		say("This runs later!")
	}

	schedule(100.ticks, myFunction)           // appends a new schedule
	schedule(myFunction).replace(5.seconds)   // replaces any pending schedule of the function
	schedule(myFunction).clear()
}
```

Generated output:

```mcfunction
schedule function my_datapack:delayed_action 100 append
schedule function my_datapack:delayed_action 5s replace
schedule clear my_datapack:delayed_action
```

### Loot Command

The
`loot` command generates items from [Loot Tables](/docs/data-driven/loot-tables) and distributes them to players, containers, or the world.
Sources include fishing, killing entities, mining blocks, or direct loot table references.

```kotlin
function("loot_examples") {
	// roll a loot table and put the result straight into the player's inventory
	loot(self()) {
		loot(LootTables.Gameplay.CAT_MORNING_GIFT)
	}

	// roll it as if fished at a position with that rod, so rod enchantments apply
	loot(self()) {
		fish(LootTables.Gameplay.CAT_MORNING_GIFT, vec3(), Items.FISHING_ROD)
	}

	// roll the drops the entity would give if killed, without killing it
	loot(self()) {
		kill(self())
	}

	// roll the drops the block would give if mined with that tool
	loot(self()) {
		mine(vec3(), Items.DIAMOND_PICKAXE)
	}

	// drop the items into the container at that position instead of an inventory
	loot {
		target {
			insert(vec3())
		}
		source {
			kill(self())
		}
	}

	// overwrite one slot of a container block, CONTAINER[0] being its first slot
	loot {
		target {
			replaceBlock(vec3(), CONTAINER[0])
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// overwrite an equipment slot, here the helmet
	loot {
		target {
			replaceEntity(self(), ARMOR.HEAD)
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// mobs that carry their own inventory (villagers, piglins) use mob.inventory.*
	loot {
		target {
			replaceEntity(self(), MOB.INVENTORY[0])
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// the source can also be a loot table written inline, with no separate file
	loot {
		target {
			give(self())
		}
		source {
			loot {
				pool {
					rolls(1)
					entries {
						item(Items.ANVIL)
					}
				}
			}
		}
	}
}
```

Generated output:

```mcfunction
loot give @s loot minecraft:gameplay/cat_morning_gift
loot give @s fish minecraft:gameplay/cat_morning_gift ~ ~ ~ minecraft:fishing_rod
loot give @s kill @s
loot give @s mine ~ ~ ~ minecraft:diamond_pickaxe
loot insert ~ ~ ~ kill @s
loot replace block ~ ~ ~ container.0 loot minecraft:gameplay/cat_morning_gift
loot replace entity @s armor.head loot minecraft:gameplay/cat_morning_gift
loot replace entity @s mob.inventory.0 loot minecraft:gameplay/cat_morning_gift
loot give @s loot {pools:[{entries:[{type:"minecraft:item",name:"minecraft:anvil"}],rolls:1}]}
```

The `target` block chooses where items go (`give`, `insert` into a container, `replaceBlock`, `replaceEntity`) and the
`source` block chooses where they come from (`loot`, `fish`, `kill`, `mine`). Slot constants come from the item slot
types: `CONTAINER[n]` for block inventories, `ARMOR.HEAD` and friends for equipment, and `MOB.INVENTORY[n]` for mobs
that carry their own inventory such as villagers and piglins. The last example shows that `source { loot { ... } }`
also accepts an inline [loot table](/docs/data-driven/loot-tables) instead of a reference.

### Particle Command

The `particle` command spawns visual particle effects in the world. Particles have position, spread (delta), speed, and count. Use
`force` mode to render particles for every player regardless of their particle setting, up to 512 blocks away instead of 32 in `normal` mode.

A particle without options is a plain `Particles` entry. Every parameter after the particle has a default, and the game reads delta,
speed and count together, so setting any of them fills the others with `0 0 0`, `0` and `0`. A selector needs a mode, which is `normal`
when omitted.

```kotlin
function("particle_examples") {
	particle(Particles.FLAME)
	particle(Particles.FLAME, vec3())

	// arguments are position, delta (how far particles scatter), speed, then count
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2)

	// count alone is enough, delta and speed default to zero
	particle(Particles.ASH, vec3(), count = 5)

	// force renders the particle up to 512 blocks away, whatever the particle setting of the player
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.FORCE)

	// a last selector limits who sees the particle
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.NORMAL, allEntities())
	particle(Particles.ASH, vec3(), viewers = allEntities())
}
```

Generated output:

```mcfunction
particle minecraft:flame
particle minecraft:flame ~ ~ ~
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2
particle minecraft:ash ~ ~ ~ 0 0 0 0 5
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 force
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 normal @e
particle minecraft:ash ~ ~ ~ 0 0 0 0 0 normal @e
```

Passing delta, speed, count, a mode or a selector without a position throws an `IllegalArgumentException`, since the command can't
express it.

#### Particle Options

A particle that carries extra data is a `ParticleOptions`, built by one factory per shape of options. `particle(options, ...)` takes
the same position, delta, speed, count, mode and selector parameters as above, and renders the namespaced ID followed by the options as
SNBT. Colors are packed ints: RGB for `dust`, `dust_color_transition`, `effect`, `instant_effect` and `trail`, ARGB for
`entity_effect`, `flash` and `tinted_leaves` (a color without alpha is opaque, see [Colors](/docs/concepts/colors)).

| Factory                       | Particle IDs                                                      | Options                                                     | Example command                                                                    |
|-------------------------------|-------------------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------------------------------------------|
| `blockParticle`               | `block`, `block_crumble`, `block_marker`, `dust_pillar`, `falling_dust` | A block, its states included                          | `particle minecraft:block{block_state:"minecraft:stone"}`                          |
| `colorParticle`               | `entity_effect`, `flash`, `tinted_leaves`                         | An ARGB color                                               | `particle minecraft:entity_effect{color:-11141291}`                                |
| `dragonBreathParticle`        | `dragon_breath`                                                   | An optional power                                           | `particle minecraft:dragon_breath{power:0.5f}`                                     |
| `dustColorTransitionParticle` | `dust_color_transition`                                           | A start color, an end color and a scale                     | `particle minecraft:dust_color_transition{from_color:5592575,scale:2.0f,to_color:16733525}` |
| `dustParticle`                | `dust`                                                            | An RGB color and a scale                                    | `particle minecraft:dust{color:16733525,scale:1.0f}`                               |
| `effectParticle`              | `effect`, `instant_effect`                                        | An optional RGB color and an optional power                 | `particle minecraft:effect{color:5635925,power:2.0f}`                              |
| `geyserBurstParticle`         | `geyser_base`, `geyser_poof`                                      | A water column height and a burst impulse                   | `particle minecraft:geyser_base{burst_impulse_base:0.5f,water_blocks:3}`           |
| `geyserParticle`              | `geyser`, `geyser_plume`                                          | A water column height                                       | `particle minecraft:geyser{water_blocks:3}`                                        |
| `itemParticle`                | `item`                                                            | An item, [components](/docs/concepts/components) included   | `particle minecraft:item{item:{id:"minecraft:diamond"}}`                           |
| `sculkChargeParticle`         | `sculk_charge`                                                    | A roll angle in radians                                     | `particle minecraft:sculk_charge{roll:1.5f}`                                       |
| `shriekParticle`              | `shriek`                                                          | A delay in ticks                                            | `particle minecraft:shriek{delay:100}`                                             |
| `simpleParticle`              | every other particle                                              | None                                                        | `particle minecraft:ash`                                                           |
| `trailParticle`               | `trail`                                                           | An RGB color, a duration in ticks and a target position     | `particle minecraft:trail{color:16733525,duration:10,target:[1.0d,2.0d,3.0d]}`     |
| `vibrationParticle`           | `vibration`                                                       | A target block position and an arrival time in ticks        | `particle minecraft:vibration{arrival_in_ticks:10,destination:{pos:[1,2,3],type:block}}` |

Factories that cover several IDs take the ID as a last parameter, defaulting to the first one of their row: `blockParticle(Blocks.SAND,
Particles.DUST_PILLAR)`, `colorParticle(Color.WHITE, Particles.FLASH)`.

A particle ID given the options of another row throws an `IllegalArgumentException` when written, so `blockParticle(Blocks.STONE,
Particles.FLAME)` and `particle(Particles.DUST, vec3())` (a dust without color and scale) fail instead of producing a command the game
rejects.

A vibration targeting an entity isn't possible because the game only accepts a `block` position source here.

The same factories work in the [`spawnParticles` and `explode` effects of enchantments](/docs/data-driven/enchantments) and in the
[ambient and dripstone particles](/docs/data-driven/worldgen/environment-attributes) of environment attributes.

```kotlin
import kotlin.math.PI

function("particle_options") {
	particle(blockParticle(Blocks.STONE_SLAB(states = mapOf("half" to "top"))))
	particle(blockParticle(Blocks.STONE, Particles.BLOCK_CRUMBLE))
	particle(colorParticle(Color.GREEN))
	particle(colorParticle(Color.WHITE, Particles.FLASH))
	particle(dragonBreathParticle(0.5f))
	particle(dustParticle(Color.PURPLE, 2f))
	particle(dustColorTransitionParticle(Color.BLUE, Color.RED, 2f))
	particle(effectParticle(Color.GREEN, 2f, Particles.INSTANT_EFFECT))
	particle(geyserBurstParticle(4, 1.5f, Particles.GEYSER_POOF))
	particle(itemParticle(Items.DIAMOND_SWORD {
		enchantments {
			enchantment(Enchantments.SHARPNESS, 5)
		}
	}))
	particle(sculkChargeParticle((PI / 2).toFloat()))
	particle(vibrationParticle(1, 2, 3, 10), vec3(), count = 1, mode = ParticleMode.FORCE)
}
```

Generated output:

```mcfunction
particle minecraft:block{block_state:{id:"minecraft:stone_slab",properties:{half:top}}}
particle minecraft:block_crumble{block_state:"minecraft:stone"}
particle minecraft:entity_effect{color:-11141291}
particle minecraft:flash{color:-1}
particle minecraft:dragon_breath{power:0.5f}
particle minecraft:dust{color:11141375,scale:2.0f}
particle minecraft:dust_color_transition{from_color:5592575,scale:2.0f,to_color:16733525}
particle minecraft:instant_effect{color:5635925,power:2.0f}
particle minecraft:geyser_poof{burst_impulse_base:1.5f,water_blocks:4}
particle minecraft:item{item:{id:"minecraft:diamond_sword",components:{enchantments:{"minecraft:sharpness":5}}}}
particle minecraft:sculk_charge{roll:1.5707964f}
particle minecraft:vibration{arrival_in_ticks:10,destination:{pos:[1,2,3],type:block}} ~ ~ ~ 0 0 0 0 1 force
```

A ring of colored dust, one command per point, each one visible from far away:

```kotlin
function("dust_ring") {
	val orange = dustParticle(rgb(0xFF8800), 1.5f)

	for ((x, z) in listOf(102 to 100, 100 to 102, 98 to 100, 100 to 98)) {
		particle(orange, vec3(x, 64, z), count = 3, mode = ParticleMode.FORCE)
	}
}
```

```mcfunction
particle minecraft:dust{color:16746496,scale:1.5f} 102 64 100 0 0 0 0 3 force
particle minecraft:dust{color:16746496,scale:1.5f} 100 64 102 0 0 0 0 3 force
particle minecraft:dust{color:16746496,scale:1.5f} 98 64 100 0 0 0 0 3 force
particle minecraft:dust{color:16746496,scale:1.5f} 100 64 98 0 0 0 0 3 force
```

### Clone Command

The
`clone` command copies blocks from one region to another. Supports cross-dimension cloning, filtering by block type, and different modes:
`replace` (all blocks), `masked` (non-air only), `move` (removes source). Use `strict` to fail if regions overlap incorrectly.

```kotlin
function("clone_examples") {
	// begin and end are the two opposite corners of the region to copy,
	// destination is where its lowest corner lands
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
	}

	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(0, 64, 0)
		from = Dimensions.THE_NETHER
		to = Dimensions.OVERWORLD
	}

	// masked skips air blocks, so the copy does not erase what is already there
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
		masked(CloneMode.MOVE)
	}

	// only copies blocks matching the tag, everything else is left untouched
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
		filter(Tags.Block.BASE_STONE_OVERWORLD, CloneMode.FORCE)
	}

	// source and destination overlap here, strict makes that fail instead of corrupting the copy
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(5, 64, 5)
		strict = true
	}
}
```

Generated output:

```mcfunction
clone 0 64 0 10 74 10 100 64 100
clone from minecraft:the_nether 0 64 0 10 74 10 to minecraft:overworld 0 64 0
clone 0 64 0 10 74 10 100 64 100 masked move
clone 0 64 0 10 74 10 100 64 100 filtered #minecraft:base_stone_overworld force
clone 0 64 0 10 74 10 5 64 5 strict
```

### WorldBorder Command

The `worldborder` command controls the world border size, position, damage, and warning settings. The time parameter for `add` and `set` is
specified in ticks.

```kotlin
function("worldborder_examples") {
	worldBorder {
		// sizes are the border's full diameter in blocks, times are in ticks
		add(10.0, time = 200) // grow by 10 blocks over 10 seconds

		set(1000.0) // instant

		set(500.0, time = 6000) // shrink over 5 minutes

		center(0.0, 0.0)

		damageAmount(0.5f) // damage per second per block past the buffer
		damageBuffer(5.0)  // blocks of grace beyond the border before damage starts

		setWarningDistance(10)
		setWarningTime(15)

		get()
	}
}
```

Generated output:

```mcfunction
worldborder add 10 200
worldborder set 1000
worldborder set 500 6000
worldborder center 0 0
worldborder damage amount 0.5
worldborder damage buffer 5
worldborder warning distance 10
worldborder warning time 15
worldborder get
```

## Selectors

Selectors target entities in the world.
Kore provides type-safe selector builders with filters for entity type, distance, scores, NBT, and more.
If you want a selector-focused walkthrough beyond the command examples below, read
the [Selectors](/docs/concepts/selectors) page alongside this reference:

```kotlin
function("selector_examples") {
	tellraw(allPlayers(), textComponent("Hi")) // @a
	teleport(nearestPlayer(), vec3(0, 64, 0)) // @p
	give(randomPlayer(), Items.DIAMOND)       // @r

	// sort decides who the limit keeps, so this is the 10 closest zombies within 10 blocks
	kill(allEntities {
		type = EntityTypes.ZOMBIE
		limit = 10
		sort = Sort.NEAREST
		distance = rangeOrIntEnd(10)
	})

	effect(allEntities {
		scores {
			score("kills") greaterThanOrEqualTo 5
		}
	}) { give(Effects.STRENGTH, duration = 60) }

	kill(allEntities {
		nbt = nbt {
			this["CustomName"] = "Target"
		}
	})
}
```

## Macros

Macros allow dynamic command arguments that are substituted at runtime. They're useful for creating reusable functions with parameters.

```kotlin
function("greet_player") {
	say("Hello, ${macro("player_name")}!")
}

load {
	function("greet_player", arguments = nbt { this["player_name"] = "Steve" })
}
```

Generated output:

```mcfunction
$say Hello, $(player_name)!
```

For detailed macro usage including macro classes and validation, see [Macros](/docs/commands/macros).

## Raw Commands

For commands not yet supported by Kore or for special cases, use
`addLine`. This is also useful when working with [Macros](/docs/commands/macros) for fully dynamic commands:

```kotlin
function("raw_commands") {
	addLine("say This is a raw command")
	addLine("execute as @a run say Hello")
}
```

> Note: Using raw commands bypasses type safety. Prefer the DSL builders when available.

## Custom Commands

Create your own command builders for mods or custom functionality. See [Functions](/docs/commands/functions) for more details on the
Function context:

```kotlin
import io.github.ayfri.kore.functions.Function

fun Function.myModCommand(target: EntityArgument, value: Int) =
	addLine(command("mymod", literal(target.asString()), int(value)))

function("custom") {
	myModCommand(self(), 42)
}
```

Generated output:

```mcfunction
mymod @s 42
```

For broader composition patterns such as extracting reusable wrappers around commands,
the [Cookbook](/docs/guides/cookbook) gives more realistic project-scale examples.

## See Also

- [Functions](/docs/commands/functions) - Create and organize command functions
- [Macros](/docs/commands/macros) - Dynamic command arguments
- [Chat Components](/docs/concepts/chat-components) - Formatted text in commands
- [Cookbook](/docs/guides/cookbook) - Practical command composition patterns in real datapacks
- [World Clocks](/docs/data-driven/world-clocks) - World clocks, timelines, time markers, and `timeCheck`

### External Resources

- [Minecraft Wiki: Commands](https://minecraft.wiki/w/Commands) - Complete command reference
- [Minecraft Wiki: Target selectors](https://minecraft.wiki/w/Target_selectors) - Selector syntax
