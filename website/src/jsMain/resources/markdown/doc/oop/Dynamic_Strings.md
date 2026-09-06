---
root: .components.layouts.MarkdownLayout
title: Dynamic Strings
nav-title: Dynamic Strings
description: Manipulate Minecraft datapack strings with a Kotlin-like API - substring, split, join, replace, trim, pad and case conversion backed by NBT storage and macros.
keywords: minecraft, datapack, kore, oop, string, nbt, storage, macro, substring, split, join, replace, trim, pad, concat
date-created: 2026-04-16
date-modified: 2026-09-06
routeOverride: /docs/oop/dynamic-strings
---

# Dynamic Strings

`DynamicString` wraps an NBT slot inside a shared storage (`kore_string_lib:memory`, path `heap.<name>`) so datapack
strings can be manipulated with an idiomatic Kotlin API: `substring`, `split`, `join`, `replace`, `trim`, `padStart`,
`uppercase`, and the rest of the `kotlin.String` vocabulary.

Only the helpers you actually call are materialized as `mcfunction` files, so an unused API costs nothing in the
generated pack.

## Quick start

```kotlin
fun DataPack.hello() {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val buffer = dynamicString("buffer")

	function("greet") {
		greeting.set("  Hello, WORLD  ")
		greeting.trim()
		greeting.lowercase()
		greeting.capitalize(target = buffer)
		tellraw(allPlayers(), buffer.asChatComponents())
	}
}
```

## Concepts

Three things are worth knowing before reading the rest of this page.

