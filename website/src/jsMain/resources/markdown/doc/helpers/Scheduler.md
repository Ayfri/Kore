---
root: .components.layouts.MarkdownLayout
title: Scheduler
nav-title: Scheduler
description: A guide for scheduling tasks in Kore.
keywords: minecraft, datapack, kore, guide, scheduler, schedule, loop, task, tasks
date-created: 2025-03-26
date-modified: 2025-03-26
routeOverride: /docs/helpers/scheduler
---

# Scheduler in Kore

This document explains how to schedule and run tasks at specific times or intervals using Kore's built-in scheduler. Schedulers help automate recurring actions, delayed tasks, and cleanup when tasks are no longer needed.

## Overview

A "Scheduler" in Kore lets you:

- Schedule a one-time execution of a function or a block of code.
- Schedule repeating tasks with a fixed period.
- Persist references to scheduled tasks so they can be modified or canceled.

All scheduling logic revolves around three core classes:

1. [Scheduler](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/helpers/Scheduler.kt#L34) – Represents a single scheduled task (with optional delay and period).
2. [UnScheduler](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/helpers/UnScheduler.kt#L34) – Cancels, or clears, repeating tasks.
3. [SchedulerManager](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/helpers/SchedulerManager.kt#L42) – Maintains a list of schedulers for a given DataPack and offers convenience methods to add or remove them.

**Note:**
**Schedulers are saved and loaded from a `scheduler_setup` function that is added to the `minecraft/load.json` tag.**

## Basic Usage

Use the DataPack extension function schedulerManager to get or create a SchedulerManager for your datapack:

```kotlin
val datapack = dataPack("my_datapack") {
	// ...
	schedulerManager {
		// ...
	}
	// ...
}
```

Inside the schedulerManager block, you can add schedulers with different behaviors by calling addScheduler. Common calls are:

- `addScheduler(delay)` – Executes once after a given delay.
- `addScheduler(delay, period)` – Executes once after delay, then repeats every period.
- `addScheduler(block)` – Executes right away if no delay or period is specified.

In many cases, you'll pass a function block (Function.() -> Command) so you can include DSL commands:

### One-Time Execution

If you only want to run a function once after a delay:

```kotlin
schedulerManager {
	addScheduler(5.seconds) {
		say("I run after 5 seconds!")
	}
}
```

### Recurrent Execution

For periodic tasks:

```kotlin
schedulerManager {
	addScheduler(3.seconds, 1.seconds) {
		// This code runs first after 3 seconds, then every 1 second.
		debug("Repeated task!")
	}
}
```

### Schedule by reference

You can also schedule a task by reference:

```kotlin
val myFunction = function("my_function") {
	debug("Hello, world!")
}

schedulerManager {
	addScheduler(myFunction, 10.seconds)
}
```

### Canceling a Repeating Task

If you have a repeating scheduler, you can unschedule it when you no longer need it:

1. Pass a named function or store the return value of addScheduler.
2. Call unSchedule or removeScheduler.

Example removing by reference:

```kotlin
val repeatingScheduler = addScheduler(2.seconds, 2.seconds) {
	debug("Repeat every 2 seconds!")
}

// Later in the code, for instance in another function:
unSchedule(repeatingScheduler.function)  // stops the repeating task
```

Or remove by function name:

```kotlin
removeScheduler("my_function_to_remove")
```

### Canceling all tasks

You can cancel all tasks by calling clearSchedulers:

```kotlin
unScheduleAll()
```

## Complex Example

Below is a more advanced scenario showing how to:

1. Run a periodic task (initial delay + repeating period).
2. Store custom data in a named storage using the /data command.
3. Use execute to conditionally run commands based on stored data.

In this example, we keep a running "counter" in a storage and increment it every time the repeating task runs. We also demonstrate how to read from that storage in subsequent commands.

### Full Example

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute
import io.github.ayfri.kore.commands.value
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.EntityTypes

fun DataPack.complexSchedulerExample() {
	// Create a storage reference for storing and reading data
	val myStorage = storage("kore_example:counter_storage")

	// A function that resets the counter at any time
	function("reset_counter") {
		data(myStorage) {
			merge {
				value("counter", 0)
			}
		}
		say("Counter has been reset to 0!")
	}

	schedulerManager {
		// Turn on debug logs for demonstration
		debug = true

		// Create a repeating scheduler. It starts after 2 seconds, repeats every 4 seconds.
		addScheduler(2.seconds, 4.seconds) {
			val tempEntity = entity("#temp_entity")

			// 1) Store the value in a score
			execute {
				storeResult {
					score(tempEntity, "counter")
				}

				run {
					data(myStorage) {
						get("counter")
					}
				}
			}

			// 2) Increment the score
			scoreboard.objective(tempEntity, "counter").add(1)

			// 3) Store the score in the storage
			execute {
				storeResult {
					storage(myStorage, "counter")
				}

				run {
					scoreboard.objective(tempEntity, "counter").get()
				}
			}

			// 4) Print out the current counter using execute + data get
			execute {
				// We can conditionally run commands if the counter is above a threshold, etc.
				run {
					// Show the updated counter in chat every time this repeats
					data(myStorage) {
						get("counter")
					}
					// This prints the raw integer. Let's also do a friendly message:
					say("Counter incremented!")
				}
			}

			// 5) Condition example: if the counter >= 5, summon something or do logic

			// Check if the score is >= 5, as we already stored the value in a score
			execute {
				// Compare the counter with a threshold, e.g. 5
				ifCondition {
					score(tempEntity, "counter") greaterOrEqual 5
				}
				run {
					summon(EntityTypes.LIGHTNING_BOLT)
					say("Counter is >= 5. Summoned lightning!")
				}
			}
		}

		// Another single-run task in 10 seconds to reset everything
		addScheduler(10.seconds) {
			function("reset_counter")
			say("All done, resetting!")
		}
	}
}
```

Explanation:

1. We create a custom storage named "kore_example:counter_storage" to maintain a key called "counter".
2. We set up a repeating task (delayed by 2 seconds, repeats every 4 seconds). Inside that repeating schedule:
	- We store the counter in a score
	- We increment the counter in storage.
	- We show the updated counter using data get and say commands.
	- We run a condition (ifData … >= 5) to check if "counter" has reached 5 or more, then summon a lightning bolt.
3. We also add a single-run scheduler at 10 seconds that calls a function to reset the counter and prints a final message.

## Conclusion

- Schedulers let you automate tasks in your datapack with fixed delays or repetition.
- Use a SchedulerManager on your DataPack via `schedulerManager { … }`.
- Add, remove, or clear schedulers by referencing either the assigned function or the function name.
- Combine schedulers with any usual commands for fully automated or repeated logic (debugging, storing data, advanced "execute" conditions, etc.).

This powerful system helps keep your datapack logic neatly organized and easy to maintain when you need repeated or delayed operations.
