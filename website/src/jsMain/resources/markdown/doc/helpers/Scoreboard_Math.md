---
root: .components.layouts.MarkdownLayout
title: Scoreboard Math Engine
nav-title: Scoreboard Math
description: Fast fixed-point sine, cosine, atan2 and exact integer square root on Minecraft scoreboards with Kore, shared functions and two commands per call.
keywords: minecraft, datapack, kore, helpers, math, scoreboard, trigonometry, sine, cosine, atan2, sqrt, fixed point
date-created: 2026-03-03
date-modified: 2026-09-23
routeOverride: /docs/helpers/scoreboard-math
---

# Scoreboard Math Engine

The math module computes trigonometry and roots at runtime on scoreboards. Every heavy routine lives in **one shared
generated function**, so each call site costs two commands: copy the input, then store the function's return value.

```kotlin
val math = registerMath()

function("orbit") {
	math.cos(angle, offsetX)
	math.sin(angle, offsetZ)
}
```

```mcfunction
scoreboard players operation #x kore_math = @s angle
execute store result score @s offset_x run function my_pack:generated_scopes/kore_math/cos_1
```

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
unit, from a degree-5 polynomial evaluated purely on scoreboards: no entity and no lookup table.

```kotlin
function("swing_sword") {
	math.sin(swingAngle, liftY) // 90 -> 1000, -30 -> -500
}
```

Pass `angleScale` when the angle carries decimals, `100` means hundredths of a degree. Kore generates one function per
scale with its constants precomputed. Scales above `238` are rejected because the fixed-point product would overflow an
`Int`.

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

`sqrt` returns exactly `floor(sqrt(input))` for every non-negative `Int`, and `0` for negative inputs. It runs a
piecewise-linear first guess plus three Newton steps, checked against all 2³¹ inputs.

```kotlin
function("shockwave_radius") {
	math.sqrt(energy, radius) // 1000000 -> 1000
}
```

For the length of a vector, prefer [`ScoreVector.length`](/docs/helpers/score-vectors#length-and-distance): it never
overflows and keeps decimals.

## Squared distance and parabola

`distanceSquared` computes `(x2 - x1)² + (y2 - y1)² + (z2 - z1)²` from six scores of one entity. It is enough for range
checks, but it overflows once an axis difference goes past `46340`.

```kotlin
function("in_blast_range") {
	math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
}
```

`parabola` computes a projectile height `v0 × t - g × t² / 2` from four scores:

```kotlin
function("arrow_preview") {
	math.parabola(time, launchSpeed, gravity, height)
}
```

## Helper entities

`atan2` and the vector helpers use two entities that `kore_math_init` summons in a forceloaded chunk at
`-30000000 1664`: a `marker` and a `text_display`. Both have fixed UUIDs (`4b4f5245-0-0-0-1` and `-2`) and carry the
Smithed `smithed.entity` and `smithed.strict` tags, so well-behaved packs leave them alone. Change
`HelpersConstants.mathEntitiesX` / `mathEntitiesZ` before registering to use another chunk.

## Function reference

| Function                     | Output                                      | Cost at the call site |
|------------------------------|---------------------------------------------|-----------------------|
| `sin` / `cos`                | `value × 1000`, error of at most one unit   | 2 commands            |
| `atan2`                      | Degrees × `angleScale`, in `(-180, 180]`    | 3 commands            |
| `sqrt`                       | Exact `floor(sqrt(x))`                      | 2 commands            |
| `distanceSquared`            | Squared 3D distance                         | 13 commands           |
| `parabola`                   | `v0 × t - g × t² / 2`                       | 7 commands            |
| `sinTo` / `cosTo` / `sqrtTo` | Infix forms for scoreboard delegates        | 2 commands            |

## See also

- [Score Vectors](/docs/helpers/score-vectors) - Runtime 3D vectors, directions, teleports and motion built on this
  module.
- [State Delegates](/docs/helpers/state-delegates) - Scoreboard-backed values to feed into these helpers.
- [Scoreboards](/docs/oop/scoreboards) - Higher-level scoreboard utilities.
