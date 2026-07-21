---
root: .components.layouts.MarkdownLayout
title: Creating A Datapack
nav-title: Creating A Datapack
description: A guide for creating a Minecraft datapack using Kore.
keywords: minecraft, datapack, kore, guide
date-created: 2024-02-26
date-modified: 2026-07-21
routeOverride: /docs/guides/creating-a-datapack
---

# Creating a DataPack

A DataPack in Kore represents a Minecraft datapack that contains custom game data and resources.

If you already maintain large hand-written datapacks and want migration/architecture patterns rather than basics, read
[From Datapacks to Kore](/docs/guides/from-datapacks-to-kore).

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
  path("%appdata%/.minecraft/saves/my_world/datapacks")
}
```

## Separating the Output Folder Name from the Namespace

By default the output folder/archive is named after the datapack's `name`, which is also the default namespace used by
every function and generator that doesn't set an explicit `namespace`. Sometimes you need those to differ, for example
when plugging Kore into a modded Fabric project's datagen step: Fabric Loom expects generated resources under a fixed
`generated` directory, but the resource namespace inside should be your mod ID, not the literal string `generated`.

Set `folderName` to decouple the two:

```kotlin
dataPack("mymod") {
	folderName("generated")
	path("src/main")

	predicate("always_true")
}.generate()
// -> src/main/generated/pack.mcmeta
// -> src/main/generated/data/mymod/predicate/always_true.json
```

The output folder (and `.zip`/`.jar` file name) becomes `generated`, while every resource still defaults to the `mymod`
namespace. Individual functions/generators can still override their own `namespace` as before.

## Adding Icon

To add an icon to the datapack, use the `iconPath` function:

```kotlin
dataPack("my_datapack") {
  iconPath("icon.png")
}
```

## Configuration

See [Configuration](/docs/guides/configuration)

## Pack Metadata

The `dataPack` function generates a `pack.mcmeta` file containing metadata about the datapack.

Configure this metadata using the `pack` block:

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat(94)
		maxFormat(94)
		description = textComponent("My Datapack")
	}
}
```

- `minFormat` - The minimum supported pack format version.
- `maxFormat` - The maximum supported pack format version.
- `description` - A text component for the datapack description.

`minFormat` and `maxFormat` are shortcut functions that accept the same arguments as `packFormat()`:

```kotlin
pack {
	minFormat(94)        // plain integer
	minFormat(94, 0)     // [major, minor] pair
	maxFormat(94)
}
```

You can also assign a `PackFormat` value directly:

```kotlin
pack {
	minFormat = packFormat(94)
	maxFormat = packFormat(94)
}
```

### Legacy `pack_format` and `supportedFormats`

`pack.mcmeta`'s `pack_format` field is always a plain integer, even on modern Minecraft versions - some
third-party tools (e.g. Modrinth) still read it for compatibility. Kore includes it by default, defaulting to the
current Minecraft version's pack format as a plain integer, unless you set `packFormat = null` yourself.

If your `minFormat` is below the threshold (82 for DataPacks, 65 for ResourcePacks), Kore additionally includes the
legacy `supported_formats` field in the generated `pack.mcmeta` file to ensure compatibility with older versions of
Minecraft.

You can override `packFormat` explicitly - it only accepts a plain integer (`PackFormatMajor`); assigning a
`[major, minor]` pair or a decimal value triggers a warning:

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat(94)
		maxFormat(94)
		packFormat = packFormat(94) // must stay a plain integer
	}
}
```

You can also set `supportedFormats` explicitly with a small DSL:

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat(48)
		maxFormat(60)
		description = textComponent("My Datapack")

		supportedFormats(48..60)
		supportedFormats(min = 48) // max is optional
	}
}
```

### Targeting Minecraft 1.21.9+

Starting with Minecraft 1.21.9 (25w31a), `min_format` and `max_format` are the primary fields in `pack.mcmeta`, and
can use a `[major, minor]` pair to target snapshots or minor versions:

```kotlin
dataPack("my_datapack") {
	pack {
		minFormat(94, 1)
		maxFormat(94, 1)
		description = textComponent("Targeting 1.21.9")
	}
}
```

Note that `minFormat`, `maxFormat`, and `packFormat` do not accept decimal values - use a plain integer or a
`[major, minor]` pair for `minFormat`/`maxFormat`, and a plain integer for `packFormat`. `PackFormatDecimal` exists
only for parsing legacy third-party `pack.mcmeta` files that used a decimal `pack_format`; don't set it yourself.

## Overlays

Overlays allow you to apply different resources depending on the pack format version of the client. Use the `overlays`
DSL to declare
overlay entries:

```kotlin
dataPack("my_datapack") {
	overlays {
		entry("my_overlay") {
			minFormat(82)
			maxFormat(93)
		}
	}
}
```

Each `entry` takes a directory name and a block where you configure `minFormat` and `maxFormat` using the same shortcut
functions as in the `pack` block.

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
To change the output folder, use the `path` function:

```kotlin
dataPack("my_datapack") {
  path("%appdata%/.minecraft/saves/my_world/datapacks")
}.generate()
```

### Zip Generation

To generate a zip file of the datapack, use the `generateZip` function:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateZip()
```

Generated ZIP entries follow the ZIP specification and always use forward slashes (`/`) internally.
This keeps the archive compatible with strict tools such as Windows Explorer in addition to WinRAR and other archive
managers.

### Jar Generation

To generate a JAR file for your datapack, use the `generateJar` function.
This function packages the datapack into a JAR file which can then be used directly with your Minecraft installation or
distributed for others to use.

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar()
```

By calling `generateJar()`, the generated JAR file will be placed in the default output folder.
If you wish to specify a different location, use the `path` function:

```kotlin
dataPack("my_datapack") {
  path("path/to/output/folder")
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
Kore creates the temporary directory used for extraction automatically before unzipping, then merges the extracted
files with the generated datapack. This temporary folder is not removed automatically.

#### Checking for compatibility

When merging with other datapacks, Kore will check if the pack format range overlaps. If it does not,
it will print a warning message.

Example:

```kotlin
val myDatapack1 = dataPack("my_datapack 1") {
	// datapack code here

	pack {
		minFormat(40)
		maxFormat(40)
	}
}

val myDatapack2 = dataPack("my_datapack 2") {
	// datapack code here
	pack {
		minFormat(50)
		maxFormat(50)
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

## What to read next

- [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore) - migration strategy, architecture choices, and
  production workflow for advanced authors
- [Cookbook](/docs/guides/cookbook) - practical patterns once your pack structure is in place
- [Functions](/docs/commands/functions) - reusable logic, tags, and generated functions
- [Selectors](/docs/concepts/selectors) - target entities and players with typed filters
- [Configuration](/docs/guides/configuration) - tune JSON formatting and generation behavior

## Publishing and Distribution

Once you've generated your datapack, you may want to distribute it to the community.
For automated publishing to platforms like Modrinth, CurseForge, and GitHub Releases,
see the [GitHub Actions Publishing](/docs/advanced/github-actions-publishing) guide.

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

