---
root: .components.layouts.MarkdownLayout
title: Raycasts
nav-title: Raycasts
description: Recursive raycast system with the Kore helpers module — step-based raycasting with block hit, max distance, and per-step callbacks.
keywords: minecraft, datapack, kore, helpers, raycast, ray, block, hit, step, recursive
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/helpers/raycasts
position: 11
---

# Raycasts

Raycasts generate a set of recursive functions that step forward from the entity's eyes until they hit a block or reach
max distance.

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
