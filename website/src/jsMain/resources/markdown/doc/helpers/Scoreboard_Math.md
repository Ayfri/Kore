---
root: .components.layouts.MarkdownLayout
title: Scoreboard Math Engine
nav-title: Scoreboard Math
description: Fast fixed-point sine, cosine, atan2, exact integer square root and squared distance on Minecraft scoreboards with Kore, mostly one compute command per call.
keywords: minecraft, datapack, kore, helpers, math, scoreboard, trigonometry, sine, cosine, atan2, sqrt, fixed point, compute
date-created: 2026-03-03
date-modified: 2026-10-24
routeOverride: /docs/helpers/scoreboard-math
---

# Scoreboard Math Engine

The math module computes trigonometry and roots at runtime on scoreboards. Sine, cosine and square root are **one
`compute` command** at the call site, as are `distanceSquared` and `parabola`. `atan2` lives in **one shared generated
function** that a call site feeds with a copy of its input.

```kotlin
val math = registerMath()

function("orbit") {
	math.cos(angle, offsetX)
	math.sin(angle, offsetZ)
}
```

```mcfunction
execute store result score @s offset_x run compute entity @s float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:cos",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:angle,target:{type:"minecraft:context",target:target_entity}},right:360}},0.017453292d]}},1000]}}
execute store result score @s offset_z run compute entity @s float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:sin",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:angle,target:{type:"minecraft:context",target:target_entity}},right:360}},0.017453292d]}},1000]}}
```

> [!FOOTPRINT]
> - **Objectives:** `kore_math`.
> - **Storage:** none, except for the routines listed in [Helper entities](#helper-entities).
> - **Entities:** none, except for the routines listed in [Helper entities](#helper-entities).
> - **Forceloaded chunks:** none, except for those same routines.
> - **Every tick:** no.
> - **Load and files:** `kore_math_init` creates the objective, and `atan2` is one shared function under `generated_scopes/kore_math/`.

## Registering the module

`registerMath()` returns the datapack's `MathHandle` (the same one on every call) and creates the `kore_math_init` load
function. Constants, scratch scores and helper entities are added to it only when an operation needs them, so an unused
routine adds nothing to the pack.

Every example below uses `ScoreboardEntity` scores. Each routine also accepts an `(entity, "input", "output")` form and
[scoreboard delegates](/docs/helpers/state-delegates).

## Fixed-point convention

Scoreboards only hold integers, so results are scaled by **1000**, `MATH_SCALE`:

- `1000` means `1.0`
- `-707` means `-0.707`

## Sine and cosine

The input is an angle in degrees, any value including negative ones. The result is `sin × 1000`, off by at most one
unit. One `compute` command reads the game's own 65536-entry sine table: no helper entity and no shared function.

```kotlin
function("swing_sword") {
	math.sin(swingAngle, liftY) // 90 -> 1000, -30 -> -500
}
```

Pass `angleScale` when the angle carries decimals, `100` means hundredths of a degree. The scale goes up to `46603`,
past it `360 × angleScale` isn't exact as a float. The sine table steps every 0.0055 degrees, so a finer angle doesn't
change the result.

```kotlin
function("smooth_orbit") {
	math.cos(preciseAngle, orbitX, angleScale = 100) // 4500 -> 707
}
```

## atan2

`atan2(y, x, output)` returns the angle of the point `(x, y)` in degrees, in `(-180, 180]`. Both inputs only need to
share a scale. It uses the math marker's `facing` rotation, so it is exact to the float.

```kotlin
function("turret_aim") {
	math.atan2(deltaZ, deltaX, yaw)
}
```

## Integer square root

`sqrt` returns exactly `floor(sqrt(input))` for every non-negative `Int`, and `0` for negative inputs, checked against
all 2³² inputs. One `compute` takes the game's float square root and subtracts `1` when its square exceeds the input,
which the float rounding causes from `16785407`.

```kotlin
function("shockwave_radius") {
	math.sqrt(energy, radius) // 1000000 -> 1000
}
```

For the length of a vector, prefer [`ScoreVector.length`](/docs/helpers/score-vectors#length-and-distance): it never
overflows and keeps decimals.

## Squared distance and parabola

`distanceSquared` computes `(x2 - x1)² + (y2 - y1)² + (z2 - z1)²` from six scores of one entity, in one `compute`. It
is enough for range checks, but a result that doesn't fit an `Int` (an axis difference past `46340`) stores `0`. The
entity must be a single entity (`limitToOne`).

```kotlin
function("in_blast_range") {
	math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
}
```

`parabola` computes a projectile height `v0 × t - g × t² / 2` from four scores, in one `compute`. The division floors,
and a product or a result that doesn't fit an `Int` stores `0`, except a product past 64 bits, which wraps first
(`g = 4` and `t = -2^31` give `g × t² = 0`). The entities read must be single entities (`limitToOne`).

```kotlin
function("arrow_preview") {
	math.parabola(time, launchSpeed, gravity, height) // t = 10, v0 = 500, g = 10 -> 4500
}
```

A `compute` reads the scores of one real entity at most. When the inputs belong to several real entities, the scores of
the others are first copied into scratch fake players, one extra command each.

## Helper entities

`atan2` and the vector helpers use two entities that `kore_math_init` summons in a forceloaded chunk at
`-30000000 1664`: a `marker` and a `text_display`. Both have fixed UUIDs (`4b4f5245-0-0-0-1` and `-2`) and carry the
Smithed `smithed.entity` and `smithed.strict` tags, so well-behaved packs leave them alone. Change
`HelpersConstants.mathEntitiesX` / `mathEntitiesZ` before registering to use another chunk.

## Function reference

| Function                     | Output                                      | Cost at the call site |
|------------------------------|---------------------------------------------|-----------------------|
| `sin` / `cos`                | `value × 1000`, error of at most one unit   | 1 command             |
| `atan2`                      | Degrees × `angleScale`, in `(-180, 180]`    | 3 commands            |
| `sqrt`                       | Exact `floor(sqrt(x))`                      | 1 command             |
| `distanceSquared`            | Squared 3D distance, `0` on overflow        | 1 command             |
| `parabola`                   | `v0 × t - g × t² / 2`, `0` on overflow      | 1 command             |
| `sinTo` / `cosTo` / `sqrtTo` | Infix forms for scoreboard delegates        | 1 command             |

## See also

- [Score Vectors](/docs/helpers/score-vectors) - Runtime 3D vectors, directions, teleports and motion built on this
  module.
- [State Delegates](/docs/helpers/state-delegates) - Scoreboard-backed values to feed into these helpers.
- [Scoreboards](/docs/oop/scoreboards) - Higher-level scoreboard utilities.
