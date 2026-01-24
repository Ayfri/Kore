---
root: .components.layouts.MarkdownLayout
title: Functions
nav-title: Functions
description: A guide for creating functions in a datapack using Kore.
keywords: minecraft, datapack, kore, guide, functions
date-created: 2024-04-06
date-modified: 2024-04-06
routeOverride: /docs/functions
---

# Functions

Functions represent reusable pieces of logic callable in a datapack.

Create a function with the `function` builder:

```kotlin
function("my_function") {
	say("Hello world!")
}
```

Then in game, call the function with `/function my_datapack:my_function`.

To call functions from other datapacks, see [Bindings](./bindings).

The `function` builder returns a `FunctionArgument` object that you can reuse to call the function from other functions:

```kotlin
val myFunction = function("my_function") {
	say("Hello world!")
}

function("my_second_function") {
	function(myFunction)
}
```

## Tags

You can set the tag of the current function you're working in with the `setTag` function:

```kotlin
function("my_function") {
	setTag(tagFile = "load", tagNamespace = "minecraft")
}
```

This will add the function to the `minecraft:load` tag.

But you have simpler builders for the most common tags:

```kotlin
load {
	say("Hello world!")
}

tick {
	execute {
		ifCondition(myPredicate)

		run {
			say("Hello world!")
		}
	}
}
```

- `load` tag: `minecraft:load`
- `tick` tag: `minecraft:tick`

This will create functions with randomly generated names, but you can also specify the name of the function:

```kotlin
load("my_load_function") {
	say("Hello world!")
}
```

# Commands

Many common commands have convenience builders like `say`, `teleport`, etc.

For example:

```kotlin
function("commands") {
	say("Hello!") // say command
	teleport(player("Steve"), 100.0, 64.0, 100.0) // tp command
}
```

You can also build raw command strings and execute them:

```kotlin
addLine("say Hello from raw command!")
```

> Note: This is not recommended, but can be useful for commands not yet supported by the DSL, or if you use [Macros](./functions/macros).

## Available Commands

All commands from the version cited in the [README](https://github.com/Ayfri/Kore/README.md) are available.

## Custom Commands

You can pretty easily add new commands by creating your own builders. For example, imagine you created a mod that adds a new command
`/my_command` that takes a player name and a message as arguments.

You can create a builder for this command like this:

```kotlin
import io.github.ayfri.kore.functions.Function

fun Function.myCommand(player: String, message: String) = addLine(command("my_command", literal(player), literal(message)))
```

Then you can use it like any other command:

```kotlin
function("my_function") {
	myCommand("Steve", "Hello!")
}
```

For commands that take complex types as arguments, you should use the `.asArg()` function inside `literal()` function. For Argument types,
you don't have to use this.

See the code of the repository for more examples.<br>
[Link to `time` command.](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/commands/Time.kt)<br>
[Link to `weather` command.](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/commands/Weather.kt)

## Complex Commands

Some commands are more complex and require more than just a few arguments. For example, the `execute` or `data` commands.

In that case, you can use complex builders that includes all the arguments of the command. But the syntax may vary depending on the command
and you should definitely check the tests to see how to use them.

An example of the `execute` command:

```kotlin
execute {
	asTarget(allEntities {
		limit = 3
		sort = Sort.RANDOM
	})

	ifCondition {
		score(self(), "test") lessThan 10
		predicate(myPredicate)
	}

	run { // be sure to import the run function, do not use the one from kotlin.
		teleport(entity)
	}
}
```

You can use predicates in the `ifCondition` block to check complex conditions. See the [Predicates](./predicates) documentation for more
details.

You may also have commands where you can create "contexts".

An example of the `data` command:

```kotlin
data(self()) {
	modify("Health", 20)
	modify("Inventory[0]", Items.DIAMOND_SWORD)
}
```

# Macros

See [Macros](./functions/macros).

# Generated Functions

The same way the `load` and `tick` builders generate functions with random names, the `execute` builder also generates a function with a
random name if you call multiple commands inside the `run` block.

```kotlin
execute {
	run {
		say("Hello world!")
		say("Hello world2!")
	}
}
```

This will generate a function with a random name that will be called by the `execute` command.

> Note: The generated functions will be generated inside a folder named `generated_scopes` in the `functions` folder.
> You can change the folder to whatever you want in [Configuration](./configuration).

> Note: The generated name will have this pattern `generated_${hashCode()}`, where `hashCode()` is the hash code of the function.
> This means that if you use the same `execute` builder multiple times, it will generate the same function name and reuse the same function.

# Debugging

You have multiple ways to debug your functions. First, a `debug` function is available, it is pretty much the same as `tellraw` but always
displaying the message to everyone.

```kotlin
function("my_function") {
	debug("Hello world!", Color.RED)
}
```

You also have a `debug` block for printing a log message to the console for each command you call inside the block.

```kotlin
function("my_function") {
	debug {
		say("hello !")
	}
}
```

This will add a command call to `tellraw` command, writing the exact command generated, clicking on the text will also call the command.
Example of what is generated:

```mcfunction
say hello !
tellraw @a {"text":"/say hello !","click_event":{"action":"suggest_command","command":"say hello !"},"hoverEvent":{"action":"show_text","value":{"text":"Click to copy command","color":"gray","italic":true}}}
```

The last example is a function call to `startDebug()` (which is called by the `debug` block), this will add log messages to the start and
the end of the function, plus a log message for each command called inside the function.

```mcfunction
tellraw @a [{"text":"Running function ","color":"gray","italic":true},{"text":"my_datapack:my_function","color":"white","bold":true,"click_event":{"action":"run_command","command":"function my_datapack:my_function"},"hoverEvent":{"action":"show_text","value":{"text":"Click to execute function","color":"gray","italic":true}},"italic":true}]
say hello !
tellraw @a {"text":"/say hello !","click_event":{"action":"suggest_command","command":"say hello !"},"hoverEvent":{"action":"show_text","value":{"text":"Click to copy command","color":"gray","italic":true}}}
tellraw @a [{"text":"Finished running function ","color":"gray","italic":true},{"text":"my_datapack:my_function","color":"white","bold":true,"click_event":{"action":"run_command","command":"function my_datapack:my_function"},"hoverEvent":{"action":"show_text","value":{"text":"Click to execute function","color":"gray","italic":true}},"italic":true}]
```

You can call the command by clicking on the debug texts added.

Also running `toString()` in a function will return the generated function as a string, so you can manipulate it as you want.
