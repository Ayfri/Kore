---
root: .components.layouts.MarkdownLayout
title: Providers
nav-title: Providers
description: Vertical anchors, height providers, int providers and float providers - the randomized values shared by every Kore worldgen builder.
keywords: minecraft, datapack, kore, worldgen, vertical anchor, height provider, int provider, float provider, uniform, trapezoid
date-created: 2026-08-21
date-modified: 2026-08-21
routeOverride: /docs/data-driven/worldgen/providers
---

# Providers

Most worldgen fields do not take a plain number: they take a **provider**, a small object the game samples at every position. That is how
one ore configuration can produce veins of varying size, or one carver can dig tunnels at varying heights.

Four families exist, and they show up across carvers, features, structures and surface rules:

- **Vertical anchors** - a single Y level, expressed absolutely or relative to the world bounds.
- **Height providers** - a distribution of vertical anchors.
- **Int providers** - a distribution of integers, for counts and sizes.
- **Float providers** - a distribution of floats, for radii, chances and scales.

Every builder is scoped: it only resolves inside a block actually accepting that kind of value, so nothing pollutes the global completion
list.

## Vertical Anchors

A vertical anchor is one Y level. It serializes as a one-key object, and the resolved Y is always clamped to the build height of the
dimension.

| Builder          | JSON                  | Meaning                                                           |
|------------------|-----------------------|-------------------------------------------------------------------|
| `absolute(y)`    | `{"absolute": y}`     | Absolute Y coordinate, the one shown on the F3 screen.            |
| `aboveBottom(n)` | `{"above_bottom": n}` | `n` blocks above the bottom of the dimension, `0` being `min_y`.  |
| `belowTop(n)`    | `{"below_top": n}`    | `n` blocks below the top of the dimension, larger values go down. |

The builders extend `VerticalAnchorScope`, so they resolve inside a placed feature, a carver configuration, a `surfaceRules { }` block, and
anywhere a height provider is being built.

Reference: [Vertical anchor](https://minecraft.wiki/w/Custom_world_generation/vertical_anchor)

## Height Providers

A height provider picks a Y level between two anchors, following a distribution. It is what `heightRange()` on a placed feature, `y` on a
carver and `startHeight` on a jigsaw structure take.

| Builder                                                                  | Behavior                                                                  |
|--------------------------------------------------------------------------|---------------------------------------------------------------------------|
| `constantHeightProvider(anchor)`                                         | Always the given anchor. Serializes as the bare anchor, with no wrapper.  |
| `constantAbsolute(y)` / `constantAboveBottom(n)` / `constantBelowTop(n)` | Shorthands for `constantHeightProvider` on each anchor form.              |
| `uniformHeightProvider(min, max)`                                        | Equal chance at every level between both bounds, included.                |
| `trapezoidHeightProvider(min, max, plateau)`                             | Flat top of `plateau` blocks in the middle, linear falloff on both sides. |
| `biasedToBottomHeightProvider(min, max, inner)`                          | Uniform over the `inner` bottom blocks, exponential falloff above them.   |
| `veryBiasedToBottomHeightProvider(min, max, inner)`                      | Same shape with a sharper falloff, the vanilla diamond pattern.           |
| `weightedListHeightProvider { }`                                         | Picks one of the nested providers, by weight.                             |

Every builder except `weightedListHeightProvider` also takes plain `Int` bounds, read as absolute Y coordinates. They extend
`HeightProviderScope`, which itself extends `VerticalAnchorScope`, so anchors are available in the same block.

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

| Builder                                                | Behavior                                                                            |
|--------------------------------------------------------|-------------------------------------------------------------------------------------|
| `constant(value)`                                      | Always returns `value`. Serializes as a plain integer, no wrapper object.           |
| `uniform(minInclusive, maxInclusive)`                  | Uniform random integer in `[min, max]`.                                             |
| `biasedToBottom(minInclusive, maxInclusive)`           | Random integer in `[min, max]`, weighted towards the minimum.                       |
| `trapezoid(min, max, plateau)`                         | Trapezoid distribution over `[min, max]` with a flat top of width `plateau`.        |
| `clampedNormal(minInclusive, maxInclusive, mean, dev)` | Samples a normal distribution (`mean`/`dev`) and clamps the result to `[min, max]`. |
| `clamped(minInclusive, maxInclusive, source)`          | Evaluates `source` and clamps its result to `[min, max]`.                           |
| `weightedList { }`                                     | Randomly selects one entry from a weighted pool.                                    |

```kotlin
count(constant(10))
count(biasedToBottom(3, 8))
count(clampedNormal(1, 10, mean = 5.0f, deviation = 2.0f))

// Reroll uniform(0, 20), but never below 4 or above 12.
count(clamped(4, 12, uniform(0, 20)))

// 70% chance of 1, 30% chance of 3.
count(weightedList {
	add(weightedEntry(7, constant(1)))
	add(weightedEntry(3, constant(3)))
})
```

## Float Providers

Float providers fill float fields such as `yScale` and `floorLevel` on carvers, `radius` on foliage placers, `heightScale` and
`stalactiteBluntness` on features, and `volume` or `pitch` on enchantment effects.

| Builder                                    | Behavior                                                                                  |
|--------------------------------------------|-------------------------------------------------------------------------------------------|
| `constant(value)`                          | Always returns `value`. Serializes as a plain float, no wrapper object.                   |
| `uniform(minInclusive, maxExclusive)`      | Uniform random float in `[min, max)`. `maxExclusive` cannot be less than `minInclusive`.  |
| `trapezoid(min, max, plateau)`             | Trapezoid distribution over `[min, max]` with a flat top of width `plateau`.              |
| `clampedNormal(mean, deviation, min, max)` | Samples a normal distribution (`mean`/`deviation`) and clamps the result to `[min, max]`. |

```kotlin
yScale = constant(1.5f)
pitch = uniform(0.8f, 1.2f)
floorLevel = clampedNormal(mean = 0.5f, deviation = 0.2f, min = 0.0f, max = 1.0f)
thickness = trapezoid(min = 0.0f, max = 2.0f, plateau = 0.5f)
```

`constant`, `uniform`, `trapezoid` and `clampedNormal` are named the same for ints and floats and are told apart by their argument types.
When both imports are in scope and the call is ambiguous, use the `*FloatProvider` aliases: `constantFloatProvider`, `uniformFloatProvider`,
`trapezoidFloatProvider`, `clampedNormalFloatProvider`.

## See Also

- [Carvers](/docs/data-driven/worldgen/carvers) - `y`, `yScale`, radius multipliers and canyon shape fields
- [Features](/docs/data-driven/worldgen/features) - `heightRange`, `count` and the configured feature parameters
- [Noise & Terrain](/docs/data-driven/worldgen/noise) - `yAbove` and `verticalGradient` surface rule conditions
- [Structures](/docs/data-driven/worldgen/structures) - `startHeight` on jigsaw structures, `height` on nether fossils
