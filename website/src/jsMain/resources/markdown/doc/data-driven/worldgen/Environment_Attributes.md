---
root: .components.layouts.MarkdownLayout
title: Environment Attributes
nav-title: Environment Attributes
description: Data-driven environment attributes to control visual, audio, and gameplay systems in biomes and dimensions.
keywords: minecraft, datapack, kore, worldgen, environment attributes, biome, dimension type, fog, sky, music
date-created: 2026-02-09
date-modified: 2026-02-10
routeOverride: /docs/data-driven/worldgen/environment-attributes
---

# Environment Attributes

Environment Attributes provide a data-driven way to control a variety of visual, audio, and gameplay systems. Each attribute controls a
specific effect: for example, `visual/sky_color` controls the color of the sky, and `gameplay/water_evaporates` controls
whether water can be placed at a given location.

Both **biomes** and **dimension types** can define environment attributes via the `attributes` block.

References: [Biome definition](https://minecraft.wiki/w/Biome_definition), [Dimension type](https://minecraft.wiki/w/Dimension_type)

---

## Sources & Priority

Environment Attribute values can be provided by the following sources (low → high priority):

1. **Dimensions** - base values for the entire dimension
2. **Biomes** - override or modify per-biome

When a biome provides an attribute, it takes priority over the dimension value. For example, if the overworld dimension sets
`sky_color = green` and the plains biome sets `sky_color = red`, a player in the plains biome will see a red sky.

## Modifiers

By default, an attribute value uses the **override** modifier, fully replacing any lower-priority value. However, you can apply a different
modifier to combine with the preceding value instead.

```kotlin
attributes {
	// Simple override (default modifier)
	skyColor(0x78A7FF)

	// Explicit modifier - multiplies the preceding value
	fogEndDistance(0.85f, EnvironmentAttributeModifier.MULTIPLY)
}
```

When a modifier is set, the JSON expands to `{ "argument": ..., "modifier": "..." }`.

### Boolean Modifiers

Applicable to boolean attributes. Argument format: `boolean`.

| Modifier   | Description                           |
|------------|---------------------------------------|
| `OVERRIDE` | Replaces the preceding value entirely |
| `AND`      | Logical AND with the preceding value  |
| `NAND`     | Logical NAND with the preceding value |
| `OR`       | Logical OR with the preceding value   |
| `NOR`      | Logical NOR with the preceding value  |
| `XOR`      | Logical XOR with the preceding value  |
| `XNOR`     | Logical XNOR with the preceding value |

### Float Modifiers

Applicable to float attributes. Argument format: `float`.

| Modifier   | Description                                           |
|------------|-------------------------------------------------------|
| `OVERRIDE` | Replaces the preceding value entirely                 |
| `ADD`      | Adds the argument to the preceding value              |
| `SUBTRACT` | Subtracts the argument from the preceding value       |
| `MULTIPLY` | Multiplies the preceding value by the argument        |
| `MINIMUM`  | Takes the minimum of the preceding value and argument |
| `MAXIMUM`  | Takes the maximum of the preceding value and argument |

### Color Modifiers

Applicable to color attributes. Argument format: RGB color (except `ALPHA_BLEND` which uses ARGB).

| Modifier      | Description                                                          |
|---------------|----------------------------------------------------------------------|
| `OVERRIDE`    | Replaces the preceding value entirely                                |
| `ADD`         | Component-wise additive color blending                               |
| `SUBTRACT`    | Component-wise subtractive color blending                            |
| `MULTIPLY`    | Component-wise multiplicative color blending                         |
| `ALPHA_BLEND` | Traditional alpha blending (ARGB argument; alpha=1 acts as override) |

## Interpolation

Some attributes support **smooth interpolation** between biomes. As a player moves between biomes, interpolated attributes will gradually
transition based on biomes within an 8-block radius of the camera. Non-interpolated attributes use only the biome at the exact position.

---

## All Environment Attributes

All attributes are listed below in alphabetical order by their Minecraft ID.

---

### `audio/ambient_sounds`

Controls which ambient sounds are played around the camera: looping sounds, mood-based sounds, and additional random sounds.

| Property      | Value                                                                          |
|---------------|--------------------------------------------------------------------------------|
| Value type    | Object                                                                         |
| Default value | `{}`                                                                           |
| Modifiers     | `override` only                                                                |
| Interpolated  | No                                                                             |
| Resolved at   | Camera position                                                                |
| Replaces      | Biome `effects.ambient_sound`, `effects.mood_sound`, `effects.additions_sound` |

**Fields:**

- `loop` - optional Sound Event, continually looped sound
- `mood` - optional object for mood sounds:
	- `sound` - Sound Event to play
	- `tickDelay` - ticks between mood sounds (default: `6000`)
	- `blockSearchExtent` - radius for light level sampling (default: `8`)
	- `offset` - distance offset for produced sounds (default: `2.0`)
- `additions` - list of additional random sounds:
	- `sound` - Sound Event to play
	- `tickChance` - probability within a tick to play the sound

```kotlin
attributes {
	ambientSounds(loop = SoundEvents.Ambient.CAVE) {
		mood(sound = SoundEvents.Ambient.CAVE)
		addition(SoundEvents.Ambient.CAVE, 0.01f)
	}
}
```

---

### `audio/background_music`

Controls how and which background music is played.

| Property      | Value                 |
|---------------|-----------------------|
| Value type    | Object                |
| Default value | `{}`                  |
| Modifiers     | `override` only       |
| Interpolated  | No                    |
| Resolved at   | Camera position       |
| Replaces      | Biome `effects.music` |

**Fields:**

- `default` - optional music track with:
	- `sound` - Sound Event to play
	- `minDelay` - minimum delay in ticks between tracks
	- `maxDelay` - maximum delay in ticks between tracks
	- `replaceCurrentMusic` - optional boolean (default: `false`)
- `creative` - optional track, overrides `default` when in Creative Mode
- `underwater` - optional track, overrides `default` when underwater

```kotlin
attributes {
	backgroundMusic {
		default(
			sound = SoundEvents.Music.CREATIVE,
			minDelay = 100,
			maxDelay = 200,
			replaceCurrentMusic = true,
		)
	}
}
```

---

### `audio/music_volume`

The volume at which music should play. Any music playing will fade over time to this value.

| Property      | Value                        |
|---------------|------------------------------|
| Value type    | Float (0 to 1)               |
| Default value | `1.0`                        |
| Modifiers     | Float Modifiers              |
| Interpolated  | No                           |
| Resolved at   | Camera position              |
| Replaces      | Biome `effects.music_volume` |

```kotlin
attributes {
	musicVolume(0.8f)
}
```

---

### `gameplay/bed_rule`

Controls whether a Bed can be used to sleep, set a respawn point, or whether it explodes.

| Property      | Value                                               |
|---------------|-----------------------------------------------------|
| Value type    | Object                                              |
| Default value | `{canSleep: "when_dark", canSetSpawn: "when_dark"}` |
| Modifiers     | `override` only                                     |
| Interpolated  | No                                                  |
| Resolved at   | Head position of the Bed block                      |
| Replaces      | Dimension Type `bed_works`                          |

**Fields:**

- `canSleep` - one of `ALWAYS`, `WHEN_DARK`, `NEVER`
- `canSetSpawn` - one of `ALWAYS`, `WHEN_DARK`, `NEVER`
- `explodes` - optional boolean, if `true` the Bed explodes when interacted with (default: `false`)
- `errorMessage` - optional Text Component shown when unable to sleep

```kotlin
attributes {
	bedRule(
		canSleep = BedSleepRule.ALWAYS,
		canSetSpawn = BedSleepRule.WHEN_DARK,
		explodes = false,
		errorMessage = textComponent("Cannot sleep here"),
	)
}
```

---

### `gameplay/can_start_raid`

If `false`, a Raid cannot be started by a player with Raid Omen.

| Property      | Value                          |
|---------------|--------------------------------|
| Value type    | Boolean                        |
| Default value | `true`                         |
| Modifiers     | Boolean Modifiers              |
| Interpolated  | No                             |
| Resolved at   | Position where the Raid starts |
| Replaces      | Dimension Type `has_raids`     |

```kotlin
attributes {
	canStartRaid(false)
}
```

---

### `gameplay/fast_lava`

Controls whether Lava should spread faster and further, as well as have a stronger pushing force on entities when flowing.

| Property      | Value                                      |
|---------------|--------------------------------------------|
| Value type    | Boolean                                    |
| Default value | `false`                                    |
| Modifiers     | Boolean Modifiers                          |
| Interpolated  | No                                         |
| Resolved at   | Whole dimension (cannot be set on a Biome) |
| Replaces      | Dimension Type `ultrawarm`                 |

```kotlin
attributes {
	fastLava(true)
}
```

---

### `gameplay/increased_fire_burnout`

Controls whether Fire blocks burn out more rapidly than normal.

| Property      | Value                               |
|---------------|-------------------------------------|
| Value type    | Boolean                             |
| Default value | `false`                             |
| Modifiers     | Boolean Modifiers                   |
| Interpolated  | No                                  |
| Resolved at   | Position of the burning Fire block  |
| Replaces      | `#increased_fire_burnout` Biome Tag |

```kotlin
attributes {
	increasedFireBurnout(true)
}
```

---

### `gameplay/nether_portal_spawns_piglin`

Controls whether Nether Portal blocks can spawn Piglins.

| Property      | Value                                    |
|---------------|------------------------------------------|
| Value type    | Boolean                                  |
| Default value | `false`                                  |
| Modifiers     | Boolean Modifiers                        |
| Interpolated  | No                                       |
| Resolved at   | Position of a random Nether Portal block |
| Replaces      | Dimension Type `natural`                 |

```kotlin
attributes {
	netherPortalSpawnsPiglin(true)
}
```

---

### `gameplay/piglins_zombify`

Controls whether Piglins and Hoglins should zombify.

| Property      | Value                             |
|---------------|-----------------------------------|
| Value type    | Boolean                           |
| Default value | `true`                            |
| Modifiers     | Boolean Modifiers                 |
| Interpolated  | No                                |
| Resolved at   | Position of the zombifying entity |
| Replaces      | Dimension Type `piglin_safe`      |

```kotlin
attributes {
	piglinsZombify(false)
}
```

---

### `gameplay/respawn_anchor_works`

Controls whether Respawn Anchors can be used to set spawn. If `false`, the Respawn Anchor will explode once charged.

| Property      | Value                                 |
|---------------|---------------------------------------|
| Value type    | Boolean                               |
| Default value | `false`                               |
| Modifiers     | Boolean Modifiers                     |
| Interpolated  | No                                    |
| Resolved at   | Position of the Respawn Anchor block  |
| Replaces      | Dimension Type `respawn_anchor_works` |

```kotlin
attributes {
	respawnAnchorWorks(true)
}
```

---

### `gameplay/snow_golem_melts`

Controls whether a Snow Golem should be damaged.

| Property      | Value                         |
|---------------|-------------------------------|
| Value type    | Boolean                       |
| Default value | `false`                       |
| Modifiers     | Boolean Modifiers             |
| Interpolated  | No                            |
| Resolved at   | Position of the Snow Golem    |
| Replaces      | `#snow_golem_melts` Biome Tag |

```kotlin
attributes {
	snowGolemMelts(true)
}
```

---

### `gameplay/water_evaporates`

If `true`, Water cannot be placed with a Bucket, melting Ice will not produce water, Wet Sponge will dry out when placed, and Dripstone will
not produce water from Mud blocks.

| Property      | Value                       |
|---------------|-----------------------------|
| Value type    | Boolean                     |
| Default value | `false`                     |
| Modifiers     | Boolean Modifiers           |
| Interpolated  | No                          |
| Resolved at   | Position of the interaction |
| Replaces      | Dimension Type `ultrawarm`  |

```kotlin
attributes {
	waterEvaporates(true)

	// With a modifier
	waterEvaporates(true, EnvironmentAttributeModifier.OR)
}
```

---

### `visual/ambient_particles`

Controls ambient particles that randomly spawn around the camera.

| Property      | Value                    |
|---------------|--------------------------|
| Value type    | List of particle entries |
| Default value | `[]`                     |
| Modifiers     | `override` only          |
| Interpolated  | No                       |
| Resolved at   | Camera position          |
| Replaces      | Biome `effects.particle` |

Each entry has:

- `options` - a particle type (e.g. `Particles.ASH`)
- `probability` - float between 0 and 1, the chance to spawn in an empty space

```kotlin
attributes {
	ambientParticles(
		Particle(ParticleOptions(Particles.ASH), 0.01f),
	)
}
```

---

### `visual/cloud_color`

The color of clouds, expressed as an ARGB hex string.

| Property      | Value           |
|---------------|-----------------|
| Value type    | ARGB Color      |
| Default value | `#00000000`     |
| Modifiers     | Color Modifiers |
| Interpolated  | Yes             |
| Resolved at   | Camera position |

```kotlin
attributes {
	cloudColor(ARGB(255, 128, 64, 32))
}
```

---

### `visual/cloud_fog_end_distance`

The distance in blocks from the camera at which cloud fog ends.

| Property      | Value              |
|---------------|--------------------|
| Value type    | Non-negative float |
| Default value | `1024.0`           |
| Modifiers     | Float Modifiers    |
| Interpolated  | Yes                |
| Resolved at   | Camera position    |

```kotlin
attributes {
	cloudFogEndDistance(256.0f)
}
```

---

### `visual/cloud_height`

The height at which all clouds appear.

| Property      | Value                                                                   |
|---------------|-------------------------------------------------------------------------|
| Value type    | Float                                                                   |
| Default value | `192.33`                                                                |
| Modifiers     | Float Modifiers                                                         |
| Interpolated  | Yes                                                                     |
| Resolved at   | Camera position for rendering, or Happy Ghast position for regeneration |
| Replaces      | Dimension Type `cloud_height`                                           |

```kotlin
attributes {
	cloudHeight(192.33f)
}
```

---

### `visual/default_dripstone_particle`

The default particle to be dripped from Dripstone blocks when no fluid is placed above.

| Property      | Value                                |
|---------------|--------------------------------------|
| Value type    | Particle Options                     |
| Default value | `{type: "dripping_dripstone_water"}` |
| Modifiers     | `override` only                      |
| Interpolated  | No                                   |
| Resolved at   | Position of the Dripstone block      |
| Replaces      | Dimension Type `ultrawarm`           |

```kotlin
attributes {
	defaultDripstoneParticle(Particles.DRIPPING_DRIPSTONE_WATER)
}
```

---

### `visual/fog_color`

The color of fog when the camera is not submerged in another substance. The final value is also affected by the time of day, weather, and
potion effects.

| Property      | Value                     |
|---------------|---------------------------|
| Value type    | RGB Color                 |
| Default value | `#000000`                 |
| Modifiers     | Color Modifiers           |
| Interpolated  | Yes                       |
| Resolved at   | Camera position           |
| Replaces      | Biome `effects.fog_color` |

```kotlin
attributes {
	fogColor(rgb(255, 170, 0))

	// With a modifier
	fogColor(rgb(255, 255, 0), EnvironmentAttributeModifier.ADD)
}
```

---

### `visual/fog_end_distance`

The distance in blocks from the camera at which fog ends.

| Property      | Value              |
|---------------|--------------------|
| Value type    | Non-negative float |
| Default value | `1024.0`           |
| Modifiers     | Float Modifiers    |
| Interpolated  | Yes                |
| Resolved at   | Camera position    |

```kotlin
attributes {
	fogEndDistance(192.0f)
}
```

---

### `visual/fog_start_distance`

The distance in blocks from the camera at which fog starts.

| Property      | Value              |
|---------------|--------------------|
| Value type    | Non-negative float |
| Default value | `0.0`              |
| Modifiers     | Float Modifiers    |
| Interpolated  | Yes                |
| Resolved at   | Camera position    |

```kotlin
attributes {
	fogStartDistance(0.0f)
}
```

---

### `visual/sky_color`

The color of the sky. This color is only visible for the overworld sky. The final value is also affected by the time of day and weather.

| Property      | Value                     |
|---------------|---------------------------|
| Value type    | RGB Color                 |
| Default value | `#000000`                 |
| Modifiers     | Color Modifiers           |
| Interpolated  | Yes                       |
| Resolved at   | Camera position           |
| Replaces      | Biome `effects.sky_color` |

```kotlin
attributes {
	skyColor(Color.RED)
	// or
	skyColor(0x78A7FF)
}
```

---

### `visual/sky_fog_end_distance`

The distance in blocks from the camera at which sky fog ends.

| Property      | Value              |
|---------------|--------------------|
| Value type    | Non-negative float |
| Default value | `512.0`            |
| Modifiers     | Float Modifiers    |
| Interpolated  | Yes                |
| Resolved at   | Camera position    |

```kotlin
attributes {
	skyFogEndDistance(320.0f)
}
```

---

### `visual/water_fog_color`

The color of fog when submerged in water. The final value is also affected by the time of day, weather, and potion effects.

| Property      | Value                           |
|---------------|---------------------------------|
| Value type    | RGB Color                       |
| Default value | `#050533`                       |
| Modifiers     | Color Modifiers                 |
| Interpolated  | Yes                             |
| Resolved at   | Camera position                 |
| Replaces      | Biome `effects.water_fog_color` |

```kotlin
attributes {
	waterFogColor(rgb(5, 5, 51))
}
```

---

### `visual/water_fog_end_distance`

The distance in blocks from the camera at which underwater fog ends.

| Property      | Value           |
|---------------|-----------------|
| Value type    | float           |
| Default value | `-8.0`          |
| Modifiers     | Float Modifiers |
| Interpolated  | Yes             |
| Resolved at   | Camera position |

```kotlin
attributes {
	waterFogEndDistance(96.0f)
}
```

---

### `visual/water_fog_start_distance`

The distance in blocks from the camera at which underwater fog starts.

| Property      | Value           |
|---------------|-----------------|
| Value type    | Float           |
| Default value | `0.0`           |
| Modifiers     | Float Modifiers |
| Interpolated  | Yes             |
| Resolved at   | Camera position |

```kotlin
attributes {
	waterFogStartDistance(0.0f)
}
```

---

## Complete Example

```kotlin
fun DataPack.createCustomDimensionWithAttributes() {
	val dimType = dimensionType("custom_type") {
		ambientLight = 0.1f
		hasCeiling = false
		hasSkylight = true
		height = 384
		logicalHeight = 384
		minY = -64
		natural = true

		attributes {
			// Gameplay
			canStartRaid(true)
			fastLava(false)
			waterEvaporates(false)
			piglinsZombify(true)
			respawnAnchorWorks(false)
			increasedFireBurnout(false)
			netherPortalSpawnsPiglin(false)
			snowGolemMelts(false)
			bedRule(
				canSleep = BedSleepRule.ALWAYS,
				canSetSpawn = BedSleepRule.ALWAYS,
			)

			// Visual
			cloudColor(ARGB(255, 255, 255, 255))
			cloudHeight(192.33f)
			fogStartDistance(0.0f)
			fogEndDistance(192.0f)
			skyFogEndDistance(320.0f)

			// Audio
			musicVolume(0.8f)
			backgroundMusic {
				default(
					sound = SoundEvents.Music.CREATIVE,
					minDelay = 100,
					maxDelay = 200,
				)
			}
		}
	}

	biome("custom_biome") {
		temperature = 0.8f
		downfall = 0.4f
		hasPrecipitation = true

		attributes {
			skyColor(0x78A7FF)
			fogColor(0xC0D8FF)
			waterFogColor(0x050533)
			waterFogEndDistance(96.0f)
			waterFogStartDistance(0.0f)
			ambientParticles(
				Particle(ParticleOptions(Particles.ASH), 0.01f),
			)
			ambientSounds(loop = SoundEvents.Ambient.CAVE) {
				mood(sound = SoundEvents.Ambient.CAVE)
				addition(SoundEvents.Ambient.CAVE, 0.01f)
			}
		}

		effects {
			waterColor = color(0x3F76E4)
		}
	}
}
```

## See Also

- [Biomes](/docs/data-driven/worldgen/biomes) - Biomes can define environment attributes to control per-biome visuals and audio
- [Colors](/docs/concepts/colors) - RGB and ARGB color formats used by color attributes
- [Dimensions](/docs/data-driven/worldgen/dimensions) - Dimension types can define base environment attributes for the entire dimension
- [World Generation](/docs/data-driven/worldgen) - Overview of the worldgen system
