---
root: .components.layouts.MarkdownLayout
title: Minecraft Datapack Functions - Create MCFunctions with Kore DSL
nav-title: Functions
description: Create Minecraft datapack functions with Kore's Kotlin DSL. Build tick.json and load.json tags, organize commands into reusable functions, and generate clean MCFunction output.
keywords: datapack functions, mcfunction, tick.json datapack, load.json datapack, minecraft function tags, tags/function datapack, kore functions, datapack mcfunction generator, function scheduling, minecraft function creator
date-created: 2024-04-06
date-modified: 2026-09-26
routeOverride: /docs/commands/functions
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

To call functions from other datapacks, see [Bindings](/docs/advanced/bindings).

The `function` builder returns a `FunctionArgument` object that you can reuse to call the function from other functions:

```kotlin
val myFunction = function("my_function") {
	say("Hello world!")
}

function("my_second_function") {
	function(myFunction)
}
```

You can also package this pattern into reusable `Function` extensions:

```kotlin
fun Function.myFunction() = function("my_function") {
	say("Hello world!")
}

load {
	function(myFunction())
}
```

That helper may be called from several places without worry. Kore is optimized for recreating the same named function,
so using this pattern stays effectively instant while keeping your code easy to organize.

If you only want to reuse a small block of commands without generating a separate `/function`, prefer a regular
extension instead:

```kotlin
fun Function.saySomething() {
	say("yay")
	say("also, yay")
}

load {
	saySomething()
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

Many common commands have convenience builders like `say`,
`teleport`, etc. See the [Commands](/docs/commands/commands) page for a comprehensive guide with examples.

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

> Note: This is not recommended, but can be useful for commands not yet supported by the DSL, or if you
> use [Macros](/docs/commands/macros).

## Available Commands

All commands from the version cited in the [README](https://github.com/Ayfri/Kore/blob/master/README.md) are available.
For detailed
documentation on each command, see [Commands](/docs/commands/commands).

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

For commands that take complex types as arguments, you should use the `.asArg()` function inside
`literal()` function. For Argument types, you don't have to use this.

See the code of the repository for more examples.<br>
[Link to `time` command.](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/commands/Time.kt)<br>
[Link to `weather` command.](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/commands/Weather.kt)

## Complex Commands

Some commands are more complex and require more than just a few arguments. For example, the
[`execute`](/docs/commands/execute) or [`data`](/docs/concepts/data-storage) commands.

In that case, you can use complex builders that includes all the arguments of the command. But the syntax may vary
depending on the command, so pairing this page with the broader [Commands](/docs/commands/commands) reference and
selector-heavy examples from [Selectors](/docs/concepts/selectors) is often helpful.

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

You can use predicates in the
`ifCondition` block to check complex conditions. See the [Predicates](/docs/data-driven/predicates) documentation for
more details.

You may also have commands where you can create "contexts".

An example of the `data` command:

```kotlin
data(self()) {
	modify("Health", 20)
	modify("Inventory[0]", Items.DIAMOND_SWORD)
}
```

# Macros

See [Macros](/docs/commands/macros).

# Generated Functions

The same way the unnamed `load` and `tick` builders generate their own functions, the
`execute` builder also generates a function if you call multiple commands inside the `run` block.

```kotlin
execute {
	run {
		say("Hello world!")
		say("Hello world2!")
	}
}
```

This will generate a function that will be called by the `execute` command.

> Note: The generated functions will be generated inside a folder named `generated_scopes` in the `functions` folder.
> You can change the folder to whatever you want in [Configuration](/docs/guides/configuration).

> Note: The generated name follows the pattern `generated_<hash>`, where `<hash>` is a hash of the function body
> (`load_<hash>`, `tick_<hash>` and `schedule_<hash>` for the other builders). The same body always gets the same name,
> on every run and platform, and two builders producing the same body reuse a single function.

Your own builders get the same naming with `hashedGeneratedFunction(prefix)`, which names the function
`<prefix>_<hash of its body>`. `generatedFunctionName(prefix, lines)` returns that name alone, for a function you
register yourself:

```kotlin
val onClick = hashedGeneratedFunction("on_click") {
	say("clicked")
}
// data/<namespace>/function/generated_scopes/on_click_<hash>.mcfunction
```

Two generated functions can't share a path with different bodies: `generatedFunction("init")` called twice with two
different blocks throws an `IllegalStateException` instead of silently keeping only the first one. The same goes for
any two files of the pack at generation time, two `function("init")` or two resources with one file name writing
different contents throw instead of dropping one of them.

If you want to turn that into an explicit project pattern, the [Cookbook](/docs/guides/cookbook) shows how to wrap
reusable logic in `Function` extensions with or without dedicated generated functions.

# Debugging

You have multiple ways to debug your functions. First, a `debug` function is available, it is pretty much the same as
`tellraw` but always displaying the message to everyone.

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

This will add a command call to
`tellraw` command, writing the exact command generated, clicking on the text will also call the command. Example of what is generated:

```mcfunction
say hello !
tellraw @a {"type":"text","click_event":{"action":"suggest_command","command":"/say hello !"},"hover_event":{"action":"show_text","value":{"type":"text","color":"gray","italic":true,"text":"Click to copy command"}},"text":"/say hello !"}
```

Calling `startDebug()` yourself, without the matching `endDebug()` the `debug` block adds, keeps debug mode on until
the pack is written: every command gets its log message, and the written file also starts and ends with a message
naming the function. `toString()` and `lines` never contain those two markers, only the written file does.

```kotlin
function("my_function") {
	startDebug()
	say("hello !")
}
```

```mcfunction
tellraw @a [{"type":"text","color":"gray","italic":true,"text":"Running function "},{"type":"text","bold":true,"click_event":{"action":"run_command","command":"function my_datapack:my_function"},"color":"white","hover_event":{"action":"show_text","value":{"type":"text","color":"gray","italic":true,"text":"Click to execute function"}},"italic":true,"text":"my_datapack:my_function"}]
say hello !
tellraw @a {"type":"text","click_event":{"action":"suggest_command","command":"/say hello !"},"hover_event":{"action":"show_text","value":{"type":"text","color":"gray","italic":true,"text":"Click to copy command"}},"text":"/say hello !"}
tellraw @a [{"type":"text","color":"gray","italic":true,"text":"Finished running function "},{"type":"text","bold":true,"click_event":{"action":"run_command","command":"function my_datapack:my_function"},"color":"white","hover_event":{"action":"show_text","value":{"type":"text","color":"gray","italic":true,"text":"Click to execute function"}},"italic":true,"text":"my_datapack:my_function"}]
```

You can call the command by clicking on the debug texts added.

Also running `toString()` in a function will return the generated function as a string, so you can manipulate it as you want.

## See Also

- [Commands](/docs/commands/commands) - Complete command reference
- [Macros](/docs/commands/macros) - Dynamic command arguments
- [Tags](/docs/data-driven/tags) - Function tags for load and tick events
- [Cookbook](/docs/guides/cookbook) - Practical patterns for reusable function helpers

### External Resources

- [Minecraft Wiki: Function](https://minecraft.wiki/w/Function_(Java_Edition)) - Official function format
