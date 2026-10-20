---
root: .components.layouts.MarkdownLayout
title: Providers
nav-title: Providers
description: Vertical anchors, height providers, int providers and float providers - the randomized values shared by every Kore worldgen builder.
keywords: minecraft, datapack, kore, worldgen, vertical anchor, height provider, int provider, float provider, uniform, trapezoid
date-created: 2026-08-21
date-modified: 2026-10-20
routeOverride: /docs/data-driven/worldgen/providers
---

# Providers

Most worldgen fields do not take a plain number: they take a **provider**, a small object the game samples at every position. That is how
one ore configuration can produce veins of varying size, or one carver can dig tunnels at varying heights.

Four families exist, and they show up across carvers, features, structures and material rules:

- **Vertical anchors** - a single Y level, expressed absolutely or relative to the world bounds.
- **Height providers** - a distribution of vertical anchors.
- **Int providers** - a distribution of integers, for counts and sizes.
- **Float providers** - a distribution of floats, for radii, chances and scales.

Every builder is scoped: it only resolves inside a block actually accepting that kind of value, so nothing pollutes the global completion
list.

## Vertical Anchors

A vertical anchor is one Y level. It serializes as a one-key object, and the resolved Y is never clamped, so an anchor can point outside
the build height of the dimension.

| Builder                 | JSON                           | Meaning                                                                       |
|-------------------------|--------------------------------|-------------------------------------------------------------------------------|
| `absolute(y)`           | `{"absolute": y}`              | Absolute Y coordinate, the one shown on the F3 screen.                        |
| `aboveBottom(n)`        | `{"above_bottom": n}`          | `n` blocks above the bottom of the dimension, `0` being `min_y`.              |
| `belowTop(n)`           | `{"below_top": n}`             | `n` blocks below the top of the dimension, larger values go down.             |
| `relativeToSeaLevel(n)` | `{"relative_to_sea_level": n}` | `n` blocks above the sea level of the dimension, negative values go below it. |

In the Overworld, `aboveBottom(0)` is Y -64, `belowTop(0)` is Y 319 and `relativeToSeaLevel(0)` is Y 63. The Nether and the End resolve
against the 128 blocks of their noise settings: bottom Y 0, top Y 127, sea level 32 and 0. The diagram below places each anchor on the
world column and shades the range of a `uniformHeightProvider`, next to its Kotlin call and JSON:

{{{ .components.doc.WorldHeightDiagram }}}

