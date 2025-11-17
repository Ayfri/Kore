# Kore Bindings Module

## Overview

The bindings module imports existing Minecraft datapacks and generates Kotlin code to use them type-safely within your Kore project.

## Status & Roadmap

The module is operational and provides exploration and code generation for datapacks and many resource types. Phases 1-3 (exploration, parsing, generation, and DSL configuration) are implemented. Work that remains (Gradle plugin integration, additional download sources, enhanced caching strategies, and more) is tracked in #176.

## Generated Code Structure

### Single Namespace Datapack

For a datapack with a single namespace, resources are generated directly in the main object:

```kotlin
package kore.dependencies.mypack

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.generation.AdvancementArgument
import kotlinx.serialization.Serializable

data object MyDatapack {
	const val PATH = "path/to/datapack"
	const val FORMAT = 81
	const val NAMESPACE = "mypack"

	val pack = Pack(
		format = FORMAT,
		description = textComponent("My Datapack"),
		supportedFormats = SupportedFormats(minInclusive = 80, maxInclusive = 100)
	)

	// Flat enum for non-nested functions
	@Serializable(with = Argument.ArgumentSerializer::class)
	enum class Functions : FunctionArgument {
		MY_FUNCTION,
		MY_OTHER_FUNCTION,
		;
		override val namespace: String = NAMESPACE
	}

	// Nested sealed interface for nested resources
	sealed interface Advancements : AdvancementArgument {
		@Serializable(with = Argument.ArgumentSerializer::class)
		enum class Nether : Advancements {
			MY_ADVANCEMENT,
			;
			override fun asId() = "$NAMESPACE:nether/${name.lowercase()}"
		}

		@Serializable(with = Argument.ArgumentSerializer::class)
		enum class Overworld : Advancements {
			MY_ADVANCEMENT,
			;
			override fun asId() = "$NAMESPACE:overworld/${name.lowercase()}"
		}

		override val namespace: String = NAMESPACE
	}

	data object Worldgen {
		@Serializable(with = Argument.ArgumentSerializer::class)
		enum class Biomes : BiomeArgument {
			MY_BIOME,
			;
			override val namespace: String = NAMESPACE
		}
	}
}
```

### Multiple Namespaces

For datapacks with multiple namespaces, each namespace gets its own nested object:

```kotlin
data object MyDatapack {
	const val PATH = "..."
	const val FORMAT = 81
	val pack = Pack(...)

	data object Minecraft {
		const val NAMESPACE = "minecraft"

		@Serializable(with = Argument.ArgumentSerializer::class)
		enum class Functions : FunctionArgument {
			CUSTOM_TICK,
			;

			override val namespace: String = NAMESPACE
		}
	}

	data object Mypack {
		const val NAMESPACE = "mypack"

		@Serializable(with = Argument.ArgumentSerializer::class)
		enum class Functions : FunctionArgument {
			MY_FUNCTION,
			;

			override val namespace: String = NAMESPACE
		}

		sealed interface Advancements : AdvancementArgument { ... }
	}
}
```

## Architecture

### Core Components

1. **Explorer** (`explorer.kt`): Discovers and parses datapack structure
   - Handles directory and ZIP file formats
   - Identifies namespaces, functions, and all resource types
   - Detects macro arguments in function files
   - Parses pack.mcmeta metadata

2. **Entities** (`entities.kt`): Data models
   - `Datapack`: Complete datapack with functions, resources, and metadata
   - `Function`: Function ID with detected macro arguments
   - `Resource`: Resource ID with type information
   - `PackInfo`: Pack format and metadata

3. **Writer** (`writer.kt`): Main code generation orchestrator
   - Creates datapack Kotlin objects
   - Handles single vs. multiple namespace layouts
   - Coordinates with specialized generators

4. **Code Generators** (`generation/codeGenerators.kt`)
   - `generatePackProperty`: Creates pack metadata with format and description
   - Handles complex JSON descriptions

