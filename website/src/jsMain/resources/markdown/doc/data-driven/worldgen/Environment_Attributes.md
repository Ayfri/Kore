---
root: .components.layouts.MarkdownLayout
title: Environment Attributes
nav-title: Environment Attributes
description: Data-driven visual, audio and gameplay rules for Minecraft biomes and dimensions - fog, sky, music, mob behavior - with Kore's DSL.
keywords: minecraft, datapack, kore, worldgen, environment attributes, biome, dimension type, fog, sky color, music, modifiers
date-created: 2026-02-09
date-modified: 2026-10-21
routeOverride: /docs/data-driven/worldgen/environment-attributes
---

# Environment Attributes

Environment attributes are the data-driven knobs controlling how a place in the world looks, sounds and behaves. Each one covers a single
effect: `visual/sky_color` is the color of the sky, `gameplay/water_evaporates` decides whether a bucket can place water.

Both [biomes](/docs/data-driven/worldgen/biomes) and [dimension types](/docs/data-driven/worldgen/dimensions) declare them in an
`attributes { }` block, and both use the exact same builders. Everything the removed `effects`, `natural`, `ultrawarm`, `piglin_safe`,
`bed_works` and `has_raids` fields used to control lives here.

```kotlin
attributes {
	skyColor(0x78A7FF)
	fogEndDistance(192.0f)
	waterEvaporates(true)
}
```

