---
root: .components.layouts.MarkdownLayout
title: Test Features
nav-title: Test Features
description: A comprehensive guide for creating test instances and test environments in Minecraft's GameTest framework with Kore.
keywords: minecraft, datapack, kore, guide, test, testing, environment, instance, gametest, automation
date-created: 2025-01-08
date-modified: 2026-02-15
routeOverride: /docs/advanced/test-features
---

# Test Features

Minecraft 1.21.5 (Snapshot 25w03a) introduced a major overhaul to the **GameTest framework** - an automated end-to-end testing system for
datapack functionality. Kore provides a type-safe Kotlin DSL that generates JSON files for the `test_instance` and `test_environment`
registries.

## Test Environments

Test environments define the preconditions under which tests run. There are five types:

### Weather

Sets a fixed weather condition:

```kotlin
testEnvironments {
	weather("clear_weather", Weather.CLEAR)
	weather("rain_weather", Weather.RAIN)
	weather("storm_weather", Weather.THUNDER)
}
```

### Time of Day

Locks the time to a specific tick value:

```kotlin
testEnvironments {
	timeOfDay("morning", 1000)
	timeOfDay("noon", 6000)
	timeOfDay("night", 18000)
}
```

### Game Rules

Overrides game rules for controlled conditions:

```kotlin
testEnvironments {
    gameRules("controlled_env") {
        this[Gamerules.DO_DAYLIGHT_CYCLE] = false
        this[Gamerules.DO_MOB_SPAWNING] = false
        this[Gamerules.RANDOM_TICK_SPEED] = 0
    }
}
```

### Function

Runs datapack functions before (`setup`) and/or after (`teardown`) each test:

```kotlin
val setupFn = function("test_setup") { say("Setting up") }
val teardownFn = function("test_teardown") { say("Tearing down") }

testEnvironments {
	function("my_function_env") {
		setup(setupFn)
		teardown(teardownFn)
	}
}
```

### Combined (allOf)

Merges multiple environments into one:

```kotlin
testEnvironments {
	val rules = gameRules("no_mobs") {
		this[Gamerules.DO_MOB_SPAWNING] = false
	}
	val time = timeOfDay("dawn", 1000)

	allOf("controlled_dawn", rules, time)
}
```

> **Tip:** You can also create environments outside a `testEnvironments` block using `testEnvironmentsBuilder`:
> ```kotlin
> val env = testEnvironmentsBuilder.weather("clear", Weather.CLEAR)
> ```
> This is useful when reusing environments across multiple test instances.

## Test Instances

Test instances define the actual tests. Each one references a structure, an environment, and execution parameters.

### Test Types

- **Block-based** (`blockBased()`): Test logic is driven by redstone inside the structure using special test blocks (Start, Log, Fail,
  Accept).
- **Function-based** (`functionBased()`): Test logic is handled by a Java method reference (used by Mojang internally and mod developers).
  Requires a `function` field with a fully qualified method reference (e.g., `"com.example.MyMod::myTest"`).

### Creating Test Instances

```kotlin
testInstances {
    // Block-based test
	testInstance("redstone_test") {
        blockBased()
        environment(env)
        maxTicks = 100
        structure(Structures.AncientCity.Structures.BARRACKS)
    }

    // Function-based test
    testInstance("function_test") {
        functionBased()
	    environment(env)
	    function("com.example.MyMod::myTest")
        maxTicks = 200
	    structure(Structures.Igloo.TOP)
    }
}
```

### Configuration Options

All available properties for a test instance:

