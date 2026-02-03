---
root: .components.layouts.MarkdownLayout
title: Trims
nav-title: Trims
description: Create custom armor trim materials and patterns with Kore's type-safe DSL
keywords: minecraft, datapack, kore, trims, armor, trim material, trim pattern, customization
date-created: 2026-02-03
date-modified: 2026-02-03
routeOverride: /docs/data-driven/trims
---

# Trims

Armor trims are a customization system that allows players to add decorative patterns to their armor pieces. Trims consist of two components:
**materials** (which determine the color/texture) and **patterns
** (which determine the shape/design). Kore provides type-safe DSL builders for creating custom trim materials and patterns.

## Trim Materials

Trim materials define the color palette and appearance when a trim is applied to armor. Each material specifies:

- **Asset name**: Points to the color palette texture
- **Description**: The text shown in-game when hovering over trimmed armor
- **Override armor materials**: Optional per-armor-type color overrides (e.g., different colors for netherite vs. iron armor)

### Basic Usage

```kotlin
trimMaterial("ruby", TrimColorPalettes.AMETHYST, textComponent("Ruby Trim")) {
	description("Ruby", Color.RED)
}
```

This creates a trim material file at `data/<namespace>/trim_material/ruby.json`.

### Material with Armor Overrides

Some materials look different depending on the base armor material. For example, netherite armor uses a darker variant:

```kotlin
trimMaterial("custom_gold", TrimColorPalettes.GOLD, textComponent("Custom Gold")) {
	description("Custom Gold Trim", Color.GOLD)
	overrideArmorMaterial(ArmorMaterial.NETHERITE, Color.hex("4a3c2e"))
}
```

You can also set multiple overrides at once:

```kotlin
trimMaterial("rainbow", TrimColorPalettes.AMETHYST, textComponent("Rainbow")) {
	description("Rainbow Trim")
	overrideArmorMaterials(
		ArmorMaterial.IRON to Color.hex("c0c0c0"),
		ArmorMaterial.GOLD to Color.hex("ffd700"),
		ArmorMaterial.DIAMOND to Color.hex("00ffff"),
		ArmorMaterial.NETHERITE to Color.hex("4a4a4a")
	)
}
```

### Generated JSON

A trim material generates JSON like this:

```json
{
	"asset_name": "minecraft:amethyst",
	"description": {
		"text": "Ruby",
		"color": "#FF0000"
	}
}
```

With armor overrides:

```json
{
	"asset_name": "minecraft:gold",
	"description": {
		"text": "Custom Gold Trim",
		"color": "gold"
	},
	"override_armor_materials": {
		"netherite": "#4a3c2e"
	}
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

- [Chat Components](../concepts/chat_components) - For trim description text formatting
- [Tags](./tags) - Organize trim materials and patterns into groups

### External Resources

- [Minecraft Wiki: Tutorial - Adding custom trims](https://minecraft.wiki/w/Tutorial:Adding_custom_trims) - Official guide for creating custom trims
- [Minecraft Wiki: Armor - Trimming](https://minecraft.wiki/w/Armor#Trimming) - Information about armor trimming mechanics
