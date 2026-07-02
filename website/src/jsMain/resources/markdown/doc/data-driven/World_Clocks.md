---
root: .components.layouts.MarkdownLayout
title: World Clocks - Custom Time Systems & TimeCheck Predicates in Kore
nav-title: World Clocks
description: Create custom world clocks and timelines in Kore datapacks. Define named time counters, time markers, timeCheck predicates, and control the /time command. Integrates with dimension types and environment timelines.
keywords: minecraft world_clock, timeCheck predicate, custom time system datapack, datapack timeline, time marker minecraft, kore world clock, dimension_type clock, datapack time command, minecraft time counter
date-created: 2026-06-16
date-modified: 2026-06-16
routeOverride: /docs/data-driven/world-clocks
---

# World Clocks

Minecraft's time system is built around **world clocks** - named counters that advance every tick.
Timelines animate environment attributes by reading from a clock. The `/time` command lets you
read and manipulate clocks at runtime. Time markers are named tick positions inside a timeline
that commands and predicates can reference.

All of these features were introduced alongside the environment-attributes overhaul in snapshot
**26.1** and are fully supported in Kore's type-safe Kotlin DSL.

---

## World Clock

A world clock is a data-driven resource with no properties. Its mere existence registers a named
ticker under `data/<namespace>/world_clock/<id>.json`.

```kotlin
val dayClock = dataPack.worldClock("day")
val seasonClock = dataPack.worldClock("season")
```

Each call registers the clock and returns a `WorldClockArgument` that you can pass to timelines,
dimension types, and `/time of` commands.

### File Structure

```
data/<namespace>/world_clock/<id>.json
```

The generated JSON is an empty object:

```json
{}
```

---

## Timelines

Timelines animate environment attributes by reading from a `WorldClockArgument`. Every track
is a keyframe curve tied to one attribute; the timeline drives it forward as the clock ticks.

### Creating a Timeline

```kotlin
val dayClock = dataPack.worldClock("day")

val dayNight = dataPack.timeline("day_night", clock = dayClock) {
	periodTicks = 24000

	track(EnvironmentAttributes.Visual.FOG_START_DISTANCE) {
		ease = Linear

		keyframe(0) { value(10.0f) }
		keyframe(6000) { value(100.0f) }
		keyframe(18000) { value(10.0f) }
		keyframe(24000) { value(10.0f) }
	}

	track(EnvironmentAttributes.Gameplay.MONSTERS_BURN) {
		ease = Constant

		keyframe(0) { value(true) }
		keyframe(12000) { value(false) }
	}
}
```

`timeline()` produces `data/<namespace>/timeline/<fileName>.json` and returns a `TimelineArgument`.

### Timeline Properties

