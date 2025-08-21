---
root: .components.layouts.MarkdownLayout
title: Getting Started
nav-title: Getting Started
description: Step-by-step guide to create your first Minecraft datapack with Kore.
keywords: minecraft, datapack, kore, getting started, quickstart, kotlin
date-created: 2025-08-21
date-modified: 2025-08-21
routeOverride: /docs/getting-started
position: 1
---

# Getting Started

This guide takes you from zero to a working datapack using Kore and Kotlin. It also highlights a few Kotlin/Kore tips that Java beginners often miss.

## Prerequisites

- [Java 21 (JDK 21)](https://jdk.java.net/archive/) or higher
- Kotlin + Gradle (using the Gradle wrapper is recommended)
- An IDE with Kotlin support (IntelliJ IDEA recommended)
- Basic knowledge of Minecraft datapacks will help you.

## Option A — Use the Kore Template (recommended)

The fastest way to start is the Kore Template repository. It's preconfigured and ready to run.

- On GitHub: open https://github.com/Ayfri/Kore-Template and click “Use this template” to create your repo, or clone it directly.
- In IntelliJ IDEA:
  1) File > New > Project from Version Control
  2) Enter URL: https://github.com/Ayfri/Kore-Template and choose a directory
  3) Open the project, trust it, let Gradle import/sync
  4) In the `src/main/kotlin` folder, open `Main.kt` and run the `main` function

The template already sets Java 21 and enables the required Kotlin options.

## Option B — Add Kore to an existing Gradle project

Add the dependency.

Kotlin DSL:

```kotlin
implementation("io.github.ayfri.kore:kore:VERSION")
```

Groovy DSL:

```groovy
implementation 'io.github.ayfri.kore:kore:VERSION'
```

Enable context receivers and set Java toolchain:

```kotlin
kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}

	jvmToolchain(21)
}
```

## Your first datapack (Hello, Kore)

Create a `Main.kt` and paste:

```kotlin
fun main() {
	val datapack = dataPack("hello_kore") {
		function("display_text") {
			tellraw(allPlayers(), textComponent("Hello Kore!"))
		}

		pack {
			description = textComponent("Hello Kore datapack")
		}
	}

	// Generate a zip you can drop in your world's datapacks folder
	datapack.generateZip()
	// Alternatively, generate files in a folder: datapack.generate()
}
```

- Run `main` from your IDE.
- A datapack archive is produced; put it into your Minecraft world's `datapacks/` folder.
- In-game, run: `/function hello_kore:display_text`.

## Tips for Kotlin newcomers

- **Null safety:** Kotlin's type system distinguishes nullable and non-nullable types. Use `?` for nullable types and prefer safe calls (
  `?.`) and the Elvis operator (`?:`). See [Null Safety](https://kotlinlang.org/docs/null-safety.html).

```kotlin
val name: String? = System.getenv("USER")
val length: Int = name?.length ?: 0 // safe call + default when null
```

- **Type inference:** You rarely need to specify types explicitly—Kotlin infers them for you. Use `val` for read-only variables and
  `var` for mutable ones. Prefer `val` for immutability.

```kotlin
val count = 0 // Int inferred
var title = "Hello" // String inferred
// Prefer val; use var only when you need to reassign
```

- **Smart casts:** The compiler automatically casts types after checks (e.g., `if (x is String) { x.length }`).

```kotlin
fun lengthIfString(x: Any): Int = if (x is String) x.length else 0
```

- **Extension functions:** Add new functions to existing types without inheritance. Kore uses this for DSLs. See [Extensions](https://kotlinlang.org/docs/extensions.html).

```kotlin
fun String.titleCase(): String = replaceFirstChar { it.titlecase() }
// Usage: "kore".titleCase() -> "Kore"
```

- **Standard library:** Kotlin's standard library is rich—explore [kotlinlang.org/api/latest/jvm/stdlib/](https://kotlinlang.org/api/latest/jvm/stdlib/).

```kotlin
val names = listOf("Alex", "Steve", "Sam")
val shout = names.map { it.uppercase() }.filter { it.startsWith("S") }
```

- DSLs + context receivers: Inside `dataPack { ... }`, many builders are available without qualifiers. The compiler flag `-Xcontext-parameters` is required.
- Extract helpers with extensions: Attach utilities to `DataPack` to keep code organized:

```kotlin
fun DataPack.myBasics() = function("setup") {
	// your setup
}

fun main() = dataPack("my_pack") {
	myBasics()
}
```

- Name collisions in builders: Some builder names exist in multiple contexts (e.g., `nbt`, `predicate`). If your IDE auto-imports the wrong symbol, qualify with `this.` inside the DSL scope to target the correct function, or fix the import.
Example:
```kotlin
predicate("my_function") {
	this.nbt("my_nbt") { /* ... */ } // Use this to correctly import nbt()
}
```
Then just remove `this.` now that you know the import is correct.

- Idempotency: Declaring the same record twice is safe, Kore only keeps the last one.

- **Kotlin Playground:** Try out code online at [play.kotlinlang.org](https://play.kotlinlang.org/).
- **Official docs:** The [Kotlin documentation](https://kotlinlang.org/docs/home.html) is excellent for learning language features and best practices.

## Project layout and outputs

- Your code lives under `src/main/kotlin` (template already set).
- Generated datapacks: `.generate()` writes a folder; `.generateZip()` writes a zip; `.generateJar()` targets mod loaders.
- During development, enable pretty JSON in [Configuration](/docs/configuration) to inspect outputs.
- Split your code into multiple files to keep things organized.

## Troubleshooting

- Unresolved DSL symbols: Ensure the Kore dependency is on the classpath and `-Xcontext-parameters` is enabled.
- Java version errors: Verify `jvmToolchain(21)` and that your JDK is 21.
- Dev loop: Re-run your program after edits to regenerate the datapack.

## What to read next (Docs index)

### Core guides
- [Home](/docs/home)
- [Creating a Datapack](/docs/creating-a-datapack)
- [Configuration](/docs/configuration)

### Features
- [Functions](/docs/functions)
- [Predicates](/docs/predicates)
- [Components](/docs/components)
- [Chat Components](/docs/chat-components)
- [Colors](/docs/colors)
- [Enchantments](/docs/enchantments)
- [Recipes](/docs/recipes)
- [Loot Tables](/docs/loot-tables)
- [Item Modifiers](/docs/item-modifiers)
- [Advancements](/docs/advancements)
- [Scoreboards](/docs/scoreboards)
- [Worldgen](/docs/worldgen)
- [Test Features \(GameTest\)](/docs/test-features)

### Helpers
- [Display Entities](/docs/helpers/display-entities)
- [Inventory Manager](/docs/helpers/inventory-manager)
- [Scheduler](/docs/helpers/scheduler)

## See also

- “Hello World” walkthrough: [Kore Hello World](https://ayfri.com/articles/kore-hello-world/)
- Minecraft wiki: [Datapack](https://minecraft.wiki/w/Data_pack)
- [Kore on GitHub](https://github.com/Ayfri/Kore)
- [Kore on Maven Central](https://central.sonatype.com/search?q=io.github.ayfri.kore)
- [Kore on Discord](https://discord.ayfri.com)