| Property                           | Type                      | Description                                          |
|------------------------------------|---------------------------|------------------------------------------------------|
| `environment(env)`                 | `TestEnvironmentArgument` | **Required.** The test environment to use.           |
| `structure(struct)`                | `StructureArgument`       | **Required.** The structure template for the test.   |
| `blockBased()` / `functionBased()` | -                         | Sets the test type (default: block-based).           |
| `function(fn)`                     | `String`                  | Java method reference for function-based tests.      |
| `maxTicks`                         | `Int`                     | Maximum ticks before timeout (default: 100).         |
| `setupTicks`                       | `Int`                     | Ticks to wait before starting the test.              |
| `maxAttempts`                      | `Int`                     | Maximum retry attempts.                              |
| `requiredSuccesses`                | `Int`                     | Successes needed out of `maxAttempts`.               |
| `required`                         | `Boolean`                 | Whether the test must pass for the suite to succeed. |
| `manualOnly`                       | `Boolean`                 | Whether the test is only run manually.               |
| `skyAccess`                        | `Boolean`                 | Whether the structure needs sky access.              |
| `rotation(rot)`                    | `TestRotation`            | Rotation applied to the structure.                   |

Rotation helpers: `noRotation()`, `clockwise90()`, `rotate180()`, `counterclockwise90()`.

## Structures

Kore provides constants for all vanilla structures:

```kotlin
// Examples
structure(Structures.AncientCity.Structures.BARRACKS)
structure(Structures.Bastion.Treasure.Bases.LAVA_BASIN)
structure(Structures.Igloo.TOP)
structure(Structures.TrialChambers.Chamber.ASSEMBLY)
structure(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1)
```

## Test Commands

### Selectors

```kotlin
allTests()                     // "*:*" - all tests
minecraftTests()              // "minecraft:*" - minecraft tests
testSelector("my_pack:*")     // All tests in namespace
testSelector("*_combat")      // Pattern matching
```

### Command Usage

```kotlin
function("run_tests") {
    test {
        val selector = testSelector("my_pack:test_*")

        run(selector)
        runClosest()
        runMultiple(selector, 5)

        create(TestInstanceArgument("test", "pack"))
        locate(selector)
        pos("variable")

        clearAll()
        resetClosest()
        stop()

        verify(TestInstanceArgument("test1", "pack"),
               TestInstanceArgument("test2", "pack"))
    }
}
```

### In-Game Commands

```mcfunction
/test run my_datapack:basic_test
/test runmultiple my_datapack:basic_test my_datapack:function_test
/test runclosest
/test runfailed
/test create my_datapack:new_test 16 16 16
/test locate my_datapack:test_*
/test clearall 15
/test resetclosest
/test stop
```

## Complete Example

```kotlin
fun DataPack.createTestSuite() {
	val setupFn = function("test_setup") { say("Setting up test") }
	val cleanupFn = function("test_cleanup") { say("Cleaning up test") }
	// Create reusable environments
	val controlled = testEnvironmentsBuilder.gameRules("controlled") {
		this[Gamerules.DO_DAYLIGHT_CYCLE] = false
		this[Gamerules.DO_MOB_SPAWNING] = false
		this[Gamerules.RANDOM_TICK_SPEED] = 0
	}
	val dayTime = testEnvironmentsBuilder.timeOfDay("day", 6000)
	val controlledDay = testEnvironmentsBuilder.allOf("controlled_day", controlled, dayTime)

	// Function environment (setup/teardown)
	testEnvironments {
		function("test_functions") {
			setup(setupFn)
			teardown(cleanupFn)
		}
    }

    testInstances {
        testInstance("redstone_basic") {
            blockBased()
	        environment(controlledDay)
            maxTicks = 100
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }

        testInstance("complex_logic_test") {
            functionBased()
	        environment(controlled)
	        function("com.example.MyMod::myTest")
            maxAttempts = 2
            maxTicks = 200
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }

        testInstance("directional_blocks") {
            blockBased()
            clockwise90()
	        environment(controlled)
            maxTicks = 120
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }
    }
}
```

## Best Practices

- **Combine environments** with `allOf` for complex scenarios.
- **Mark critical tests** as `required = true`.
- **Set appropriate timeouts** with `maxTicks`.
- **Use game rules** to disable randomness (mob spawning, daylight cycle, tick speed).
- **Reuse environments** via `testEnvironmentsBuilder` to avoid duplication.

## See Also

- [Functions](/docs/commands/functions) - Kore's DSL for writing datapack functions.
- [GameTest Framework](https://minecraft.fandom.com/wiki/GameTest_Framework) - Official Minecraft documentation.
