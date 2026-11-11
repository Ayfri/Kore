---
root: .components.layouts.MarkdownLayout
title: Your First Minecraft Datapack in Kotlin
nav-title: Getting Started
description: Build your first Minecraft datapack in Kotlin, from project setup to type-safe commands and functions, generating the pack and testing it in-game.
keywords: minecraft datapack tutorial, kore getting started, create datapack with kotlin, minecraft datapack generator tutorial, kore setup guide, kotlin datapack beginner, minecraft function generator, datapack development guide
date-created: 2025-08-21
date-modified: 2026-11-11
routeOverride: /docs/getting-started
---

# Getting Started

This guide takes you from zero to a real development workflow with Kore. Instead of stopping at a minimal "hello world,"
you will build a small but structured datapack, run it in-game, iterate quickly, and learn how to scale your project.

New to Kotlin? The [Kotlin basics for Kore](/docs/getting-started#kotlin-basics-for-kore) section at the end of this
page covers the small subset you need.

This guide targets the JVM, the primary way to build and export a datapack (`generate()` / `generateZip()`). The same
calls also work on Node.js; see [Multiplatform Support](/docs/advanced/multiplatform) for that and for the
browser-only `exportAsStrings()` / `generateZipBytes()` alternatives.

If you already have solid datapack experience and want an architecture-first migration guide, jump to
[Coming from Datapacks](/docs/guides/from-datapacks-to-kore). To try Kore before setting anything up, the
[playground](/playground) runs it in your browser and shows the generated pack.

## What you will build

By the end of this page, you will have:

- A Kotlin project configured for Kore.
- A datapack with metadata and multiple functions.
- A simple game loop entry point (`load` + user-triggered functions).
- A practical local development cycle to edit, regenerate, and test quickly.
- A clean starting structure you can keep expanding.

## Prerequisites

- [Java {{java.version}} (JDK {{java.version}})](https://jdk.java.net/{{java.version}}/) or higher, required by both Kore and Minecraft {{mc.version}}.
- Gradle (wrapper recommended: `./gradlew`).
- IntelliJ IDEA (recommended) or another IDE with Kotlin support.
- Minecraft Java Edition {{mc.version}} installed, with a world to test in.
- Basic understanding of Minecraft datapacks (helpful but not required).

## Step 1: Create your project

You have two ways to start.

### Option A: Use the Kore Template (recommended)

The fastest option is the template repository, which is already configured for Kotlin + Gradle + Kore, including the
[Gradle plugin](/docs/guides/gradle-plugin) that copies the generated datapack into your world.

1. Open [Kore Template](https://github.com/Kore-Minecraft/Kore-Template) and click "Use this template" (or clone it).
2. Open it in IntelliJ IDEA and let Gradle sync.
3. In `build.gradle.kts`, set `packName` and `worlds` to your datapack name and your world. Run `./gradlew koreWorlds` to
   list the world names Minecraft knows about.
4. In `src/main/kotlin/Main.kt`, set the same name in the `dataPack("...")` call.
5. Run `./gradlew koreRun`.
6. Type `/reload` in game.

### Option B: Add Kore to an existing Kotlin project

If you already have a Kotlin project, add Kore and the Gradle plugin manually.

#### Core modules

- `kore` - core DSL to generate datapacks.
- `oop` - object-oriented gameplay abstractions.
- `helpers` - helper utilities built on top of Kore.
- `bindings` - experimental importer for existing datapacks.

For your first datapack, use only `kore`.

#### Gradle Kotlin DSL

```kotlin
plugins {
	kotlin("jvm")
	id("io.github.ayfri.kore") version "{{kore.release}}"
}

dependencies {
	implementation("io.github.ayfri.kore:kore:{{kore.release}}")
}

kore {
	mainClass = "com.example.MainKt"
	packName = "my_pack"
	worlds = listOf("My World")
}
```

Applying the plugin does not add the Kore dependency, so declare it yourself. See the
[Gradle Plugin](/docs/guides/gradle-plugin) page for every option of the `kore { }` block.

#### Gradle Groovy DSL

```groovy
dependencies {
    implementation 'io.github.ayfri.kore:kore:{{kore.release}}'
}
```

`{{kore.release}}` is the latest stable release, for Minecraft {{mc.version}}. Kore also publishes builds for
Minecraft snapshots, the newest release including them is `{{kore.snapshot}}`: snapshot features can still change
from one snapshot to the next, see [Version Support](/docs/guides/version-support#snapshot-and-pre-release-builds).

#### Snapshot builds (latest unreleased changes)

If you want bleeding-edge features:

```kotlin
repositories {
	mavenCentral()
	maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
	implementation("io.github.ayfri.kore:kore:{{kore.mavenSnapshot}}")
}
```

#### Kotlin compiler and JVM settings

Kore relies on context parameters, which are stable since Kotlin 2.4. Make sure your `build.gradle.kts` contains:

```kotlin
kotlin {
	jvmToolchain({{java.version}})
}
```

## Step 2: Write a first useful datapack

Instead of a single command, create a tiny but realistic pack with:

- metadata (`pack`),
- one function for player feedback,
- one function for setup behavior.

If you are new to Kotlin syntax, read the code like this:

- `dataPack("starter_kore") { ... }` creates a builder context.
- `path(...)` sets the folder Kore writes the pack into. The Gradle plugin passes its output directory in the
  `kore.output` system property, and `out` is the fallback when you run `main` from the IDE.
- each `function("...") { ... }` block writes one generated `.mcfunction` file.
- `.generate()` writes the pack as a folder, which is what the plugin copies into your worlds.

Create `Main.kt`:

```kotlin
fun main() {
	dataPack("starter_kore") {
		path(System.getProperty("kore.output") ?: "out")

		pack {
			description = textComponent("Starter datapack generated with Kore")
		}

		function("hello") {
			tellraw(allPlayers(), textComponent("Hello from Kore"))
		}

		function("setup") {
			tellraw(allPlayers(), textComponent("Setup complete"))
		}
	}.generate()
}
```

Set `packName = "starter_kore"` in the `kore { }` block so it matches the `dataPack("starter_kore")` name, then run
`./gradlew koreRun`, which runs `main`, generates the pack and copies it into the worlds listed in `kore { worlds }`.

## Step 3: Load and test in Minecraft

1. Open your world.
2. Run `/reload`.
3. Trigger the function:

```mcfunction
/function starter_kore:hello
```

If everything is correct, chat displays your message.

The same idea runs in your browser with the [hello world example](/playground#example=hello-world) of the playground, a load function saying hello, with the commands Kore generates for it:

{{{ .components.doc.HelloWorldExample }}}

## Step 4: Use `load {}` as your real entry point

In Kore, using `load {}` is usually better than manually naming a startup function and wiring tags yourself. It directly
creates and registers a function in `minecraft:load`, which makes your startup flow explicit and less error-prone.

Most datapacks become easier to maintain when you separate:

- initialization (`load`),
- recurring logic (`tick` when needed),
- feature functions (your own namespaced functions).

Add this structure in Kore by defining dedicated functions and using consistent names:

```kotlin
fun main() {
	dataPack("starter_kore") {
		path(System.getProperty("kore.output") ?: "out")

		pack {
			description = textComponent("Starter datapack generated with Kore")
		}

		load("bootstrap") {
			tellraw(allPlayers(), textComponent("[starter_kore] datapack loaded"))
			function("feature/give_welcome")
		}

		function("feature/give_welcome") {
			tellraw(allPlayers(), textComponent("Welcome to the world"))
		}

		tick("runtime/checks") {
			// Keep tick lightweight. Route heavy logic to dedicated functions.
		}
	}.generate()
}
```

With this approach:

- `load("bootstrap")` runs once after `/reload`.
- `tick("runtime/checks")` runs every game tick.
- `function("feature/...")` stays your reusable API surface.
- folder-like names keep generated files organized as the project grows.

## Step 5: Organize code before it gets messy

A common beginner mistake is placing everything in one `main` function. Prefer small helpers and focused files early.

Example using extensions:

```kotlin
fun DataPack.registerCoreFunctions() {
	load("bootstrap") {
		tellraw(allPlayers(), textComponent("[starter_kore] datapack loaded"))
	}
}

fun DataPack.registerFeatureFunctions() {
	function("feature/give_welcome") {
		tellraw(allPlayers(), textComponent("Welcome to the world"))
	}
}

fun main() {
	dataPack("starter_kore") {
		path(System.getProperty("kore.output") ?: "out")

		pack {
			description = textComponent("Starter datapack generated with Kore")
		}

		registerCoreFunctions()
		registerFeatureFunctions()
	}.generate()
}
```

This pattern scales much better than one giant builder block.

## Step 6: Use a fast development loop

During development, run this once and leave it running:

```shell
./gradlew koreRun --continuous
```

The loop is then:

1. Edit Kotlin code and save: Gradle regenerates the pack and copies it into your worlds.
2. In-game: `/reload` (sent for you over RCON on a dedicated server).
3. Run the function you are testing.

See [The development loop](/docs/guides/gradle-plugin#the-development-loop) for the RCON setup.

Tips:

- Use `.generate()` when you want to inspect generated files locally.
- Use `.generateZip()` when you want easy distribution, see [Generating output](/docs/guides/creating-a-datapack#generating-output).
- Enable pretty JSON in [Configuration](/docs/guides/configuration) when debugging generated resources.

## Step 7: Build an advanced mini-pack with a custom enchantment

At this point, create a pack that is closer to production structure:

- startup flow with `load`,
- runtime flow with regular functions,
- one custom data-driven feature (a custom enchantment).

Example:

```kotlin
fun main() {
	dataPack("starter_kore") {
		path(System.getProperty("kore.output") ?: "out")

		pack {
			description = textComponent("Starter datapack with a custom enchantment")
		}

		// 1) Runtime functions
		function("feature/welcome") {
			tellraw(allPlayers(), textComponent("Welcome to starter_kore"))
		}

		function("feature/show_vampiric_hint") {
			tellraw(allPlayers(), textComponent("Try the Vampiric enchantment on a sword"))
		}

		// 2) Startup entry point
		load("bootstrap") {
			function("feature/welcome")
			function("feature/show_vampiric_hint")
		}

		// 3) Custom enchantment definition (data-driven content)
		enchantment("vampiric") {
			description("Vampiric")
			supportedItems(Tags.Item.SWORDS)
			primaryItems(Tags.Item.Enchantable.SHARP_WEAPON)
			weight = 2
			maxLevel = 3
			minCost(20, 15)
			maxCost(50, 15)
			anvilCost = 8
			slots(EquipmentSlot.MAINHAND)

			effects {
				// Bonus damage that scales by level
				damage {
					add(linearLevelBased(1, 1))
				}

				// Chance to heal attacker on hit
				postAttack {
					on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.ATTACKER) {
						applyMobEffect(Effects.INSTANT_HEALTH) {
							minAmplifier(0)
							maxAmplifier(0)
							minDuration(1)
							maxDuration(1)

							requirements {
								randomChance(enchantmentLevel(linearLevelBased(0.08f, 0.08f)))
							}
						}
					}
				}
			}
		}
	}.generate()
}
```

What this gives you:

- a generated `data/starter_kore/enchantment/vampiric.json`,
- startup player feedback via `minecraft:load`,
- a clear split between command functions and data-driven definitions.

To validate in-game:

1. Run `./gradlew koreRun` to regenerate the pack and copy it into your world.
2. Run `/reload`.
3. Check logs/chat for load output.
4. Test enchantment behavior with commands or controlled test scenarios.

## Going beyond a minimal datapack

Once your first commands work, move to data-driven features:

- Add recipes, loot tables, predicates, and tags.
- Create multiple function entry points per feature.
- Separate "bootstrap" code from "gameplay" code.
- Reuse helper functions to avoid duplicated command blocks.

Good expansion ideas after the custom enchantment:

- A welcome system with per-player conditions.
- A starter kit function with basic inventory setup.
- A scoreboard-based progression mechanic.
- A custom recipe that complements your enchantment.
- A balancing pass for enchantment costs, rarity, and max level.

## Kotlin basics for Kore

If you are new to Kotlin, do not worry. You only need a small subset to be productive with Kore.

### `val` and `var`

- `val` means read-only reference (preferred by default).
- `var` means mutable reference (use only when reassignment is needed).

```kotlin
val packName = "starter_kore"
var buildNumber = 1
buildNumber += 1
```

### Functions

You declare functions with `fun`. Kore code is mostly function calls inside builders.

```kotlin
fun greet(name: String): String {
	return "Hello, $name"
}
```

### Null safety

Kotlin distinguishes nullable (`String?`) and non-null (`String`) types.

```kotlin
val maybePlayer: String? = System.getenv("PLAYER_NAME")
val displayName = maybePlayer ?: "Player"
```

Use:

- `?.` for safe calls,
- `?:` for fallback values (Elvis operator).

### Lambdas and builder blocks

Kore uses Kotlin lambdas heavily:

```kotlin
function("hello") {
	tellraw(allPlayers(), textComponent("Hello"))
}
```

The `{ ... }` block is a lambda, and inside it Kore exposes a DSL context with helper functions.

### Extension functions (very important in Kore projects)

Kotlin lets you add functions to existing types without inheritance.
This is ideal for structuring large datapacks.

```kotlin
fun DataPack.registerWelcome() {
	function("feature/welcome") {
		tellraw(allPlayers(), textComponent("Welcome"))
	}
}
```

You can then call `registerWelcome()` inside your `dataPack {}` builder.

### Kotlin tips that matter specifically in Kore projects

- Prefer `val` by default; immutable declarations reduce accidental state issues.
- Use extension functions on `DataPack` to keep your DSL composable.
- If your IDE imports the wrong DSL symbol, qualify temporarily with `this.` in the builder scope, then fix the import.
- Keep function names and file-like paths consistent (`feature/x`, `system/y`) to keep generated output predictable.
- Declare each function path once: two functions sharing a path with different bodies make generation throw.
- Prefer `load {}` and `tick {}` builders over manual tag wiring for standard lifecycle hooks.

### Quick Kotlin learning resources

- [Kotlin documentation](https://kotlinlang.org/docs/home.html)
- [Kotlin null safety](https://kotlinlang.org/docs/null-safety.html)
- [Kotlin extensions](https://kotlinlang.org/docs/extensions.html)
- [Learn X in Y Minutes - Kotlin](https://learnxinyminutes.com/kotlin/)
- [Kotlin Playground](https://play.kotlinlang.org/)

## Troubleshooting

### Unresolved Kore DSL symbols

- Check that the dependency exists in the correct module.
- Confirm you're on Kotlin 2.4 or higher (context parameters are stable there).
- Refresh/sync Gradle in your IDE.

### Java/Kotlin toolchain errors

- Confirm JDK {{java.version}} is installed and selected by Gradle.
- Confirm `jvmToolchain({{java.version}})` is configured.

### Datapack generates but does not work in-game

- Confirm the namespace/function path in `/function`.
- Run `/reload` after every `./gradlew koreRun`.
- Run `./gradlew koreWorlds` and check that the world name in `kore { worlds }` is listed.
- Confirm `packName` equals the name in `dataPack("...")`, and that `main` writes a folder with `generate()`: the plugin
  copies `<output>/<packName>` only.
- Open logs and verify no JSON or command syntax error is reported.

## Recommended learning path after this guide

Start here next:

1. [Pack Setup & Output](/docs/guides/creating-a-datapack)
2. [Runtime Logic](/docs/concepts/runtime-logic) - how Kotlin `val`/`if`/`for` map to in-game scoreboards, storage, and
   `execute`
3. [Functions](/docs/commands/functions)
4. [Commands](/docs/commands/commands)
5. [Selectors](/docs/concepts/selectors)
6. [Cookbook](/docs/guides/cookbook)
7. [Recipes](/docs/data-driven/recipes)
8. [Enchantments](/docs/data-driven/enchantments)

For the full index, see [Introduction](/docs/home), and for a tour of everything Kore covers, the [Features](/features) page.

## See also

- [Kore Hello World](https://ayfri.com/articles/kore-hello-world/)
- [Kore on GitHub](https://github.com/Ayfri/Kore)
- [Kore on Maven Central](https://central.sonatype.com/search?q=io.github.ayfri.kore)
- [Kore on Discord](https://discord.ayfri.com)
- [Minecraft Wiki: Datapack](https://minecraft.wiki/w/Data_pack)
