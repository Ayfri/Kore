---
root: .components.layouts.MarkdownLayout
title: Scoreboard Math Engine
nav-title: Scoreboard Math
description: Trigonometric and algebraic functions using scoreboard operations with the Kore helpers module - sine, cosine, square root, distance, and parabolic trajectory.
keywords: minecraft, datapack, kore, helpers, math, scoreboard, trigonometry, sine, cosine, sqrt, distance, parabola
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/scoreboard-math
---

# Scoreboard Math Engine

The math module provides trigonometric and algebraic functions using scoreboard operations. All values use fixed-point
arithmetic scaled by **1000** to preserve decimal precision inside integer-only scoreboards.

## Registering the math module

```kotlin
val math = registerMath()
```

This generates a **load function** that creates the `kore_math` objective and sets useful constants (`#2`, `#360`,
`#scale`).

## Reading the results

Because scoreboards only store integers, every result is scaled by `1000`:

- `1000` means `1.0`
- `500` means `0.5`
- `-707` means approximately `-0.707`

That convention stays consistent across trigonometric helpers and formulas, which makes it easier to combine several
math operations in the same datapack.

## Trigonometric functions

Sine and cosine use a pre-computed 360-entry lookup table (one entry per degree). The input score holds an angle in
degrees; the output receives `value × 1000`.

```kotlin
function("trig_demo") {
	math.apply {
		cos(player, "angle", "cos_result")
		sin(player, "angle", "sin_result")
	}
}
```

This is particularly handy for scoreboard-driven movement systems, orbiting particles, or knockback calculations where
the input angle is already tracked as an integer.

## Square root

An iterative Babylonian / Newton approximation (8 iterations by default):

```kotlin
function("sqrt_demo") {
	math.sqrt(player, "input_val", "sqrt_result")
}
```

Use `sqrt` when you need a real distance magnitude from squared values, or when another formula requires a root instead
of a squared distance.

## Euclidean distance (squared)

Computes `(x2−x1)² + (y2−y1)² + (z2−z1)²`:

```kotlin
function("distance_demo") {
	math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
}
```

Computing the squared distance is often enough for range checks and is cheaper to chain with threshold comparisons than
an actual square root.

## Parabolic trajectory

Computes `Y = v0 × t − (g × t²) / 2` for projectile simulation:

```kotlin
function("parabola_demo") {
	math.parabola(player, "time", "#v0", "#gravity", "para_y")
}
```

## Example: projectile preview

```kotlin
function("projectile_preview") {
	math.apply {
		sin(player, "launch_angle", "sin_angle")
		cos(player, "launch_angle", "cos_angle")
		parabola(player, "travel_time", "#launch_speed", "#gravity", "height_offset")
	}
}
```

The helpers are intentionally small and composable: you can build a more complex simulation by combining multiple
scoreboard operations rather than relying on one giant black-box function.

## Function reference

| Function          | Description                                  |
|-------------------|----------------------------------------------|
| `cos`             | Cosine lookup (degrees → scaled result)      |
| `sin`             | Sine lookup (degrees → scaled result)        |
| `sqrt`            | Integer square root (Newton's method)        |
| `distanceSquared` | Squared 3D Euclidean distance                |
| `parabola`        | Parabolic Y from time, velocity, and gravity |

## See also

- [State Delegates](/docs/helpers/state-delegates) – Write scoreboard-backed values with less boilerplate before feeding
  them into math helpers.
- [Cooldowns](/docs/oop/cooldowns) – Pair time-based gameplay gates with scoreboard-driven calculations.
- [Scoreboards](/docs/oop/scoreboards) – Higher-level scoreboard utilities that fit naturally around these formulas.
