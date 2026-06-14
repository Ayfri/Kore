---
root: .components.layouts.MarkdownLayout
title: Dynamic Strings
nav-title: Dynamic Strings
description: Object-oriented string utilities backed by NBT storage and macros - full Kotlin-like API on top of datapack macros.
keywords: minecraft, datapack, kore, oop, string, nbt, storage, macro, substring, split, replace, trim, pad, concat
date-created: 2026-04-16
date-modified: 2026-05-05
routeOverride: /docs/oop/dynamic-strings
---

# Dynamic Strings

`DynamicString` wraps an NBT slot stored inside the shared `kore_string_lib:memory` storage (path `heap.<name>`) so that
datapack strings can be manipulated with an idiomatic Kotlin API. The module ships a rich set of helpers inspired by
Kotlin's `String` standard library and by the Bookshelf `bs.string` module, but everything is generated lazily: only the
helpers you actually call are materialized as `mcfunction` files inside your pack.

The module targets Minecraft **1.21.11**. Under the hood it leans on:

- `data modify ... set string` for slicing,
- `execute store result` for measuring lengths,
- **macros** (`function ... with storage ...`) for every operation that depends on runtime data (dynamic indices,
  reverse,
  find, replace, case, etc.),
- recursive "controller + step" macro pairs for loops (reverse, find, split, replace, count, case, repeat, pad, trim).

## Registering the module

A single call on your `DataPack` registers the runtime, declares the `kore_string_len` scoreboard objective and hands
you
back a `DynamicStringRuntime` used to lazily allocate helpers:

```kotlin
val stringRuntime = registerDynamicStrings()
```

Every `DynamicString`/`KoreStringList` you build after this point is validated against the runtime, so name collisions
throw a clear error at datapack-generation time rather than producing subtle command conflicts at runtime.

## Creating and mutating a string

```kotlin
val greeting = dynamicString("greeting")
val buffer = dynamicString("buffer")

function("hello") {
	greeting.set("Hello, world!")     // data modify storage ... set value "Hello, world!"
	greeting.setFrom(buffer)          // alias: greeting.set(buffer)
	greeting.copyTo(buffer)           // buffer := greeting
	greeting.clear()                  // data remove storage ... heap.greeting
}
```

`asChatComponents()` exposes the string as an NBT chat component:

```kotlin
tellraw(allPlayers(), greeting.asChatComponents())
```

## Measuring length

`length()` stores the number of characters into the `kore_string_len` objective and returns the score holder name, while
`lengthScore()` returns the typed `(holder, objective)` pair, which is useful when several lengths are kept alive at the
same time:

```kotlin
function("measure") {
	val holder = greeting.length()            // holder == "#kore_string_len"
	val typed  = greeting.lengthScore(holder = "#my_len")
	// typed.holder and typed.objective are now available for arithmetic.
}
```

## Substring, take / drop, charAt

Static bounds translate directly to `data modify ... set string`:

```kotlin
greeting.substring(0, 5)                      // slice in place
greeting.substringTo(buffer, 1, 4)            // slice into another string
greeting.take(3, buffer)                      // buffer := greeting[0..3]
greeting.drop(2, buffer)                      // buffer := greeting[2..]
greeting.takeLast(3, buffer)
greeting.dropLast(4, buffer)
greeting.charAt(5, buffer)                    // single character slot
greeting.setFrom(buffer, start = 2)           // greeting := buffer[2..]
greeting.setFrom(buffer, start = 0, end = 4)  // greeting := buffer[0..4)
```

Runtime bounds go through the hidden macro helper:

```kotlin
greeting.substringDynamic(startEntity, endEntity, target = buffer)
```

## Concatenation, append and prepend

```kotlin
greeting.append(" world")                     // greeting += " world"
greeting += " world"                          // plusAssign alias
greeting.prepend("Hello, ")
concat(target = buffer, greeting, "!", other) // buffer := greeting + "!" + other
greeting.append(other)                         // direct data modify ... append string ...
greeting.prepend(other)                        // direct data modify ... prepend string ...
greeting.appendFrom(other, start = 1)          // append substring [1..]
greeting.prependFrom(other, 0, 3)              // prepend substring [0..3)
```

## Comparison helpers

All comparison helpers write a 0 / 1 flag into a scoreboard holder on the `kore_string_len` objective. Equal maps to `0`
for convenience (same convention as `execute store success`).

```kotlin
greeting.equalsTo("Hello")            // diff == 0 when identical
greeting.isEmpty()                    // empty == 0 when empty
greeting.startsWith("He")
greeting.endsWith(buffer)             // dynamic suffix
```

## Reverse

```kotlin
greeting.reverse(target = buffer)
```

The generator emits a tail recursive macro that slices one character at a time and prepends it to the accumulator.

## Find, indexOf, contains, count

```kotlin
greeting.indexOf("world")             // writes to #kore_string_find
greeting.indexOf(buffer)              // dynamic needle
greeting.contains("hell")             // 0 / 1 flag
greeting.count(",")                   // number of matches
```

Literal and dynamic needles share the same recursive find controller.

## Splitting and listing characters

```kotlin
val parts = koreStringList("parts")
greeting.split(",", parts)
greeting.toList(parts)                // parts = list of 1-character slots
```

Both operations reuse the find / substring primitives and run in-place in a freshly cleared list.

