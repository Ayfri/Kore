---
root: .components.layouts.MarkdownLayout
title: Bindings
nav-title: Bindings
description: Import existing datapacks and generate Kotlin bindings.
keywords: kore, bindings, import, datapack, github, modrinth, curseforge
date-created: 2026-01-23
date-modified: 2026-01-24
routeOverride: /docs/commands/bindings
position: 3
---

# Bindings (Import Existing Datapacks)

> [!WARNING]
> The bindings module is **experimental**. APIs and generated output may change without notice.

The bindings module lets you import existing datapacks and generates Kotlin types so you can reference functions, resources, and tags safely in your code.

## Add the module

If you are working in a multi-module setup:

```kotlin
dependencies {
	implementation("io.github.ayfri.kore:bindings:<VERSION>")
}
```

## Quick start

```kotlin
import io.github.ayfri.kore.bindings.api.importDatapacks

importDatapacks {
	configuration {
		outputPath("src/main/kotlin")
		packagePrefix = "kore.dependencies"
	}

	github("pixigeko.minecraft-default-data:1.21.8") {
		subPath = "data"
	}

	modrinth("vanilla-refresh")
	curseforge("repurposed-structures-illager-invasion-compat")
	url("https://example.com/pack.zip")
}
```

## Using generated bindings

When you import a datapack, Kore generates a `data object` with a name derived from the datapack's name (e.g., `VanillaRefresh`).

### Accessing resources

Resources are organized by namespace and type. If a datapack has only one namespace, resources are accessible directly:

```kotlin
import kore.dependencies.vanillarefresh.VanillaRefresh

function("my_function") {
	// Call a function from the imported datapack
	function(VanillaRefresh.Functions.MAIN_TICK)

	// Reference a loot table
	loot(VanillaRefresh.LootTables.BLOCKS.IRON_ORE)
}
```

If the datapack uses multiple namespaces, they are nested:

```kotlin
VanillaRefresh.Minecraft.LootTables.CHESTS.ABANDONED_MINESHAFT
```

### Tags

Tags are also imported and can be used wherever a tag of that type is expected:

```kotlin
function("my_function") {
	// Check if the item is in an imported item tag
	execute {
		`if`(entity(self), items(VanillaRefresh.Tags.Items.MY_CUSTOM_TAG))
	}
}
```

### Pack metadata

The generated object also contains information about the original datapack:

```kotlin
val path = VanillaRefresh.PATH // Path to the source file/folder
val packMeta = VanillaRefresh.pack // PackSection object from pack.mcmeta
```

## Generated structure

For each imported datapack, a Kotlin file is generated containing:

- A `data object` named after the datapack.
- Nested `data object`s for each namespace (if multiple).
- Nested `enum`s or `object`s for each resource type:
	- `Functions`: All `.mcfunction` files.
	- `Advancements`: All advancements.
	- `LootTables`: All loot tables.
	- `Recipes`: All recipes.
	- `Tags`: All tags, further nested by type (`Blocks`, `Items`, `Functions`, etc.).
	- `Worldgen`: All worldgen resources, further nested by type (`Biomes`, `Structures`, etc.).

Subfolders in the datapack are preserved as nested objects.

## Explore without generating

You can explore the content of a datapack programmatically without generating any code.

```kotlin
import io.github.ayfri.kore.bindings.api.exploreDatapacks

val datapacks = exploreDatapacks {
	github("user.repo:main")
}

val pack = datapacks.first()
println("Datapack: ${pack.name}")
println("Functions: ${pack.functions.size}")
pack.functions.forEach { println(" - ${it.id}") }
```

## Download sources

### GitHub

Downloads a repository or a specific asset from a release.

Patterns:

- `user.repo`: Latest commit on the default branch.
- `user.repo:tag`: Specific tag, branch, or commit.
- `user.repo:tag:asset.zip`: Specific asset from a release.

```kotlin
github("pixigeko.minecraft-default-data:1.21.8")
```

### Modrinth

Downloads the latest or a specific version of a Modrinth project.

Patterns:

- `slug`: Latest stable version.
- `slug:version`: Specific version ID or number.

```kotlin
modrinth("vanilla-refresh")
```

### CurseForge

Downloads from CurseForge. Requires the `CURSEFORGE_API_KEY` environment variable.

Patterns:

- `projectId`: Latest file for the project.
- `projectId:fileId`: Specific file.
- `slug`: Project slug.
- `slug:fileId`: Specific file for a project slug.
- `URL`: Full CurseForge project URL.

```kotlin
curseforge("418120") // Project ID
```

### URL / local path

Patterns:

- `https://example.com/pack.zip`
- `./path/to/pack` (Local folder)
- `./path/to/pack.zip` (Local zip)

## Configuration

### Global configuration

Defined in the `configuration {}` block:

| Property             | Default                         | Description                                      |
|----------------------|---------------------------------|--------------------------------------------------|
| `outputPath`         | `build/generated/kore/imported` | Directory where Kotlin files will be generated.  |
| `packagePrefix`      | `kore.dependencies`             | Base package for all generated bindings.         |
| `generateSingleFile` | `true`                          | If true, generates one Kotlin file per datapack. |
| `skipCache`          | `false`                         | If true, re-downloads even if already in cache.  |
| `debug`              | `false`                         | Prints extra information during the process.     |

### Per-datapack configuration

Defined in the block following a source:

```kotlin
github("user.repo") {
	remappedName = "MyPack"       // Change the generated object name
	packageName = "custom.pkg"    // Change the package for this pack
	subPath = "datapacks/main"    // Only import from this subfolder
	includes = listOf("data/**")  // Only include files matching these patterns
	excludes = listOf("**/test/**") // Exclude files matching these patterns
}
```

## Cache

Downloaded files are cached in `~/.kore/cache/datapacks` to speed up subsequent runs. Use
`skipCache = true` in the global configuration or delete the cache folder to force a re-download.

## Troubleshooting

- **Rate Limits**: GitHub API is limited for unauthenticated calls.
- **CurseForge API**: Ensure your API key is valid and has permissions for the project.
- **Invalid resources**: If a resource type is unknown to Kore, it will be skipped to avoid generating invalid code.
