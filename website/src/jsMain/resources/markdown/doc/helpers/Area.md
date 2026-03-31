---
root: .components.layouts.MarkdownLayout
title: Area
nav-title: Area
description: Axis-aligned 3D bounding box with the Kore helpers module - geometric operations, containment checks, and spatial queries.
keywords: minecraft, datapack, kore, helpers, area, bounding box, vec3, intersect, union, contains
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/area
---

# Area

The `Area` class represents an axis-aligned 3D bounding box defined by two `Vec3` corners. It provides geometric
operations useful for zone detection, region math, and spatial queries.

Because it is purely geometric, `Area` is especially useful when you need to pre-compute regions in Kotlin and then
reuse the resulting coordinates in multiple commands, predicates, or generated functions.

## Creating an area

```kotlin
val zone = area(vec3(0, 0, 0), vec3(10, 5, 10))

zone.center     // vec3(5, 2.5, 5)
zone.size        // vec3(10, 5, 10)
zone.radius      // vec3(5, 2.5, 5)

val expanded = zone.expand(2)          // grow by 2 in all directions
val moved = zone.move(vec3(5, 0, 0))   // shift the area
val overlap = zone.intersect(otherZone) // intersection of two areas
val combined = zone.union(otherZone)    // union of two areas

// Containment checks
val inside = vec3(3, 2, 3) in zone     // true
val contains = smallerZone in zone      // true

// Operator shortcuts
val shifted = zone + vec3(1, 0, 0)
val back = zone - vec3(1, 0, 0)

// Range operator
val area = vec3(0, 0, 0)..vec3(10, 10, 10)
```

## Practical example

```kotlin
val lobby = area(vec3(-8, 64, -8), vec3(8, 72, 8))
val arena = lobby.expand(16)
val bossRoom = arena.move(vec3(32, 0, 0))

val entrance = bossRoom.center
val safeZone = bossRoom.contract(2)
val overlapsLobby = bossRoom.intersect(lobby)
```

This style works well when you want a single source of truth for multiple related regions: the base area defines the
layout and the derived areas stay consistent even if the original dimensions change.

## Common use cases

- Define lobby, arena, checkpoint, or boss-room bounds once in Kotlin.
- Derive a slightly larger trigger zone with `expand(...)`.
- Compute a smaller “safe interior” with `contract(...)`.
- Test whether a point or another region belongs inside a larger gameplay area.

## Function reference

| Function / Property | Description                        |
|---------------------|------------------------------------|
| `center`            | Center point of the area           |
| `size`              | Dimensions (x, y, z)               |
| `radius`            | Half-dimensions                    |
| `move`              | Translate the area by a vector     |
| `expand`            | Grow the area outward              |
| `contract`          | Shrink the area inward             |
| `intersect`         | Intersection with another area     |
| `union`             | Union with another area            |
| `contains` (`in`)   | Check if a point or area is inside |

## See also

- [Raycasts](/docs/helpers/raycasts) – Combine spatial bounds with line-of-sight checks or interaction beams.
- [Entities & Players](/docs/oop/entities-and-players) – Reuse computed areas to place, move, or query gameplay
  entities.
- [Predicates](/docs/data-driven/predicates) – Turn region logic into reusable condition checks when needed.