References: [Biome definition](https://minecraft.wiki/w/Biome_definition), [Dimension type](https://minecraft.wiki/w/Dimension_type)

---

## Priority, Modifiers, Interpolation

A dimension type provides the base value for the whole world, a biome overrides it locally. Setting `sky_color` to green on the dimension
and to red on the plains biome gives a red sky in the plains, green everywhere else.

By default a value **overrides** whatever the lower-priority source provided. Pass a modifier as the second argument to combine with it
instead, and the JSON expands from a bare value to `{ "argument": ..., "modifier": ... }`:

```kotlin
attributes {
	skyColor(0x78A7FF)                                            // Override, the default.
	fogEndDistance(0.85f, EnvironmentAttributeModifier.MULTIPLY)  // 85% of the dimension value.
}
```

Which modifiers apply depends on the value type of the attribute:

| Value type             | Available modifiers                                                     |
|------------------------|-------------------------------------------------------------------------|
| **Boolean**            | `OVERRIDE`, `AND`, `NAND`, `OR`, `NOR`, `XOR`, `XNOR`                   |
| **Float**              | `OVERRIDE`, `ADD`, `SUBTRACT`, `MULTIPLY`, `MINIMUM`, `MAXIMUM`         |
| **Color**              | `OVERRIDE`, `ADD`, `SUBTRACT`, `MULTIPLY`, `ALPHA_BLEND`, `BlendToGray` |
| **List**               | `OVERRIDE`, `APPEND`                                                    |
| **Natural mob spawns** | `OVERRIDE`, `OVERLAY`                                                   |
| **Object**             | `OVERRIDE` only                                                         |

`ALPHA_BLEND` takes an ARGB argument and does traditional alpha blending; an alpha of `1` behaves as an override. `BlendToGray` is the one
modifier carrying data of its own:

```kotlin
fogColor(rgb(255, 170, 0), EnvironmentAttributeModifier.BlendToGray(brightness = 0.5f, factor = 0.8f))
```

```json
{
	"argument": 16755200,
	"modifier": { "type": "blend_to_gray", "brightness": 0.5, "factor": 0.8 }
}
```

Attributes marked **interpolated** in the tables below fade smoothly as the player crosses a biome border, sampling the biomes within 8
blocks of the camera. The others snap to the biome at the exact position.

---

## Visual

| Builder                          | Value          | Default         | Interp. | Resolved at      | Controls                                                         |
|----------------------------------|----------------|-----------------|---------|------------------|------------------------------------------------------------------|
| `ambientLightColor(color)`       | RGB color      | `#000000`       | Yes     | Camera           | Tint of the scene's ambient illumination.                        |
| `ambientParticles(...)`          | Particle list  | `[]`            | No      | Camera           | Particles randomly spawning around the camera.                   |
| `blockLightTint(color)`          | RGB color      | `#000000`       | Yes     | Camera           | Tint applied to block-emitted light.                             |
| `cloudColor(argb)`               | ARGB color     | `#00000000`     | Yes     | Camera           | Color of the clouds.                                             |
| `cloudFogEndDistance(float)`     | Float, >= 0    | `1024.0`        | Yes     | Camera           | Distance in blocks at which cloud fog ends.                      |
| `cloudHeight(float)`             | Float          | `192.33`        | Yes     | Camera           | Height all clouds are drawn at.                                  |
| `defaultDripstoneParticle(type)` | Particle       | dripstone water | No      | Dripstone block  | Particle dripped when no fluid sits above the dripstone.         |
| `fogColor(color)`                | RGB color      | `#000000`       | Yes     | Camera           | Fog color out of water. Time, weather and potions affect it too. |
| `fogEndDistance(float)`          | Float, >= 0    | `1024.0`        | Yes     | Camera           | Distance in blocks at which fog ends.                            |
| `fogStartDistance(float)`        | Float, >= 0    | `0.0`           | Yes     | Camera           | Distance in blocks at which fog starts.                          |
| `moonAngle(float)`               | Float, degrees | `0.0`           | Yes     | Overworld camera | Angle of the moon.                                               |
| `moonPhase(texture)`             | Moon phase     | `full_moon`     | No      | Overworld camera | Which moon phase texture is drawn.                               |
| `nightVisionColor(color)`        | RGB color      | `#000000`       | Yes     | Camera           | Tint applied under the Night Vision effect.                      |
| `skyColor(color)`                | RGB color      | `#000000`       | Yes     | Camera           | Sky color, Overworld sky only. Time and weather affect it too.   |
| `skyFogEndDistance(float)`       | Float, >= 0    | `512.0`         | Yes     | Camera           | Distance in blocks at which sky fog ends.                        |
| `skyLightColor(color)`           | RGB color      | -               | Yes     | Camera           | Tint applied to sky light.                                       |
| `skyLightFactor(float)`          | Float          | -               | Yes     | Camera           | How strongly sky light contributes to the scene.                 |
| `starAngle(float)`               | Float, degrees | -               | Yes     | Overworld camera | Rotation of the star field.                                      |
| `starBrightness(float)`          | Float          | -               | Yes     | Overworld camera | Brightness of the stars.                                         |
| `sunAngle(float)`                | Float, degrees | -               | Yes     | Overworld camera | Angle of the sun.                                                |
| `sunriseSunsetColor(argb)`       | ARGB color     | -               | Yes     | Overworld camera | Color of the sunrise and sunset glow.                            |
| `waterFogColor(color)`           | RGB color      | `#050533`       | Yes     | Camera           | Fog color while submerged in water.                              |
| `waterFogEndDistance(float)`     | Float          | `-8.0`          | Yes     | Camera           | Distance in blocks at which underwater fog ends.                 |
| `waterFogStartDistance(float)`   | Float          | `0.0`           | Yes     | Camera           | Distance in blocks at which underwater fog starts.               |

```kotlin
attributes {
	skyColor(Color.RED)
	fogColor(rgb(255, 170, 0), EnvironmentAttributeModifier.ADD)
	cloudColor(ARGB(255, 128, 64, 32))
	blockLightTint(rgb(255, 180, 80), EnvironmentAttributeModifier.MULTIPLY)
	moonPhase(Textures.Environment.Celestial.Moon.FULL_MOON)
	defaultDripstoneParticle(Particles.DRIPPING_LAVA)
}
```

Color builders take an `Int` hex literal, a `Color`, or an `rgb(r, g, b)` / `ARGB(a, r, g, b)` call. See
[Colors](/docs/concepts/colors).

### Ambient Particles

`ambientParticles` takes `Particle` entries as varargs or a list, each pairing a particle type with the chance to spawn it in an empty
space. The `entry(...)` helper inside the block is the shortest form.

```kotlin
attributes {
	ambientParticles {
		entry(Particles.ASH, probability = 0.01f)
		entry(Particles.WHITE_ASH, probability = 0.005f)
	}
}
```

With `EnvironmentAttributeModifier.APPEND`, the particles are added after the ones of the lower-priority source, a biome keeping the
particles of its dimension for instance, instead of replacing them.

```kotlin
attributes {
	ambientParticles(Particle(ParticleOptions(Particles.WHITE_ASH), 0.1f), mod = EnvironmentAttributeModifier.APPEND)
}
```

---

## Audio

| Builder                    | Value           | Default | Interp. | Resolved at  | Controls                                             |
|----------------------------|-----------------|---------|---------|--------------|------------------------------------------------------|
| `ambientSounds(...)`       | Object          | `{}`    | No      | Camera       | Looping, mood and random ambient sounds.             |
| `backgroundMusic(...)`     | Object          | `{}`    | No      | Camera       | Which background music plays, and how often.         |
| `fireflyBushSounds(bool)`  | Boolean         | `false` | No      | Firefly bush | Whether firefly bushes make noise.                   |
| `musicVolume(float)`       | Float, 0 to 1   | `1.0`   | No      | Camera       | Target volume music fades to.                        |

### Ambient Sounds

- `loop` - a sound continually looped.
- `mood { }` - the cave-ambience style sound: `sound`, `tickDelay` (default `6000`), `blockSearchExtent` (default `8`), `offset`
  (default `2.0`).
- `addition(sound, tickChance)` - a sound with a per-tick chance to play, repeatable.

```kotlin
attributes {
	ambientSounds(loop = SoundEvents.Ambient.CAVE) {
		mood(sound = SoundEvents.Ambient.CAVE, tickDelay = 6000)
		addition(SoundEvents.Ambient.CAVE, 0.01f)
	}
}
```

### Background Music

Three tracks, each taking a `sound`, a `minDelay`/`maxDelay` range in ticks, and an optional `replaceCurrentMusic`. `creative` and
`underwater` override `default` in their respective situations.

```kotlin
attributes {
	backgroundMusic {
		default(sound = SoundEvents.Music.GAME, minDelay = 12000, maxDelay = 24000)
		creative(sound = SoundEvents.Music.CREATIVE, minDelay = 100, maxDelay = 200)
		underwater(sound = SoundEvents.Music.UNDER_WATER, minDelay = 100, maxDelay = 200, replaceCurrentMusic = true)
	}
}
```

---

## Gameplay

| Builder                          | Value                    | Default     | Interp. | Resolved at        | Controls                                                         |
|----------------------------------|--------------------------|-------------|---------|--------------------|------------------------------------------------------------------|
| `babyVillagerActivity(activity)` | Activity                 | `idle`      | No      | Villager           | What baby villagers do.                                          |
| `bedRule(...)`                   | Object                   | `when_dark` | No      | Bed head           | Whether a bed lets you sleep, set spawn, or is destroyed.        |
| `beesStayInHive(bool)`           | Boolean                  | `false`     | No      | Hive               | Whether bees stay inside their hive.                             |
| `canPillagerPatrolSpawn(bool)`   | Boolean                  | `true`      | No      | Patrol spawn       | Whether pillager patrols spawn.                                  |
| `canStartRaid(bool)`             | Boolean                  | `true`      | No      | Raid start         | Whether Raid Omen can start a raid.                              |
| `catWakingUpGiftChance(float)`   | Float                    | `0.0`       | Yes     | Cat                | Chance for a cat to leave a waking-up gift.                      |
| `creakingActive(bool)`           | Boolean                  | `false`     | No      | Creaking           | Whether the Creaking is active.                                  |
| `creatureWorldGenSpawnProbability(float)` | Float, 0 to 0.9999999 | `0.1`   | No      | Chunk generation   | Chance per chunk for the initial creature spawn at world generation. |
| `eyeblossomOpen(state)`          | `TRUE`/`FALSE`/`DEFAULT` | `DEFAULT`   | No      | Eyeblossom         | Whether eyeblossoms are open.                                    |
| `fastLava(bool)`                 | Boolean                  | `false`     | No      | Whole dimension    | Lava spreading faster, further, and pushing harder.              |
| `increasedFireBurnout(bool)`     | Boolean                  | `false`     | No      | Burning fire block | Whether fire blocks burn out faster.                             |
| `monstersBurn(bool)`             | Boolean                  | `false`     | No      | Mob                | Whether monsters burn in sunlight.                               |
| `naturalMobSpawns(...)`          | Object                   | `{}`        | No      | Spawn position     | The mobs spawned naturally and their spawn costs.                |
| `netherPortalSpawnsPiglin(bool)` | Boolean                  | `false`     | No      | Nether portal      | Whether nether portal blocks spawn piglins.                      |
| `piglinsZombify(bool)`           | Boolean                  | `true`      | No      | Zombifying entity  | Whether piglins and hoglins zombify.                             |
| `respawnAnchorWorks(bool)`       | Boolean                  | `false`     | No      | Respawn anchor     | Whether a respawn anchor sets spawn instead of exploding.        |
| `skyLightLevel(float)`           | Float, 0 to 15           | `15.0`      | No      | Whole dimension    | Sky light level of the dimension.                                |
| `snowGolemMelts(bool)`           | Boolean                  | `false`     | No      | Snow golem         | Whether snow golems take damage.                                 |
| `strawBedRule(...)`              | Object                   | `when_dark` | No      | Straw bed head     | Whether a straw bed lets you sleep, set spawn, or is destroyed.  |
| `surfaceSlimeSpawnChance(float)` | Float                    | `0.0`       | Yes     | Spawn position     | Chance for surface slimes to spawn.                              |
| `turtleEggHatchChance(float)`    | Float                    | `0.0`       | Yes     | Turtle egg         | Chance for turtle eggs to hatch.                                 |
| `villagerActivity(activity)`     | Activity                 | `idle`      | No      | Villager           | What villagers do.                                               |
| `waterEvaporates(bool)`          | Boolean                  | `false`     | No      | Interaction        | Buckets failing, ice melting dry, sponges drying, dripstone dry. |

`fastLava` and `skyLightLevel` resolve for the whole dimension and cannot be set on a biome.

```kotlin
attributes {
	fastLava(true)
	waterEvaporates(true, EnvironmentAttributeModifier.OR)
	piglinsZombify(false)
	villagerActivity(Activities.WORK)
	eyeblossomOpen(EyeblossomOpenState.TRUE)
}
```

### Natural Mob Spawns

`naturalMobSpawns` sets the mobs spawning naturally, one weighted list per mob category, and the spawn costs capping their density. It
belongs in the attributes of a [biome](/docs/data-driven/worldgen/biomes) or a dimension type, and `creatureWorldGenSpawnProbability`
sets the chance per chunk of the initial creature spawn at world generation.

`spawner(type, weight, minCount, maxCount)` writes a constant `count` when both are equal and a `uniform` one otherwise. Set `count`
inside the block for another [int provider](/docs/data-driven/worldgen/providers). A `spawnCost(type, energyBudget, charge)` makes a
mob spend `charge` from a shared `energyBudget`, so a crowded area stops spawning it.

```kotlin
attributes {
	creatureWorldGenSpawnProbability(0.07f)
	naturalMobSpawns {
		spawnsByCategory {
			ambient {
				spawner(EntityTypes.BAT, weight = 10, minCount = 8)
			}
			creature {
				spawner(EntityTypes.SHEEP, weight = 12, minCount = 4)
				spawner(EntityTypes.HORSE, weight = 5, minCount = 2, maxCount = 6)
				spawner(EntityTypes.DONKEY) {
					weight = 1
					count = uniform(1, 3)
				}
			}
		}
		spawnCosts {
			spawnCost(EntityTypes.ZOMBIE, energyBudget = 1.0f, charge = 0.5f)
		}
	}
}
```

```json
{
	"minecraft:gameplay/creature_world_gen_spawn_probability": 0.07,
	"minecraft:gameplay/natural_mob_spawns": {
		"spawn_costs": { "minecraft:zombie": { "energy_budget": 1.0, "charge": 0.5 } },
		"spawns_by_category": {
			"ambient": [{ "count": 8, "type": "minecraft:bat", "weight": 10 }],
			"creature": [
				{ "count": 4, "type": "minecraft:sheep", "weight": 12 },
				{ "count": { "type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 6 }, "type": "minecraft:horse", "weight": 5 },
				{ "count": { "type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 3 }, "type": "minecraft:donkey", "weight": 1 }
			]
		}
	}
}
```

By default the value overrides the one of the lower-priority source. With `EnvironmentAttributeModifier.OVERLAY`, the categories and spawn
costs set here replace the matching ones and the others are kept, so a biome can change the monsters of its dimension and leave the
creatures alone:

```kotlin
attributes {
	naturalMobSpawns(EnvironmentAttributeModifier.OVERLAY) {
		spawnsByCategory {
			monster {
				spawner(EntityTypes.CREEPER, weight = 100, minCount = 4)
			}
		}
	}
}
```

Structures replace these lists inside their bounding box through [spawn overrides](/docs/data-driven/worldgen/structures).

### Bed Rule

`canSleep` and `canSetSpawn` each take `BedSleepRule.ALWAYS`, `WHEN_DARK` or `NEVER`. `destroyOnUse` destroys the bed (it explodes) when it is
used, `destroyOnLeave` destroys it when the player leaves it (both default to `false`), and `errorMessage` replaces the message shown when
sleeping is refused.

```kotlin
attributes {
	bedRule(
		canSleep = BedSleepRule.NEVER,
		canSetSpawn = BedSleepRule.NEVER,
		destroyOnUse = true,
		errorMessage = textComponent("The beds here do not like you."),
	)
}
```

`strawBedRule` takes the same arguments and applies to straw beds.

```kotlin
attributes {
	strawBedRule(canSleep = BedSleepRule.ALWAYS, canSetSpawn = BedSleepRule.NEVER, destroyOnLeave = true)
}
```

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - per-biome attribute overrides
- [Colors](/docs/concepts/colors) - the RGB and ARGB formats used by color attributes
- [Dimensions](/docs/data-driven/worldgen/dimensions) - dimension-wide base attributes
- [Timelines](/docs/data-driven/timelines) - animating attributes over time with keyframes and easing
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
