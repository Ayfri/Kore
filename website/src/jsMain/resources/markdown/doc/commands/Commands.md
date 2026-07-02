---
root: .components.layouts.MarkdownLayout
title: Minecraft Commands in Kore - Type-Safe Command DSL
nav-title: Commands
description: Every Minecraft command in a type-safe Kotlin DSL. From /say, /teleport, and /give to /execute, /data, /scoreboard, and /summon -- all with code examples and generated mcfunction output.
keywords: minecraft commands, kore commands, kotlin commands dsl, mcfunction generator, execute command, data command, teleport command, summon command, minecraft command builder, type-safe commands
date-created: 2026-02-03
date-modified: 2026-07-02
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

The `teleport` (or
`tp`) command instantly moves entities to a new location. You can teleport to absolute coordinates, relative positions, or another entity's location. Optionally specify rotation (yaw/pitch) for the entity to face after teleporting.

```kotlin
function("teleport_examples") {
	// Teleport to coordinates
	teleport(allPlayers(), vec3(100, 64, 100))

	// Teleport to another entity
	teleport(allPlayers(), self())

	// Teleport with rotation
	teleport(self(), vec3(0, 100, 0), rotation(0.rot, 90.rot))
}
```

Generated output:

```mcfunction
tp @a 100 64 100
tp @a @s
tp @s 0 100 0 0 90
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
gamemode survival Steve
```

### Time Command

The `time` command controls world clocks. Time is measured in [ticks](/docs/concepts/time) (20 ticks = 1 second,
24 000 ticks = 1 Minecraft day). The `time` property on a `Function` returns a `Time` DSL scope.

For a full reference covering world clocks, timelines, time markers, and the `timeCheck` predicate, see
[World Clocks](/docs/data-driven/world-clocks).

#### Basic Time Operations

```kotlin
function("time_control") {
	time.add(6000)           // advance by 6000 ticks
	time.add(1.days)         // advance by one full day
	time.pause()             // freeze the clock
	time.resume()            // unfreeze the clock
	time.set(TimePeriod.DAY) // jump to a named period
	time.set(6000)           // jump to an exact tick
	time.query(TimeType.DAYTIME)
	time.queryTime()         // absolute game time as integer
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
time query daytime
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
	time.rate(1.0f)   // default speed
	time.rate(0.0f)   // freeze the day-night cycle
	time.rate(2.0f)   // double speed
	time.rate(0.5f)   // half speed
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
	time.of(seasonClock).query(TimeType.DAYTIME)
	time.of(seasonClock).query(Timelines.DAY)
	time.of(seasonClock).queryRepetitions(Timelines.DAY)
	time.of(seasonClock).queryTime()
	time.of(seasonClock).rate(2.0f)
}
```

Generated output:

```mcfunction
time of mymod:season add 6000
time of mymod:season pause
time of mymod:season resume
time of mymod:season set day
time of mymod:season set mymod:summer
time of mymod:season query daytime
time of mymod:season query minecraft:day
time of mymod:season query minecraft:day repetitions
time of mymod:season query time
time of mymod:season rate 2
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
		this["CustomName"] = "\"Boom\""
		this["NoAI"] = true
	}
}
```

Generated output:

```mcfunction
summon minecraft:zombie 0 64 0
summon minecraft:creeper ~ ~ ~ {CustomName:"\"Boom\"",NoAI:true}
```

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

The `fill` command fills a rectangular region with blocks. Modes include: `replace` (all blocks), `hollow` (only outer shell),
`outline` (shell without clearing inside), `keep` (only air blocks), and `destroy` (drops items).

```kotlin
function("fill_area") {
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.STONE)
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.AIR, FillMode.REPLACE)
	fill(vec3(0, 64, 0), vec3(10, 70, 10), Blocks.GLASS, FillMode.HOLLOW)
}
```

Generated output:

