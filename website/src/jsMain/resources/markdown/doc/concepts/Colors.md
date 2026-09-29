---
root: .components.layouts.MarkdownLayout
title: Colors
nav-title: Colors
description: Guide to using colors in Kore, including named colors, RGB/ARGB, dye colors, and how different contexts serialize them.
keywords: minecraft, kore, colors, rgb, argb, dyes, components, particles
date-created: 2025-08-11
date-modified: 2026-09-29
routeOverride: /docs/concepts/colors
---

# Overview

Kore provides a unified `Color` API that covers three families of colors used by Minecraft:

- `FormattingColor` and `BossBarColor` (named colors for chat, UI, teams, etc.)
- `RGB` and `ARGB` (numeric colors)
- `DyeColors` (the 16 dye colors used by entities and items)

For the vanilla reference on color behavior, see the [Minecraft Wiki - Color](https://minecraft.wiki/w/Color).

## Color types in Kore

- `Color` (sealed interface): umbrella type accepted by most helpers.
- `FormattingColor`: named chat/formatting colors (e.g. `Color.RED`, `Color.AQUA`).
- `BossBarColor`: bossbar’s named colors.
- `RGB` / `ARGB`: numeric colors. `RGB` is `#rrggbb`, `ARGB` is `#aarrggbb`.
- `DyeColors`: 16 dye colors (white, orange, magenta, …, black) for collars, shulkers, sheep, tropical fish, etc.

Helpers to create numeric colors:

```kotlin
import io.github.ayfri.kore.arguments.colors.*

val c1 = color(85, 255, 255) // RGB
val c2 = color("#55ffff") // RGB from hex string
val c3 = color(0x55ffff) // RGB from decimal
val c4 = argb(255, 85, 255, 255) // ARGB
val c5 = argb("#ff55ffff") // ARGB from hex string
```

Conversions and utils:

```kotlin
val rgb = Color.AQUA.toRGB() // Named/Bossbar/ARGB → RGB
val argb = rgb.toARGB(alpha = 200)
val mixed = mix(rgb(255, 0, 0), 0.25, rgb(0, 0, 255), 0.75)
```

## Random colors

Kore provides static `random()` helpers on the color classes to simplify testing and procedural generation. You can also use a
`Random` instance to generate random colors with a specific seed.

These helpers are available on:

- **`RGB`**: `RGB.random(random: Random = Random)`: returns a random RGB
- **`ARGB`**: `ARGB.random(random: Random = Random, alpha: Boolean = false)`: returns a random ARGB; set
  `alpha = true` to randomize the alpha channel
- **`FormattingColor`**: `FormattingColor.random(random: Random = Random)`: returns a random named formatting color
- **`BossBarColor`**: `BossBarColor.random(random: Random = Random)`: returns a random named bossbar color

### Example usage:

```kotlin
import kotlin.random.Random
import io.github.ayfri.kore.arguments.colors.*
import io.github.ayfri.kore.arguments.enums.DyeColors

val randomRgb = RGB.random()
val randomArgb = ARGB.random(random = Random(12345))
val randomArgbWithAlpha = ARGB.random(alpha = true)
val randomFormatting = FormattingColor.random()
```

They make it easy to generate example content, tests, or procedurally-generated visuals.

## Serialization formats by context

Different Minecraft systems expect colors in different formats. Kore picks the right format automatically via serializers.

- Chat components `color`: string
	- Named colors emit lowercase names (e.g. `"red"`).
	- `RGB` emits `"#rrggbb"`; `ARGB` emits `"#aarrggbb"`.

- Chat components `shadow_color`: signed ARGB int (`ColorAsARGBDecimalSerializer`), colors without alpha being fully opaque
	- `Color.BLUE` emits `-11184641` (`0xFF5555FF`), `argb(0, 0, 0, 0)` emits `0`.

- Item components (decimal ints):
	- `dyedColor(..)`: decimal (or object with `rgb` decimal when tooltip flag is present)
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/item/DyedColorComponent.kt)
	  ```kotlin
	  @Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB,
	  ```
	- `mapColor(..)`: decimal
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/item/MapColorComponent.kt)
	  ```kotlin
	  InlineAutoSerializer<MapColorComponent, RGB>(RGB.Companion.ColorAsDecimalSerializer, MapColorComponent::color, ::MapColorComponent)
	  ```
	- `potionContents(customColor=..)`: decimal
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/item/PotionContentsComponent.kt)
	  ```kotlin
	  @Serializable(RGB.Companion.ColorAsDecimalSerializer::class)
	  var customColor: RGB? = null,
	  ```
	- Firework explosion `colors` / `fade_colors`: decimal list
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/item/FireworkExplosionComponent.kt)
	  ```kotlin
	  var colors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	  @SerialName("fade_colors")
	  var fadeColors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	  ```