**Everything lives in one storage.** Strings sit at `heap.<name>`, lists at `lists.<name>`, macro arguments at
`args.<helper>`, and scratch values at `tmp.<key>`. Every root is configurable, see
[Customising the runtime](#customising-the-runtime).

**Results come back as scoreboard scores, not values.** A datapack cannot return a value, so every helper that computes
something (`length`, `indexOf`, `contains`, `count`, `equalsTo`, …) writes it into a fake-player score on the
`kore_string_len` objective and returns the holder name. Predicates all use the same convention: `1` means true, `0`
means false.

```kotlin
val holder = greeting.contains("kore")   // "#kore_string_contains"
execute {
	ifCondition { score(literal(holder), "kore_string_len", rangeOrInt(1)) }
	run { tellraw(allPlayers(), textComponent("found")) }
}
```

**Most operations write into a target.** Helpers that produce a new string take an optional `target: DynamicString`
parameter defaulting to `this`, so `greeting.uppercase()` mutates in place while `greeting.uppercase(buffer)` leaves the
source untouched.

## Registering the module

One call registers the runtime, declares the `kore_string_len` objective and returns the `DynamicStringRuntime` that
every helper self-registers against:

```kotlin
val stringRuntime = registerDynamicStrings()

val greeting = dynamicString("greeting")            // on the DataPack
val buffer = stringRuntime.dynamicString("buffer")  // or on the runtime
```

Call it before any string helper, otherwise `dynamicString` throws. Names are allocated against the runtime, so a
duplicate name fails at generation time instead of silently sharing a slot. The `kore_string_` prefix is reserved for
the module's own scratch slots and is rejected for user names.

## Creating and mutating a string

```kotlin
greeting.set("Hello, world!")   // data modify ... set value "Hello, world!"
greeting.setFrom(buffer)        // alias: greeting.set(buffer)
greeting.copyTo(buffer)         // buffer := greeting
greeting.clear()                // data remove ... heap.greeting
```

Display it with `asChatComponents()`, which builds an NBT chat component. `interpret` defaults to `true`, so the
content is parsed as a chat component itself; pass `false` to print it as raw text:

```kotlin
tellraw(allPlayers(), greeting.asChatComponents(interpret = false))
```

## Length

`length()` stores the character count into the configured objective and returns the holder name. `lengthScore()`
returns the typed `(holder, objective)` pair, useful when several lengths are alive at once:

```kotlin
val holder = greeting.length()                     // "#kore_string_len"
val typed = greeting.lengthScore("#my_len")        // DynamicStringLength("#my_len", "kore_string_len")
```

## Substring, take and drop

Static bounds compile down to a single `data modify ... set string`, with `kotlin.String.substring` semantics
(`start` inclusive, `end` exclusive, `end = null` meaning "to the end"):

```kotlin
greeting.substring(0, 5)                      // slice in place
greeting.substringTo(buffer, 1, 4)            // slice into another string
greeting.take(3, buffer)                      // buffer := greeting[0..3)
greeting.drop(2, buffer)                      // buffer := greeting[2..]
greeting.takeLast(3, buffer)
greeting.dropLast(4, buffer)
greeting.charAt(5, buffer)                    // single character
greeting.setFrom(buffer, start = 2)           // greeting := buffer[2..]
greeting.setFrom(buffer, start = 0, end = 4)  // greeting := buffer[0..4)
```

`takeLast` and `dropLast` measure the length at runtime, so they cost one extra `execute store result` and go through
the substring macro. Runtime bounds held in scores use the dynamic variants:

```kotlin
greeting.substringDynamic(startEntity, endEntity, target = buffer)
greeting.charAt(indexEntity, buffer)
```

## Concatenation

```kotlin
greeting.append(" world")             // in place
greeting += " world"                  // plusAssign alias
greeting.prepend("Hello, ")
greeting.append(other)                // append another dynamic string
greeting.appendFrom(other, start = 1) // append only other[1..]
greeting.prependFrom(other, 0, 3)     // prepend only other[0..3)
```

`concat` writes `a + b` into a target and has an overload for every literal / dynamic combination. Two literals are
folded at generation time into a single `set value`, and writing into one of the operands is safe: the operand is read
before it is overwritten.

```kotlin
concat(buffer, "Hello, ", "world")    // -> data modify ... set value "Hello, world"
concat(buffer, greeting, "!")
concat(greeting, other, greeting)     // aliasing is handled, becomes a prepend
```

For more than two operands, `concatAll` takes `StringPart`s built with the `asStringPart` extension available on both
`String` and `DynamicString`. Passing no part clears the target:

```kotlin
concatAll(buffer, greeting.asStringPart, ", ".asStringPart, other.asStringPart)
```

## Comparisons

Every comparison writes a `0` / `1` flag into a holder on the length objective and returns a `DynamicStringEquality`
carrying the holder and objective. Each one accepts an optional `resultHolder` so several results can stay alive at the
same time.

```kotlin
greeting.equalsTo("Hello")        // -> #kore_string_equals
greeting.equalsTo(buffer)         // dynamic operand
greeting.isEmpty()                // -> #kore_string_is_empty
greeting.startsWith("He")         // -> #kore_string_starts
greeting.endsWith(buffer)         // -> #kore_string_ends, runtime suffix length
```

`startsWith` and `endsWith` reject an empty literal, which would always match.

## Searching

```kotlin
greeting.indexOf("world")   // -> #kore_string_find, -1 when absent
greeting.indexOf(buffer)    // dynamic needle
greeting.contains("hell")   // -> #kore_string_contains, 0 / 1
greeting.count(",")         // -> #kore_string_count
```

All four share one recursive find controller that walks the string one offset at a time, so their cost grows with the
length of the haystack. An empty needle is rejected.

## Replacing

```kotlin
greeting.replaceRange(0, 5, "Salut")   // static range, literal or dynamic replacement
greeting.replace("l", "L")             // every occurrence
greeting.replaceFirst("l", "L")        // first occurrence only
```

`replace` resumes the search past the text it just inserted, so a growing replacement such as `replace("a", "aa")`
terminates instead of matching its own output forever.

## Case conversion (ASCII)

```kotlin
greeting.uppercase()
greeting.lowercase(target = buffer)
greeting.capitalize()      // first character only
greeting.decapitalize()
```

A translation table is written to `tables.<direction>` on world load, and each character is mapped through a single
`set from` on that table instead of a 26-branch `if` chain. `capitalize` and `decapitalize` map their single character
directly through the table, skipping the per-character loop entirely.

Characters with no table entry, including every non-ASCII one, come out unchanged. Double quotes and backslashes skip
the lookup because they cannot be used as an NBT path key; neither has a case, so the result is the same either way.

## Trim, pad and repeat

```kotlin
greeting.trim()                       // strip leading + trailing whitespace
greeting.trimStart()
greeting.trimEnd()
greeting.padStart(10, padChar = '0')  // no-op when already 10 characters or longer
greeting.padEnd(10, padChar = ' ')
greeting.repeat(3)                    // static count, 0 clears, negative throws
```

The whitespace set is `DynamicStringConfig.trimWhitespace`, see below.

## Reverse

```kotlin
greeting.reverse(target = buffer)
```

A tail-recursive macro walks the string from its last character to its first, appending each one to an accumulator. The
recursion depth equals the string length, so it is bounded by the
[`maxCommandChainLength`](https://minecraft.wiki/w/Game_rule#Miscellaneous) game rule (65 536 by default).

## Splitting and lists

```kotlin
val parts = koreStringList("parts")
greeting.split(",", parts)   // Kotlin semantics: empty tokens are preserved
greeting.toList(parts)       // one element per character
```

Both clear the target list first and reuse the find / substring primitives.

`KoreStringList` wraps an NBT list at `lists.<name>` and offers the usual list primitives:

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
	tokens.size()              // -> #kore_string_len
	tokens.elementAt(0, current)
	tokens[1] into current     // ergonomic alias of elementAt

	tokens.forEach(current) {
		tellraw(allPlayers(), current.asChatComponents())
	}
}
```

`forEach` generates a dedicated macro loop per call site and binds the current element into the `DynamicString` you
pass as the cursor. Inside the block the `Function` receiver is active, so any Kore DSL call is valid.

## Joining a list back into a string

`join` is the inverse of `split`: it walks the list once and appends every element into a target string, inserting the
separator between them and wrapping the result with an optional `prefix` / `postfix`, exactly like
`kotlin.collections.joinToString`.

```kotlin
val tokens = koreStringList("tokens")
val summary = dynamicString("summary")

function("show_tokens") {
	tokens.clear()
	tokens.append("alpha")
	tokens.append("beta")

	tokens.join(", ", summary, prefix = "Tokens: ")   // "Tokens: alpha, beta"
	tellraw(allPlayers(), summary.asChatComponents(interpret = false))
}
```

The target is overwritten, an empty list leaves it as `prefix + postfix`, and the separator only lands between
elements, never at the ends. Paired with `split`, it round-trips a list through a single string, which is how a list
fits in an item name, a sign line or one storage field:

```kotlin
tokens.join(",", csv)        // csv := "alpha,beta"
csv.split(",", tokens)       // back to a list
```

## Parsing and serialization

`parseTo` evaluates the string content as SNBT and writes the resulting value anywhere, which covers numbers (`42`,
`3.14`, `1L`), booleans, quoted strings and full NBT literals such as `{a: 1, b: [1,2,3]}`. `setFromNbt` goes the other
way, turning an arbitrary NBT value into its textual form:

```kotlin
greeting.parseTo(storage, "some.path")
buffer.setFromNbt(storage, "some.path")
```

## Customising the runtime

Every storage slot, scoreboard objective and NBT root is configurable through `DynamicStringConfig`. This matters when
several modules share a datapack, or when you want the module to live inside an existing storage convention:

```kotlin
val runtime = registerDynamicStrings(
	DynamicStringConfig(
		argsRoot = "kore.args",
		heapRoot = "kore.heap",
		lengthHolder = "#my_pack.len",
		lengthObjective = "my_pack_len",
		listsRoot = "kore.lists",
		storageName = "state",
		storageNamespace = "my_pack",
		tablesRoot = "kore.tables",
		tmpRoot = "kore.tmp",
	),
)
```

`trimWhitespace` belongs to the same config. Its entries land verbatim inside the generated commands, so they use SNBT
escapes, not Kotlin ones:

```kotlin
registerDynamicStrings(
	DynamicStringConfig(trimWhitespace = listOf(" ", "\\t", "\\n", "\\r", "\\u00A0")),
)
```

Every field defaults to the matching `OopConstants.string*` value, so changing one of those moves the default for every
datapack in the project instead of for a single one.

## End-to-end example

A pack setting is a good fit for these helpers: admins edit one string with a single `/data modify`, the pack turns it
into typed values. The alternative is one `execute if data` per accepted value, hardcoded in the pack, which caps the
setting to the values you thought of when writing it.

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.strings.*

fun DataPack.packConfig() {
	registerDynamicStrings()

	val settings = storage("settings", "my_pack")   // /data modify storage my_pack:settings config set value "Spawn_Radius = 64 ; hud = on"
	val raw = dynamicString("config_raw")
	val entry = dynamicString("config_entry")
	val key = dynamicString("config_key")
	val value = dynamicString("config_value")
	val summary = dynamicString("config_summary")
	val entries = koreStringList("config_entries")
	val pair = koreStringList("config_pair")

	function("load_config") {
		raw.setFromNbt(settings, "config")
		raw.trim()
		raw.lowercase()
		raw.split(";", entries)

		entries.forEach(entry) {
			entry.trim()
			entry.split("=", pair)
			pair[0] into key
			pair[1] into value

			value.trim()
			value.parseTo(settings, "pending")   // "64" -> the int 64, usable by execute store / if data

			val isRadius = key.equalsTo("spawn_radius")
			execute {
				ifCondition { score(literal(isRadius.holder), isRadius.objective, rangeOrInt(1)) }
				run { data(settings) { modify("spawn_radius") { set(settings, "pending") } } }
			}
		}

		entries.join(", ", summary, prefix = "Config loaded: ")
		tellraw(allPlayers(), summary.asChatComponents(interpret = false))
	}
}
```

The `lowercase` pass makes `Spawn_Radius` and `spawn_radius` the same key, and `trim` absorbs the spaces around each
separator. An entry without `=` leaves `pair[1]` out of range, so check `pair.size()` first when the input is
player-written.

Every helper is generated once per datapack, so calling the same pipeline from several places adds no extra function.

## Cost and limits

Helpers fall into three tiers, worth keeping in mind when a string is long or a helper runs every tick:

| Tier           | Helpers                                                                                                                                           | Cost                                               |
|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------|
| Constant       | `set`, `setFrom`, `clear`, `substring`, `substringTo`, `take`, `drop`, `charAt(Int)`, `append`, `prepend`, `concat`                               | 1 to 3 commands, no macro                          |
| One macro call | `substringDynamic`, `takeLast`, `dropLast`, `capitalize`, `decapitalize`, `parseTo`, `setFromNbt`                                                 | a handful of commands plus one function call       |
| Recursive      | `reverse`, `indexOf`, `contains`, `count`, `replace`, `split`, `join`, `toList`, `uppercase`, `lowercase`, `trim`, `repeat`, `padStart`, `padEnd` | one function call per character, offset or element |

Recursive helpers are bounded by the `maxCommandChainLength` game rule (65 536 by default), which is far above any
realistic string length but is the hard ceiling.

The module handles ASCII text. Case conversion only maps `a-z` / `A-Z`, and indices are NBT string indices, so
characters outside the Basic Multilingual Plane do not behave like single characters.

## See also

- [Scoreboards](/docs/oop/scoreboards) – consume the length / find / predicate score holders returned by these helpers.
- [Macros](/docs/commands/macros) – the underlying mechanism used by every dynamic helper.
- [Data command](/docs/commands/commands#data-command) – NBT read/write via Kore's `data` command helpers.