```mcfunction
fill 0 64 0 10 70 10 minecraft:stone
fill 0 64 0 10 70 10 minecraft:air replace
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
`difficulty` command gets or sets the world's difficulty level (Peaceful, Easy, Normal, Hard). Difficulty affects mob damage, hunger depletion, and whether hostile mobs spawn.

```kotlin
function("difficulty_examples") {
	difficulty()  // Query current difficulty
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
`spawnpoint` command sets where a player respawns after death. Each player can have their own spawn point. Optionally specify the facing direction on respawn.

```kotlin
function("spawnpoint_examples") {
	spawnPoint()  // Set at current position
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

### StopSound Command

The
`stopsound` command stops currently playing sounds for players. You can filter by sound source (master, music, weather, etc.) and specific sound. Useful for stopping looping sounds or music.

```kotlin
function("stopsound_examples") {
	stopSound(self())
	stopSound(self(), PlaySoundMixer.MASTER)
	stopSound(self(), PlaySoundMixer.MASTER, Sounds.Mob.Bat.TAKEOFF)
	stopSoundAllSources(self())
	stopSoundAllSources(self(), Sounds.Mob.Bat.TAKEOFF)
}
```

Generated output:

```mcfunction
stopsound @s
stopsound @s master
stopsound @s master minecraft:mob/bat/takeoff
stopsound @s *
stopsound @s * minecraft:mob/bat/takeoff
```

### Stopwatch Command

The `stopwatch` command manages server-side timers that count game ticks. Stopwatches persist across sessions and can be queried in execute
conditions. Useful for cooldowns, timed events, and measuring durations.

```kotlin
function("stopwatch_examples") {
	val myStopwatch = stopwatch("my_timer")
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
			stopwatch(stopWatch("my_timer"), rangeOrInt(100))
		}
		run {
			say("Timer reached 100 ticks!")
		}
	}
}
```

Generated output:

```mcfunction
execute if stopwatch my_datapack:my_timer 100 run say Timer reached 100 ticks!
```

### Message Commands

The `msg` command (aliases: `tell`, `w`) sends a private message to a specific player. The `teammsg` command (alias:
`tm`) sends a message to all members of the sender's team. See [Scoreboards](/docs/concepts/scoreboards) for team
management.

```kotlin
function("message_examples") {
	msg(self(), "Hello!")
	tell(self(), "Hello!")  // Alias for msg
	w(self(), "Hello!")     // Alias for msg
	teamMsg("Hello team!")
	tm("Hello team!")       // Alias for teamMsg
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
`spectate` command makes a player in Spectator mode view the game from another entity's perspective. Call without arguments to stop spectating.

```kotlin
function("spectate_examples") {
	spectate()  // Stop spectating
	spectate(self())  // Spectate target
	spectate(self(), self())  // Target and spectator
}
```

Generated output:

```mcfunction
spectate
spectate @s
spectate @s @s
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
execute as @e[limit=3,sort=random] if score @s points >= 10 run say You have enough points!
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
			time.query(TimeQuery.DAYTIME)
		}
	}
}
```

Generated output:

```mcfunction
execute store result score @s my_score run time query daytime
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
			this["CustomName"] = "\"Hero\""
			this["Invulnerable"] = true
		}
	}
}
```

This `merge { ... }` block uses the same NBT builder described in [NBTs](/docs/concepts/nbts), so you can reuse the same
assignment patterns in commands, predicates, and chat-related APIs.

Generated output:

```mcfunction
data merge entity @s {CustomName:"\"Hero\"",Invulnerable:true}
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
data modify entity @s Tags prepend value "new_tag"
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
	// Objectives
	scoreboard.objectives.add("kills", "playerKillCount", textComponent("Player Kills"))
	scoreboard.objectives.remove("old_objective")
	scoreboard.objectives.setDisplay(DisplaySlot.SIDEBAR, "kills")

	// Players
	scoreboard.players.set(allPlayers(), "kills", 0)
	scoreboard.players.add(self(), "kills", 1)
	scoreboard.players.remove(self(), "kills", 5)
	scoreboard.players.reset(self(), "kills")

	// Operations
	scoreboard.players.operation(self(), "total", Operation.ADD, self(), "kills")
}
```

### Bossbar Command

The
`bossbar` command creates and controls boss bars - the progress bars normally shown during boss fights. Boss bars can display custom text, colors, and progress values. They're useful for timers, progress indicators, and UI elements.

```kotlin
function("bossbar_examples") {
	bossbar.add("my_bar", textComponent("My Boss Bar"))
	bossbar.set("my_bar") {
		color(BossBarColor.RED)
		max(100)
		value(50)
		visible(true)
		players(allPlayers())
		style(BossBarStyle.NOTCHED_10)
	}
	bossbar.remove("my_bar")
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
	attribute(self(), Attributes.GENERIC_MAX_HEALTH) {
		get()
		base.get()
		base.set(40.0)
	}

	attribute(self(), Attributes.GENERIC_MOVEMENT_SPEED) {
		modifiers.add("speed_boost", 0.1, AttributeModifierOperation.ADD_VALUE)
		modifiers.remove("speed_boost")
	}
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

	schedule.function(myFunction, 100.ticks)
	schedule.function(myFunction, 5.seconds, ScheduleMode.REPLACE)
	schedule.clear(myFunction)
}
```

### Loot Command

The
`loot` command generates items from [Loot Tables](/docs/data-driven/loot-tables) and distributes them to players, containers, or the world.
Sources include fishing, killing entities, mining blocks, or direct loot table references.

```kotlin
function("loot_examples") {
	// Give loot to a player
	loot(self()) {
		loot(LootTables.Gameplay.CAT_MORNING_GIFT)
	}

	// Fish loot with a tool
	loot(self()) {
		fish(LootTables.Gameplay.CAT_MORNING_GIFT, vec3(), Items.FISHING_ROD)
	}

	// Kill loot from an entity
	loot(self()) {
		kill(self())
	}

	// Mine loot from a position
	loot(self()) {
		mine(vec3(), Items.DIAMOND_PICKAXE)
	}

	// Insert loot into a container
	loot {
		target {
			insert(vec3())
		}
		source {
			kill(self())
		}
	}

	// Replace block inventory slot
	loot {
		target {
			replaceBlock(vec3(), CONTAINER[0])
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// Replace entity equipment slot
	loot {
		target {
			replaceEntity(self(), ARMOR.HEAD)
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// Replace a mob inventory slot (villager / piglin use mob.inventory.*)
	loot {
		target {
			replaceEntity(self(), MOB.INVENTORY[0])
		}
		source {
			loot(LootTables.Gameplay.CAT_MORNING_GIFT)
		}
	}

	// Inline loot table definition
	loot {
		target {
			give(self())
		}
		source {
			loot {
				pool {
					rolls(1f)
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
loot give @s loot {pools:[{rolls:1.0f,entries:[{type:"minecraft:item",name:"minecraft:anvil"}]}]}
```

### Particle Command

The `particle` command spawns visual particle effects in the world. Particles have position, spread (delta), speed, and count. Use
`force` mode to make particles visible from far away or through blocks.

```kotlin
function("particle_examples") {
	// Simple particle
	particle(Particles.ASH)

	// Particle at position with delta and count
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2)

	// Particle with force mode (visible from far away)
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.FORCE)

	// Particle visible only to specific players
	particle(Particles.ASH, vec3(), vec3(), 1.0, 2, ParticleMode.NORMAL, allEntities())
}
```

Generated output:

```mcfunction
particle minecraft:ash
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 force
particle minecraft:ash ~ ~ ~ ~ ~ ~ 1 2 normal @e
```

#### Special Particle Types

```kotlin
function("special_particles") {
	particles {
		// Block particles with state
		block(Blocks.STONE_SLAB(states = mapOf("half" to "top")))

		// Block crumble effect
		blockCrumble(Blocks.STONE)

		// Block marker (invisible barrier visualization)
		blockMarker(Blocks.STONE)

		// Falling dust
		fallingDust(Blocks.STONE)

		// Colored dust particles
		dust(Color.PURPLE, 2.0)
		dust(rgb(0xabcdef), 2.0)

		// Dust color transition
		dustColorTransition(Color.BLUE, 2.0, Color.RED)

		// Entity effect with color
		entityEffect(color = Color.GREEN)

		// Item particle with components
		item(Items.DIAMOND_SWORD {
			enchantments {
				enchantment(Enchantments.SHARPNESS, 5)
			}
		})

		// Sculk charge with angle
		sculkCharge(PI / 2)

		// Shriek with delay
		shriek(100)

		// Trail particle
		trail(Color.RED, Triple(1, 2, 3), 10)

		// Vibration to position
		vibration(vec3(1, 2, 3), 10)
	}
}
```

### Clone Command

The
`clone` command copies blocks from one region to another. Supports cross-dimension cloning, filtering by block type, and different modes:
`replace` (all blocks), `masked` (non-air only), `move` (removes source). Use `strict` to fail if regions overlap incorrectly.

```kotlin
function("clone_examples") {
	// Basic clone
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
	}

	// Clone between dimensions
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(0, 64, 0)
		from = Dimensions.THE_NETHER
		to = Dimensions.OVERWORLD
	}

	// Clone with mask mode
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
		masked(CloneMode.MOVE)  // Only non-air blocks, move instead of copy
	}

	// Clone with block filter
	clone {
		begin = vec3(0, 64, 0)
		end = vec3(10, 74, 10)
		destination = vec3(100, 64, 100)
		filter(Tags.Block.BASE_STONE_OVERWORLD, CloneMode.FORCE)
	}

	// Strict mode (fail if regions overlap incorrectly)
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
		// Expand border by 10 blocks over 200 ticks (10 seconds)
		add(10.0, time = 200)

		// Set border to 1000 blocks instantly
		set(1000.0)

		// Set border to 500 blocks over 6000 ticks (5 minutes)
		set(500.0, time = 6000)

		// Set center
		center(0.0, 0.0)

		// Damage settings
		damageAmount(0.2f)
		damageBuffer(5.0)

		// Warning settings
		setWarningDistance(10)
		setWarningTime(15)

		// Query current size
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
worldborder damage amount 0.2
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
	// All players
	say(allPlayers())

	// Nearest player
	teleport(nearestPlayer(), vec3(0, 64, 0))

	// Random player
	give(randomPlayer(), Items.DIAMOND)

	// All entities with filters
	kill(allEntities {
		type = EntityTypes.ZOMBIE
		limit = 10
		sort = Sort.NEAREST
		distance = rangeOrIntEnd(10)
	})

	// Entities with scores
	effect(allEntities {
		scores {
			score("kills") greaterThanOrEqualTo 5
		}
	}) { give(Effects.STRENGTH, duration = 60) }

	// Entities with NBT
	kill(allEntities {
		nbt = nbt {
			this["CustomName"] = "\"Target\""
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

// Call with arguments
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
fun Function.myModCommand(target: EntityArgument, value: Int) =
	addLine(command("mymod", literal(target.asString()), int(value)))

// Usage
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
