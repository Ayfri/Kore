---
root: .components.layouts.MarkdownLayout
title: Area
nav-title: Area
description: Axis-aligned 3D bounding box with the Kore helpers module - geometric operations, containment checks, and spatial queries.
keywords: minecraft, datapack, kore, helpers, area, bounding box, vec3, intersect, union, contains
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/area
position: 2
---

# Area

The `Area` class represents an axis-aligned 3D bounding box defined by two `Vec3` corners. It provides geometric
operations useful for zone detection, region math, and spatial queries.

```kotlin
val zone = Area(Vec3(0, 0, 0), Vec3(10, 5, 10))

zone.center     // Vec3(5, 2.5, 5)
zone.size        // Vec3(10, 5, 10)
zone.radius      // Vec3(5, 2.5, 5)

val expanded = zone.expand(2)          // grow by 2 in all directions
val moved = zone.move(Vec3(5, 0, 0))   // shift the area
val overlap = zone.intersect(otherZone) // intersection of two areas
val combined = zone.union(otherZone)    // union of two areas

// Containment checks
val inside = Vec3(3, 2, 3) in zone     // true
val contains = smallerZone in zone      // true

// Operator shortcuts
val shifted = zone + Vec3(1, 0, 0)
val back = zone - Vec3(1, 0, 0)

// Range operator
val area = Vec3(0, 0, 0)..Vec3(10, 10, 10)
```

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
