---
root: .components.layouts.MarkdownLayout
title: Bindings
nav-title: Bindings
description: Import existing datapacks and generate Kotlin bindings.
keywords: kore, bindings, import, datapack, github, modrinth, curseforge
date-created: 2026-01-23
date-modified: 2026-04-21
routeOverride: /docs/advanced/bindings
position: 3
---

# Bindings (Import Existing Datapacks)

> [!WARNING]
> The bindings module is **experimental**. APIs and generated output may change without notice.

The bindings module lets you import existing datapacks and generates Kotlin types so you can reference functions, resources, and tags safely in your code.

## Install this module

Add the `bindings` artifact when you want to import an existing datapack and generate Kotlin bindings from it.

### Artifact

```kotlin
dependencies {
	implementation("io.github.ayfri.kore:bindings:VERSION")
}
```

### Best fit

- Use `kore` for the main DSL.
- Add `bindings` when you want type-safe access to resources coming from an external datapack.
- Combine it with `oop` or `helpers` only if your project also needs those higher-level utilities.

## Quick start

1. Add the dependency.
2. Import `importDatapacks`.
3. Configure the output directory and package prefix.
4. Declare at least one source pack.

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
}
```

This example generates bindings into `src/main/kotlin` under the `kore.dependencies` package. If you need another
source, replace `github(...)` with `modrinth(...)`, `curseforge(...)`, or `url(...)`.
Those are explained below in the [Download sources](#download-sources) section.

## Using generated bindings

When you import a datapack, Kore generates a `data object` with a name derived from the datapack's name (e.g., `VanillaRefresh`).

### Accessing resources

Resources are organized by namespace and type. If a datapack has only one namespace, resources are accessible directly:

```kotlin
import kore.dependencies.vanillarefresh.VanillaRefresh

function("my_function") {
	// Call a function from the imported datapack
	function(VanillaRefresh.Functions.MAIN_TICK)

	// Reference a loot table (see Commands for loot usage)
	loot(VanillaRefresh.LootTables.BLOCKS.IRON_ORE)
}
```

For more on using commands with imported resources, see [Commands](/docs/commands/commands).

If the datapack uses multiple namespaces, they are nested:

```kotlin
VanillaRefresh.Minecraft.LootTables.CHESTS.ABANDONED_MINESHAFT
```

### Tags

Tags are also imported and can be used wherever a tag of that type is expected:

```kotlin
function("my_function") {
	// Wrap the imported tag in an item predicate so `execute` can read it.
	execute {
		ifCondition {
			// Check if the nearest player has the tagged item in their cursor.
			items(nearestPlayer(), PLAYER.CURSOR, itemPredicate {
				itemArgument = VanillaRefresh.Tags.Items.MY_CUSTOM_TAG
			})
		}
	}
}
```

### Pack metadata

The generated object also contains information about the original datapack:

```kotlin
val path = VanillaRefresh.PATH // Path to the source file/folder
val packMeta = VanillaRefresh.pack // PackSection object from pack.mcmeta
```

When pack metadata is reconstructed in generated bindings, Kore emits the `PackSection` using the same helpers as
handwritten code, including `packFormat(...)` for version formats and `SupportedFormats(...)` when legacy supported
formats are present.

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

For higher API rate limits (or when your environment requires authenticated GitHub API access), set
`GITHUB_API_KEY` as an environment variable or `github.api.key` as a system property.

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

Downloads from CurseForge. Requires the `CURSEFORGE_API_KEY` environment variable or the `curseforge.api.key` system property

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

```kotlin
url("https://example.com/pack.zip")
```

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
	packageName = "custom.pkg"    // Change the package for this pack
	subPath = "datapacks/main"    // Only import from this subfolder
	includes = listOf("data/**")  // Only include files matching these patterns
	excludes = listOf("**/test/**") // Exclude files matching these patterns
	body("{\"token\":\"abc123\"}") // Optional HTTP request body for url("https://...") sources
	header("Authorization", "Bearer your-token") // Add one request header for url("https://...")
	headers(mapOf("Accept" to "application/zip")) // Replace all request headers for url("https://...")

	remappings {
		objectName("MyPack")                           // Change the generated object name
		namespace("old_namespace", "NewNamespace")     // Rename a specific namespace object
	}
}
```

> [!NOTE]
> The `remappedName` property is deprecated. Use `remappings { objectName("...") }` instead.

For HTTP(S) sources declared with `url("https://...")`, you can customize request payload and headers from the
per-datapack block with `body("...")`, `header("key", "value")`, and `headers(mapOf(...))`.

### Namespace normalization

Namespace names are automatically normalized when generating Kotlin object names: dots (`.`) and other
non-alphanumeric characters are replaced with underscores, then converted to PascalCase. For example,
`my.namespace` becomes `MyNamespace`. You can override this behavior for any namespace using the
`remappings {}` block described above.

## Cache

Downloaded files are cached to speed up subsequent runs in a different directory depending on your OS:
- On Windows, `LOCALAPPDATA` or `~/AppData/Local/kore`.
- On macOS, `~/Library/Caches`.
- On Linux, XDG cache directory or fallback to `~/.cache/kore/datapacks`.
This can be overridden by the `KORE_CACHE_HOME` environment variable or the `kore.cache.home` system property.
Use `skipCache = true` in the global configuration or delete the cache folder to force a re-download.

## Troubleshooting

- **Rate Limits**: GitHub API is limited for unauthenticated calls.
- **CurseForge API**: Ensure your API key is valid and has permissions for the project.
- **Invalid resources**: If a resource type is unknown to Kore, it will be skipped to avoid generating invalid code.
