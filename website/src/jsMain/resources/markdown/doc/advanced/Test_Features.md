---
root: .components.layouts.MarkdownLayout
title: Test Features
nav-title: Test Features
description: A comprehensive guide for creating test instances and test environments in Minecraft's GameTest framework with Kore.
keywords: minecraft, datapack, kore, guide, test, testing, environment, instance, gametest, automation
date-created: 2025-01-08
date-modified: 2025-01-08
routeOverride: /docs/advanced/test-features
---

# Test Features

Minecraft 1.21.5 (Snapshot 25w03a) introduced a major overhaul to the **GameTest framework
** - an automated end-to-end testing system for datapack functionality. Kore provides comprehensive support using a type-safe Kotlin DSL that generates JSON files for the
`test_instance` and `test_environment` registries.

## Test Environments

Test environments define preconditions and context for automated test execution.

### Creating Test Environments

```kotlin
dataPack("my_datapack") {
    testEnvironments {
        // Environment definitions
    }
}
```

### Environment Types

#### Combined Environments

Combine multiple conditions using `allOf`:

```kotlin
testEnvironments {
    val rules = gameRules("no_mobs") {
        this[Gamerules.DO_MOB_SPAWNING] = false
    }
    val time = timeOfDay("dawn", 1000)
    val weather = weather("rain", Weather.RAIN)

    val combined = allOf("complex_test", rules, time, weather)
}
```

#### Function Environment

Execute setup and teardown functions:

```kotlin
val myFunction = function("test_function") {
    say("Hello, world!")
}

testEnvironments {
    function("setup_env") {
        setup(myFunction)
        teardown(myFunction)
    }
}
```

#### Game Rules Environment

Create controlled testing conditions:

```kotlin
testEnvironments {
    gameRules("controlled_env") {
        this[Gamerules.DO_DAYLIGHT_CYCLE] = false
        this[Gamerules.DO_FIRE_TICK] = false
        this[Gamerules.DO_MOB_SPAWNING] = false
        this[Gamerules.MAX_ENTITY_CRAMMING] = 100
        this[Gamerules.RANDOM_TICK_SPEED] = 0
    }
}
```

#### Time Environment

Ensure consistent timing:

```kotlin
testEnvironments {
    val morningEnv = timeOfDay("morning", 1000)     // Dawn
    val noonEnv = timeOfDay("noon", 6000)           // Noon
    val nightEnv = timeOfDay("night", 18000)        // Midnight
}
```

#### Weather Environment

Control weather conditions:

```kotlin
testEnvironments {
    val clearEnv = weather("clear_weather", Weather.CLEAR)
    val rainEnv = weather("rain_weather", Weather.RAIN)
    val stormEnv = weather("storm_weather", Weather.THUNDER)
}
```

## Test Instances

Test instances define automated tests with structures and execution parameters.

### Test Types

- **Block-Based**: Use test blocks (Start, Log, Fail, Accept) with redstone logic
- **Function-Based**: Use programmatic function control

### Basic Examples

```kotlin
testInstances {
    // Block-based test
    testInstance("basic_test") {
        blockBased()
        environment(env)
        maxTicks = 100
        structure(Structures.AncientCity.Structures.BARRACKS)
    }

    // Function-based test
    testInstance("function_test") {
        environment(env)
        functionBased()
        maxTicks = 200
        structure(Structures.AncientCity.Structures.BARRACKS)

        function {
            setup(myFunction)
            teardown(myFunction)
        }
    }
}
```

### Advanced Configuration

```kotlin
testInstance("advanced_test") {
    clockwise90()                       // Structure rotation
    environment(env)
    functionBased()
    manualOnly = true                   // Manual execution only
    maxAttempts = 3                     // Retry attempts
    maxTicks = 300                      // Timeout
    required = true                     // Must pass
    requiredSuccesses = 2               // Success count needed
    setupTicks = 20                     // Preparation time
    skyAccess = false                   // Structure placement
    structure(Structures.AncientCity.Structures.BARRACKS)

    function {
        setup(myFunction)
        teardown(myFunction)
    }
}
```

## Using Predefined Structures

```kotlin
// Ancient City
structure(Structures.AncientCity.CityCenter.CITY_CENTER_1)
structure(Structures.AncientCity.Structures.BARRACKS)

// Bastion
structure(Structures.Bastion.Treasure.Bases.LAVA_BASIN)
structure(Structures.Bastion.Units.CenterPieces.CENTER_0)

// Simple structures
structure(Structures.Igloo.TOP)
structure(Structures.Shipwreck.RIGHTSIDEUP_FULL)

// Trial Chambers
structure(Structures.TrialChambers.Chamber.ASSEMBLY)
structure(Structures.TrialChambers.Corridor.ENTRANCE_1)

// Villages
structure(Structures.Village.Desert.Houses.DESERT_TEMPLE_1)
structure(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1)
```

## Test Commands

### Test Selectors

