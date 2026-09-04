---
root: .components.layouts.MarkdownLayout
title: Kore Gradle Plugin - Build, Link and Live Reload Your Datapack
nav-title: Gradle Plugin
description: Generate your Kore datapack from Gradle, copy it straight into your Minecraft worlds, and reload a running server automatically on every source change.
keywords: kore gradle plugin, minecraft datapack watch mode, datapack live reload, datapack hot reload, gradle continuous build, minecraft rcon reload, datapack development loop
date-created: 2026-09-04
date-modified: 2026-09-05
routeOverride: /docs/guides/gradle-plugin
position: 6
---

# Gradle plugin

The Kore Gradle plugin turns the edit-generate-copy-reload cycle into one command. It runs your entry point, copies the
generated pack into every world you name, and sends `/reload` to a running server over RCON.

Combined with Gradle's continuous build, `gradlew koreRun --continuous` rebuilds and relinks on every source change.

## Setup

The plugin is on the Gradle Plugin Portal, so applying it needs no repository configuration. It shares its version with
the library, so the same string goes in both blocks:

```kotlin
plugins {
	kotlin("jvm")
	id("io.github.ayfri.kore") version "2.13.1-26.2"
}

dependencies {
	implementation("io.github.ayfri.kore:kore:2.13.1-26.2")
}

kore {
	mainClass = "com.example.MainKt"
	packName = "my_pack"
	worlds = listOf("My World")
}
```

Applying the plugin does not bring the Kore dependency in, so declare it yourself. The Minecraft suffix in the version
is what the *library* targets; the plugin is version-agnostic and only carries the suffix so both coordinates stay in
step.

It is published to Maven Central too, as `io.github.ayfri.kore:kore-gradle-plugin`, for builds that resolve plugins
from there instead.

The entry point receives the output directory twice, as its first program argument and as the `kore.output` system
property. Use whichever fits your `main`:

```kotlin
fun main() {
	dataPack("my_pack") {
		path(System.getProperty("kore.output") ?: "out")

		function("load") {
			say("Hello from Kore")
		}
	}.generate()
}
```

`packName` must match the folder Kore writes, which is the datapack `name` unless you override it with `folderName`.
The plugin also passes that value as the `kore.packName` system property, so `folderName(System.getProperty("kore.packName"))`
keeps the two in sync automatically.

## Tasks

Every task sits in the `kore` group, so `gradlew tasks --group kore` lists them.

| Task                   | What it does                                                                                         |
|------------------------|------------------------------------------------------------------------------------------------------|
| `koreBuild`            | Runs the entry point and generates the packs into `outputDirectory`.                                 |
| `koreLinkDataPack`     | Copies the datapack into every world and extra target.                                               |
| `koreLinkResourcePack` | Copies the resource pack into every `resourcepacks` folder. Skipped until `resourcePackName` is set. |
| `koreLink`             | Runs both link tasks.                                                                                |
| `koreReload`           | Sends a command to a running server over RCON, `reload` by default.                                  |
| `koreRun`              | `koreLink` then `koreReload`. The task to use with `--continuous`.                                   |
| `koreClean`            | Deletes the generated output and unlinks every pack from its targets.                                |
| `koreWorlds`           | Lists the worlds found in the Minecraft directory, with the `datapacks` folder of each.              |

## The development loop

```bash
gradlew koreRun --continuous
```

Gradle watches your sources, so every save regenerates the pack and copies it into the worlds. Add an RCON password and
the running server reloads too, with no alt-tab at all:

```properties
# server.properties
enable-rcon=true
rcon.port=25575
rcon.password=changeit
```

```kotlin
kore {
	rcon {
		password = providers.environmentVariable("RCON_PASSWORD")
	}
}
```

`RCON_PASSWORD` is read by default, so the block above is only needed to point somewhere else. Without a password the
reload is reported and skipped rather than failing, which keeps the loop running while the game is closed. A server that
is down behaves the same way, since `failOnError` is `false`: a refused connection is a warning, not a broken build.

RCON is a dedicated server feature. A singleplayer world reaches the plugin through `koreLink` only, and picks the new
pack up on the next `/reload` you type in chat.

## Choosing where the pack lands

The Minecraft directory defaults to the standard location of your OS, `%APPDATA%\.minecraft` on Windows,
`~/Library/Application Support/minecraft` on macOS and `~/.minecraft` elsewhere. The `MINECRAFT_DIR` environment
variable overrides it, and so does `minecraftDirectory`.

Run `gradlew koreWorlds` to see what the plugin finds. A world counts as a world when it has a `level.dat`.

```kotlin
kore {
	// Named worlds, discovered worlds, or arbitrary folders. They add up.
	worlds = listOf("My World", "Test World")
	linkToAllWorlds = false
	dataPackTargets = listOf("D:/servers/survival/world/datapacks")
}
```

`linkMode = LinkMode.SYMLINK` links instead of copying, so the game reads the generated folder directly. Windows refuses
symlinks without Developer Mode or administrator rights, in which case the plugin falls back to a copy and says so.

## Keeping rebuilds correct

`cleanBeforeBuild` is on by default: the output directory is wiped before each generation, so a resource you deleted
from your Kotlin sources also disappears from the pack.

Up-to-date checks follow the runtime classpath. If your entry point reads files that are not on it, textures or an
external JSON for instance, declare them so a change to one triggers a rebuild:

```kotlin
kore {
	additionalInputs.from(file("assets"), file("data/balance.json"))
}
```

## Full configuration reference

```kotlin
kore {
	mainClass = "com.example.MainKt"          // required
	packName = "my_pack"                       // defaults to the project name
	outputDirectory = layout.buildDirectory.dir("kore")
	cleanBeforeBuild = true

	runtimeClasspath.setFrom(/* ... */)        // defaults to the `main` source set, set it for Kotlin Multiplatform
	arguments = listOf("--verbose")            // appended after the output directory
	jvmArguments = listOf("-Xmx2g")
	systemProperties = mapOf("debug" to "true")
	additionalInputs.from(file("assets"))

	minecraftDirectory = file("D:/games/minecraft")
	worlds = listOf("My World")
	linkToAllWorlds = false
	dataPackTargets = listOf("D:/servers/survival/world/datapacks")

	resourcePackName = "my_assets"             // unset means no resource pack linking
	resourcePackTargets = listOf("D:/games/minecraft/resourcepacks")

	linkMode = LinkMode.COPY                   // or LinkMode.SYMLINK

	rcon {
		enabled = true
		host = "localhost"
		port = 25575
		password = providers.environmentVariable("RCON_PASSWORD")
		command = "reload"
		timeoutMillis = 5000
		failOnError = false
	}
}
```

## Kotlin Multiplatform projects

A multiplatform project has no `main` source set, so the classpath has to be named explicitly:

```kotlin
kore {
	mainClass = "com.example.MainKt"
	runtimeClasspath.setFrom(kotlin.targets.getByName("jvm").compilations.getByName("main").runtimeDependencyFiles)
}
```

## What to read next

- [Creating a Datapack](/docs/guides/creating-a-datapack) - what `path`, `folderName` and `generate()` do to the output
  this plugin moves around
- [Configuration](/docs/guides/configuration) - JSON formatting and generated function naming
- [GitHub Actions Publishing](/docs/advanced/github-actions-publishing) - automate releases to Modrinth, CurseForge and
  GitHub
