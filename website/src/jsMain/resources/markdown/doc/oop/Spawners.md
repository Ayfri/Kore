---
root: .components.layouts.MarkdownLayout
title: Spawners
nav-title: Spawners
description: Reusable entity spawner handles with the Kore OOP module - register, spawn, and batch-summon entities.
keywords: minecraft, datapack, kore, oop, spawner, summon, entity, mob, wave
date-created: 2026-03-03
date-modified: 2026-03-31
routeOverride: /docs/oop/spawners
---

# Spawners

Spawners wrap [entity summoning](https://minecraft.wiki/w/Commands/summon) into reusable, pre-configured handles.

They are useful whenever the same mob or entity should be spawned multiple times with the same base configuration.

## Registering a spawner

```kotlin
val zombieSpawner = registerSpawner("zombie", EntityTypes.ZOMBIE) {
	position = vec3(0, 64, 0)
}
```

Registering a spawner once helps keep coordinates, entity type, and spawn behavior in one predictable place.

## Using a spawner

```kotlin
function("spawn_wave") {
	with(zombieSpawner) {
		spawn()                     // summon at configured position
		spawnAt(vec3(10, 64, 10))   // summon at a specific position
		spawnMultiple(5)            // summon multiple at configured position
	}
}
```

| Function        | Description                         |
|-----------------|-------------------------------------|
| `spawn`         | Summon at the configured position   |
| `spawnAt`       | Summon at a specific position       |
| `spawnMultiple` | Summon N entities at configured pos |

## Example: spawning a wave

```kotlin
function("spawn_wave") {
	with(zombieSpawner) {
		spawnMultiple(5)
		spawnAt(vec3(12, 64, 12))
	}
}
```

This makes spawners a natural fit for waves, encounter scripts, arena refills, or scripted boss phases.

## See also

- [Entities & Players](/docs/oop/entities-and-players) – Operate on the spawned entities afterwards with higher-level
  helpers.
- [Teams](/docs/oop/teams) – Assign spawned waves to teams when organizing PvE or faction-based encounters.
- [Events](/docs/oop/events) – React to deaths or interactions involving spawned entities.
