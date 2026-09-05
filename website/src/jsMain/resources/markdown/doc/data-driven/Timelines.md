---
root: .components.layouts.MarkdownLayout
title: Timelines
nav-title: Timelines
description: Learn how to use timelines in your Kore datapacks
keywords: minecraft, datapack, kore, timelines, environment attributes, easing, keyframes, time markers, world clock
date-created: 2026-02-10
date-modified: 2026-06-16
routeOverride: /docs/data-driven/timelines
---

# Timelines

Timelines control game behaviour and visuals based on a world clock through environment attributes.
They define tracks that animate environment attributes over time using keyframes and easing functions.
Optional named **time markers** act as labelled tick positions inside a timeline.

Timelines were added in snapshot [**25w45a**](https://minecraft.wiki/w/Java_Edition_25w45a) (Minecraft 1.21.11).

For a broader overview that covers world clocks, the `/time` command, and the `timeCheck` predicate
condition together, see [World Clocks](/docs/data-driven/world-clocks).

## Basic Usage

Every timeline must be bound to a `WorldClockArgument`. Register a clock first, then pass it when
creating the timeline:

```kotlin
val dayClock = dataPack.worldClock("day")

val myTimeline = dataPack.timeline("day_fog", clock = dayClock) {
	periodTicks = 24000

	track(EnvironmentAttributes.Visual.FOG_START_DISTANCE) {
		ease = Linear

		keyframe(0) { value(0.0f) }
		keyframe(12000) { value(100.0f) }
	}
}
```

The `timeline` function creates and registers a timeline in your DataPack. It produces a file at
`data/<namespace>/timeline/<fileName>.json` and returns a `TimelineArgument`.

## Timeline Properties

| Property      | Type                           | Description                                                                   |
|---------------|--------------------------------|-------------------------------------------------------------------------------|
| `clock`       | `WorldClockArgument`           | **Required.** The world clock this timeline reads from.                       |
| `periodTicks` | `Int?`                         | Duration in ticks before the timeline loops. Omit for a one-shot timeline.    |
| `timeMarkers` | `Map<String, TimelineMarker>?` | Named tick positions inside this timeline. See [Time Markers](#time-markers). |

## Tracks

Tracks map environment attributes to keyframe-based animations. Each track specifies an easing type,
an optional modifier, and a list of keyframes.

```kotlin
dataPack.timeline("multi_track", clock = dayClock) {
	periodTicks = 24000

	track(EnvironmentAttributes.Visual.FOG_START_DISTANCE) {
		ease = Linear
		keyframe(0) { value(0.0f) }
		keyframe(12000) { value(100.0f) }
	}

	track(EnvironmentAttributes.Visual.FOG_END_DISTANCE) {
		ease = InOutCubic
		keyframe(0) { value(50.0f) }
		keyframe(6000) { value(200.0f) }
	}
}
```

### Track Properties

| Property    | Type                           | Description                                                          |
|-------------|--------------------------------|----------------------------------------------------------------------|
| `ease`      | `EasingType`                   | The easing type for interpolation between keyframes. Default: linear |
| `modifier`  | `EnvironmentAttributeModifier` | The environment attribute modifier ID. Default: `OVERRIDE`           |
| `keyframes` | `List`                         | A list of keyframes defining values at specific ticks                |

## Keyframes

Each keyframe defines a value at a specific tick within the timeline period:

```kotlin
keyframe(0) {
	value(0.0f)
}

keyframe(12000) {
	value(100.0f)
}
```

The `value` function accepts `Float`, `Int`, `Boolean`, `String`, `Color`, or any `EnvironmentAttributesType`
(e.g., `FloatValue`, `BooleanValue`, `ColorValue`).

Using typed values with `EnvironmentAttributesType`:

```kotlin
keyframe(0) {
	value(RGB(255, 128, 0))  // Color value
}

keyframe(12000) {
	value(FloatValue(1.0f))  // Explicit EnvironmentAttributesType
}
```

## Time Markers

Time markers are named tick positions inside a timeline. Commands can jump a clock to a marker
position via `/time set <timeMarker>`, and they appear in command auto-complete when
`showInCommands` is `true`.

```kotlin
dataPack.timeline("seasons", clock = seasonClock) {
	periodTicks = 96000

	timeMarker("spring", ticks = 0, showInCommands = true)
	timeMarker("summer", ticks = 24000, showInCommands = true)
	timeMarker("autumn", ticks = 48000, showInCommands = true)
	timeMarker("winter", ticks = 72000, showInCommands = true)
}
```

Reference a marker in a function using `TimeMarkerArgument`:

```kotlin
function("skip_to_summer") {
	time.set(timeMarker("summer", "mymod"))
}
```

See [World Clocks - Time Markers](/docs/data-driven/world-clocks#time-markers) for the full command usage.

## Easing Types

Easing types control how values are interpolated between keyframes.

### Non-interpolating Types

| Easing Type | Description                                         |
|-------------|-----------------------------------------------------|
| `Constant`  | Always selects the value from the previous keyframe |
| `Linear`    | Linearly interpolates between keyframes (lerp)      |

### Interpolating Types

Each interpolation kind is available in three forms: `In*`, `Out*`, and `InOut*`.

| Kind    | Ease In     | Ease Out     | Ease In-Out    |
|---------|-------------|--------------|----------------|
| Back    | `InBack`    | `OutBack`    | `InOutBack`    |
| Bounce  | `InBounce`  | `OutBounce`  | `InOutBounce`  |
| Circ    | `InCirc`    | `OutCirc`    | `InOutCirc`    |
| Cubic   | `InCubic`   | `OutCubic`   | `InOutCubic`   |
| Elastic | `InElastic` | `OutElastic` | `InOutElastic` |
| Expo    | `InExpo`    | `OutExpo`    | `InOutExpo`    |
| Quad    | `InQuad`    | `OutQuad`    | `InOutQuad`    |
| Quart   | `InQuart`   | `OutQuart`   | `InOutQuart`   |
| Quint   | `InQuint`   | `OutQuint`   | `InOutQuint`   |
| Sine    | `InSine`    | `OutSine`    | `InOutSine`    |

### Cubic Bezier

For custom easing curves, use `CubicBezier` with two control points:

```kotlin
dataPack.timeline("bezier_example", clock = dayClock) {
	periodTicks = 12000

	track(EnvironmentAttributes.Gameplay.SKY_LIGHT_LEVEL) {
		ease = CubicBezier(0.25f, 0.1f, 0.25f, 1.0f)

		keyframe(0) { value(0) }
		keyframe(6000) { value(15) }
	}
}
```

This serializes as:

```json
{
  "ease": {
    "cubic_bezier": [
      0.25,
      0.1,
      0.25,
      1.0
    ]
  }
}
```

## Environment Attributes

Tracks reference environment attributes from the `EnvironmentAttributes` sealed interface, organized into categories:

- **`EnvironmentAttributes.Audio`** - Sound-related attributes (ambient sounds, music volume, etc.)
- **`EnvironmentAttributes.Gameplay`** - Gameplay mechanics (monster burning, bed rules, sky light level, etc.)
- **`EnvironmentAttributes.Visual`** - Visual effects (fog, clouds, sky color, particles, etc.)

## Complete Example

Here's a complete example of a day/night cycle timeline:

```kotlin
val dayClock = dataPack.worldClock("day")

dataPack.timeline("day_night_cycle", clock = dayClock) {
	periodTicks = 24000

	timeMarker("noon", ticks = 6000, showInCommands = true)
	timeMarker("midnight", ticks = 18000, showInCommands = true)

	track(EnvironmentAttributes.Visual.FOG_START_DISTANCE) {
		ease = InOutSine
		modifier = EnvironmentAttributeModifier.ADD

		keyframe(0) { value(10.0f) }
		keyframe(6000) { value(100.0f) }
		keyframe(12000) { value(100.0f) }
		keyframe(18000) { value(10.0f) }
	}

	track(EnvironmentAttributes.Gameplay.MONSTERS_BURN) {
		ease = Constant

		keyframe(0) { value(true) }
		keyframe(12000) { value(false) }
	}

	track(EnvironmentAttributes.Visual.SKY_LIGHT_FACTOR) {
		ease = CubicBezier(0.42f, 0.0f, 0.58f, 1.0f)

		keyframe(0) { value(1.0f) }
		keyframe(12000) { value(0.0f) }
	}
}
```

## See Also

- [World Clocks](/docs/data-driven/world-clocks) - Clocks, time markers, the `/time` command, and `timeCheck`
- [Environment Attributes](/docs/data-driven/worldgen/environment-attributes) - Environment attributes used in timeline
  tracks
- [Tags](/docs/data-driven/tags) - Use tags to group timelines

### External Resources

- [Minecraft Wiki: Timeline](https://minecraft.wiki/w/Timeline) - Official JSON format reference
