---
root: .components.layouts.MarkdownLayout
title: Colors
nav-title: Colors
description: Guide to using colors in Kore, including named colors, RGB/ARGB, dye colors, and how different contexts serialize them.
keywords: minecraft, kore, colors, rgb, argb, dyes, components, particles
date-created: 2025-08-11
date-modified: 2025-08-11
routeOverride: /docs/colors
---

### Overview

Kore provides a unified `Color` API that covers three families of colors used by Minecraft:

- `FormattingColor` and `BossBarColor` (named colors for chat, UI, teams, etc.)
- `RGB` and `ARGB` (numeric colors)
- `DyeColors` (the 16 dye colors used by entities and items)

For the vanilla reference on color behavior, see the Minecraft Wiki’s Color page (`https://minecraft.wiki/w/Color`).

### Color types in Kore

- `Color` (sealed interface): umbrella type accepted by most helpers.
- `FormattingColor`: named chat/formatting colors (e.g. `Color.RED`, `Color.AQUA`).
- `BossBarColor`: bossbar’s named colors.
- `RGB` / `ARGB`: numeric colors. `RGB` is `#rrggbb`, `ARGB` is `#aarrggbb`.
- `DyeColors`: 16 dye colors (white, orange, magenta, …, black) for collars, shulkers, sheep, tropical fish, etc.

Helpers to create numeric colors:

```kotlin
import io.github.ayfri.kore.arguments.colors.*

val c1 = color(85, 255, 255) // RGB
val c2 = color('#55ffff') // RGB from hex string
val c3 = color(0x55ffff) // RGB from decimal
val c4 = argb(255, 85, 255, 255) // ARGB
val c5 = argb('#ff55ffff') // ARGB from hex string
```

Conversions and utils:

```kotlin
val rgb = Color.AQUA.toRGB() // Named/Bossbar/ARGB → RGB
val argb = rgb.toARGB(alpha = 200)
val mixed = mix(rgb(255, 0, 0), 0.25, rgb(0, 0, 255), 0.75)
```

### Serialization formats by context

Different Minecraft systems expect colors in different formats. Kore picks the right format automatically via serializers.

- Chat components (`color`, `shadow_color`): string
  - Named colors emit lowercase names (e.g. `"red"`).
  - `RGB` emits `"#rrggbb"`; `ARGB` emits `"#aarrggbb"`.

- Item components (decimal ints):
  - `dyedColor(..)`: decimal (or object with `rgb` decimal when tooltip flag is present)
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/item/DyedColorComponent.kt#L14)
    ```kotlin
    @Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB,
    ```
  - `mapColor(..)`: decimal
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/item/MapColorComponent.kt#L13)
    ```kotlin
    InlineSerializer<MapColorComponent, RGB>(RGB.Companion.ColorAsDecimalSerializer, MapColorComponent::color)
    ```
  - `potionContents(customColor=..)`: decimal
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/item/PotionContentsComponent.kt#L29-L30)
    ```kotlin
    @Serializable(RGB.Companion.ColorAsDecimalSerializer::class)
    var customColor: RGB? = null,
    ```
  - Firework explosion `colors` / `fade_colors`: decimal list
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/item/FireworkExplosionComponent.kt#L30-L32)
    ```kotlin
    var colors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
    @SerialName("fade_colors")
    var fadeColors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
    ```

