---
root: .components.layouts.MarkdownLayout
title: World Presets
nav-title: World Presets
description: Add your own world types to the Minecraft world creation screen, and superflat presets to its customization screen, with Kore.
keywords: minecraft, datapack, kore, worldgen, world preset, world type, flat level generator preset, superflat
date-created: 2026-02-03
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/world-presets
---

# World Presets

A world preset is an entry in the **World Type** dropdown of the world creation screen. It describes the full set of dimensions a new world
starts with, so it is how you replace the Overworld rather than merely add a dimension next to it. Vanilla ships Default, Superflat, Large
Biomes, Amplified and Single Biome.

A flat level generator preset is the smaller sibling: an entry in the Superflat customization screen, holding one layer stack.

References: [World preset](https://minecraft.wiki/w/World_preset), [Superflat presets](https://minecraft.wiki/w/Superflat#Presets)

---

## World Preset

A preset needs at least a `minecraft:overworld` dimension; the Nether, the End and any dimension of your own are optional.

`dimension(id, type)` takes two distinct things. `id` is the id the world knows the dimension by - what `/execute in` and portals resolve.
`type` is the [dimension type](/docs/data-driven/worldgen/dimensions) file holding the height bounds, lighting and environment attributes.
They happen to share a name for the vanilla dimensions, which is exactly why a preset can put a custom type behind the vanilla
`minecraft:overworld` id.

```kotlin
val customWorld = dp.worldPreset("custom_world") {
	// Vanilla id, custom type and terrain.
	dimension(Dimensions.OVERWORLD, myOverworldType) {
		noiseGenerator(settings = myNoiseSettings, biomeSource = multiNoise(BiomePresets.OVERWORLD))
	}

	// Untouched vanilla Nether.
	dimension(Dimensions.THE_NETHER, DimensionTypes.THE_NETHER) {
		noiseGenerator(settings = NoiseSettings.NETHER, biomeSource = multiNoise(BiomePresets.NETHER))
	}

	// Untouched vanilla End.
	dimension(Dimensions.THE_END, DimensionTypes.THE_END) {
		noiseGenerator(settings = NoiseSettings.END, biomeSource = theEnd())
	}

	// A dimension of your own, reachable with /execute in my_pack:mining.
	dimension(DimensionArgument("mining", "my_pack"), miningDimType) {
		noiseGenerator(settings = NoiseSettings.CAVES, biomeSource = fixed(Biomes.DRIPSTONE_CAVES))
	}
}
```

The generator block is the same one a standalone dimension uses, `noiseGenerator`, `flatGenerator` or `debugGenerator` included. See
[Dimensions](/docs/data-driven/worldgen/dimensions#generators).

A preset only shows up in the dropdown once it is listed in the `minecraft:normal` world preset tag:

```kotlin
dp.worldPresetTag("normal", namespace = "minecraft") {
	add(customWorld)
}
```

---

## Flat Level Generator Preset

A preset is an icon plus the superflat settings it applies. Everything left untouched keeps the vanilla `classic_flat` values, so a preset
only declares what it changes; called with no arguments at all, it writes `classic_flat` itself - a grass block icon, the plains biome, a
bedrock/dirt/grass stack, and villages as the only structure set.

```kotlin
val tunnelersDream = dp.flatLevelGeneratorPreset("tunnelers_dream", Items.STONE) {
	settings {
		biome = Biomes.WINDSWEPT_HILLS
		layers {
			layer(Blocks.BEDROCK)
			layer(Blocks.STONE, height = 230)
			layer(Blocks.DIRT, height = 5)
			layer(Blocks.GRASS_BLOCK)
		}
		structureOverrides(StructureSets.MINESHAFTS, StructureSets.STRONGHOLDS)
	}
}
```

`settings { }` is the same `FlatGeneratorSettings` block the
[flat generator](/docs/data-driven/worldgen/dimensions#flat-generator) of a dimension takes, with the same fields and the same three ways of
declaring layers.

A preset only shows up in the superflat customization screen once it is listed in the `minecraft:visible` flat level generator preset tag:

```kotlin
dp.flatLevelGeneratorPresetTag("visible", namespace = "minecraft") {
	add(tunnelersDream)
}
```

## See Also

- [Dimensions](/docs/data-driven/worldgen/dimensions) - dimension types and the generator builders reused here
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - the noise settings a preset points at
- [Tags](/docs/data-driven/tags) - the `normal` and `visible` tags making a preset selectable
- [World Generation](/docs/data-driven/worldgen) - overview of the worldgen system