## Replace

```kotlin
greeting.replaceRange(0, 5, "Salut")   // static range, literal or dynamic replacement
greeting.replace("l", "L")             // all occurrences
greeting.replaceFirst("l", "L")        // first only
```

## Case conversion (ASCII)

```kotlin
greeting.uppercase()
greeting.lowercase(target = buffer)
greeting.capitalize()
greeting.decapitalize()
```

A lookup table macro is emitted per direction the first time it is used. Non-ASCII characters are left untouched.

## Repeat, pad, trim

```kotlin
greeting.repeat(3)                    // static count
greeting.padStart(10, padChar = '0')  // pad with leading zeroes
greeting.padEnd(10, padChar = ' ')    // pad with trailing spaces
greeting.trim()                       // strip leading + trailing ASCII whitespace
greeting.trimStart()
greeting.trimEnd()
```

## Parsing and serialization

```kotlin
greeting.parseTo(storage, "some.path")  // evaluates the string content as SNBT and writes into storage
buffer.setFromNbt(storage, "some.path") // serialises an arbitrary NBT value back into the string
```

## Lists with `KoreStringList`

`KoreStringList` wraps an NBT list at `kore_string_lib:memory lists.<name>` and offers the full set of list primitives:

```kotlin
val tokens = koreStringList("tokens")
val current = dynamicString("current")

function("tokenize") {
	tokens.clear()
	tokens.append("alpha")
	tokens.append(current)
	tokens.prepend("beta")
	tokens.insertAt(1, "middle")
	tokens.removeAt(2)
	tokens.setAt(0, current)
	tokens.size()                    // -> #kore_string_len
	tokens.elementAt(0, current)
	tokens[1] into current           // ergonomic alias of elementAt

	tokens.forEach(current) {
		tellraw(allPlayers(), current.asChatComponents())
	}
}
```

`forEach` generates a dedicated macro loop per call site and binds the current element into the `DynamicString` you pass
as the cursor. Inside the block the `Function` receiver is active, so any Kore DSL call is valid.

## Customising the runtime

Every storage slot, scoreboard objective and NBT root is configurable through `DynamicStringConfig`. Pass a
`DynamicStringConfig`
when calling `registerDynamicStrings` to change where the module writes its data, which is particularly useful when
several
modules share a datapack or when you want to inline the module inside an existing convention:

```kotlin
val runtime = registerDynamicStrings(
	DynamicStringConfig(
		argsRoot       = "kore.args",
		heapRoot       = "kore.heap",
		lengthHolder   = "#my_pack.len",
		lengthObjective = "my_pack_len",
		listsRoot      = "kore.lists",
		storageName    = "state",
		storageNamespace = "my_pack",
		tmpRoot        = "kore.tmp",
	),
)
```

Whitespace handled by `trim`, `trimStart` and `trimEnd` is also customizable per datapack:

```kotlin
runtime.trimWhitespace = listOf(" ", "\t", "\n", "\r", "\u00A0") // non-breaking space added
```

## A complete end-to-end example

The following snippet wires every major feature together: it reads a player-provided command argument, normalises it,
splits it on `,`, iterates on every piece, and writes a summary in chat. It exercises macros, recursion, the list
primitives and the customisable runtime.

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.strings.*

fun DataPack.greetingPipeline() {
	val runtime = registerDynamicStrings()
	runtime.trimWhitespace = listOf(" ", "\t", "\n")

	val raw      = dynamicString("raw_input")
	val normal   = dynamicString("normalized")
	val buffer   = dynamicString("scratch")
	val current  = dynamicString("current_token")
	val tokens   = koreStringList("tokens")

	function("greet") {
		raw.set("  Hello, WORLD ,, Kore  ")

		raw.trim(normal)
		normal.lowercase()
		normal.replace(",,", ",")
		normal.split(",", tokens)

		tellraw(allPlayers(), textComponent("Tokens:"))
		tokens.forEach(current) {
			current.trim()
			current.capitalize()
			tellraw(allPlayers(), current.asChatComponents())
		}

		normal.repeat(2, buffer)
		tellraw(allPlayers(), textComponent("Repeated: "))
		tellraw(allPlayers(), buffer.asChatComponents())

		normal.contains("kore") // -> #kore_string_contains
		normal.count("o")       // -> #kore_string_count
	}
}
```

Running the function above on a fresh world produces commands that:

1. write `"  Hello, WORLD ,, Kore  "` in `kore_string_lib:memory heap.raw_input`,
2. allocate (on first call) all the macro helpers required by `trim`, `lowercase`, `replace`, `split`, `forEach`,
   `repeat`, `contains` and `count`,
3. trim / normalise the value into `heap.normalized`,
4. split the normalised value into `lists.tokens` and iterate every item, printing it capitalised,
5. finally duplicate the normalised value and expose two useful scores for downstream command blocks.

Every helper is generated only once per datapack, so calling the same pipeline from several places adds no extra cost.

## See also

- [Scoreboards](/docs/oop/scoreboards) – consume the length / diff / find score holders returned by these helpers.
- [Macros](/docs/commands/macros) – underlying mechanism used by every dynamic helper.
- [Data command](/docs/commands/commands#data-command) – NBT read/write via Kore’s `data` command helpers (same page as
  the full [Commands](/docs/commands/commands) reference).