- Worldgen Biomes (decimal ints):
  [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/features/worldgen/biome/types/BiomeEffects.kt#L12-L17)
  ```kotlin
  @Serializable(ColorAsDecimalSerializer::class) var skyColor: Color = color(7907327)
  @Serializable(ColorAsDecimalSerializer::class) var fogColor: Color = color(12638463)
  @Serializable(ColorAsDecimalSerializer::class) var waterColor: Color = color(4159204)
  @Serializable(ColorAsDecimalSerializer::class) var waterFogColor: Color = color(329011)
  @Serializable(ColorAsDecimalSerializer::class) var grassColor: Color? = null
  @Serializable(ColorAsDecimalSerializer::class) var foliageColor: Color? = null
  ```

- Particles:
  - Command particles (decimal ints): Dust, DustColorTransition, Trail
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/commands/particle/types/DustParticleType.kt#L21)
    ```kotlin
    var color: @Serializable(ColorAsDecimalSerializer::class) Color,
    ```
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/commands/particle/types/DustColorTransitionParticleType.kt#L22-L25)
    ```kotlin
    var fromColor: @Serializable(ColorAsDecimalSerializer::class) Color,
    var toColor: @Serializable(ColorAsDecimalSerializer::class) Color,
    ```
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/commands/particle/types/TrailParticleType.kt#L22)
    ```kotlin
    var color: @Serializable(ColorAsDecimalSerializer::class) Color,
    ```
  - Enchantment effect particles (double arrays `[r, g, b]` in 0..1):
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/DustParticleType.kt#L11)
    ```kotlin
    var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
    ```
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/DustColorTransitionParticleType.kt#L12-L14)
    ```kotlin
    var fromColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
    var toColor: @Serializable(ColorAsDoubleArraySerializer::class) Color,
    ```
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/features/enchantments/effects/entity/spawnparticles/types/EntityEffectParticleType.kt#L11)
    ```kotlin
    var color: @Serializable(ColorAsDoubleArraySerializer::class) Color,
    ```

- UI and commands using named colors (strings):
  - Teams, Scoreboards, Bossbar: `FormattingColor` / `BossBarColor`
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/commands/Teams.kt#L43)
    ```kotlin
    fun color(color: FormattingColor) = fn.addLine(..., literal("color"), color)
    ```
    [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/commands/BossBar.kt#L57)
    ```kotlin
    fun setColor(color: BossBarColor) = fn.addLine(..., literal("color"), color)
    ```

### Dye colors and where they’re used

`DyeColors` are used for entity variants and certain item/entity data components:

- `catCollar(..)`, `wolfCollar(..)`, `sheepColor(..)`, `shulkerColor(..)`
  [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/entity/CatCollar.kt#L11-L22)
  ```kotlin
  data class CatCollar(var color: DyeColors)
  // ... existing code ...
  fun ComponentsScope.catCollar(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/entity/WolfCollar.kt#L11-L22)
  ```kotlin
  data class WolfCollar(var color: DyeColors)
  // ... existing code ...
  fun ComponentsScope.wolfCollar(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/entity/SheepColor.kt#L11-L22)
  ```kotlin
  data class SheepColor(var color: DyeColors)
  // ... existing code ...
  fun ComponentsScope.sheepColor(color: DyeColors)
  ```
  [See on GitHub](https://github.com/Ayfri/Kore/tree/master/kore/src/main/kotlin/io/github/ayfri/kore/arguments/components/entity/ShulkerColor.kt#L11-L22)
  ```kotlin
  data class ShulkerColor(var color: DyeColors)
  // ... existing code ...
  fun ComponentsScope.shulkerColor(color: DyeColors)
  ```

### Practical examples

Chat components (string serialization):

```kotlin
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color

val title = textComponent('Legendary Sword', Color.AQUA)
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
import io.github.ayfri.kore.generated.FireworkExplosionShape
import io.github.ayfri.kore.arguments.colors.Color

val rocket = Items.FIREWORK_ROCKET {
    fireworks(flightDuration = 1) {
        explosion(FireworkExplosionShape.BURST) {
            colors(Color.AQUA)
            fadeColors(Color.BLACK, Color.WHITE)
            hasTrail = true
            hasFlicker = true
        }
    }
}
```

Biome effects (decimal):

```kotlin
import io.github.ayfri.kore.features.worldgen.biome.types.BiomeEffects
import io.github.ayfri.kore.arguments.colors.color

val effects = BiomeEffects(
    skyColor = color(7907327),
    waterColor = color(4159204)
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
      type = ParticleTypeArgument('minecraft:dust'),
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

### Notes

- Passing `Color` to component helpers that use decimal or double-array formats is safe: Kore converts automatically via `toRGB()`.
- Use `DyeColors` when the game mechanic expects a dye color (collars, sheep, shulkers, tropical fish), and `FormattingColor`/`BossBarColor` for chat/UI tints.

### Further reading

- Minecraft Wiki — Color: [`https://minecraft.wiki/w/Color`](https://minecraft.wiki/w/Color)
