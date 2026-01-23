---
root: .components.layouts.MarkdownLayout
title: Bindings
nav-title: Bindings
description: Import existing datapacks and generate Kotlin bindings.
keywords: kore, bindings, import, datapack, github, modrinth, curseforge
date-created: 2026-01-23
date-modified: 2026-01-23
routeOverride: /docs/bindings
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
	implementation(project(":bindings"))
}
```

## Quick start

```kotlin
import io.github.ayfri.kore.bindings.api.importDatapacks

importDatapacks {
	configuration {
		outputPath("src/main/kotlin")
		packagePrefix = "kore.dependencies"
		skipCache = false
	}

	github("pixigeko.minecraft-default-data:1.21.8") {
		subPath = "data"
	}

	modrinth("vanilla-refresh")
	curseforge("repurposed-structures-illager-invasion-compat")
	url("https://example.com/pack.zip")
}
```

## Explore without generating

```kotlin
import io.github.ayfri.kore.bindings.api.exploreDatapacks

val datapacks = exploreDatapacks {
	github("user.repo:main")
	modrinth("my-pack")
}

println("Functions: ${datapacks.first().functions.size}")
```

## Download sources

### GitHub

Patterns:

- `github:user.repo`
- `github:user.repo:tag`
- `github:user.repo:tag:asset.zip`

### Modrinth

Patterns:

- `modrinth:slug`
- `modrinth:slug:version`

### CurseForge

Patterns:

- `curseforge:projectId`
- `curseforge:projectId:fileId`
- `curseforge:slug`
- `curseforge:slug:fileId`
- `curseforge:https://www.curseforge.com/minecraft/data-packs/your-pack`

> [!IMPORTANT]
> CurseForge downloads require the `CURSEFORGE_API_KEY` environment variable.

### URL / local path

Patterns:

- `https://example.com/pack.zip`
- `./path/to/pack`
- `./path/to/pack.zip`

## Configuration

Global configuration (`configuration {}`):

- `outputPath`: Output directory for generated Kotlin files.
- `packagePrefix`: Base package for generated bindings.
- `skipCache`: Force re-download even if cached.
- `debug`: Print extra exploration details.

Per-datapack configuration (inside each source block):

- `packageName`: Override package name for this datapack.
- `remappedName`: Override the Kotlin object name.
- `subPath`: Import only a subfolder inside a repo or zip.
- `includes`: Include only files matching these glob patterns.
- `excludes`: Exclude files matching these glob patterns.

Example:

```kotlin
importDatapacks {
	github("user.repo:main") {
		subPath = "datapacks/my-pack"
		includes = listOf("*.mcfunction", "advancements/**")
		excludes = listOf("**/*.bak", "test/**")
	}
}
```

## Cache

Downloaded URLs are cached under `~/.kore/cache/datapacks`.
Set `skipCache = true` in `configuration {}` to force a re-download.

## Troubleshooting

- **GitHub**: Unauthenticated API calls are rate-limited. Try again later if you hit the limit.
- **CurseForge**: Ensure `CURSEFORGE_API_KEY` is set and valid.