| Property      | Type                                                            | Description                                                                   |
|---------------|-----------------------------------------------------------------|-------------------------------------------------------------------------------|
| `clock`       | `WorldClockArgument`                                            | **Required.** Clock this timeline reads from.                                 |
| `periodTicks` | `Int?`                                                          | Loop period in ticks. Omit for a one-shot timeline that runs to the end.      |
| `timeMarkers` | `Map<String, TimelineMarker>?`                                  | Named tick positions inside this timeline. See [Time Markers](#time-markers). |
| `tracks`      | `Map<EnvironmentAttributeArgument, EnvironmentAttributeTrack>?` | Attribute animations.                                                         |

### Tracks

Each `track()` call maps an `EnvironmentAttributeArgument` to an animation curve:

```kotlin
track(EnvironmentAttributes.Visual.SKY_LIGHT_FACTOR) {
	ease = CubicBezier(0.42f, 0.0f, 0.58f, 1.0f)
	modifier = EnvironmentAttributeModifier.OVERRIDE

	keyframe(0) { value(1.0f) }
	keyframe(12000) { value(0.0f) }
}
```

The `value()` function inside a keyframe block accepts `Float`, `Int`, `Boolean`, `String`,
`Color`, or any `EnvironmentAttributesType`.

### Easing Types

| Type          | Description                                          |
|---------------|------------------------------------------------------|
| `Constant`    | Holds the previous keyframe value until the next one |
| `Linear`      | Straight lerp between keyframes                      |
| `CubicBezier` | Custom four-point Bézier curve                       |

Interpolating variants are available as `In*`, `Out*`, and `InOut*` for: `Back`, `Bounce`,
`Circ`, `Cubic`, `Elastic`, `Expo`, `Quad`, `Quart`, `Quint`, `Sine`.

---

## Time Markers

Time markers are named tick positions inside a timeline. Commands can target them with
`/time set <timeMarker>` to jump the clock to that exact position. They also serve as
human-readable labels for `/time query`.

### Defining Time Markers

```kotlin
dataPack.timeline("seasons", clock = seasonClock) {
	periodTicks = 96000   // 4 × 24000 - one in-game "year"

	timeMarker("spring", ticks = 0)
	timeMarker("summer", ticks = 24000)
	timeMarker("autumn", ticks = 48000)
	timeMarker("winter", ticks = 72000, showInCommands = true)
}
```

`showInCommands` controls whether the marker appears in command auto-complete suggestions.
When omitted (or `null`), the default game behaviour applies.

### Referencing a Time Marker in Commands

Use `TimeMarkerArgument` (or the `timeMarker()` factory) to pass a marker to `/time set`:

```kotlin
function("skip_to_summer") {
	time.set(timeMarker("summer", "mymod"))
}
```

```kotlin
function("skip_to_summer_on_clock") {
	time.of(seasonClock).set(timeMarker("summer", "mymod"))
}
```

---

## The `/time` Command

Kore's `time` DSL mirrors the full Minecraft `/time` command tree. Access it via the `time`
property on any `Function`:

```kotlin
function("my_func") {
	time.add(1000)
	time.set(TimePeriod.NOON)
	time.query(TimeType.DAYTIME)
}
```

### Subcommands

| DSL call                       | Emitted command                    | Effect                                        |
|--------------------------------|------------------------------------|-----------------------------------------------|
| `time.add(1000)`               | `time add 1000`                    | Advance the default clock by 1000 ticks       |
| `time.add(1.days)`             | `time add 1d`                      | Advance by one full day                       |
| `time.pause()`                 | `time pause`                       | Freeze the default clock                      |
| `time.resume()`                | `time resume`                      | Unfreeze the default clock                    |
| `time.set(6000)`               | `time set 6000`                    | Jump to tick 6000                             |
| `time.set(TimePeriod.NOON)`    | `time set noon`                    | Jump to noon                                  |
| `time.set(marker)`             | `time set <ns>:<name>`             | Jump to a named time marker                   |
| `time.query(TimeType.DAYTIME)` | `time query daytime`               | Output current daytime                        |
| `time.query(timeline)`         | `time query <ns>:<timeline>`       | Output progress through a timeline            |
| `time.queryRepetitions(tl)`    | `time query <ns>:<tl> repetitions` | Output how many times the timeline has looped |
| `time.queryTime()`             | `time query time`                  | Output absolute game time                     |

### Targeting a Specific Clock with `time.of(clock)`

When your datapack defines multiple world clocks, use `time.of(clock)` to scope every
subcommand to that specific clock:

```kotlin
function("advance_season") {
	time.of(seasonClock).add(6000)
	time.of(seasonClock).set(timeMarker("summer", "mymod"))
	time.of(seasonClock).query(TimeType.DAYTIME)
}
```

Or use the builder form to avoid repeating `of(seasonClock)`:

```kotlin
function("advance_season") {
	val seasonTime = time.of(seasonClock)
	seasonTime.add(6000)
	seasonTime.query(TimeType.DAYTIME)
}
```

`time.of(clock)` returns a `TimeWithClock` instance. It supports the same `add`, `pause`,
`resume`, `set`, `query`, `queryRepetitions`, and `queryTime` methods as the default `Time` DSL.

---

## `timeCheck` Predicate Condition

The `timeCheck` condition passes when the queried world time falls within a specified range.
The optional `clock` parameter selects which clock to query; it defaults to the standard day clock.

```kotlin
predicate("is_daytime") {
	timeCheck(value = 0f..12000f)
}
```

Pass a `period` to apply a modulo first - useful for checking time within a repeating sub-cycle:

```kotlin
predicate("is_second_quarter") {
	timeCheck(min = 6000f, max = 12000f, period = 24000)
}
```

Target a specific world clock:

```kotlin
predicate("is_summer") {
	timeCheck(min = 24000f, max = 48000f, clock = seasonClock)
}
```

See [Predicates](/docs/data-driven/predicates) for the full condition reference.

---

## `defaultClock` in Dimension Type

`DimensionType` has an optional `defaultClock` field that selects which world clock drives the
dimension's default time-dependent behaviour (sunrise/sunset, mob spawning light threshold, etc.).
When omitted, the standard overworld day clock is used.

```kotlin
dataPack.dimensionType("twilight_dimension") {
	defaultClock = dayClock
	hasSkylight = true
	natural = true
	logicalHeight = 256
	infiniburn = Tags.Block.INFINIBURN_OVERWORLD
	minY = -64
	height = 384
	monsterSpawnBlockLightLimit = 0
	monsterSpawnLightLevel = constant(0)
}
```

See [World Generation](/docs/data-driven/worldgen) for the full `DimensionType` reference.

---

## Complete Example

The following snippet wires together all the concepts in this guide:

```kotlin
dataPack("my_mod") {
	// 1. Register a custom clock
	val season = worldClock("season")

	// 2. Build a timeline driven by that clock
	timeline("seasons", clock = season) {
		periodTicks = 96000

		timeMarker("spring", ticks = 0, showInCommands = true)
		timeMarker("summer", ticks = 24000, showInCommands = true)
		timeMarker("autumn", ticks = 48000, showInCommands = true)
		timeMarker("winter", ticks = 72000, showInCommands = true)

		track(EnvironmentAttributes.Visual.FOG_END_DISTANCE) {
			ease = InOutSine
			keyframe(0) { value(200.0f) }
			keyframe(24000) { value(300.0f) }
			keyframe(48000) { value(200.0f) }
			keyframe(72000) { value(80.0f) }
		}
	}

	// 3. Wire the clock to a custom dimension
	val dimType = dimensionType("my_dimension_type") {
		defaultClock = season
		natural = true
		hasSkylight = true
		logicalHeight = 256
		infiniburn = Tags.Block.INFINIBURN_OVERWORLD
		minY = -64
		height = 384
		monsterSpawnBlockLightLimit = 0
		monsterSpawnLightLevel = constant(0)
	}

	// 4. Predicate: is it currently summer?
	predicate("is_summer") {
		timeCheck(min = 24000f, max = 48000f, clock = season)
	}

	// 5. Command: skip to winter
	function("skip_to_winter") {
		time.of(season).set(timeMarker("winter", "my_mod"))
	}

	// 6. Command: advance the season clock
	function("tick_season") {
		time.of(season).add(1)
		time.of(season).query(TimeType.DAYTIME)
	}
}
```

---

## See Also

- [Timelines](/docs/data-driven/timelines) - detailed track, keyframe, and easing reference
- [Predicates](/docs/data-driven/predicates) - all available predicate conditions including `timeCheck`
- [World Generation](/docs/data-driven/worldgen) - `DimensionType` and other worldgen resources
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - attributes available in timeline tracks

### External Resources

- [Minecraft Wiki: World clock](https://minecraft.wiki/w/World_clock)
- [Minecraft Wiki: Timeline](https://minecraft.wiki/w/Timeline)
- [Minecraft Wiki: Commands/time](https://minecraft.wiki/w/Commands/time)
- [Minecraft Wiki: Predicate - time_check](https://minecraft.wiki/w/Predicate#time_check)
