---
root: .components.layouts.MarkdownLayout
title: Kore Configuration - Datapack Settings, Pack Format & Output Options
nav-title: Configuration
description: Configure your Minecraft datapack output with Kore. Set pack_format, Minecraft version, description, namespace, pretty printing, ZIP/JAR export, and resource pack integration.
keywords: minecraft datapack pack_format, pack_format datapack, datapack configuration, kore settings, datapack output, datapack namespace, pack format minecraft, datapack zip export, datapack description, kore configure
date-created: 2024-04-06
date-modified: 2026-07-02
routeOverride: /docs/guides/configuration
position: 2
---

# DataPack configuration

The `configuration { }` block on a [DataPack](https://kore.ayfri.com/docs/guides/creating-a-datapack) controls how Kore
serializes JSON (and related formats) and where **generated** functions live. Output **location** and **archive shape**
are chosen when you call `generate()`, `generateZip()`, or `generateJar()`.

## Example

```kotlin
dataPack("mypack") {
	configuration {
		prettyPrint = true
		prettyPrintIndent = "  "
	}

	// ... rest of datapack code
}
```

## `prettyPrint`

When `prettyPrint` is `true`, Kore’s shared `Json` encoder formats JSON resources (advancements, tags, recipes, worldgen
JSON, `pack.mcmeta`, etc.) with line breaks and indentation. When `false`, output is compact (smaller files, slightly
faster I/O).

- Default: `false` (release-friendly; smaller packs).
- Set `true` when you want readable diffs in version control or easier manual inspection while developing.

`prettyPrint` does not change game behavior; it only affects on-disk JSON layout.

## Indentation (`prettyPrintIndent`)

`prettyPrintIndent` is the indent string passed to Kotlin serialization when `prettyPrint` is enabled. The default is a
single tab (`"\t"`). Common choices are `"\t"`, `"  "`, or `"    "`.

For **TOML** serialization (used for some generated files), Kore maps the indent string
to [ktoml](https://github.com/akuleshov7/ktoml) styles: tab, two spaces, and four spaces get proper TOML indentation;
other values fall back to no extra indentation for TOML output. Matching your JSON indent to one of those three keeps
JSON and TOML visually consistent.

## Generated function folder (`generatedFunctionsFolder`)

Kore may emit **generated** `.mcfunction` files (for example when `execute` chains are lowered into separate functions).
Those files are placed under:

`data/<namespace>/function/<generatedFunctionsFolder>/...`

The default folder name is `generated_scopes` (see `DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER` in the Kore source).
Change `generatedFunctionsFolder` if you want a different directory name (shorter paths, naming that matches your
project, or avoiding clashes with hand-written `function/` trees).

## Comments on generated function calls (`generateCommentOfGeneratedFunctionCall`)

When Kore inserts a call to a newly generated function from an `execute` block, it can add a **comment line** in the
calling function documenting that call, for example:

`# Generated function namespace:path/to/caller`

This is controlled by `generateCommentOfGeneratedFunctionCall`. Default: `false`. Turn it on while debugging or learning
generated control flow; turn it off for minimal `.mcfunction` output in releases.

## Where files go (`path`) and generation modes

- **`dataPack.path`** (default: `out`): base directory for generation. The generator resolves packs relative to this
  path (see below).
- **`generate()`** writes an **unzipped folder**: `<path>/<datapack.name>/` with `pack.mcmeta`, `data/`, etc. Best for
  pointing Minecraft at a dev folder, CI checks, or tools that expect a plain tree.
- **`generateZip()`** writes **`<path>/<datapack.name>.zip`**. Same contents as the folder layout, in one archive.
  Convenient for distribution and often faster for the game to load than thousands of loose files.
- **`generateJar()`** writes **`<path>/<datapack.name>.jar`**. The comment on the API states this is intended for use *
  *as a mod**: JAR generation runs optional **providers** (Fabric, Forge, NeoForge, Quilt, etc.) to add loader metadata
  and package the datapack accordingly. Do not expect a plain datapack ZIP renamed to `.jar` unless you only need the
  archive format without mod infrastructure.

`generate()` and `generateZip()` accept `DataPackGenerationOptions` (for example `mergeWithPacks`). `generateJar()` uses
`DataPackJarGenerationOptions`, which supports the same merge list plus loader-specific configuration.

## Development vs release-oriented setups

A practical split:

| Concern                                  | Development                                                               | Release                                                                            |
|------------------------------------------|---------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| `prettyPrint`                            | `true` for readable JSON                                                  | `false` for smaller files                                                          |
| `generateCommentOfGeneratedFunctionCall` | `true` if comments help tracing                                           | `false` for lean functions                                                         |
| Output                                   | `generate()` to a folder under `path`, or `generateZip()` for quick share | `generateZip()` for vanilla datapacks; `generateJar()` only when shipping as a mod |
| `path`                                   | e.g. a world `datapacks` folder or `./build/datapack`                     | your build output directory or CI artifact path                                    |

You can keep one `dataPack { }` definition and branch configuration with build constants, or use separate `main`/`debug`
entry points, depending on your Gradle setup.

## Reference

| Option                                   | Description                                                        | Default              |
|------------------------------------------|--------------------------------------------------------------------|----------------------|
| `generateCommentOfGeneratedFunctionCall` | Insert a comment when calling a generated function from `execute`. | `false`              |
| `generatedFunctionsFolder`               | Subfolder under `function/` for generated `.mcfunction` files.     | `"generated_scopes"` |
| `prettyPrint`                            | Pretty-print JSON resources.                                       | `false`              |
| `prettyPrintIndent`                      | Indent string when pretty-printing JSON.                           | `"\t"`               |

Configuring a datapack is especially useful for debugging and for tuning pack size and readability in production.
