---
root: .components.layouts.MarkdownLayout
title: Geometric Particle VFX Engine
nav-title: VFX Particles
description: "Generate geometric particle effects with Kore: circles, lines, spheres, spirals and helixes, in world, relative or local coordinates."
keywords: minecraft, datapack, kore, helpers, vfx, particles, shape, circle, line, sphere, spiral, helix, geometry, coordinates, relative, local
date-created: 2026-03-03
date-modified: 2026-08-15
routeOverride: /docs/helpers/vfx-particles
---

# Geometric Particle VFX Engine

The VFX engine generates [particle](https://minecraft.wiki/w/Commands/particle) commands for geometric shapes.
Each shape is emitted as a generated function containing pre-computed positions.

This is especially useful when you want repeatable visual effects without manually writing dozens of particle commands.
You describe the geometry once and then call the generated function wherever you need it.

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

## Calling the generated function

Both helpers return the generated `Function`, which you call with the `function` command. Since positions default to
the relative coordinate space, running it under `execute at` centers the shape on the target:

```kotlin
val ring = drawCircle("fire_ring", Particles.FLAME, radius = 5.0, points = 16)

function("cast_ring") {
	execute {
		at(self())
		run { function(ring) }
	}
}
```

The geometry is computed at generation time, so the runtime cost is one `function` call plus one `particle` command per
point. Keep `points` in check for effects that run every tick.

## Choosing between helpers

- Use `drawCircle(...)` when you only need a quick single-purpose helper.
- Use `drawShape(...)` when you want one DSL entry point that can switch shape types or expose more parameters.

Both approaches generate reusable functions, so you can keep expensive geometry decisions at generation time rather than
recomputing them mentally for every particle command.

## Available shapes

| Shape    | Description                                      |
|----------|--------------------------------------------------|
| `CIRCLE` | Flat circle on the XZ plane                      |
| `LINE`   | Straight line along a direction vector           |
| `SPHERE` | Fibonacci-distributed points on a sphere surface |
| `SPIRAL` | Expanding spiral that rises along Y              |
| `HELIX`  | Fixed-radius helix that rises along Y            |

## VfxShape properties

| Property       | Default                   | Used by                       |
|----------------|---------------------------|-------------------------------|
| `particle`     | -                         | All shapes                    |
| `radius`       | `1.0`                     | CIRCLE, SPHERE, SPIRAL, HELIX |
| `points`       | `20`                      | All shapes                    |
| `height`       | `3.0`                     | SPIRAL, HELIX                 |
| `length`       | `5.0`                     | LINE                          |
| `dx/dy/dz`     | `1,0,0`                   | LINE direction                |
| `turns`        | `3`                       | SPIRAL, HELIX                 |
| `positionType` | `PosNumber.Type.RELATIVE` | All shapes                    |
| `origin`       | `vec3(0, 0, 0)`           | All shapes                    |

`points` must be strictly positive, otherwise `drawShape` throws. The `dx`/`dy`/`dz` direction is normalized, so only
its ratio matters, and a zero direction vector produces no particles.

## Coordinate space

Every generated point is written in `positionType`'s coordinate space:

- `PosNumber.Type.RELATIVE` (the default) writes `~x ~y ~z`, so the shape is centered on wherever the generated
  function is executed.
- `PosNumber.Type.LOCAL` writes `^x ^y ^z`, so the shape additionally rotates with the executing entity's facing
  direction.
- `PosNumber.Type.WORLD` writes absolute coordinates, so the shape always lands at the same place in the world
  regardless of where the function is executed.

```kotlin
drawCircle("fire_ring", Particles.FLAME, radius = 5.0, points = 4, positionType = PosNumber.Type.LOCAL)
```

```mcfunction
particle minecraft:flame ^5 ^ ^
...
```

Local coordinates read as `^left ^up ^forward`, so a circle generated on the XZ plane maps to the left/forward plane and
tilts with where the entity looks, which suits casting and shield effects that should track the player's aim.

## Offsetting with origin

`origin` shifts every point of the shape before the coordinate space is applied. It is the way to lift a shape off the
ground or to place a world-space shape somewhere other than `0 0 0`:

```kotlin
drawShape("halo") {
	shape = Shape.CIRCLE
	particle = Particles.END_ROD
	radius = 0.5
	points = 12
	origin = vec3(0, 2.2, 0)
}
```

Only the three numeric values of `origin` are used. Any `~` or `^` marker it carries is discarded, since `positionType`
alone decides the coordinate space of the output.

## Example: arena intro effect

```kotlin
drawShape("arena_intro") {
	shape = Shape.SPIRAL
	particle = Particles.HAPPY_VILLAGER
	radius = 4.0
	points = 60
	height = 6.0
	turns = 5
}
```

This kind of effect works well for spawn platforms, ritual circles, victory moments, or waypoint markers.
