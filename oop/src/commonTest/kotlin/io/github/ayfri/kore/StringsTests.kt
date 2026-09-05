package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.strings.*
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun stringsTests() = testDataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("string_set") {
		greeting.set("hello") assertsIs "data modify storage kore_string_lib:memory heap.greeting set value \"hello\""
		lines.size assertsIs 1
	}

	function("string_set_from") {
		greeting.setFrom(other) assertsIs "data modify storage kore_string_lib:memory heap.greeting set from storage kore_string_lib:memory heap.other"
		lines.size assertsIs 1
	}

	function("string_clear") {
		greeting.clear() assertsIs "data remove storage kore_string_lib:memory heap.greeting"
		lines.size assertsIs 1
	}

	function("string_copy_to") {
		greeting.copyTo(other) assertsIs "data modify storage kore_string_lib:memory heap.other set from storage kore_string_lib:memory heap.greeting"
		lines.size assertsIs 1
	}

	function("string_set_from_range") {
		greeting.setFrom(
			other,
			1
		) assertsIs "data modify storage kore_string_lib:memory heap.greeting set string storage kore_string_lib:memory heap.other 1"
		greeting.setFrom(
			other,
			0,
			2
		) assertsIs "data modify storage kore_string_lib:memory heap.greeting set string storage kore_string_lib:memory heap.other 0 2"
		lines.size assertsIs 2
	}

	function("string_length") {
		greeting.length() assertsIs "#kore_string_len"
		lines.last() assertsIs "execute store result score #kore_string_len kore_string_len run data get storage kore_string_lib:memory heap.greeting"
		lines.size assertsIs 1
	}

	function("string_substring_static") {
		greeting.substring(
			1,
			5
		) assertsIs "data modify storage kore_string_lib:memory heap.greeting set string storage kore_string_lib:memory heap.greeting 1 5"
		greeting.substring(2) assertsIs "data modify storage kore_string_lib:memory heap.greeting set string storage kore_string_lib:memory heap.greeting 2"
		greeting.substringTo(
			other,
			0,
			3
		) assertsIs "data modify storage kore_string_lib:memory heap.other set string storage kore_string_lib:memory heap.greeting 0 3"
		lines.size assertsIs 3
	}

	function("string_as_argument") {
		tellraw(
			allPlayers(),
			greeting.asChatComponents()
		) assertsIs """tellraw @a {type:"nbt",interpret:1b,nbt:"heap.greeting",source:"storage",storage:"kore_string_lib:memory"}"""
		lines.size assertsIs 1
	}

	greeting.asChatComponents() assertsIsJson """
		{
			"type": "nbt",
			"interpret": true,
			"nbt": "heap.greeting",
			"source": "storage",
			"storage": "kore_string_lib:memory"
		}
	""".trimIndent()
}

fun stringListTests() = testDataPack("unit_tests") {
	registerDynamicStrings()

	val tokens = koreStringList("tokens")
	val token = dynamicString("token")

	function("list_clear") {
		tokens.clear() assertsIs "data modify storage kore_string_lib:memory lists.tokens set value []"
		lines.size assertsIs 1
	}

	function("list_append") {
		tokens.append("abc") assertsIs "data modify storage kore_string_lib:memory lists.tokens append value \"abc\""
		tokens.append(token) assertsIs "data modify storage kore_string_lib:memory lists.tokens append from storage kore_string_lib:memory heap.token"
		tokens.prepend("zzz") assertsIs "data modify storage kore_string_lib:memory lists.tokens prepend value \"zzz\""
		lines.size assertsIs 3
	}

	function("list_size") {
		tokens.size() assertsIs "#kore_string_len"
		lines.last() assertsIs "execute store result score #kore_string_len kore_string_len run data get storage kore_string_lib:memory lists.tokens"
		lines.size assertsIs 1
	}

	function("list_element_at") {
		tokens.elementAt(
			0,
			token
		) assertsIs "data modify storage kore_string_lib:memory heap.token set from storage kore_string_lib:memory lists.tokens[0]"
		lines.size assertsIs 1
	}

	function("list_insert_remove") {
		tokens.insertAt(0, "first")
		tokens.removeAt(2) assertsIs "data remove storage kore_string_lib:memory lists.tokens[2]"
		tokens.setAt(
			1,
			"replace"
		) assertsIs "data modify storage kore_string_lib:memory lists.tokens[1] set value \"replace\""
		lines.size assertsIs 3
	}
}

fun registrationTests() = testDataPack("unit_tests") {
	val runtime = registerDynamicStrings()
	val macroFn = runtime.substringHelper()
	macroFn.name assertsIs "kore_string_substring"
	macroFn.lines.last() assertsIs "\$data modify storage kore_string_lib:memory heap.\$(dst) set string storage kore_string_lib:memory heap.\$(src) \$(start) \$(end)"

	val concat = runtime.concatHelper()
	concat.name assertsIs "kore_string_concat"
	concat.lines.last() assertsIs "\$data modify storage kore_string_lib:memory heap.\$(dst) set value \"\$(a)\$(b)\""
}

