---
root: .components.layouts.MarkdownLayout
title: Known Issues
nav-title: Known Issues
description: A list of known issues and limitations in Kore.
keywords: kore, guide, documentation, known issues
date-created: 2025-08-27
date-modified: 2025-08-27
routeOverride: /docs/known-issues
---
# Missing features

## SNBT

Some SNBT features are not supported yet. Kore depends on another library for writing SNBT which does not support them yet. <br>
The main features that are not supported are:
- Heterogeneous lists (e.g. `[1, "string", {key: "value"}]`)
- SNBT operations (`bool(arg)`, `uuid(arg)`)
- 
Such features would be very hard to implement just in Kore, but if you really need them, maybe we could consider creating our own SNBT library.

## Resource Packs

Kore is only designed to create Data Packs, not Resource Packs, but it could be implemented in the future.

## Use existing Data Packs

Using already existing Data Packs could be done, it wouldn't be that hard to implement and some things to consider have already been tried. <br>
The main challenge is to know what format to use as an interface to call other Data Packs features.

To import such packs, we would need to create a `Kore` Gradle Plugin that you import and configure in your `build.gradle.kts` file.
Example:
```kotlin
plugins {
	id("io.github.ayfri.kore") version "0.1.0"
}

kore {
	// Experimental name, to be defined
	generateMappings {
		datapacks("path/to/other/datapack1", "path/to/other/datapack2")
	}
}
```

Importing Resource Packs could also be done in a similar way.

Here some examples of what it would generate:

### Example 1

```kotlin
data object MyOtherPack {
	const val NAMESPACE = "my_other_pack"
	
	val myFunction = FunctionArgument("my_function", NAMESPACE)
	val myRecipe = RecipeArgument("my_recipe", NAMESPACE)
}

// Usage:
dataPack("my_main_pack") {
	load {
		function(MyOtherPack.myFunction)
		recipe(allPlayers()).give(MyOtherPack.myRecipe)
	}
}
```

### Example 2

```kotlin
const val MY_OTHER_PACK = "my_other_pack"

fun DataPack.myFunction() = FunctionArgument("my_function", MY_OTHER_PACK)
fun DataPack.myRecipe() = RecipeArgument("my_recipe", MY_OTHER_PACK)
```

But deciding what to do is still an open question, and creating a Gradle Plugin is not a small task.