5. **Enum Generators** (`generation/enumGenerators.kt`)
   - `generateSimpleFunctionsEnum`: Flat enum for non-nested resources
   - `generateFunctionsEnumTree`: Nested sealed interfaces for nested directories
   - `generateSimpleResourceEnum`: Flat enum for non-nested resources
   - `generateResourceEnumTree`: Nested sealed interfaces for nested resources
   - `generateWorldgenObject`: Special object grouping all worldgen resources

6. **Utils** (`generation/utils.kt`)
   - Case conversions and string utilities
   - `ResourceTypeInfo`: Metadata mapping folder names to Kore argument types

7. **API** (`api/DatapackImport.kt`, `api/Configuration.kt`): Public DSL interface

## Usage

### Kotlin DSL API

Import datapacks using the DSL-based API:

```kotlin
import io.github.ayfri.kore.bindings.api.importDatapacks

// Basic import with defaults
importDatapacks {
    url("my_datapack")
    url("https://example.com/pack.zip")
}

// With configuration
importDatapacks {
    configuration {
        skipCache = true // Force redownload from URLs
        outputPath("src/main/kotlin")
        packagePrefix = "com.example.datapacks"
    }

    url("my_datapack") {
        packageName = "com.custom.package"
        remappedName = "CustomPack"
    }

    url("another_pack")
}
```

**Configuration Options:**

Global configuration:
- `debug: Boolean`: Enable debug logging to see resource discovery details (default: false)
- `packagePrefix: String`: Package name prefix (default: "kore.dependencies")
- `outputPath: String`: Output directory for generated code (default: "build/generated/kore/imported")
- `skipCache: Boolean`: Skip cache and force redownload from URLs (default: false)

Per-datapack configuration:
- `packageName: String`: Override package name for this datapack
- `remappedName: String`: Override the generated Kotlin object name

**Debug Option:**

Enable debug logging to see details about datapack exploration:

```kotlin
importDatapacks {
    configuration {
        debug = true // Shows data files found and resource types discovered
    }

    url("my_datapack")
}
```

When enabled, debug output includes:
- Number of data files found during exploration
- Resource types discovered (advancement, tags, recipes, worldgen types, etc.)
- Summary count of each resource type

Local paths are resolved with automatic fallbacks:
1. Direct path (absolute or relative)
2. With `.zip` extension added
3. Common datapack directories

URLs are downloaded automatically to a cache directory inside `%user.home%/.kore/cache/datapacks`.
Use `skipCache(true)` in the configuration to force redownload.

### Tags Support

The importing module correctly handles all tag types, including:
- Standard tags: `tags/block`, `tags/item`, `tags/entity_type`, etc.
- Worldgen tags: `tags/worldgen/structure`, `tags/worldgen/biome`, etc.
- Custom namespace tags from mods and datapacks (e.g., `my_pack:shrine`)

**Namespace Extraction:**
The importer correctly extracts namespaces from nested tag paths. For example:
- Path: `data/my_pack/tags/worldgen/structure/shrine.json`
- Namespace extracted: `my_pack` (not `my_pack/tags`)
- Full resource ID: `my_pack:shrine`

This ensures compatibility with tags from custom mods and datapacks that use non-minecraft namespaces.

### Exploration without Code Generation

```kotlin
import io.github.ayfri.kore.bindings.api.exploreDatapacks

val datapacks = exploreDatapacks {
    url("my_datapack")
    url("another_pack")
}

// Inspect datapack structure
datapacks.forEach { pack ->
    println("Functions: ${pack.functions.size}")
    println("Resources: ${pack.resources.size}")
}
```

### Testing

```bash
./gradlew :bindings:runUnitTests
```

## Implementation Notes

- **Kotlin 2.2.0** with JVM toolchain 17
- **KotlinPoet** for code generation
- **Clikt** for CLI support (future use)
- **kotlinx-serialization** for JSON parsing

## Limitations & Future Work

- CurseForge, Modrinth, and GitHub downloads not yet implemented - see `ISSUE.md` for tracking
- No Gradle plugin yet (DSL is POC for future integration) - Gradle plugin tasks tracked in `ISSUE.md`
- Macro detection is based on regex matching of `$()` syntax
- No conflict resolution for duplicate resource IDs across namespaces
