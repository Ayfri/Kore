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
		greeting += '!'      // "hello, world!"

		buffer.build {       // "kore says: hello, world!"
			+"kore says: "
			+greeting
		}
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
`kore_string_len` objective and returns a `DynamicStringResult`. Predicates all use the same convention: `1` means true,
`0` means false. `then` and `otherwise` branch on it, `matching` takes any range.

```kotlin
(greeting contains "kore").then { tellraw(allPlayers(), textComponent("found")) }
greeting.count(",").matching(rangeOrIntStart(3)) { tellraw(allPlayers(), textComponent("too many")) }
```

A `DynamicStringResult` **is** a `ScoreboardEntity`, so it also feeds straight into any helper taking a runtime score,
and into the whole scoreboard DSL:

```kotlin
bar.repeat(greeting.length())            // as many bars as characters
myScore.copyFrom(greeting.indexOf("="))  // persist a result before the next call reuses the holder
```

**Most operations write into a target.** Helpers that produce a new string take an optional `target: DynamicString`
parameter defaulting to `this`, so `greeting.uppercase()` mutates in place while `greeting.uppercase(buffer)` leaves the
source untouched. Each one also has an expression form (`uppercased()`, `trimmed()`, `repeated(3)`, `paddedStart(4)`, …)
writing into a fresh anonymous slot and returning it, so transformations chain without declaring a scratch string.

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

`length()` stores the character count into the configured objective and returns it as a `DynamicStringResult`. Pass a
custom holder when several lengths must stay alive at once:

```kotlin
val len = greeting.length()             // DynamicStringResult("#kore_string_len", "kore_string_len")
val alive = greeting.length("#my_len")  // a second, independent length
buffer.padStart(len, '0')               // a result is a score, so it drives any runtime-width helper
```

## Substring, take and drop

Indexing slices into a fresh anonymous slot and returns it, with inclusive Kotlin range semantics on the indices:

```kotlin
greeting.set("minecraft")
val initial = greeting[0]   // "m"
val word = greeting[0..3]   // "mine"
val tail = greeting[4..<9]  // "craft"
```

The named forms write into an explicit target (defaulting to `this`) instead of allocating one, with
`kotlin.String.substring` semantics (`start` inclusive, `end` exclusive, `end = null` meaning "to the end"). All of them
compile down to a single `data modify ... set string`:

```kotlin
greeting.substring(0, 4)             // "mine", in place
greeting.substring(0..3)             // same slice, range form
greeting.substringTo(buffer, 4, 9)   // buffer = "craft"
greeting.take(4, buffer)             // buffer = "mine"
greeting.drop(4, buffer)             // buffer = "craft"
greeting.takeLast(5, buffer)         // buffer = "craft"
greeting.dropLast(5, buffer)         // buffer = "mine"
greeting.charAt(1, buffer)           // buffer = "i"
greeting.setFrom(buffer, start = 2)  // greeting = buffer[2..]
```

`takeLast` and `dropLast` measure the length at runtime, so they cost one extra `execute store result` and go through
the substring macro. Runtime bounds held in scores use the dynamic variants:

```kotlin
// startEntity holds 4 and endEntity holds 9 at runtime
greeting.substringDynamic(startEntity, endEntity, target = buffer)  // buffer = "craft"
greeting.charAt(indexEntity, buffer)                                // buffer = "c"
```

## Concatenation

```kotlin
greeting.set("Hello")
greeting += ", world"  // "Hello, world"
greeting += '!'        // "Hello, world!"
greeting += other      // append another dynamic string
greeting += kills      // append a score, rendered as its decimal value

val fullName = first + " " + last  // expression form, into a fresh anonymous slot
```

`build` rewrites a string from any number of operands: literals, characters, other dynamic strings and scores, each
pushed with a `+`. Consecutive literals are folded at generation time into a single `set value`, the first operand seeds
the target instead of being appended to it, and writing into one of the operands is safe because it is read before it is
overwritten.

```kotlin
summary.build {
	+"Player "
	+playerName  // another DynamicString
	+": "
	+kills       // a ScoreboardEntity, rendered as its decimal value
	+'!'
}
```

An empty block clears the target, and `plus` leaves both its operands untouched.

