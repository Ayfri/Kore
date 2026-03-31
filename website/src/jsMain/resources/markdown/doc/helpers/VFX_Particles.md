---
root: .components.layouts.MarkdownLayout
title: Geometric Particle VFX Engine
nav-title: VFX Particles
description: Generate particle commands for geometric shapes with the Kore helpers module — circles, lines, spheres, spirals, and helixes.
keywords: minecraft, datapack, kore, helpers, vfx, particles, shape, circle, line, sphere, spiral, helix, geometry
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/vfx-particles
position: 18
---

# Geometric Particle VFX Engine

The VFX engine generates [particle](https://minecraft.wiki/w/Commands/particle) commands for geometric shapes.
Each shape is emitted as a generated function containing pre-computed positions.

## Drawing shapes

```kotlin
drawCircle("fire_ring", Particles.FLAME, radius = 5.0, points = 16)

drawShape("soul_helix") {
	shape = Shape.HELIX
	particle = Particles.SOUL_FIRE_FLAME
	radius = 2.0
	points = 40
	height = 5.0
	turns = 4
}
```

## Available shapes

| Shape    | Description                                      |
|----------|--------------------------------------------------|
| `CIRCLE` | Flat circle on the XZ plane                      |
| `LINE`   | Straight line along a direction vector           |
| `SPHERE` | Fibonacci-distributed points on a sphere surface |
| `SPIRAL` | Expanding spiral that rises along Y              |
| `HELIX`  | Fixed-radius helix that rises along Y            |

## VfxShape properties

| Property   | Default | Used by                       |
|------------|---------|-------------------------------|
| `particle` | -       | All shapes                    |
| `radius`   | `1.0`   | CIRCLE, SPHERE, SPIRAL, HELIX |
| `points`   | `20`    | All shapes                    |
| `height`   | `3.0`   | SPIRAL, HELIX                 |
| `length`   | `5.0`   | LINE                          |
| `dx/dy/dz` | `1,0,0` | LINE direction                |
| `turns`    | `3`     | SPIRAL, HELIX                 |
