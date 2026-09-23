---
root: .components.layouts.MarkdownLayout
title: Trims
nav-title: Trims
description: Create custom armor trim materials and patterns with Kore's type-safe DSL
keywords: minecraft, datapack, kore, trims, armor, trim material, trim pattern, customization
date-created: 2026-02-03
date-modified: 2026-09-23
routeOverride: /docs/data-driven/trims
---

# Trims

Armor trims are a customization system that allows players to add decorative patterns to their armor pieces. Trims consist of two components:
**materials** (which determine the color/texture) and **patterns
** (which determine the shape/design). Kore provides type-safe DSL builders for creating custom trim materials and patterns.

## Trim Materials

Trim materials define the color palette and appearance when a trim is applied to armor. Each material specifies:

- **Asset name**: The color palette applied to the trim, written as its bare name (`amethyst`)
- **Description**: The text shown in-game when hovering over trimmed armor
- **Override armor assets**: Optional palettes swapped in on specific equipment, like vanilla gold using `gold_darker` on
  gold armor so the trim stays visible

### Basic Usage

```kotlin
trimMaterial("ruby", Textures.Trims.ColorPalettes.AMETHYST) {
	description("Ruby", Color.RED)
}
```

This creates a trim material file at `data/<namespace>/trim_material/ruby.json`:

```json
{
	"asset_name": "amethyst",
	"description": {
		"text": "Ruby",
		"color": "red"
	}
}
```

### Palette Overrides per Equipment

A trim in the same color as the armor it sits on disappears, so vanilla swaps in a darker palette on matching armor:

```kotlin
trimMaterial("custom_gold", Textures.Trims.ColorPalettes.GOLD) {
	description("Custom Gold Trim", Color.GOLD)
	overrideArmorAsset(EquipmentAssets.GOLD, Textures.Trims.ColorPalettes.GOLD_DARKER)
}
```

```json
{
	"asset_name": "gold",
	"description": {
		"text": "Custom Gold Trim",
		"color": "gold"
	},
	"override_armor_assets": {
		"minecraft:gold": "gold_darker"
	}
}
```

`overrideArmorAssets` sets several overrides at once:

```kotlin
trimMaterial("dark_quartz", Textures.Trims.ColorPalettes.QUARTZ) {
	description("Dark Quartz")
	overrideArmorAssets(
		EquipmentAssets.DIAMOND to Textures.Trims.ColorPalettes.DIAMOND_DARKER,
		EquipmentAssets.NETHERITE to Textures.Trims.ColorPalettes.NETHERITE_DARKER,
	)
}
```

## Trim Patterns

Trim patterns define the visual design applied to armor. Each pattern specifies:

- **Asset ID**: Points to the pattern texture model
- **Description**: The text shown in-game when hovering over trimmed armor
- **Decal**: Whether the pattern should render as a decal overlay (like netherite patterns)

### Basic Usage

```kotlin
trimPattern("stripes", Models.TRIMS_MODELS_ARMOR_COAST, textComponent("Stripes")) {
	description("Striped Pattern", Color.GRAY)
}
```

This creates a trim pattern file at `data/<namespace>/trim_pattern/stripes.json`.

### Pattern as Decal

Setting
`decal = true` makes the pattern render as an overlay, which is useful for patterns that should appear on top of the base armor texture without replacing it (similar to how netherite trim patterns work):

```kotlin
trimPattern("overlay", Models.TRIMS_MODELS_ARMOR_SENTRY, textComponent("Overlay"), decal = true) {
	description("Overlay Pattern")
}
```

### Generated JSON

A trim pattern generates JSON like this:

```json
{
	"asset_id": "minecraft:trims/models/armor/coast",
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
	"asset_id": "minecraft:trims/models/armor/sentry",
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

