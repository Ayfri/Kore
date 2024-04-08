---
root: .components.layouts.MarkdownLayout
title: Creating A Datapack
nav-title: Creating A Datapack
description: A guide for creating a Minecraft datapack using Kore.
keywords: minecraft, datapack, kore, guide
date-created: 2024-02-26
date-modified: 2024-04-06
routeOverride: /docs/creating-a-datapack
---

# Creating a DataPack

A DataPack in Kore represents a Minecraft datapack that contains custom game data and resources.

To create a DataPack, use the `dataPack` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate()
```

This will generate the datapack with the given name in the `out` folder by default.
If `generate()` is not called, the datapack will not be generated.

**Use `generateZip()` method to generate the datapack as a zip file.**

## Changing Output Folder

To change the output folder, use the `path` property:

```kotlin
dataPack("my_datapack") {
	path = Path("%appdata%/.minecraft/saves/my_world/datapacks")
}
```

## Adding Icon

To add an icon to the datapack, use the `iconPath` property:

```kotlin
dataPack("my_datapack") {
	iconPath = Path("icon.png")
}
```

## Configuration

See [Configuration](/docs/configuration)

## Pack Metadata

The `dataPack` function generates a `pack.mcmeta` file containing metadata about the datapack.

Configure this metadata using the `pack` block:

```kotlin
dataPack("mydatapack") {
	pack {
		format = 15
		description = textComponent("My Datapack")
		supportedFormat(15..20)
	}
}
```

- `format` - The datapack format version.
- `description` - A text component for the datapack description.
- `supportedFormats` - The supported format versions.

## Filters

Filters are used to filter out certain files from the datapack.
For now, you can only filter out block files.

For example, to filter out all `.txt` files:

```kotlin
dataPack("my_datapack") {
	filter {
		blocks("stone*")
	}
}
```

This will filter out all block files that start with `stone`.

## Content

The main content of the datapack is generated from the various builder functions like `biome`, `lootTable`, etc.

For example:

```kotlin
dataPack("my_datapack") {

	// ...

	recipes {
		craftingShaped("enchanted_golden_apple") {
			pattern(
				"GGG",
				"GAG",
				"GGG"
			)

			key("G", Items.GOLD_BLOCK)
			key("A", Items.APPLE)

			result(Items.ENCHANTED_GOLDEN_APPLE)
		}
	}
}
```

This demonstrates adding a custom recipe to the datapack.
