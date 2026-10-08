---
root: .components.layouts.MarkdownLayout
title: Trims
nav-title: Trims
description: Create custom armor trim materials and patterns with Kore's type-safe DSL
keywords: minecraft, datapack, kore, trims, armor, trim material, trim pattern, customization
date-created: 2026-02-03
date-modified: 2026-10-08
routeOverride: /docs/data-driven/trims
---

# Trims

Armor trims are a customization system that allows players to add decorative patterns to their armor pieces. Trims consist of two components:
**materials** (which determine the color/texture) and **patterns** (which determine the shape/design). Kore provides type-safe DSL builders for creating custom trim materials and patterns.

## Trim Materials

Trim materials define the color palette and appearance when a trim is applied to armor. Each material specifies:

- **Palette ID**: The palette texture coloring the trim, `minecraft:trim/amethyst` reads
  `textures/palettes/trim/amethyst.png` from the resource pack
- **Description**: The text shown in-game when hovering over trimmed armor

### Basic Usage

```kotlin
trimMaterial("ruby", Textures.Palettes.Trim.AMETHYST) {
	description("Ruby", Color.RED)
}
```

This creates a trim material file at `data/<namespace>/trim_material/ruby.json`:

```json
{
	"description": {
		"text": "Ruby",
		"color": "red"
	},
	"palette_id": "minecraft:trim/amethyst"
}
```

A custom palette takes its path relative to `textures/palettes/`:

```kotlin
// Reads assets/my_pack/textures/palettes/trim/ruby.png
trimMaterial("ruby", TrimColorPaletteArgument("trim/ruby", "my_pack")) {
	description("Ruby", Color.RED)
}
```

The darker palette vanilla uses on armor of the same color as the trim, like `gold_darker` on gold armor, isn't set by
the trim material: the resource pack's equipment asset swaps it through `trim_palette_replacements`.

## Trim Patterns

Trim patterns define the visual design applied to armor. Each pattern specifies:

- **Asset ID**: The id the game builds the texture paths from, `minecraft:coast` reads
  `textures/trims/entity/humanoid/coast.png` and `textures/trims/entity/humanoid_leggings/coast.png` from the resource
  pack. It defaults to the pattern's own id
- **Description**: The text shown in-game when hovering over trimmed armor
- **Decal**: Whether the pattern should render as a decal overlay (like netherite patterns)

### Basic Usage

```kotlin
// Reuses the vanilla coast textures
trimPattern("stripes", TrimPatterns.COAST, textComponent("Stripes")) {
	description("Striped Pattern", Color.GRAY)
}

// Asset id <namespace>:custom, textures in assets/<namespace>/textures/trims/entity/humanoid/custom.png
trimPattern("custom") {
	description("Custom Pattern")
}
```

This creates trim pattern files at `data/<namespace>/trim_pattern/stripes.json` and `custom.json`.

### Pattern as Decal

Setting
`decal = true` makes the pattern render as an overlay, which is useful for patterns that should appear on top of the base armor texture without replacing it (similar to how netherite trim patterns work):

```kotlin
trimPattern("overlay", TrimPatterns.SENTRY, textComponent("Overlay"), decal = true) {
	description("Overlay Pattern")
}
```

### Generated JSON

A trim pattern generates JSON like this:

```json
{
	"asset_id": "minecraft:coast",
	"description": {
		"text": "Striped Pattern",
		"color": "gray"
	},
	"decal": false
}
```

With decal enabled:

```json
{
	"asset_id": "minecraft:sentry",
	"description": {
		"text": "Overlay Pattern"
	},
	"decal": true
}
```

## See Also

- [Chat Components](/docs/concepts/chat-components) - For trim description text formatting
- [Tags](/docs/data-driven/tags) - Organize trim materials and patterns into groups

### External Resources

- [Minecraft Wiki: Tutorial - Adding custom trims](https://minecraft.wiki/w/Tutorial:Adding_custom_trims) - Official guide for creating custom trims
- [Minecraft Wiki: Armor - Trimming](https://minecraft.wiki/w/Armor#Trimming) - Information about armor trimming mechanics

