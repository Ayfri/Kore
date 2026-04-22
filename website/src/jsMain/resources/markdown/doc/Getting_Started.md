---
root: .components.layouts.MarkdownLayout
title: Getting Started
nav-title: Getting Started
description: Step-by-step guide to create your first Minecraft datapack with Kore.
keywords: minecraft, datapack, kore, getting started, quickstart, kotlin
date-created: 2025-08-21
date-modified: 2026-04-22
routeOverride: /docs/getting-started
position: 1
---

# Getting Started

This guide takes you from zero to a real development workflow with Kore. Instead of stopping at a minimal "hello world,"
you will build a small but structured datapack, run it in-game, iterate quickly, and learn how to scale your project.

## What you will build

By the end of this page, you will have:

- A Kotlin project configured for Kore.
- A datapack with metadata and multiple functions.
- A simple game loop entry point (`load` + user-triggered functions).
- A practical local development cycle to edit, regenerate, and test quickly.
- A clean starting structure you can keep expanding.

## Prerequisites

- [Java 21 (JDK 21)](https://jdk.java.net/archive/) or higher.
- Gradle (wrapper recommended: `./gradlew`).
- IntelliJ IDEA (recommended) or another IDE with Kotlin support.
- Basic understanding of Minecraft datapacks (helpful but not required).

## Step 1: Create your project

You have two ways to start.

### Option A: Use the Kore Template (recommended)

The fastest option is the template repository, which is already configured for Kotlin + Gradle + Kore.

1. Open [Kore Template](https://github.com/Kore-Minecraft/Kore-Template).
2. Click "Use this template" (or clone directly).
3. Open it in IntelliJ IDEA.
4. Let Gradle sync.
5. Run the `main` function in `src/main/kotlin/Main.kt`.

### Option B: Add Kore to an existing Kotlin project

If you already have a Kotlin project, add Kore manually.

#### Core modules

- `kore` - core DSL to generate datapacks.
- `oop` - object-oriented gameplay abstractions.
- `helpers` - helper utilities built on top of Kore.
- `bindings` - experimental importer for existing datapacks.

For your first datapack, use only `kore`.

#### Gradle Kotlin DSL

```kotlin
dependencies {
	implementation("io.github.ayfri.kore:kore:VERSION")
}
```

#### Gradle Groovy DSL

```groovy
dependencies {
    implementation 'io.github.ayfri.kore:kore:VERSION'
}
```

#### Snapshot builds (latest unreleased changes)

If you want bleeding-edge features:

```kotlin
repositories {
	mavenCentral()
	maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
	implementation("io.github.ayfri.kore:kore:VERSION-SNAPSHOT")
}
```

#### Kotlin compiler and JVM settings

Kore relies on context parameters. Make sure your `build.gradle.kts` contains:

```kotlin
kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}

	jvmToolchain(21)
}
```

## Step 2: Write a first useful datapack

Instead of a single command, create a tiny but realistic pack with:

- metadata (`pack`),
- one function for player feedback,
- one function for setup behavior.

Create `Main.kt`:

```kotlin
fun main() {
	val datapack = dataPack("starter_kore") {
		pack {
			description = textComponent("Starter datapack generated with Kore")
		}

		function("hello") {
			tellraw(allPlayers(), textComponent("Hello from Kore"))
		}

		function("setup") {
			tellraw(allPlayers(), textComponent("Setup complete"))
		}
	}

	datapack.generateZip()
}
```

Run `main`, then put the generated zip into your world's `datapacks` folder.

## Step 3: Load and test in Minecraft

1. Open your world.
2. Run `/reload`.
3. Trigger the function:

```mcfunction
/function starter_kore:hello
```

If everything is correct, chat displays your message.

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
	val datapack = dataPack("starter_kore") {
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
	}

	datapack.generate()
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
	function("load") {
		tellraw(allPlayers(), textComponent("[starter_kore] datapack loaded"))
	}
}

fun DataPack.registerFeatureFunctions() {
	function("feature/give_welcome") {
		tellraw(allPlayers(), textComponent("Welcome to the world"))
	}
}

fun main() {
	val datapack = dataPack("starter_kore") {
		pack {
			description = textComponent("Starter datapack generated with Kore")
		}

		registerCoreFunctions()
		registerFeatureFunctions()
	}

	datapack.generateZip()
}
```

This pattern scales much better than one giant builder block.

## Step 6: Use a fast development loop

During development, your loop should be:

1. Edit Kotlin code.
2. Re-run `main`.
3. Copy or regenerate output into your world datapack folder.
4. In-game: `/reload`.
5. Run the function you are testing.

Tips:

- Use `.generate()` when you want to inspect generated files locally.
- Use `.generateZip()` when you want easy distribution.
- Enable pretty JSON in [Configuration](/docs/guides/configuration) when debugging generated resources.

## Step 7: Build an advanced mini-pack with a custom enchantment

At this point, create a pack that is closer to production structure:

- startup flow with `load`,
- runtime flow with regular functions,
- one custom data-driven feature (a custom enchantment).

Example:

```kotlin
fun main() {
	val datapack = dataPack("starter_kore") {
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
			description(textComponent("Vampiric"))
			supportedItems(Tags.Item.SWORDS)
			primaryItems(Tags.Item.SWORD_ENCHANTABLE)
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
					applyMobEffect(
						PostAttackSpecifier.ATTACKER,
						PostAttackSpecifier.ATTACKER,
						Effects.INSTANT_HEALTH,
					) {
						minAmplifier(0)
						maxAmplifier(0)
						minDuration(1)
						maxDuration(1)

						requirements {
							randomChance(linearLevelBased(0.08, 0.08))
						}
					}
				}
			}
		}
	}

	datapack.generateZip()
}
```

What this gives you:

- a generated `data/starter_kore/enchantment/vampiric.json`,
- startup player feedback via `minecraft:load`,
- a clear split between command functions and data-driven definitions.

To validate in-game:

1. Regenerate your datapack.
2. Put it in your world.
3. Run `/reload`.
4. Check logs/chat for load output.
5. Test enchantment behavior with commands or controlled test scenarios.

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

## Kotlin tips that matter specifically in Kore projects

- Prefer `val` by default; immutable declarations reduce accidental state issues.
- Use extension functions on `DataPack` to keep your DSL composable.
- If your IDE imports the wrong DSL symbol, qualify temporarily with `this.` in the builder scope, then fix the import.
- Keep function names and file-like paths consistent (`feature/x`, `system/y`) to keep generated output predictable.
- Re-declaring the same logical entry in Kore is idempotent: the last declaration wins.
- Prefer `load {}` and `tick {}` builders over manual tag wiring for standard lifecycle hooks.

## Troubleshooting

### Unresolved Kore DSL symbols

- Check that the dependency exists in the correct module.
- Verify the compiler flag `-Xcontext-parameters`.
- Refresh/sync Gradle in your IDE.

### Java/Kotlin toolchain errors

- Confirm JDK 21 is installed and selected by Gradle.
- Confirm `jvmToolchain(21)` is configured.

### Datapack generates but does not work in-game

- Confirm the namespace/function path in `/function`.
- Run `/reload` after every regeneration.
- Check the world folder path and datapack placement.
- Open logs and verify no JSON or command syntax error is reported.

## Recommended learning path after this guide

Start here next:

1. [Creating a Datapack](/docs/guides/creating-a-datapack)
2. [Functions](/docs/commands/functions)
3. [Commands](/docs/commands/commands)
4. [Selectors](/docs/concepts/selectors)
5. [Cookbook](/docs/guides/cookbook)
6. [Recipes](/docs/data-driven/recipes)
7. [Enchantments](/docs/data-driven/enchantments)
8. [Bindings](/docs/advanced/bindings) (experimental)

For the full index, see [Home](/docs/home).

## See also

- [Kore Hello World](https://ayfri.com/articles/kore-hello-world/)
- [Kore on GitHub](https://github.com/Ayfri/Kore)
- [Kore on Maven Central](https://central.sonatype.com/search?q=io.github.ayfri.kore)
- [Kore on Discord](https://discord.ayfri.com)
- [Minecraft Wiki: Datapack](https://minecraft.wiki/w/Data_pack)

