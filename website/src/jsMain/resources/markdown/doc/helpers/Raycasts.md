---
root: .components.layouts.MarkdownLayout
title: Raycasts
nav-title: Raycasts
description: Recursive raycast system with the Kore helpers module - step-based raycasting with block hit, max distance, and per-step callbacks.
keywords: minecraft, datapack, kore, helpers, raycast, ray, block, hit, step, recursive
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/raycasts
---

# Raycasts

Raycasts generate a set of recursive functions that step forward from the entity's eyes until they hit a block or reach
max distance.

They are ideal for “look at” interactions, custom tools, line-of-sight checks, or visual debugging because the helper
pre-builds the recursive command chain for you.

## DSL builder

```kotlin
val ray = raycast {
	name = "my_ray"
	maxDistance = 50
	step = 0.5
	onHitBlock = { say("Hit!") }
	onMaxDistance = { say("Too far!") }
	onStep = { particle(Particles.FLAME, vec3()) }
}

function("use_ray") {
	with(ray) { cast() }
}
```

## Callback lifecycle

- `onStep` runs for every successful step before the ray stops.
- `onHitBlock` runs when the next step collides with a block.
- `onMaxDistance` runs when the ray reaches the configured limit without hitting anything.

This makes it easy to keep gameplay logic explicit: particles can live in `onStep`, impact logic in `onHitBlock`, and
fallback behavior in `onMaxDistance`.

## Practical usage pattern

```kotlin
val scanner = raycast {
	name = "scanner"
	maxDistance = 24
	step = 0.25
	onStep = { particle(Particles.END_ROD, vec3()) }
	onHitBlock = { say("Target acquired") }
	onMaxDistance = { say("No target found") }
}

function("scan_once") {
	with(scanner) { cast() }
}
```

For precise interaction beams, prefer a smaller `step`; for cheaper “good enough” scans, use a larger one.

## See also

- [Area](/docs/helpers/area) – Define spatial zones that complement what a ray can detect or trigger.
- [VFX Particles](/docs/helpers/vfx-particles) – Use particles inside `onStep` for visual debugging or beam effects.
- [Entities & Players](/docs/oop/entities-and-players) – Execute raycasts from player or mob contexts with reusable
  selectors.
