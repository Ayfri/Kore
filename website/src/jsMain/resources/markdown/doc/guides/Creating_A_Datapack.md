---
root: .components.layouts.MarkdownLayout
title: Creating a Minecraft Datapack with Kore - Metadata, Output & Packaging
nav-title: Creating a Datapack
description: Shape a Kore datapack end to end - namespace, output folder, pack.mcmeta, overlays and filters, then generate a folder, ZIP or a mod JAR for Fabric or NeoForge.
keywords: minecraft datapack, kore datapack, pack.mcmeta, pack format, datapack zip, datapack jar, fabric datapack mod, datapack overlays, datapack namespace, generate datapack
date-created: 2024-02-26
date-modified: 2026-08-05
routeOverride: /docs/guides/creating-a-datapack
position: 1
---

# Creating a Datapack

Everything in Kore hangs off one object: the `DataPack`. It holds your namespace, your metadata, every function and
data-driven resource you declare, and it decides what lands on disk.

This page is about the **shell** around your gameplay code - naming, metadata, packaging - in the order you actually
need it:

1. [Declaring the pack](#declaring-the-pack) and choosing where it is written.
2. [Pack metadata](#pack-metadata) - `pack.mcmeta`, pack formats, overlays, filters.
3. [Adding content](#adding-content) - the one-line version, with links to the real reference pages.
4. [Generating output](#generating-output) - folder, ZIP, mod JAR, and merging with other packs.

For the gameplay code itself, see [Functions](/docs/commands/functions) and
[Commands](/docs/commands/commands). If you already maintain large hand-written datapacks and want migration and
architecture patterns instead of basics, read [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore).

## Declaring the pack

`dataPack` builds the pack; `generate()` writes it. Nothing is written to disk until you call one of the generation
functions.

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate()
```

The name you pass does two things: it becomes the **output folder name** and the **default namespace** for every
function and resource that does not set its own.

### Output folder

`path` sets the base directory that generation writes into. It defaults to `out`.

```kotlin
dataPack("my_datapack") {
	path("%appdata%/.minecraft/saves/my_world/datapacks")
}.generate()
```

Pointing `path` straight at a world's `datapacks` folder is the fastest development loop: regenerate, then `/reload`
in-game.

### Separating the folder name from the namespace

Sometimes the folder and the namespace must differ. The usual case is plugging Kore into a modded Fabric project's
datagen step: Fabric Loom expects generated resources under a fixed `generated` directory, but the resource namespace
inside should be your mod ID, not the literal string `generated`.

`folderName` decouples the two:

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
namespace. Individual functions and generators can still override their own `namespace` as before.

### Icon

`iconPath` points at the `pack.png` shown in the datapack list:

```kotlin
dataPack("my_datapack") {
	iconPath("icon.png")
}
```

### Serialization settings

How the JSON is formatted, and where Kore puts the functions it generates for you, is controlled by a separate
`configuration { }` block - see [Configuration](/docs/guides/configuration).

## Pack metadata

Every datapack needs a `pack.mcmeta`. Kore always generates one; the `pack` block configures it.

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat(94)
		maxFormat(94)
		description = textComponent("My Datapack")
	}
}
```

- `minFormat` - the minimum supported pack format version.
- `maxFormat` - the maximum supported pack format version.
- `description` - a text component for the datapack description.

`minFormat` and `maxFormat` are shortcut functions accepting the same arguments as `packFormat()`:

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

### Targeting Minecraft 1.21.9+

Since Minecraft 1.21.9 (25w31a), `min_format` and `max_format` are the primary fields in `pack.mcmeta`, and accept a
`[major, minor]` pair to target snapshots or minor versions:

```kotlin
dataPack("my_datapack") {
	pack {
		minFormat(94, 1)
		maxFormat(94, 1)
		description = textComponent("Targeting 1.21.9")
	}
}
```

`minFormat`, `maxFormat` and `packFormat` do not accept decimal values - use a plain integer or a `[major, minor]` pair
for `minFormat`/`maxFormat`, and a plain integer for `packFormat`. `PackFormatDecimal` exists only for parsing legacy
third-party `pack.mcmeta` files that used a decimal `pack_format`; do not set it yourself.

### Legacy `pack_format` and `supportedFormats`

`pack.mcmeta`'s `pack_format` field is always a plain integer, even on modern Minecraft versions - some third-party
tools (Modrinth, for example) still read it for compatibility. Kore includes it by default, using the current Minecraft
version's pack format, unless you set `packFormat = null` yourself.

If your `minFormat` is below the threshold (82 for datapacks, 65 for resource packs), Kore additionally writes the
legacy `supported_formats` field so older Minecraft versions still accept the pack.

Overriding `packFormat` explicitly only accepts a plain integer (`PackFormatMajor`); a `[major, minor]` pair or a
decimal value triggers a warning:

```kotlin
dataPack("mydatapack") {
	pack {
		minFormat(94)
		maxFormat(94)
		packFormat = packFormat(94) // must stay a plain integer
	}
}
```

`supportedFormats` can also be set explicitly:

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

### Overlays

Overlays let one pack ship different resources per client pack format - the standard way to support several Minecraft
versions from a single download. Declare each overlay directory with `entry`:

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

Each `entry` takes a directory name and a block configuring `minFormat`/`maxFormat` with the same shortcut functions as
the `pack` block. Clients in that format range load the overlay directory on top of the base pack.

### Filters

A filter tells Minecraft to **ignore** resources coming from packs applied *below* yours - the vanilla way to disable
data from another datapack (or from vanilla itself) rather than trying to override it. Any file matching a blocked
pattern is treated as if it were not present at all.

Each `block` entry takes a `namespace` and a `path`, both optional and both interpreted as regexes. Omitting one means
"match anything":

```kotlin
dataPack("my_datapack") {
	filter {
		block(namespace = "minecraft", path = "recipe/.*_boat.json")
		block(path = "loot_table/blocks/stone.*")
	}
}
```

Here every vanilla boat recipe, and every stone-related block loot table from lower packs, stop existing for the game.
`block` also accepts a `FilteredBlock` or a builder block, and `blocks(...)` adds several at once.

Careful: this is not scoped to your namespace. `block(path = ".*")` with no namespace disables the entire data of every
pack below yours.

## Adding content

Everything else in the `dataPack { }` block is content, declared through the builder matching the resource you want:
`function`, `advancement`, `lootTable`, `recipes`, `predicate`, `enchantment`, `biome`, and so on.

```kotlin
dataPack("my_datapack") {
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

Each builder has its own reference page - start from [Recipes](/docs/data-driven/recipes),
[Loot Tables](/docs/data-driven/loot-tables), [Predicates](/docs/data-driven/predicates), or
[Functions](/docs/commands/functions).

## Generating output

Three generation functions, three shapes. All of them write into `path` (default `out`).

| Call | Produces | Use it for |
|------|----------|------------|
| `generate()` | `<path>/<name>/` folder | development, reviewable diffs, CI checks |
| `generateZip()` | `<path>/<name>.zip` | distributing a normal datapack |
| `generateJar()` | `<path>/<name>.jar` | shipping the pack as a mod |

### Folder

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate()
```

Best default while developing: you can open the generated `.mcfunction` and JSON files and check exactly what Kore
emitted.

### ZIP

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateZip()
```

Same contents as the folder, in one archive - what you upload for players to install. Generated ZIP entries follow the
ZIP specification and always use forward slashes (`/`) internally, keeping the archive readable by strict tools such as
Windows Explorer as well as WinRAR and other archive managers.

### Mod JAR

`generateJar()` packages the datapack as a **mod**, so players install it in `mods/` instead of per-world `datapacks/`:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generateJar()
```

The JAR lands in the same output folder; `path` moves it like for the other modes. On its own, `generateJar()` produces
a bare archive - you also declare a loader block to get the metadata that loader needs.

#### Fabric

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

This writes the Fabric mod metadata with the version, contact information, and author.

To make individual resources load conditionally inside a Fabric mod (gated on other mods, tags, registries, or feature
flags), see [Fabric Resource Conditions](/docs/guides/fabric-resource-conditions).

#### Forge

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

#### Quilt

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

#### NeoForge

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

### Merging with existing datapacks

`mergeWithDatapacks` folds other packs into the generated output, so you can ship one download that also contains a
dependency or a pack you do not own the source of:

```kotlin
dataPack("my_datapack") {
	// datapack code here
}.generate {
	mergeWithDatapacks("existing_datapack 1", "existing_datapack 2")
}
```

A path to a zip is treated as a datapack and merged too. Kore creates the temporary directory used for extraction
automatically before unzipping, then merges the extracted files with the generated datapack. **This temporary folder is
not removed automatically.**

#### Pack format compatibility

When merging, Kore checks whether the pack format ranges overlap and warns when they do not:

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

Prints:

```
The pack format range of the other pack is different from the current one. This may cause issues.
Format range: current: 40..40 other: 50..50.
```

`supportedFormats` is checked the same way, with a warning when the other pack is not supported.

#### Lifecycle tags are merged, not overwritten

Both packs' `minecraft:load` and `minecraft:tick` function tags are combined, so nothing silently loses its entry
point:

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

The resulting `load.json` contains both:

```json
{
	"replace": false,
	"values": [
		"my_datapack_1:generated_scope/my_main_function",
		"my_datapack_2:generated_scope/load"
	]
}
```

## What to read next

- [Configuration](/docs/guides/configuration) - JSON formatting and generated function naming
- [Functions](/docs/commands/functions) - reusable logic, lifecycle tags, and generated functions
- [Cookbook](/docs/guides/cookbook) - practical patterns once your pack structure is in place
- [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore) - migration strategy and production workflow
- [Fabric Resource Conditions](/docs/guides/fabric-resource-conditions) - load resources conditionally in a Fabric mod
- [GitHub Actions Publishing](/docs/advanced/github-actions-publishing) - automate releases to Modrinth, CurseForge and
  GitHub
