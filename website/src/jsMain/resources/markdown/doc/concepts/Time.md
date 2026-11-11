---
root: .components.layouts.MarkdownLayout
title: "Minecraft Time Units: Ticks, Seconds and Days"
nav-title: Time Units
description: Convert and combine Minecraft ticks, seconds and days with the typed TimeNumber in Kotlin, for commands, schedulers and timers.
keywords: minecraft time, ticks to seconds, minecraft tick timer, kore TimeNumber, datapack duration, tick conversion, minecraft time command, schedule delay, minecraft day cycle, time arithmetic
date-created: 2026-06-16
date-modified: 2026-11-11
routeOverride: /docs/concepts/time
---

# Time Units

Minecraft time is measured in **[ticks](https://minecraft.wiki/w/Tick)** (20 ticks = 1 second, 24 000 ticks = 1 day).
Kore models this with `TimeNumber`,
a typed value that carries both the numeric amount and the time unit (`TICKS`, `SECONDS`, `DAYS`).

Commands that accept a duration - `schedule`, `title times`, `time add`, `weather`, `worldborder` - take a `TimeNumber`
argument, so you never have to remember the raw tick math yourself.

## Creating time values

Use the extension properties on any `Number`:

```kotlin
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.days

val a = 100.ticks    // 100t  →  5 seconds
val b = 5.seconds    // 5s
val c = 2.days       // 2d    →  48 000 ticks
val d = 1.5.seconds  // 1.5s  (fractional values are preserved)
```

Or use the `time()` factory for dynamic construction:

```kotlin
import io.github.ayfri.kore.arguments.numbers.time
import io.github.ayfri.kore.arguments.numbers.TimeType

val e = time(200)                    // 200t
val f = time(10, TimeType.SECONDS)   // 10s
```

## Serialization (command output)

`TimeNumber.toString()` produces what Minecraft commands expect:

| Expression    | Output |
|---------------|--------|
| `100.ticks`   | `100`  |
| `1.5.ticks`   | `1.5`  |
| `5.seconds`   | `5s`   |
| `1.5.seconds` | `1.5s` |
| `2.days`      | `2d`   |
| `0.2.days`    | `0.2d` |

Integer values drop the `.0` suffix; fractional values are preserved as-is.

## Arithmetic

`TimeNumber` supports the standard operators (same unit required for meaningful results):

```kotlin
val delay = 3.seconds + 2.seconds   // TimeNumber(5.0, SECONDS)
val half = 10.ticks / 2.ticks      // TimeNumber(5.0, TICKS)
val neg = (-1).days                  // TimeNumber(-1.0, DAYS)
```

## Unit conversions

Convert between units while keeping the logical duration the same:

```kotlin
val twentyTicks = 20.ticks
twentyTicks.inSeconds()   // TimeNumber(1.0, SECONDS)
twentyTicks.inDays()      // TimeNumber(1/1200 ≈ 0.000833, DAYS)

val oneDay = 1.days
oneDay.inTicks()          // TimeNumber(24000.0, TICKS)
oneDay.inSeconds()        // TimeNumber(1200.0, SECONDS)
```

`wholeTicks()` gives the duration as an `Int` of ticks, truncated, for NBT and score values:

```kotlin
2.seconds.wholeTicks()     // 40
0.07.seconds.wholeTicks()  // 1, truncated
1.days.wholeTicks()        // 24000
```

`toTicks()` / `toSeconds()` / `toDays()` reinterpret the raw number without converting (rarely needed):

```kotlin
100.ticks.toSeconds()  // TimeNumber(100.0, SECONDS) - still 100, just retagged as "s"
```

Enter a duration to see it in every unit, and how a command prints it.

{{{ .components.doc.TimeConverter }}}

## Usage in commands

Any command that takes a duration accepts `TimeNumber` directly:

```kotlin
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.numbers.days

// schedule
schedule(5.seconds, myFunction)
schedule(myFunction).replace(100.ticks)

// title times (fade-in, stay, fade-out)
title(self(), 0.5.seconds, 3.seconds, 0.5.seconds)

// world time
time.add(1.days)

// weather
weatherClear(30.seconds)

// worldborder grow
worldBorder.add(10.0, 200.ticks)
```

Here is a complete pack, the [scheduling example](/playground#example=scheduling) of the playground, which warns players then
summons TNT 5 seconds later, with the commands Kore generates for it:

{{{ .components.doc.SchedulingExample }}}

## Further reading

- [Schedule command](/docs/commands/commands#schedule-command) - scheduling functions with a delay
- [Minecraft Wiki - Schedule command](https://minecraft.wiki/w/Commands/schedule) - vanilla `/schedule` syntax and
  behavior
- [Scheduler helper](/docs/helpers/scheduler) - OOP wrapper for recurring schedules
- [Timers](/docs/oop/timers) - OOP timer utilities built on ticks
- [Minecraft Wiki - Tick](https://minecraft.wiki/w/Tick)
