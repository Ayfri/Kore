---
root: .components.layouts.MarkdownLayout
title: Macros
nav-title: Macros
description: A guide for using macros in Minecraft functions.
keywords: minecraft, datapack, kore, guide, macros, functions
date-created: 2024-04-06
date-modified: 2024-04-06
routeOverride: /docs/functions/macros
---

# Macros

Macros recently added support for macros inside functions in Minecraft 1.20.2.

## Using Macros

To define a macro, use the `macro()` function:

```kotlin
say("I'm gonna use the macro ${macro("foo")}")
```

Inside a Minecraft function:

```kotlin
function("my_function") {
	say("This is my macro: ${macro("bar")}")
}
```

When called, this will substitute the actual text of the macro.

You can also evaluate a list of macros and have fully dynamic commands:

```kotlin 
eval("command", "arg1", "arg2")
// equals to minecraft code:
// $$(command) $(arg1) $(arg2)
```

## Calling functions with macros

You can call a function with macros by using the new `arguments` argument.

```kotlin
function("my_function", arguments = nbt { this["bar"] = "baz" })
```

That can also be a DataArgument (block position/entity selector/storage).

```kotlin
function(
	"my_function",
	arguments = allEntities {
		type = EntityTypes.MARKER
		name = "test"
	},
	path = "data.test" // optional path is available
)
```

## Defining Macro Classes

For more complex macro usage, you can create a `Macros` subclass to define your macros:

```kotlin
class MyMacros : Macros() {
	val myMacro by "my_macro"
}
```

Then pass an instance to your function:

```kotlin
function("my_function", ::MyMacros) {
	say(macros.myMacro)
} 
```

Now you can access the macros on the `macros` property.

This also allows validating macros that are required when calling the function with an NBT Compound.

Exemple:

```kotlin
class TeleportMacros : Macros() {
	val player by "player"
}

datapack {
	val teleportToSpawn = function("teleport_to_spawn", ::TeleportMacros) {
		teleport(player(macros.player), vec3())
	}

	load {
		function(teleportToSpawn, arguments = nbt { this["name"] = "jeb_" }) // Will throw an error because function expects "player" macro

		function(teleportToSpawn, arguments = nbt { this["player"] = "jeb_" }) // Works fine
	}
}
```

## Best Practice

When using macros, you can create a function with arguments that calls the function with the macros:

```kotlin
fun main() {
	dataPack {
		function("teleport_to_spawn") {
			teleport(player(macro("player")), vec3())
		}
	}
}

fun Function.teleportToSpawn(player: String) {
	function("teleport_to_spawn", arguments = nbt { this["player"] = player })
}
```

Then you can call this function with your argument as a macro:

```kotlin
teleportToSpawn("jeb_")
```

## Limitations

- Macros can only be used in functions.
- Macros aren't variables, they are just text substitutions, you can't do operations on them.
- Macros are not type-checked.
- It would be very difficult and long for Kore to allow macros as any argument of commands because of the wide variety of argument types and contexts in Minecraft commands.