- Worldgen Biomes (decimal ints):
	- `effects.waterColor`, `effects.grassColor`, `effects.foliageColor`, etc. use decimal ints.
	- Sky/fog/water fog colors are set via **environment attributes** (`attributes`).
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/features/worldgen/biome/types/BiomeEffects.kt)
  ```kotlin
  // BiomeEffects (decimal ints in JSON)
  @Serializable(ColorAsDecimalSerializer::class) var waterColor: Color = color(4159204)
  @Serializable(ColorAsDecimalSerializer::class) var grassColor: Color? = null
  @Serializable(ColorAsDecimalSerializer::class) var foliageColor: Color? = null
  @Serializable(ColorAsDecimalSerializer::class) var dryFoliageColor: Color? = null

  // Environment attributes (also typically decimal ints for worldgen colors)
  attributes {
  	skyColor(0x78A7FF)
  	fogColor(0xC0D8FF)
  	waterFogColor(0x050533)
  }
  ```

- Particles:
	- Command particles (decimal ints): Dust, DustColorTransition, Trail
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/commands/particle/types/DustParticleType.kt)
	  ```kotlin
	  var color: @Serializable(ColorAsDecimalSerializer::class) Color,
	  ```
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/commands/particle/types/DustColorTransitionParticleType.kt)
	  ```kotlin
	  var fromColor: @Serializable(ColorAsDecimalSerializer::class) Color,
	  var toColor: @Serializable(ColorAsDecimalSerializer::class) Color,
	  ```
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/commands/particle/types/TrailParticleType.kt)
	  ```kotlin
	  var color: @Serializable(ColorAsDecimalSerializer::class) Color,
	  ```
	- Enchantment effect particles (double arrays `[r, g, b]` in 0..1):
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/DustParticleType.kt)
	  ```kotlin
	  var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	  ```
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/DustColorTransitionParticleType.kt)
	  ```kotlin
	  var fromColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	  var toColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	  ```
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/EffectParticleType.kt)
	  ```kotlin
	  var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
	  ```

- UI and commands using named colors (strings):
	- Teams, Scoreboards, Bossbar: `FormattingColor` / `BossBarColor`
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/commands/Teams.kt)
	  ```kotlin
	  fun color(color: FormattingColor) = fn.addLine(..., literal("color"), color)
	  ```
	  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/commands/BossBar.kt)
	  ```kotlin
	  fun setColor(color: BossBarColor) = fn.addLine(..., literal("color"), color)
	  ```

## Dye colors and where they’re used

`DyeColors` are used for entity variants and certain item/entity data components:

- `catCollar(..)`, `wolfCollar(..)`, `sheepColor(..)`, `shulkerColor(..)`
  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/entity/CatCollar.kt)
  ```kotlin
  data class CatCollar(var color: DyeColors)
  fun ComponentsScope.catCollar(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/entity/WolfCollar.kt)
  ```kotlin
  data class WolfCollar(var color: DyeColors)
  fun ComponentsScope.wolfCollar(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/entity/SheepColor.kt)
  ```kotlin
  data class SheepColor(var color: DyeColors)
  fun ComponentsScope.sheepColor(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/arguments/components/entity/ShulkerColor.kt)
  ```kotlin
  data class ShulkerColor(var color: DyeColors)
  fun ComponentsScope.shulkerColor(color: DyeColors)
  ```

## Practical examples

Chat components (string serialization):

```kotlin
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color

val title = textComponent("Legendary Sword", Color.AQUA)
```

Dyed leather color (decimal serialization):

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.colors.Color

val dyedHelmet = Items.LEATHER_HELMET {
	dyedColor(Color.AQUA)
}
```

Map color (decimal serialization):

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.colors.rgb

val withMapColor = Items.STONE {
	mapColor(rgb(85, 255, 255))
}
```

Fireworks (decimal lists):

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.components.item.FireworkExplosionShape
import io.github.ayfri.kore.arguments.colors.Color

val rocket = Items.FIREWORK_ROCKET {
	fireworks(flightDuration = 1) {
		explosion(FireworkExplosionShape.BURST) {
			colors(Color.AQUA)
			fadeColors(Color.BLACK, Color.WHITE)
			hasTrail = true
			hasTwinkle = true
		}
	}
}
```

Biome effects (decimal):

```kotlin
import io.github.ayfri.kore.features.worldgen.biome.types.BiomeEffects
import io.github.ayfri.kore.arguments.colors.color

val effects = BiomeEffects(
	waterColor = color(4159204),
	grassColor = color(0x79C05A)
)
```

Particles

- Command dust (decimal):
  ```kotlin
  import io.github.ayfri.kore.commands.particle.types.Dust
  import io.github.ayfri.kore.arguments.colors.Color

  val p = Dust(color = Color.RED, scale = 1.0)
  ```

- Enchantment dust (double array):
  ```kotlin
  import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.DustParticleType
  import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
  import io.github.ayfri.kore.arguments.colors.rgb

  val enchantDust = DustParticleType(
      type = ParticleTypeArgument("dust"),
      color = rgb(255, 0, 0)
  )
  ```

Entity variant dye usage:

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.enums.DyeColors

val cat = Items.CAT_SPAWN_EGG {
	catCollar(DyeColors.RED)
}
```

## Notes

- Passing `Color` to component helpers that use decimal or double-array formats is safe: Kore converts automatically via
  `toRGB()`.
- Use `DyeColors` when the game mechanic expects a dye color (collars, sheep, shulkers, tropical fish), and `FormattingColor`/
  `BossBarColor` for chat/UI tints.

## Further reading

- [Chat Components](/docs/concepts/chat-components) - Use colors in chat components
- [Minecraft Wiki - Color](https://minecraft.wiki/w/Color)
