---
root: .components.layouts.MarkdownLayout
title: Kore Configuration - JSON Formatting & Generated Function Options
nav-title: Configuration
description: Tune how Kore serializes your datapack - pretty-printed JSON, indentation, the generated functions folder, and debug comments on generated function calls.
keywords: kore configuration, datapack pretty print, datapack json formatting, generated functions folder, kore settings, minecraft datapack debug, generated_scopes, mcfunction output
date-created: 2024-04-06
date-modified: 2026-08-05
routeOverride: /docs/guides/configuration
position: 2
---

# DataPack configuration

The `configuration { }` block controls **how Kore writes** your pack: JSON formatting, and the naming of the functions
Kore generates on your behalf. It has four options, all listed at the bottom of this page.

It does **not** control *where* output goes or *what shape* it takes - that is `path` plus your choice of `generate()`,
`generateZip()` or `generateJar()`, covered in [Creating a Datapack](/docs/guides/creating-a-datapack).

None of these settings change in-game behavior. They exist to make development and debugging easier, and releases
leaner.

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

## Development vs release setups

The defaults are release-oriented (compact JSON, no comments). While developing, flipping both booleans makes the output
far easier to read:

| Concern | Development | Release |
|---------|-------------|---------|
| `prettyPrint` | `true` for readable JSON and clean git diffs | `false` for smaller files |
| `generateCommentOfGeneratedFunctionCall` | `true` to trace where a generated function came from | `false` for lean `.mcfunction` output |
| Output mode | `generate()` into a world's `datapacks` folder | `generateZip()`, or `generateJar()` when shipping as a mod |

Keep one `dataPack { }` definition and branch on a build constant, or use separate `main`/`debug` entry points,
depending on your Gradle setup:

```kotlin
val debug = System.getenv("KORE_DEBUG") != null

dataPack("mypack") {
	configuration {
		prettyPrint = debug
		generateCommentOfGeneratedFunctionCall = debug
	}
}
```

## Reference

| Option                                   | Description                                                        | Default              |
|------------------------------------------|--------------------------------------------------------------------|----------------------|
| `generateCommentOfGeneratedFunctionCall` | Insert a comment when calling a generated function from `execute`. | `false`              |
| `generatedFunctionsFolder`               | Subfolder under `function/` for generated `.mcfunction` files.     | `"generated_scopes"` |
| `prettyPrint`                            | Pretty-print JSON resources.                                       | `false`              |
| `prettyPrintIndent`                      | Indent string when pretty-printing JSON.                           | `"\t"`               |

## What to read next

- [Creating a Datapack](/docs/guides/creating-a-datapack) - metadata, output location, and packaging
- [Functions](/docs/commands/functions) - what produces the generated functions this page names
- [Execute](/docs/commands/execute) - the main source of generated scope functions
