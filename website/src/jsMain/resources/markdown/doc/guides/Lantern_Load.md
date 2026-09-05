---
root: .components.layouts.MarkdownLayout
title: Lantern Load - Datapack Load Order, Versioning and Dependencies
nav-title: Lantern Load
description: Generate the full Lantern Load boilerplate from Kotlin, publish your pack version on the load.status scoreboard, and guard against missing or outdated dependencies.
keywords: lantern load kore, minecraft datapack load order, datapack dependencies, load.status scoreboard, datapack versioning, smithed interop, bookshelf dependency
date-created: 2026-09-05
date-modified: 2026-09-05
routeOverride: /docs/guides/lantern-load
position: 7
---

# Lantern Load

[Lantern Load](https://github.com/LanternMC/load) is the convention nearly every serious datapack library follows to
control **load order** and to **advertise its version**. Bookshelf, Smithed libraries, GM4 and PlayerDB all rely on it,
so a pack that speaks it can depend on them and be depended on in return.

`lanternLoad { }` generates the whole thing, boilerplate included:

```kotlin
dataPack("my_pack") {
	lanternLoad {
		version(1, 2, 3)
		dependency("bs.math", 3, 1)

		load {
			say("my_pack loaded")
		}
	}
}
```

## What it generates

| File                                                | Role                                                                |
|-----------------------------------------------------|---------------------------------------------------------------------|
| `data/minecraft/tags/function/load.json`            | Hands control to `#load:_private/load`.                             |
| `data/load/tags/function/_private/load.json`        | The Lantern Load chain: init, then `pre_load`, `load`, `post_load`. |
| `data/load/tags/function/_private/init.json`        | Points at the objective setup function.                             |
| `data/load/function/_private/init.mcfunction`       | Creates `load.status` and resets it on every load.                  |
| `data/load/tags/function/load.json`                 | Lists every pack in the world, yours included as `#my_pack:load`.   |
| `data/my_pack/tags/function/load.json`              | Your entry point: dependencies, then `enumerate`, then `resolve`.   |
| `data/my_pack/tags/function/load/dependencies.json` | Pulls each dependency's own load tag in first.                      |
| `data/my_pack/function/load/enumerate.mcfunction`   | Publishes your version on `load.status`.                            |
| `data/my_pack/function/load/resolve.mcfunction`     | Checks every dependency version, then calls `init`.                 |
| `data/my_pack/function/load/init.mcfunction`        | The body of your `load { }` block.                                  |

The boilerplate is written once per pack and merges cleanly with other packs shipping the same files, so leaving
`generateBoilerplate` on is the normal choice. Turn it off when another artifact in the same build already emits it:

```kotlin
lanternLoad {
	generateBoilerplate = false
}
```

## Versioning

Your version is published as three fake players on the `load.status` objective, which is how other packs detect you:

```mcfunction
# my_pack:load/enumerate
scoreboard players set my_pack.major load.status 1
scoreboard players set my_pack.minor load.status 2
scoreboard players set my_pack.patch load.status 3
```

`version` accepts either components or a string, so it can be wired straight to your build:

```kotlin
lanternLoad {
	version("2.0.1")
	scoreHolder = "#my_pack"  // fake player prefix, defaults to the namespace
}
```

## Dependencies

A dependency does two things: it pulls the dependency's load tag in before yours, and it generates a runtime guard.

```kotlin
lanternLoad {
	dependency("bs.math", 3, 1)          // major 3, minor 1 or higher
	dependency("smithed.actionbar")      // load order only, no version check
	dependency("bs.block", "2.4.0", required = true)
}
```

The guard follows the ecosystem compatibility rule: the major version must match exactly, the minor version must be at
least the requested one, and the patch version is ignored.

```mcfunction
# my_pack:load/resolve
execute if score bs.math.major load.status matches 3 if score bs.math.minor load.status matches 1.. run function my_pack:load/init
execute unless score bs.math.major load.status matches 3 run tellraw @a {type:"text",color:"red",text:"[my_pack] Missing dependency bs.math 3.1."}
execute if score bs.math.major load.status matches 3 unless score bs.math.minor load.status matches 1.. run tellraw @a {type:"text",color:"red",text:"[my_pack] Missing dependency bs.math 3.1."}
```

`init` runs only when every guard passes, so your pack never half-initializes against a missing library. A dependency
without a version affects load order only and generates no guard. `required = true` makes Minecraft error out when the
dependency is absent instead of letting the guard report it in chat.

Change the wording with `missingDependencyMessage`:

```kotlin
lanternLoad {
	dependency("bs.math", 3, 1)
	missingDependencyMessage = { dependency ->
		textComponent("My Pack needs ${dependency.namespace} ${dependency.version}", Color.GOLD)
	}
}
```

## Pre-load and post-load

`#load:pre_load` runs before every pack's `load`, `#load:post_load` after. Use them for work that has to bracket the
whole world's initialization, such as clearing shared state or printing a summary:

```kotlin
lanternLoad {
	preLoad {
		scoreboard.players.reset(literal("*"), "my_pack.state")
	}

	load {
		function("setup")
	}

	postLoad {
		say("Every pack is ready")
	}
}
```

## Ticking

Lantern Load recommends against `#minecraft:tick`, because it runs *before* `#minecraft:load` and can tick your pack
before it is initialized. Schedule your tick function from `load` instead:

```kotlin
dataPack("my_pack") {
	val tick = function("tick") {
		say("tick")
		schedule(1.ticks, "my_pack:tick")
	}

	lanternLoad {
		version(1)
		load {
			schedule(1.ticks, tick)
		}
	}
}
```

## Reference

`lanternLoad { }` returns the generated `init` function, so it can be called from elsewhere in the pack.

| Property                   | Default       | Role                                                           |
|----------------------------|---------------|----------------------------------------------------------------|
| `namespace`                | the pack name | Namespace owning the generated functions and tags.             |
| `version`                  | `1.0.0`       | Version published on the status objective.                     |
| `scoreHolder`              | `namespace`   | Fake player prefix carrying the version.                       |
| `objective`                | `load.status` | Objective holding the load status of every pack.               |
| `directory`                | `load`        | Directory of the generated functions, also the entry tag name. |
| `generateBoilerplate`      | `true`        | Emits the Lantern Load pack files.                             |
| `missingDependencyMessage` | red chat line | Message broadcast when a guard fails.                          |

## What to read next

- [Creating a Datapack](/docs/guides/creating-a-datapack) - `path`, `folderName` and `generate()`, the basics this guide builds on
- [Gradle Plugin](/docs/guides/gradle-plugin) - build, link and reload your pack automatically on every change
- [Fabric Resource Conditions](/docs/guides/fabric-resource-conditions) - gate generated resources on mods, tags or feature flags