```kotlin
// Basic selectors
allTests()                     // "*:*" - all tests
minecraftTests()              // "minecraft:*" - minecraft tests
testSelector("my_pack:*")     // All tests in namespace
testSelector("*_combat")      // Tests ending with "_combat"
testSelector("test_?")        // Single character wildcard
```

### Command Examples

```kotlin
function("test_example") {
    test {
        val selector = testSelector("my_pack:test_*")

        // Basic usage
        run(selector)
        runClosest()
        runMultiple(selector, 5)

        // Management
        create(TestInstanceArgument("test", "pack"))
        locate(selector)
        pos("variable")

        // Cleanup
        clearAll()
        resetClosest()
        stop()

        // Verification
        verify(TestInstanceArgument("test1", "pack"),
               TestInstanceArgument("test2", "pack"))
    }
}
```

## Test Execution

### Manual Commands

```mcfunction
# Basic execution
/test run my_datapack:basic_test
/test runmultiple my_datapack:basic_test my_datapack:function_test
/test runclosest
/test runfailed

# Management
/test create my_datapack:new_test 16 16 16
/test locate my_datapack:test_*
/test pos test_position

# Cleanup
/test clearall 15
/test resetclosest
/test stop
```

## Complete Example

```kotlin
fun DataPack.createTestSuite() {
    // Test functions
    val cleanupFunction = function("test_cleanup") {
        say("Cleaning up test")
    }
    val setupFunction = function("test_setup") {
        say("Setting up test")
    }

    // Environments
    testEnvironments {
        val controlled = gameRules("controlled") {
            this[Gamerules.DO_DAYLIGHT_CYCLE] = false
            this[Gamerules.DO_MOB_SPAWNING] = false
            this[Gamerules.RANDOM_TICK_SPEED] = 0
        }
        val dayTime = timeOfDay("day", 6000)
        val controlledDay = allOf("controlled_day", controlled, dayTime)
    }

    testInstances {
        // Basic redstone test
        testInstance("redstone_basic") {
            blockBased()
            environment(testEnvironmentsBuilder.allOf("controlled_day",
                testEnvironmentsBuilder.gameRules("controlled") {
                    this[Gamerules.DO_DAYLIGHT_CYCLE] = false
                },
                testEnvironmentsBuilder.timeOfDay("day", 6000)
            ))
            maxTicks = 100
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }

        // Complex function test
        testInstance("complex_logic_test") {
            environment(testEnvironmentsBuilder.gameRules("controlled") {
                this[Gamerules.DO_DAYLIGHT_CYCLE] = false
                this[Gamerules.DO_MOB_SPAWNING] = false
                this[Gamerules.RANDOM_TICK_SPEED] = 0
            })
            functionBased()
            maxAttempts = 2
            maxTicks = 200
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)

            function {
                setup(setupFunction)
                teardown(cleanupFunction)
            }
        }

        // Directional test
        testInstance("directional_blocks") {
            blockBased()
            clockwise90()
            environment(testEnvironmentsBuilder.gameRules("controlled") {
                this[Gamerules.DO_DAYLIGHT_CYCLE] = false
            })
            maxTicks = 120
            required = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }

        // Lightning test
        testInstance("lightning_rod_test") {
            blockBased()
            environment(testEnvironmentsBuilder.weather("thunder", Weather.THUNDER))
            maxAttempts = 3
            maxTicks = 400
            skyAccess = true
            structure(Structures.AncientCity.Structures.BARRACKS)
        }

        // Mob spawning test
        testInstance("mob_spawning") {
            blockBased()
            environment(testEnvironmentsBuilder.timeOfDay("night", 18000))
            maxTicks = 300
            setupTicks = 40
            structure(Structures.AncientCity.Structures.BARRACKS)
        }
    }
}
```

## Best Practices

- **Combine environments** with `allOf` for complex scenarios
- **Mark critical tests** as `required = true`
- **Set appropriate timeouts** with `maxTicks`
- **Use controlled conditions** with game rules for predictable results
- **Use predefined structures** from `Structures` constants

## API Reference

### Test Environment Functions

```kotlin
testEnvironments {
    allOf(name, ...environments)
    function(name) { setup(...); teardown(...) }
    gameRules(name) { this[rule] = value }
    timeOfDay(name, time)
    weather(name, weather)
}
```

### Test Instance Functions

```kotlin
testInstances {
    testInstance(name) {
        blockBased() / functionBased()
        environment(env)
        function { setup(...); teardown(...) }
        maxTicks = 100
        required = true
        structure(Structures.Category.STRUCTURE_NAME)
        // rotation, timing, and other properties
    }
}
```

### Game Rules Configuration

```kotlin
gameRules("name") {
    this[Gamerules.DO_DAYLIGHT_CYCLE] = false
    this[Gamerules.DO_FIRE_TICK] = false
    this[Gamerules.DO_MOB_SPAWNING] = false
    this[Gamerules.MAX_ENTITY_CRAMMING] = 100
    this[Gamerules.RANDOM_TICK_SPEED] = 0
}
```

This DSL provides a type-safe way to create automated tests for Minecraft datapacks using the GameTest framework introduced in snapshot 25w03a.
