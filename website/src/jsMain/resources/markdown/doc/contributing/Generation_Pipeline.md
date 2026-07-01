---
root: .components.layouts.MarkdownLayout
title: "Contributing: The Generation Pipeline"
nav-title: "Generation Pipeline"
description: How the generation module downloads Minecraft source data and produces the enums and argument types under kore/src/main/generated.
keywords: arguments, codegen, contributing, generation, kore, registries
date-created: 2026-07-01
date-modified: 2026-07-01
routeOverride: /docs/contributing/generation-pipeline
---

# Contributing: The Generation Pipeline

`generation/` is a standalone Kotlin/JVM app, not a library other modules depend on. Running it writes Kotlin files
straight into [`kore/src/main/generated`][kore-generated] - it is the *only* thing allowed to touch that folder.

## What it does

`generation/src/main/kotlin/Main.kt` runs, in order:

1. Clear `kore/src/main/generated` and (with `--reload-cache`) the download cache.
2. Download datapacks, the default datapack version, gamerules and item component types.
3. Run every simple generator ([`launchAllSimpleGenerators`][generators-kt]) - lists and registries that become
   enums or enum trees.
4. Run the argument type generator ([`launchArgumentTypeGenerators`][arguments-kt]) - registries that become typed
   `*Argument` / `*OrTagArgument` / `*TagArgument` interfaces.
5. Write the resolved Minecraft version into the generated package.

Source data is fetched from the [`PixiGeko/Minecraft-generated-data`][source-repo] GitHub repo, pinned to the
`minecraft.version` in `gradle.properties`. Downloads are cached under `generation/build/cache`; pass
`--reload-cache` to force a re-download after a Minecraft version bump.

## Adding a new generated list or registry

Most new registries only need an entry in [`generators/generators.kt`][generators-kt], inside `lists` (plain
resource lists, e.g. `loot_table`) or `registries` (vanilla registries, e.g. `item`):

```kotlin
gen("TradeSets", "trade_set")
```

`gen(name, fileName) { ... }` ([`generators/Generator.kt`][generator-kt]) takes the enum name and the upstream file
name, and exposes a small builder for the common cases:

- `argumentClassName` - override the generated `*Argument` type name when it should differ from `name` (for example
  `"Model M"` routes the argument to `arguments/types/resources` instead of the generated tree - see the
  `M`-suffix rule in [Contributing: Creating a New Generator][new-generator]).
- `transform { ... }` - strip a suffix/prefix from raw entries (`.json`, `.ogg`, `minecraft:`, ...).
- `enumTree` / `separator` - force or configure a path-based enum tree instead of a flat enum, for entries that
  contain `/`.
- `extractEnums(...)` - split out a sub-enum from entries sharing a prefix.
- `tagsParents(...)` / `subInterfacesParents(...)` - map resource folders or sub-namespaces to their parent
  argument type, used by the `Tags` and `Textures` generators.

Whether an entry lands in `lists` or `registries` only changes which upstream `.txt` folder it reads from
(`custom-generated/lists/...` vs `custom-generated/registries/...`); both feed the same enum/enum-tree codegen.

Registries that need a typed argument (referenced from other DSL builders, taggable, etc.) are handled separately by
`launchArgumentTypeGenerators()` in [`generators/arguments.kt`][arguments-kt] - it reads the registry list straight
from the datapack report (`minecraft-generated/reports/datapack.json`) rather than from `generators.kt`, so a
vanilla registry usually needs no manual entry at all. `additionalTypes`/`ignoreList` in that file are only for
registries the report doesn't expose or that already have a hand-written model (`block`, `item`, `tag`).

## Running it

```
./gradlew :generation:run
```

Add `--args='--reload-cache'` after bumping the Minecraft version to invalidate the download cache.

Once it finishes, diff `kore/src/main/generated` and `kore/src/main/kotlin/io/github/ayfri/kore/generated` (the
`Argument`-type files) to confirm the new registry produced the expected enum/argument shape before writing any DSL
code against it.

## See also

- [Contributing: Architecture and Patterns][architecture]
- [Contributing: Creating a New Generator][new-generator]

[architecture]: /docs/contributing/architecture-and-patterns

[arguments-kt]: https://github.com/ayfri/kore/blob/master/generation/src/main/kotlin/generators/arguments.kt

[generator-kt]: https://github.com/ayfri/kore/blob/master/generation/src/main/kotlin/generators/Generator.kt

[generators-kt]: https://github.com/ayfri/kore/blob/master/generation/src/main/kotlin/generators/generators.kt

[kore-generated]: https://github.com/ayfri/kore/tree/master/kore/src/main/generated

[new-generator]: /docs/contributing/creating-a-new-generator

[source-repo]: https://github.com/PixiGeko/Minecraft-generated-data
