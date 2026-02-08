---
root: .components.layouts.MarkdownLayout
title: Creating A Datapack
nav-title: Creating A Datapack
description: A guide for creating a Minecraft datapack using Kore.
keywords: minecraft, datapack, kore, guide
date-created: 2024-02-26
date-modified: 2024-01-08
routeOverride: /docs/guides/creating-a-datapack
---

# Creating a DataPack

A DataPack in Kore represents a Minecraft datapack that contains custom game data and resources.

To create a DataPack, use the `dataPack` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate()
```

This will generate the datapack with the given name in the `out` folder by default. If
`generate()` is not called, the datapack will not be generated.

Check the [Generation](#generation) section for more information.

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

See [Configuration](./configuration)

## Pack Metadata

The `dataPack` function generates a `pack.mcmeta` file containing metadata about the datapack.

Configure this metadata using the `pack` block:

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat = packFormat(15)
		maxFormat = packFormat(20)
		description = textComponent("My Datapack")
	}
}
```

- `minFormat` - The minimum supported pack format version.
- `maxFormat` - The maximum supported pack format version.
- `packFormat` - The explicit pack format version (optional).
- `description` - A text component for the datapack description.
- `supportedFormats` - The supported format versions (optional, automatically generated from range if needed).

Kore automatically handles backward compatibility for older game versions. If your
`minFormat` is below the threshold (82 for DataPacks, 65 for ResourcePacks), Kore will automatically include the legacy
`pack_format` and `supported_formats` fields in the generated
`pack.mcmeta` file to ensure compatibility with older versions of Minecraft.

### Targeting Minecraft 1.21.9+

Starting with Minecraft 1.21.9 (25w02a), the
`pack_format` can be a decimal value to represent snapshots or minor versions. You can set this using `packFormat(Double)`:

```kotlin
dataPack("my_1.21.9_datapack") {
	pack {
		minFormat = packFormat(94)
		maxFormat = packFormat(94)
		packFormat = packFormat(94.1)
		description = textComponent("Targeting 1.21.9")
	}
}
```

Note that `minFormat` and `maxFormat` still only accept integer values (or full format `[major, minor]`).

## Filters

Filters are used to filter out certain files from the datapack. For now, you can only filter out block files.

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

## Generation

To generate the datapack, call the `generate()` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate()
```

This will generate the datapack with the given name in the `out` folder by default.<br>
To change the output folder, use the `path` property:

```kotlin
dataPack("my_datapack") {
	path = Path("%appdata%/.minecraft/saves/my_world/datapacks")
}.generate()
```

### Zip Generation

To generate a zip file of the datapack, use the `generateZip` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateZip()
```

### Jar Generation

To generate a JAR file for your datapack, use the
`generateJar` function. This function packages the datapack into a JAR file which can then be used directly with your Minecraft installation or distributed for others to use.

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar()
```

By calling
`generateJar()`, the generated JAR file will be placed in the default output folder. If you wish to specify a different location, use the
`path` property:

```kotlin
dataPack("my_datapack") {
	path = Path("path/to/output/folder")
}.generateJar()
```

You can also configure the JAR generation for different mod loaders such as Fabric, Forge, Quilt, and NeoForge.<br>
This will add metadata to the JAR file that is specific to the mod loader.<br>
You will be able to include your datapack as a mod for your mod loader and simplify the installation process for users.

Below are examples of how to set up these mod loaders:

#### Fabric

To configure Fabric mod loader, use the `fabric` block inside the `generateJar` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar {
	fabric {
		version = "1.2.5"
		contact {
			email = "kore@kore.kore"
			homepage = "https://kore.ayfri.com"
		}

		author("Ayfri")
	}
}
```

This sets the Fabric version, and includes contact information and the author's name.

#### Forge

To configure Forge mod loader, use the `forge` block:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar {
	forge {
		mod {
			authors = "Ayfri"
			credits = "Generated by Kore"

			dependency("my_dependency") {
				mandatory = true
				version = "1.2.5"
			}
		}
	}
}
```

This sets the mod authors, credits, and dependencies for Forge.

#### Quilt

To configure Quilt mod loader, use the `quilt` block:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar {
	quilt("kore") {
		metadata {
			contact {
				email = "kore@kore.kore"
				homepage = "https://kore.ayfri.com"
			}
			contributor("Ayfri", "Author")
		}

		version = "1.2.5"
	}
}
```

This sets the metadata such as contact information and contributors for Quilt.

#### NeoForge

To configure NeoForge mod loader, use the `neoForge` block:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar {
	neoForge {
		mod {
			authors = "Ayfri"
			credits = "Generated by Kore"

			dependency("my_dependency") {
				type = NeoForgeDependencyType.REQUIRED
				version = "1.2.5"
			}
		}
	}
}
```

This sets the authors, credits, and dependencies for NeoForge.

### Merging with existing datapacks

To merge the generated datapack with an existing datapack, use the DSL with the function `mergeWithDatapacks`:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate {
	mergeWithDatapacks("existing_datapack 1", "existing_datapack 2")
}
```

If a zip is provided, it will be considered as a datapack and merged with the generated datapack.<br>
It will unzip the zip in a temporary folder of your system and merge it with the generated datapack, this will not remove the temporary folder.

#### Checking for compatibility

When merging with other datapacks, Kore will check if the pack format range overlaps. If it does not, it will print a warning message.

Example:

```kotlin
val myDatapack1 = dataPack("my_datapack 1") {
	// datapack code here

	pack {
		minFormat = packFormat(40)
		maxFormat = packFormat(40)
	}
}

val myDatapack2 = dataPack("my_datapack 2") {
	// datapack code here
	pack {
		minFormat = packFormat(50)
		maxFormat = packFormat(50)
	}
}

myDatapack1.generate {
	mergeWithDatapacks(myDatapack2)
}
```

This will print out the following message:

```
The pack format range of the other pack is different from the current one. This may cause issues.
Format range: current: 40..40 other: 50..50.
```

It also checks for `supportedFormats` and warns if the other pack is not supported.

## Publishing and Distribution

Once you've generated your datapack, you may want to distribute it to the community. For automated publishing to platforms like Modrinth,
CurseForge, and GitHub Releases, see the [GitHub Actions Publishing](/docs/advanced/github-actions-publishing) guide.

#### Tags

When merging with other datapacks, Kore will merge the tags `minecraft/tags/function/load.json` and
`minecraft/tags/functions/tick.json`.

Example:

```kotlin
val myDatapack1 = dataPack("my_datapack 1") {
	// datapack code here

	load("my_main_function") {
		say("Hello World!")
	}
}

val myDatapack2 = dataPack("my_datapack 2") {
	// datapack code here
	load("load") {
		say("Hello Everyone!")
	}
}

myDatapack1.generate {
	mergeWithDatapacks(myDatapack2)
}
```

The resulting `load.json` file will contain:

```json
{
	"replace": false,
	"values": [
		"my_datapack_1:generated_scope/my_main_function",
		"my_datapack_2:generated_scope/load"
	]
}
```