Reference: [Vertical anchor](https://minecraft.wiki/w/Custom_world_generation/vertical_anchor)

## Height Providers

A height provider picks a Y level between two anchors, following a distribution. It is what `heightRange()` on a placed feature, `y` on a
carver and `startHeight` on a jigsaw structure take.

| Builder                                                                  | Behavior                                                                  | Vanilla example                                                           |
|--------------------------------------------------------------------------|---------------------------------------------------------------------------|---------------------------------------------------------------------------|
| `constantHeightProvider(anchor)`                                         | Always the given anchor. Serializes as the bare anchor, with no wrapper.  | `structure/abandoned_camp_forest` `start_height`                          |
| `constantAbsolute(y)` / `constantAboveBottom(n)` / `constantBelowTop(n)` | Shorthands for `constantHeightProvider` on each anchor form.              | `structure/abandoned_camp_forest` `start_height` is `constantAbsolute(0)` |
| `uniformHeightProvider(min, max)`                                        | Equal chance at every level between both bounds, included.                | `carver/cave` `y`, `placed_feature/amethyst_geode` `height_range`         |
| `trapezoidHeightProvider(min, max, plateau)`                             | Flat top of `plateau` blocks in the middle, linear falloff on both sides. | `placed_feature/ore_diamond` `height_range`                               |
| `biasedToBottomHeightProvider(min, max, inner)`                          | Uniform over the `inner` bottom blocks, exponential falloff above them.   | -                                                                         |
| `veryBiasedToBottomHeightProvider(min, max, inner)`                      | Same shape with a sharper falloff.                                        | `placed_feature/spring_lava` `height_range`                               |
| `weightedListHeightProvider { }`                                         | Picks one of the nested providers, by weight.                             | -                                                                         |

Every builder except `weightedListHeightProvider` also takes plain `Int` bounds, read as absolute Y coordinates.

```kotlin
heightRange(uniformHeightProvider(minInclusive = 0, maxInclusive = 64))
heightRange(trapezoidHeightProvider(minInclusive = 0, maxInclusive = 64, plateau = 20))
heightRange(constantAbsolute(32))
heightRange(constantHeightProvider(belowTop(16)))
heightRange(veryBiasedToBottomHeightProvider(aboveBottom(8), absolute(16)))

heightRange(weightedListHeightProvider {
	entry(3, constantAbsolute(32))
	entry(1, uniformHeightProvider(aboveBottom(0), absolute(16)))
})
```

Reference: [Height provider](https://minecraft.wiki/w/Custom_world_generation/height_provider)

## Int Providers

Int providers fill every field expecting a variable count or size: `count()` and `countOnEveryLayer()` on placed features, the `limit` of a
`capped` processor, the layer heights of a `blockColumn`, and so on.

| Builder                                                | Behavior                                                                            | Vanilla example                                 |
|--------------------------------------------------------|-------------------------------------------------------------------------------------|-------------------------------------------------|
| `constant(value)`                                      | Always returns `value`. Serializes as a plain integer, no wrapper object.           | `placed_feature/basalt_blobs` `count`           |
| `uniform(minInclusive, maxInclusive)`                  | Uniform random integer in `[min, max]`.                                             | `placed_feature/blue_ice` `count`               |
| `biasedToBottom(minInclusive, maxInclusive)`           | Random integer in `[min, max]`, weighted towards the minimum.                       | `placed_feature/glowstone_extra` `count`        |
| `veryBiasedToBottom(minInclusive, maxInclusive)`       | Random integer in `[min, max]`, strongly weighted towards the minimum.              | `carver/cave` `count`                           |
| `trapezoid(min, max, plateau)`                         | Trapezoid distribution over `[min, max]` with a flat top of width `plateau`.        | `feature/glowstone_extra` `xy_offset`           |
| `clampedNormal(minInclusive, maxInclusive, mean, dev)` | Samples a normal distribution (`mean`/`dev`) and clamps the result to `[min, max]`. | `placed_feature/pointed_dripstone` `offset` `x` |
| `clamped(minInclusive, maxInclusive, source)`          | Evaluates `source` and clamps its result to `[min, max]`.                           | `placed_feature/forest_flowers` `count`         |
| `weightedList { }`                                     | Randomly selects one entry from a weighted pool.                                    | `placed_feature/bamboo_vegetation` `count`      |

Each provider has its own distribution shape. The explorer below samples one 10 000 times with the game's formulas, for your
parameters or a vanilla value, and prints the matching Kotlin call and JSON (every provider above except `weightedList`).

{{{ .components.doc.ProviderExplorer }}}

```kotlin
// A plain Int is a constant, only count() and countOnEveryLayer() accept it.
count(10)
count(biasedToBottom(3, 8))
count(clampedNormal(1, 10, mean = 5.0f, deviation = 2.0f))

// Reroll uniform(0, 20), but never below 4 or above 12.
count(clamped(4, 12, uniform(0, 20)))

// 70% chance of 1, 30% chance of 3.
count(weightedList {
	entry(7, constant(1))
	entry(3, constant(3))
})

// Same pool, written as weight-to-provider pairs.
count(weightedList(7 to constant(1), 3 to constant(3)))
```

Reference: [Int provider](https://minecraft.wiki/w/Custom_world_generation/int_provider)

## Float Providers

Float providers fill float fields such as `roomVerticalRadiusMultiplier` and `floorLevel` on carvers, `radius` on foliage placers,
`heightScale` and `stalactiteBluntness` on features, and `volume` or `pitch` on enchantment effects.

| Builder                                    | Behavior                                                                                  | Vanilla example                                     |
|--------------------------------------------|-------------------------------------------------------------------------------------------|-----------------------------------------------------|
| `constant(value)`                          | Always returns `value`. Serializes as a plain float, no wrapper object.                   | `carver/nether_cave` `horizontal_radius_multiplier` |
| `uniform(minInclusive, maxExclusive)`      | Uniform random float in `[min, max)`. `maxExclusive` cannot be less than `minInclusive`.  | `carver/cave` `horizontal_radius_multiplier`        |
| `trapezoid(min, max, plateau)`             | Trapezoid distribution over `[min, max]` with a flat top of width `plateau`.              | `carver/cave` `thickness`                           |
| `clampedNormal(mean, deviation, min, max)` | Samples a normal distribution (`mean`/`deviation`) and clamps the result to `[min, max]`. | `feature/dripstone_cluster` `wetness`               |

```kotlin
floorLevel = constant(-0.5f)
pitch = uniform(0.8f, 1.2f)
roomVerticalRadiusMultiplier = clampedNormal(mean = 0.5f, deviation = 0.2f, min = 0.0f, max = 1.0f)
thickness = trapezoid(min = 0.0f, max = 2.0f, plateau = 0.5f)
```

The explorer under the [int providers](#int-providers) table also covers the float providers.

`constant`, `uniform`, `trapezoid` and `clampedNormal` are named the same for ints and floats. A block accepting both, such as
`largeDripstone { }`, tells them apart by argument type: `constant(3)` builds an int provider and `constant(3f)` a float one.

Reference: [Float provider](https://minecraft.wiki/w/Custom_world_generation/float_provider)

## See Also

- [Carvers](/docs/data-driven/worldgen/carvers) - `y`, `count`, radius multipliers and canyon shape fields
- [Features](/docs/data-driven/worldgen/features) - `heightRange`, `count` and the configured feature parameters
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - `yAbove` and `verticalGradient` material rule conditions
- [Structures](/docs/data-driven/worldgen/structures) - `startHeight` on jigsaw structures, `height` on nether fossils
