---
root: .components.layouts.MarkdownLayout
title: Home
nav-title: Home
description: Welcome to the Kore wiki!
keywords: minecraft, datapack, kore, guide
date-created: 2024-04-06
date-modified: 2025-01-08
routeOverride: /docs/home
position: 0
---

# Kore

**Welcome to the Kore wiki!**

Kore is a Kotlin library for creating Minecraft datapacks. It provides a type-safe and concise way to generate Minecraft datapacks using
Kotlin DSL. The library is compatible with Minecraft Java 1.20 and later versions.

## Getting Started

The easiest way to start with Kore is to use the [Kore Template](https://github.com/Ayfri/Kore-Template). This template repository provides a ready-to-use project structure with all the necessary configurations.

### Prerequisites

-   Java Development Kit (JDK) version 21 or higher

### Features

Kore comes with a rich set of features:

-   Generate datapacks as files, zips, or jar files for mod-loaders
-   Create functions with a clean Kotlin DSL
-   Support for all Minecraft commands with their subcommands and syntaxes
-   Generate all JSON-based features (Advancements, Loot Tables, Recipes, etc.)
-   Work with Selectors, NBT tags, and Chat components
-   Access lists for all Minecraft registries (Blocks, Items, Entities, Advancements)
-   Use Colors, Vectors, Rotations with common operations
-   Support for Macros
-   Manage inventories and schedulers
-   Merge datapacks, even with existing zips
-   Manage scoreboard displays
-   Built-in debugging system
-   Common NBT tags generation
-   Experimental OOP module

### Explore Kore

{{{ .components.doc.FeatureGrid }}}

### Quick Example

```kotlin
fun main() {
    val datapack = dataPack("test") {
        function("display_text") {
            tellraw(allPlayers(), textComponent("Hello World!"))
        }

        pack {
            description = textComponent("My First ", Color.GOLD) + text("Kore", Color.AQUA) { bold = true }
        }
    }

    datapack.generateZip()
}
```

For more detailed information, examples, and guides, explore our documentation sections.
