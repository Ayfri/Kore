---
root: .components.layouts.MarkdownLayout
title: Spawners
nav-title: Spawners
description: Reusable entity spawner handles with the Kore OOP module - register, spawn, and batch-summon entities.
keywords: minecraft, datapack, kore, oop, spawner, summon, entity, mob, wave
date-created: 2026-03-03
date-modified: 2026-03-03
routeOverride: /docs/oop/spawners
position: 14
---

# Spawners

Spawners wrap [entity summoning](https://minecraft.wiki/w/Commands/summon) into reusable, pre-configured handles.

## Registering a spawner

```kotlin
val zombieSpawner = registerSpawner("zombie", EntityTypes.ZOMBIE) {
	position = vec3(0, 64, 0)
}
```

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
