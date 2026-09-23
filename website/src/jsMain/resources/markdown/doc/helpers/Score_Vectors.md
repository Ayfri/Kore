---
root: .components.layouts.MarkdownLayout
title: Score Vectors
nav-title: Score Vectors
description: Runtime 3D vectors on Minecraft scoreboards with Kore - look directions, dot and cross products, exact length, normalize, teleports and motion.
keywords: minecraft, datapack, kore, helpers, vector, position, motion, velocity, normalize, dash, knockback, teleport, look direction
date-created: 2026-09-23
date-modified: 2026-09-23
routeOverride: /docs/helpers/score-vectors
---

# Score Vectors

A `ScoreVector` is a runtime 3D vector stored in three scores of an entity (`<name>_x`, `<name>_y`, `<name>_z`). It
reads positions, motion and look directions, does the vector maths, then writes the result back as a teleport, a
motion or a rotation. The same data covers what Bookshelf splits into `bs.vector`, `bs.position`, `bs.move` and
`bs.view`.

```kotlin
val math = registerMath()

function("dash") {
	val dash = math.vector("dash", player)
	dash.setToLookDirection(self())
	dash *= 3
	dash.teleport(self(), TeleportMode.RELATIVE)
}
```

Components are fixed-point with a `scale` of `1000` by default, so `1500` means `1.5` blocks. `math.vector` registers the
three objectives in the math load function. Operations between two vectors expect the same scale.

## Reading vectors

```kotlin
function("track_arrow") {
	val velocity = math.vector("velocity", arrow)
	velocity.setToMotion(self()) // blocks per tick
}
```

- `setToPosition(entity)` reads `Pos`, `setToMotion(entity)` reads `Motion`.
- `setToLookDirection(entity)` gives the unit vector the entity looks along, the direction of `^ ^ ^1`.
- `set(x, y, z)` takes plain numbers, `set(0.0, 0.5, 0.0)` stores `0 500 0`.
- `setTo(other)` copies another vector.

## Arithmetic

Operators come first, they read like plain Kotlin:

```kotlin
function("wind_push") {
	velocity += wind
	velocity *= 2
	velocity /= 4
}
```

- `+=` / `-=` add or subtract another vector.
- `*=` / `/=` with an `Int` scale every component, with Minecraft's floored division.
- `*=` with a `ScoreboardEntity` multiplies by a fixed-point factor of the same scale, `500` halves the vector.

## Dot and cross products

```kotlin
function("backstab_check") {
	attackerLook.dot(victimLook, alignment) // 1000 = same direction
}
```

`dot(other, output)` writes the fixed-point dot product. `cross(other, output)` writes `this × other` into a third
vector, which must differ from both operands.

## Length and distance

```kotlin
function("leash_range") {
	playerPos.distanceTo(petPos, distance)
}
```

`length(output)` and `distanceTo(other, output)` write the length in the vector's scale, exact to the float and without
overflow. The components go into the first column of a `text_display` transformation matrix, and the game's own
decomposition returns the length as `scale[0]`.

`lengthSquared(output)` stays on scoreboards and is enough for comparisons, but it overflows past about `46` blocks at
scale `1000`.

## Normalizing

```kotlin
function("homing_missile") {
	toTarget.setTo(targetPos)
	toTarget -= missilePos
	toTarget.normalize()
}
```

`normalize()` rescales the vector to a length of `1000`. It moves the math marker to the vector's coordinates and
teleports it one block along `facing`, so it is exact to the float. A zero vector has no direction and gives an
undefined result.

## Writing vectors back

```kotlin
function("launch_pad") {
	launch.set(0.0, 1.2, 0.0)
	launch.applyAsMotion(self())
}
```

- `applyAsMotion(entity)` writes `Motion`, in blocks per tick. Players ignore `Motion` changes, teleport them instead.
- `teleport(entity, mode)` teleports through a macro `tp`, so it works on players:
	- `TeleportMode.ABSOLUTE` treats the vector as world coordinates,
	- `TeleportMode.RELATIVE` offsets from the entity (`~x ~y ~z`),
	- `TeleportMode.LOCAL` offsets along the entity's view (`^x ^y ^z`, x is left, z is forward).
- `lookAlong(entity)` turns the entity to face along the vector, through `tp` so it also works on players.

## Native physics attributes

For bounces and sliding, the game's attributes handle collision themselves, with no per-tick resolution in the pack:
`bounciness` sets how much velocity survives a collision, `friction_modifier` scales ground friction, and
`air_drag_modifier` scales air drag.

```kotlin
function("rubber_slime") {
	attributes(self(), Attributes.BOUNCINESS) {
		base { set(0.9) }
	}
}
```

## See also

- [Scoreboard Math](/docs/helpers/scoreboard-math) - Sine, cosine, atan2 and square root on the same fixed-point scale.
- [Raycasts](/docs/helpers/raycasts) - Step along a look direction until a block is hit.
- [Display Entities](/docs/helpers/display-entities) - Compile-time transformations with `Vec3f` and quaternions.