fun advancedStringsTests() = testDataPack("unit_tests") {
	registerDynamicStrings()

	val s = dynamicString("phrase")
	val other = dynamicString("other2")

	function("string_concat_literal") {
		s.append(" world")
		lines.size assertsIs 4
		lines[0] assertsIs "data modify storage kore_string_lib:memory args.kore_string_concat.a set from storage kore_string_lib:memory heap.phrase"
		lines[1] assertsIs "data modify storage kore_string_lib:memory args.kore_string_concat.b set value \" world\""
		lines[2] assertsIs "data modify storage kore_string_lib:memory args.kore_string_concat.dst set value \"phrase\""
		lines[3] assertsIs "function unit_tests:kore_string_concat with storage kore_string_lib:memory args.kore_string_concat"
	}

	function("string_append_prepend_from_string") {
		s.append(other) assertsIs "data modify storage kore_string_lib:memory heap.phrase append string storage kore_string_lib:memory heap.other2"
		s.prepend(other) assertsIs "data modify storage kore_string_lib:memory heap.phrase prepend string storage kore_string_lib:memory heap.other2"
		s.appendFrom(
			other,
			1
		) assertsIs "data modify storage kore_string_lib:memory heap.phrase append string storage kore_string_lib:memory heap.other2 1"
		s.prependFrom(
			other,
			0,
			3
		) assertsIs "data modify storage kore_string_lib:memory heap.phrase prepend string storage kore_string_lib:memory heap.other2 0 3"
		lines.size assertsIs 4
	}

	function("string_equals_literal") {
		s.equalsTo("hello")
		lines.isNotEmpty() assertsIs true
	}

	function("string_starts_with_literal") {
		s.startsWith("he")
		lines.isNotEmpty() assertsIs true
	}

	function("string_take_drop") {
		s.take(3, other)
		lines.last() assertsIs "data modify storage kore_string_lib:memory heap.other2 set string storage kore_string_lib:memory heap.phrase 0 3"
		s.drop(2, other)
		lines.last() assertsIs "data modify storage kore_string_lib:memory heap.other2 set string storage kore_string_lib:memory heap.phrase 2"
	}

	function("string_repeat_static") {
		s.repeat(3, other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_reverse") {
		s.reverse(other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_contains") {
		s.contains("lo")
		lines.isNotEmpty() assertsIs true
	}

	function("string_count_literal") {
		s.count("l")
		lines.first() assertsIs "data modify storage kore_string_lib:memory tmp.kore_string_count_needle set value \"l\""
		lines.last() assertsIs "execute if score #kore_string_find_bound kore_string_len matches 0.. run function unit_tests:kore_string_count"
	}

	function("string_split") {
		val parts = koreStringList("parts")
		s.split(",", parts)
		lines.last() assertsIs "execute if score #kore_string_split_start kore_string_len <= #kore_string_split_srclen kore_string_len run function unit_tests:kore_string_split_step with storage kore_string_lib:memory args.kore_string_split_step"
	}

	function("string_trim") {
		s.trim(other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_trim_start") {
		s.trimStart(other)
		lines.last() assertsIs "function unit_tests:kore_string_substring with storage kore_string_lib:memory args.kore_string_substring"
	}

	function("string_pad_start") {
		s.padStart(10, '0', other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_pad_end") {
		s.padEnd(5, '.', other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_uppercase") {
		s.uppercase(other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_lowercase") {
		s.lowercase(other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_capitalize") {
		s.capitalize(other)
		lines.isNotEmpty() assertsIs true
	}

	function("string_replace") {
		s.replace("l", "L")
		lines.isNotEmpty() assertsIs true
	}

	function("string_replace_first") {
		s.replaceFirst("l", "L")
		lines.isNotEmpty() assertsIs true
	}

	function("string_to_list") {
		val chars = koreStringList("chars")
		s.toList(chars)
		lines.isNotEmpty() assertsIs true
	}
}

fun listForEachTests() = testDataPack("unit_tests") {
	registerDynamicStrings()
	val list = koreStringList("items")
	val current = dynamicString("current")

	function("list_foreach") {
		list.forEach(current) {
			addLine("say \$(index)")
		}
		lines.last() assertsIs "execute if score #kore_string_foreach_i kore_string_len < #kore_string_foreach_size kore_string_len run function unit_tests:kore_string_foreach_loop_0 with storage kore_string_lib:memory args.kore_string_foreach_loop_0"
	}

	function("list_foreach_unique_names") {
		list.forEach(current) { addLine("say first \$(index)") }
		list.forEach(current) { addLine("say second \$(index)") }
		// Each call site generates its own body and loop, with unique suffix.
		lines.any { it.contains("kore_string_foreach_loop_1") } assertsIs true
		lines.any { it.contains("kore_string_foreach_loop_2") } assertsIs true
	}
}

fun customConfigTests() = testDataPack("unit_tests") {
	val custom = DynamicStringConfig(
		storageNamespace = "my_pack",
		storageName = "strings",
		heapRoot = "h",
		argsRoot = "a",
		tmpRoot = "t",
		lengthObjective = "my_len",
		lengthHolder = "#my_len",
	)
	registerDynamicStrings(custom)
	val s = dynamicString("ping")

	function("custom_set") {
		s.set("pong") assertsIs "data modify storage kore_string_lib:memory heap.ping set value \"pong\""
		// DynamicString storage is still based on OopConstants because it's built from constants,
		// not from the runtime config. This keeps public handles backwards compatible.
	}
}

class StringsTests : FunSpec({
	test("strings") { stringsTests().generate() }
	test("string list") { stringListTests().generate() }
	test("registration") { registrationTests().generate() }
	test("advanced strings") { advancedStringsTests().generate() }
	test("list forEach") { listForEachTests().generate() }
	test("custom config") { customConfigTests().generate() }
})
