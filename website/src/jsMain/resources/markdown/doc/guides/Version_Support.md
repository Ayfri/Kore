---
root: .components.layouts.MarkdownLayout
title: Kore Version Support - Minecraft Versions, Versioning and Deprecations
nav-title: Version Support
description: Which Minecraft version each Kore release targets, how to read versions like 2.15.0-26.2, what counts as a breaking change and how deprecations migrate.
keywords: kore version, kore minecraft version, datapack library versioning, kore breaking changes, kore deprecation, kore snapshot builds, pack format
date-created: 2026-10-07
date-modified: 2026-10-07
routeOverride: /docs/guides/version-support
position: 9
---

# Version support and stability

Every Kore artifact carries two versions in one string: the Kore version, then the Minecraft version it targets.

```kotlin
dependencies {
	implementation("io.github.ayfri.kore:kore:2.15.0-26.2") // Kore 2.15.0, for Minecraft 26.2
}
```

The [Version Matrix](/docs/home#version-matrix) lists the latest Kore release for each Minecraft version, built from
the GitHub releases on every website build.

## Reading a version

Kore versions never contain a dash, so everything before the first `-` is the Kore version and everything after it is
the Minecraft version:

| Version                  | Kore     | Minecraft                    |
|--------------------------|----------|------------------------------|
| `2.15.0-26.2`            | `2.15.0` | 26.2                         |
| `2.13.1-26.2-pre-3`      | `2.13.1` | 26.2 Pre-Release 3           |
| `2.12.0-26.2-snapshot-8` | `2.12.0` | 26.2 Snapshot 8              |
| `2.15.0-SNAPSHOT`        | `2.15.0` | continuous build of `master` |

That is why the string isn't plain semver: tools comparing versions have to split it first. The Kore part follows its
own numbering:

| Change         | Example           | Meaning                                                             |
|----------------|-------------------|---------------------------------------------------------------------|
| Second number  | `2.14.0 → 2.15.0` | Breaking changes possible, read the release notes                   |
| Third number   | `2.13.0 → 2.13.1` | Fixes and additions, existing code keeps compiling                  |
| Minecraft part | `-26.1.2 → -26.2` | New target version, see [below](#one-minecraft-version-per-release) |

All modules (`kore`, `oop`, `helpers`, `bindings`) and the [Gradle plugin](/docs/guides/gradle-plugin) share the same
version, so one string goes in both the `plugins { }` and the `dependencies { }` block.

## One Minecraft version per release

A Kore release targets exactly one Minecraft version. That version decides:

- the generated registries (`Items`, `Blocks`, `Biomes`...), downloaded from the game data of that version, so an item
  removed from the game disappears from `Items` too;
- the shape of every command and JSON resource Kore writes;
- the default `min_format`/`max_format` of `pack.mcmeta`, both set to that version's pack format.

`MINECRAFT_VERSION` (`io.github.ayfri.kore.generated`) holds the targeted version at runtime.

Loading the pack in another Minecraft version only works when nothing it uses changed between the two versions. You
can widen the declared range with `minFormat`/`maxFormat` and split version-specific files with overlays, both shown in
[Creating a Datapack](/docs/guides/creating-a-datapack#pack-metadata), but Kore only checks its output against the
targeted version.

Fixes land on the latest release only, they aren't backported to older Minecraft targets. To stay on an older game
version, pin the last Kore release made for it in the Version Matrix.

## Snapshot and pre-release builds

Kore publishes builds for Minecraft snapshots, pre-releases and release candidates, tagged with the game version
(`2.12.0-26.2-snapshot-8`). They let you start on the next version early, with two caveats:

- Mojang changes snapshot features between snapshots, and Kore follows: an API added for one snapshot can change in the
  next one.
- They are GitHub pre-releases, so the stable rows of the Version Matrix skip them; the latest one shows up as the
  `Snapshot` row.

The `-SNAPSHOT` build is something else: a Maven snapshot published from every commit on `master`, see
[Getting Started](/docs/getting-started). Use it to try a fix before its release, never in a pack you ship.

## What counts as a breaking change

These bump the second number:

- removing or renaming a public declaration;
- changing a signature so existing calls stop compiling;
- changing what an existing call writes to the pack, when the game didn't ask for it.

A Minecraft update is the exception. When Mojang renames a command argument, removes a field or changes a JSON shape,
Kore writes what the new version reads, even if that breaks a Kore API: a pack that compiles but fails to load is worse
than a compile error. Those changes come with the Minecraft part of the version and are listed in the release notes.

## Deprecations

A replaced API stays available under `@Deprecated`, with a message naming its replacement and, when the new call maps
one to one, a `ReplaceWith` that the IDE applies through its *Replace with* quick fix:

```kotlin
advancement("get_diamond") {
	// Deprecated, the IDE offers the replacement
	criteria("get_diamond", InventoryChanged())

	// Current form
	criteria {
		inventoryChanged("get_diamond")
	}
}
```

Fields the game ignores are deprecated with the reason and the field it reads instead, like
`TrimMaterial.overrideArmorMaterials`, which points to `overrideArmorAssets`.

## Release notes

Every release lists its additions, fixes and breaking changes on the [Updates](/updates) page and on
[GitHub releases](https://github.com/Ayfri/Kore/releases). Read the notes before bumping the second number or the
Minecraft part.

## What to read next

- [Getting Started](/docs/getting-started) - adding Kore to a project, stable or snapshot
- [Creating a Datapack](/docs/guides/creating-a-datapack) - pack format ranges and overlays
- [Debugging](/docs/guides/debugging) - finding what broke after an update
