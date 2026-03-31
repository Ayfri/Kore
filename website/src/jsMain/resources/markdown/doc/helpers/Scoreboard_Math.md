---
root: .components.layouts.MarkdownLayout
title: Scoreboard Math Engine
nav-title: Scoreboard Math
description: Trigonometric and algebraic functions using scoreboard operations with the Kore helpers module - sine, cosine, square root, distance, and parabolic trajectory.
keywords: minecraft, datapack, kore, helpers, math, scoreboard, trigonometry, sine, cosine, sqrt, distance, parabola
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/scoreboard-math
position: 12
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

## Square root

An iterative Babylonian / Newton approximation (8 iterations by default):

```kotlin
function("sqrt_demo") {
	math.sqrt(player, "input_val", "sqrt_result")
}
```

## Euclidean distance (squared)

Computes `(x2−x1)² + (y2−y1)² + (z2−z1)²`:

```kotlin
function("distance_demo") {
	math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
}
```

## Parabolic trajectory

Computes `Y = v0 × t − (g × t²) / 2` for projectile simulation:

```kotlin
function("parabola_demo") {
	math.parabola(player, "time", "#v0", "#gravity", "para_y")
}
```

## Function reference

| Function          | Description                                  |
|-------------------|----------------------------------------------|
| `cos`             | Cosine lookup (degrees → scaled result)      |
| `sin`             | Sine lookup (degrees → scaled result)        |
| `sqrt`            | Integer square root (Newton's method)        |
| `distanceSquared` | Squared 3D Euclidean distance                |
| `parabola`        | Parabolic Y from time, velocity, and gravity |