The named forms cover prepending and partial operands, which have no operator:

```kotlin
greeting.append(", world")             // same as +=
greeting.prepend("> ")                 // "> Hello, world!"
greeting.appendFrom(other, start = 1)  // append only other[1..]
greeting.prependFrom(other, 0, 3)      // prepend only other[0..3)
```

## Comparisons

Every comparison writes a `0` / `1` flag into a holder on the length objective and returns a `DynamicStringResult`.
`eq`, `startsWith` and `endsWith` are infix, so a comparison reads like a condition:

```kotlin
greeting.set("minecraft")
greeting eq "minecraft"     // 1  -> #kore_string_equals
greeting eq buffer          // dynamic operand
greeting.isEmpty()          // 0  -> #kore_string_is_empty
greeting startsWith "mine"  // 1  -> #kore_string_starts
greeting endsWith buffer    // -> #kore_string_ends, runtime suffix length

(greeting startsWith "mine").then { tellraw(allPlayers(), textComponent("a mine")) }
(greeting eq buffer).otherwise { buffer.setFrom(greeting) }
```

`equalsTo` is the non-infix form of `eq`, and takes an optional `resultHolder` when several equality results must stay
alive at the same time.

`startsWith` and `endsWith` reject an empty literal, which would always match.

## Searching

```kotlin
greeting.set("minecraft")
greeting.indexOf("craft")  // 4   -> #kore_string_find, -1 when absent
greeting.indexOf("kore")   // -1
greeting.indexOf(buffer)   // dynamic needle
greeting contains "mine"   // 1   -> #kore_string_contains, 0 / 1
greeting.count("a")        // 1   -> #kore_string_count
```

All four share one recursive find controller that walks the string one offset at a time, so their cost grows with the
length of the haystack. An empty needle is rejected.

## Replacing

```kotlin
greeting.set("minecraft")
greeting -= "mine"                  // "craft", strips every occurrence

greeting.set("minecraft")
greeting.replaceRange(0, 4, "war")  // "warcraft", literal or dynamic replacement
greeting.replace("a", "4")          // "w4rcr4ft", every occurrence
greeting.replaceFirst("r", "R")     // "w4Rcr4ft", first occurrence only
```

Both the needle and the replacement accept a `DynamicString`, so a find-and-replace can be driven entirely by values
computed in game. An empty runtime needle replaces nothing instead of looping forever:

```kotlin
greeting.replace(needle, replacement)
greeting.replaceFirst("-", replacement)
```

`replace` resumes the search past the text it just inserted, so a growing replacement such as `replace("a", "aa")`
terminates instead of matching its own output forever.

## Case conversion (ASCII)

```kotlin
greeting.set("KoRe 42")
greeting.uppercase()                 // "KORE 42"
greeting.lowercase(target = buffer)  // buffer = "kore 42"
buffer.capitalize()                  // "Kore 42", first character only
buffer.decapitalize()                // "kore 42"
```

A translation table is written to `tables.<direction>` on world load, and each character is mapped through a single
`set from` on that table instead of a 26-branch `if` chain. `capitalize` and `decapitalize` map their single character
directly through the table, skipping the per-character loop entirely.

Characters with no table entry, including every non-ASCII one, come out unchanged. Double quotes and backslashes skip
the lookup because they cannot be used as an NBT path key; neither has a case, so the result is the same either way.

## Trim, pad and repeat

```kotlin
greeting.set("  hi  ")
greeting.trimStart()  // "hi  "
greeting.trimEnd()    // "hi"
greeting.trim()       // strips both ends at once

greeting.set("42")
greeting.padStart(5, padChar = '0')  // "00042", no-op when already 5 characters or longer
greeting.padEnd(7, padChar = '.')    // "00042..", pads on the right instead

greeting.set("ab")
greeting *= 3       // "ababab", 0 clears, negative throws
greeting.repeat(3)  // named form, takes an explicit target
```

The width and the count also accept a score, so a progress bar or an aligned column can be sized in game:

```kotlin
// width holds 5 and count holds 3 at runtime
greeting.padStart(width, '0')  // "00042"
bar.repeat(count)              // "|||" when bar holds "|"
```

The whitespace set is `DynamicStringConfig.trimWhitespace`, see below.

## Reverse

```kotlin
greeting.reverse(target = buffer)
```

A tail-recursive macro walks the string from its last character to its first, appending each one to an accumulator. The
recursion depth equals the string length, so it is bounded by the
[`max_command_sequence_length`](https://minecraft.wiki/w/Game_rule#Miscellaneous) game rule (65 536 by default).

## Splitting and lists

```kotlin
val parts = koreStringList("parts")

greeting.set("a,b,,c")
greeting.split(",", parts)        // ["a", "b", "", "c"], empty tokens are preserved
greeting.split(separator, parts)  // the delimiter can itself be a DynamicString

greeting.set("kore")
greeting.toList(parts)  // ["k", "o", "r", "e"], one element per character
```

Both clear the target list first and reuse the find / substring primitives. A runtime delimiter that turns out to be
empty leaves the list empty rather than looping forever.

`KoreStringList` wraps an NBT list at `lists.<name>` and offers the usual list primitives:

```kotlin
val tokens = koreStringList("tokens")
val current = dynamicString("current")

function("tokenize") {
	tokens.clear()
	tokens += "alpha"          // append a literal
	tokens += current          // append a dynamic string
	val second = tokens[1]     // read into a fresh anonymous slot

	tokens.prepend("beta")
	tokens.insertAt(1, "middle")
	tokens.removeAt(2)
	tokens.setAt(0, current)
	tokens.elementAt(0, current)  // read into an explicit target
	tokens.size()                 // -> DynamicStringResult on #kore_string_len

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

## Numbers and scores

Scores are the only values a datapack can compute, so `DynamicString` converts in both directions. `setFrom(score)`
renders a score as text, `toScore` parses the text back into a score:

```kotlin
val kills = ScoreboardEntity("stats", fakePlayer("#kills"))

label.setFrom(kills)      // "7"
label.padStart(3, '0')    // "007"
label.prepend("Kills: ")  // "Kills: 007"

label.appendFrom(kills)   // append a score to an existing string
label.prependFrom(kills)  // prepend one

input.toScore(kills)      // "42" -> the score 42
```

This is what makes a formatted clock, a leaderboard line or a numeric config value work without a `score` chat
component, and the result stays a string that `padStart`, `split` or `join` can keep working on.

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

## End-to-end example: a typewriter dialogue box

Revealing a line of dialogue one character at a time is a staple of adventure maps, and it is the exact thing vanilla
cannot do: a text component is fixed at write time, so the usual workarounds are one hardcoded `tellraw` per frame
(`"T"`, `"Th"`, `"The"`, …) or shipping a third-party string library such as
[String-Parser](https://github.com/5uso/String-Parser) and driving it by hand.

With a `DynamicString` the effect is a growing substring, and the dialogue text stays a plain Kotlin string:

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ticks
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.add
import io.github.ayfri.kore.scoreboard.set
import io.github.ayfri.kore.strings.*
import io.github.ayfri.kore.commands.function as callFunction

fun DataPack.dialogueTypewriter() {
	registerDynamicStrings()

	val line = dynamicString("dialogue_line")
	val shown = dynamicString("dialogue_shown")
	val start = ScoreboardEntity("dialogue", fakePlayer("#start"))
	val cursor = ScoreboardEntity("dialogue", fakePlayer("#cursor"))

	function("dialogue_tick") {
		cursor.add(1)
		line.substringDynamic(start, cursor, target = shown)   // shown := line[0..cursor)
		title(allPlayers(), TitleLocation.ACTIONBAR, shown.asChatComponents(interpret = false))
		playSound(SoundEvents.Block.NoteBlock.HAT, target = allPlayers())

		val length = line.length()
		execute {
			ifCondition {
				score(cursor.entity.asScoreHolder(), cursor.name, literal(length.holder), length.objective, Relation.LESS_THAN)
			}
			run { schedule(1.ticks, "${datapack.name}:dialogue_tick") }
		}
	}

	function("dialogue_start") {
		line.set("The keeper looks up. You made it.")
		start.set(0)
		cursor.set(0)
		callFunction(datapack.name, "dialogue_tick")
	}
}
```

The whole effect is a handful of commands per tick, one of them the substring macro, and the text is never
duplicated: changing the line, translating it, or
feeding it from a sign, a book or a `KoreStringList` of lines only touches `line`. `substringDynamic` reads its bounds
from scores, so the same function drives a slow reveal, an instant skip (set `cursor` to the length) or a scrolling
window (advance `start` too).

## Another example: parsing a config line

A pack setting is the other everyday fit: admins edit one string with a single `/data modify`, the pack turns it into
typed values. The alternative is one `execute if data` per accepted value, hardcoded, which caps the setting to the
values you thought of when writing the pack.

```kotlin
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
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
			pair.elementAt(0, key)
			pair.elementAt(1, value)

			value.trim()
			value.parseTo(settings, "pending")   // "64" -> the int 64, usable by execute store / if data

			(key eq "spawn_radius").then {
				data(settings) { modify("spawn_radius") { set(settings, "pending") } }
			}
		}

		entries.join(", ", summary, prefix = "Config loaded: ")
		tellraw(allPlayers(), summary.asChatComponents(interpret = false))
	}
}
```

The `lowercase` pass makes `Spawn_Radius` and `spawn_radius` the same key, and `trim` absorbs the spaces around each
separator. An entry without `=` leaves index `1` of the pair out of range, so check `pair.size()` first when the input is
player-written.

Every helper is generated once per datapack, so calling the same pipeline from several places adds no extra function.

## Operator reference

Every operator is a thin alias over the named helper, so it carries the exact same cost.

| Operator              | Equivalent                   | Notes                                                  |
|-----------------------|------------------------------|--------------------------------------------------------|
| `s += "x"` / `+= 'x'` | `s.append("x")`              | also accepts a `DynamicString` or a `ScoreboardEntity` |
| `s -= "x"`            | `s.replace("x", "")`         | strips every occurrence, literal or dynamic needle     |
| `s *= 3`              | `s.repeat(3)`                | also accepts a runtime count as a `ScoreboardEntity`   |
| `a + b`               | copy of `a` then `append(b)` | expression form, writes into a fresh anonymous slot    |
| `s[2]` / `s[1..3]`    | `charAt` / `substringTo`     | inclusive range, writes into a fresh anonymous slot    |
| `list += "x"`         | `list.append("x")`           | also accepts a `DynamicString`                         |
| `list[1]`             | `list.elementAt(1, target)`  | writes into a fresh anonymous slot                     |
| `a eq b`              | `a.equalsTo(b)`              | infix, like `startsWith`, `endsWith` and `contains`    |

Anonymous slots come from `tempString()` (or `tempDynamicString()` on a `DataPack`), which is also the helper to call
when a pipeline needs a scratch string of its own. They live in the same heap as named strings, under the reserved
`kore_string_temp_<n>` prefix, and are never reused across call sites.

## Cost and limits

Helpers fall into three tiers, worth keeping in mind when a string is long or a helper runs every tick:

| Tier           | Helpers                                                                                                                                           | Cost                                               |
|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------|
| Constant       | `set`, `setFrom`, `clear`, `substring`, `substringTo`, `take`, `drop`, `charAt(Int)`, `append`, `prepend`, `build`                                | 1 to 3 commands, no macro                          |
| One macro call | `substringDynamic`, `takeLast`, `dropLast`, `capitalize`, `decapitalize`, `parseTo`, `setFromNbt`, `setFrom(score)`, `toScore`                    | a handful of commands plus one function call       |
| Recursive      | `reverse`, `indexOf`, `contains`, `count`, `replace`, `split`, `join`, `toList`, `uppercase`, `lowercase`, `trim`, `repeat`, `padStart`, `padEnd` | one function call per character, offset or element |

Recursive helpers are bounded by the `max_command_sequence_length` game rule (65 536 by default), which is far above any
realistic string length but is the hard ceiling.

The module handles ASCII text. Case conversion only maps `a-z` / `A-Z`, and indices are NBT string indices, so
characters outside the Basic Multilingual Plane do not behave like single characters.

## See also

- [Scoreboards](/docs/oop/scoreboards) – consume the length / find / predicate score holders returned by these helpers.
- [Macros](/docs/commands/macros) – the underlying mechanism used by every dynamic helper.
- [Data command](/docs/commands/commands#data-command) – NBT read/write via Kore's `data` command helpers.
