package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.strings.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

private const val LIB = "kore_string_lib:memory"

fun stringsTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("string_set") {
		greeting.set("hello") assertsIs """data modify storage $LIB heap.greeting set value "hello""""
		lines.size assertsIs 1
	}

	function("string_set_from") {
		greeting.setFrom(other) assertsIs "data modify storage $LIB heap.greeting set from storage $LIB heap.other"
		lines.size assertsIs 1
	}

	function("string_clear") {
		greeting.clear() assertsIs "data remove storage $LIB heap.greeting"
		lines.size assertsIs 1
	}

	function("string_copy_to") {
		greeting.copyTo(other) assertsIs "data modify storage $LIB heap.other set from storage $LIB heap.greeting"
		lines.size assertsIs 1
	}

	function("string_set_from_range") {
		greeting.setFrom(other, 1) assertsIs
			"data modify storage $LIB heap.greeting set string storage $LIB heap.other 1"
		greeting.setFrom(other, 0, 2) assertsIs
			"data modify storage $LIB heap.greeting set string storage $LIB heap.other 0 2"
		lines.size assertsIs 2
	}

	function("string_length") {
		greeting.length().holder assertsIs "#kore_string_len"
		lines.last() assertsIs
			"execute store result score #kore_string_len kore_string_len run data get storage $LIB heap.greeting"

		val typed = greeting.length("#custom_len")
		typed.holder assertsIs "#custom_len"
		typed.objective assertsIs "kore_string_len"
		lines.last() assertsIs
			"execute store result score #custom_len kore_string_len run data get storage $LIB heap.greeting"
		lines.size assertsIs 2
	}

	function("string_as_argument") {
		tellraw(allPlayers(), greeting.asChatComponents()) assertsIs
			"""tellraw @a {type:"nbt",interpret:1b,nbt:"heap.greeting",source:"storage",storage:"$LIB"}"""
		lines.size assertsIs 1
	}

	function("string_as_raw_argument") {
		tellraw(allPlayers(), greeting.asChatComponents(interpret = false)) assertsIs
			"""tellraw @a {type:"nbt",interpret:0b,nbt:"heap.greeting",source:"storage",storage:"$LIB"}"""
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

fun substringTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("substring_static") {
		greeting.substring(1, 5) assertsIs
			"data modify storage $LIB heap.greeting set string storage $LIB heap.greeting 1 5"
		greeting.substring(2) assertsIs
			"data modify storage $LIB heap.greeting set string storage $LIB heap.greeting 2"
		greeting.substringTo(other, 0, 3) assertsIs
			"data modify storage $LIB heap.other set string storage $LIB heap.greeting 0 3"
		lines.size assertsIs 3
	}

	function("take_and_drop") {
		greeting.take(3, other) assertsIs
			"data modify storage $LIB heap.other set string storage $LIB heap.greeting 0 3"
		greeting.drop(2, other) assertsIs
			"data modify storage $LIB heap.other set string storage $LIB heap.greeting 2"
		greeting.charAt(4, other) assertsIs
			"data modify storage $LIB heap.other set string storage $LIB heap.greeting 4 5"
		lines.size assertsIs 3
	}

	function("take_last") {
		greeting.takeLast(3, other)
		lines assertsIs listOf(
			"execute store result score #kore_string_takelast_len kore_string_len run data get storage $LIB heap.greeting",
			"scoreboard players operation #kore_string_takelast_start kore_string_len = #kore_string_takelast_len kore_string_len",
			"scoreboard players remove #kore_string_takelast_start kore_string_len 3",
			"""data modify storage $LIB args.kore_string_substring.src set value "greeting"""",
			"""data modify storage $LIB args.kore_string_substring.dst set value "other"""",
			"execute store result storage $LIB args.kore_string_substring.start int 1.0 run scoreboard players get #kore_string_takelast_start kore_string_len",
			"execute store result storage $LIB args.kore_string_substring.end int 1.0 run scoreboard players get #kore_string_takelast_len kore_string_len",
			"function unit_tests:kore_string_substring with storage $LIB args.kore_string_substring",
		)
	}

	function("drop_last") {
		greeting.dropLast(2, other)
		lines.take(4) assertsIs listOf(
			"execute store result score #kore_string_droplast_len kore_string_len run data get storage $LIB heap.greeting",
			"scoreboard players operation #kore_string_droplast_end kore_string_len = #kore_string_droplast_len kore_string_len",
			"scoreboard players remove #kore_string_droplast_end kore_string_len 2",
			"scoreboard players set #kore_string_zero kore_string_len 0",
		)
		lines.last() assertsIs "function unit_tests:kore_string_substring with storage $LIB args.kore_string_substring"
		lines.size assertsIs 9
	}

	function("substring_dynamic") {
		val start = ScoreboardEntity("bounds", fakePlayer("start"))
		val end = ScoreboardEntity("bounds", fakePlayer("end"))
		greeting.substringDynamic(start, end, other)
		lines.first() assertsIs """data modify storage $LIB args.kore_string_substring.src set value "greeting""""
		lines.last() assertsIs "function unit_tests:kore_string_substring with storage $LIB args.kore_string_substring"
		lines.size assertsIs 5
	}

	function("char_at_dynamic") {
		val index = ScoreboardEntity("bounds", fakePlayer("index"))
		greeting.charAt(index, other)
		lines[1] assertsIs
			"scoreboard players operation #kore_string_char_end kore_string_len = #kore_string_char_start kore_string_len"
		lines[2] assertsIs "scoreboard players add #kore_string_char_end kore_string_len 1"
		lines.size assertsIs 8
	}

	shouldThrow<IllegalArgumentException> { function("bad_take_last") { greeting.takeLast(-1) } }
	shouldThrow<IllegalArgumentException> { function("bad_drop_last") { greeting.dropLast(-1) } }
}

fun concatTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")
	val target = dynamicString("target")

	// Consecutive literals are folded at generation time, no macro and no runtime work at all.
	function("build_literals") {
		target.build {
			+"x"
			+'y'
		}
		lines assertsIs listOf("""data modify storage $LIB heap.target set value "xy"""")
	}

	function("build_mixed") {
		target.build {
			+greeting
			+"!"
		}
		lines assertsIs listOf(
			"data modify storage $LIB heap.target set from storage $LIB heap.greeting",
			"""data modify storage $LIB tmp.kore_string_concat_tmp set value "!"""",
			"data modify storage $LIB heap.target append string storage $LIB tmp.kore_string_concat_tmp",
		)

		target.build {
			+"!"
			+greeting
		}
		lines[3] assertsIs """data modify storage $LIB heap.target set value "!""""
		lines[4] assertsIs "data modify storage $LIB heap.target append string storage $LIB heap.greeting"

		target.build {
			+greeting
			+other
		}
		lines[5] assertsIs "data modify storage $LIB heap.target set from storage $LIB heap.greeting"
		lines[6] assertsIs "data modify storage $LIB heap.target append string storage $LIB heap.other"
		lines.size assertsIs 7
	}

	// Writing into one of the operands must not clobber it before it is read.
	function("build_aliased") {
		target.build {
			+target
			+other
		}
		lines assertsIs listOf("data modify storage $LIB heap.target append string storage $LIB heap.other")

		target.build {
			+target
			+target
		}
		lines[1] assertsIs "data modify storage $LIB tmp.kore_string_concat_tmp set from storage $LIB heap.target"
		lines[2] assertsIs "data modify storage $LIB heap.target append string storage $LIB tmp.kore_string_concat_tmp"
		lines.size assertsIs 3
	}

	function("build_empty") {
		target.build {}
		lines assertsIs listOf("""data modify storage $LIB heap.target set value """"")
	}

	function("plus_expression") {
		val full = greeting + "-" + other
		full.name assertsIs "kore_string_temp_1"
		lines assertsIs listOf(
			"data modify storage $LIB heap.kore_string_temp_0 set from storage $LIB heap.greeting",
			"""data modify storage $LIB tmp.kore_string_concat_tmp set value "-"""",
			"data modify storage $LIB heap.kore_string_temp_0 append string storage $LIB tmp.kore_string_concat_tmp",
			"data modify storage $LIB heap.kore_string_temp_1 set from storage $LIB heap.kore_string_temp_0",
			"data modify storage $LIB heap.kore_string_temp_1 append string storage $LIB heap.other",
		)
	}

	function("plus_assign_operators") {
		greeting += other
		greeting += '!'
		lines assertsIs listOf(
			"data modify storage $LIB heap.greeting append string storage $LIB heap.other",
			"""data modify storage $LIB tmp.kore_string_concat_tmp set value "!"""",
			"data modify storage $LIB heap.greeting append string storage $LIB tmp.kore_string_concat_tmp",
		)
	}

	function("append_and_prepend_dynamic") {
		greeting.append(other) assertsIs
			"data modify storage $LIB heap.greeting append string storage $LIB heap.other"
		greeting.prepend(other) assertsIs
			"data modify storage $LIB heap.greeting prepend string storage $LIB heap.other"
		greeting.appendFrom(other, 1) assertsIs
			"data modify storage $LIB heap.greeting append string storage $LIB heap.other 1"
		greeting.prependFrom(other, 0, 3) assertsIs
			"data modify storage $LIB heap.greeting prepend string storage $LIB heap.other 0 3"
		lines.size assertsIs 4
	}

	function("plus_assign") {
		greeting += other
		lines.first() assertsIs "data modify storage $LIB heap.greeting append string storage $LIB heap.other"

		greeting += "!"
		lines[1] assertsIs """data modify storage $LIB tmp.kore_string_concat_tmp set value "!""""
		lines[2] assertsIs "data modify storage $LIB heap.greeting append string storage $LIB tmp.kore_string_concat_tmp"
		lines.size assertsIs 3
	}
}

fun compareTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("equals_literal") {
		val result = greeting.equalsTo("hello")
		result.holder assertsIs "#kore_string_equals"
		result.objective assertsIs "kore_string_len"
		lines assertsIs listOf(
			"""data modify storage $LIB tmp.kore_string_equality_check set value "hello"""",
			"execute store success score #kore_string_diff kore_string_len run data modify storage $LIB tmp.kore_string_equality_check set from storage $LIB heap.greeting",
			"scoreboard players set #kore_string_equals kore_string_len 0",
			"execute if score #kore_string_diff kore_string_len matches 0 run scoreboard players set #kore_string_equals kore_string_len 1",
		)
	}

	function("equals_dynamic") {
		greeting.equalsTo(other)
		lines.first() assertsIs
			"data modify storage $LIB tmp.kore_string_equality_check set from storage $LIB heap.greeting"
		lines[1] assertsIs
			"execute store success score #kore_string_diff kore_string_len run data modify storage $LIB tmp.kore_string_equality_check set from storage $LIB heap.other"
		lines.size assertsIs 4
	}

	function("is_empty") {
		val result = greeting.isEmpty()
		result.holder assertsIs "#kore_string_is_empty"
		lines.first() assertsIs """data modify storage $LIB tmp.kore_string_equality_check set value """""
		lines.last() assertsIs
			"execute if score #kore_string_diff kore_string_len matches 0 run scoreboard players set #kore_string_is_empty kore_string_len 1"
		lines.size assertsIs 4
	}

	function("starts_with_literal") {
		greeting.startsWith("he")
		lines.first() assertsIs
			"data modify storage $LIB heap.kore_string_starts_scratch set string storage $LIB heap.greeting 0 2"
		lines.last() assertsIs
			"execute if score #kore_string_diff kore_string_len matches 0 run scoreboard players set #kore_string_starts kore_string_len 1"
		lines.size assertsIs 5
	}

	function("starts_with_dynamic") {
		greeting.startsWith(other)
		lines.first() assertsIs "scoreboard players set #kore_string_zero kore_string_len 0"
		lines[1] assertsIs
			"execute store result score #kore_string_starts_len kore_string_len run data get storage $LIB heap.other"
		lines.size assertsIs 11
	}

	function("ends_with_literal") {
		greeting.endsWith("lo")
		lines.first() assertsIs
			"execute store result score #kore_string_ends_srclen kore_string_len run data get storage $LIB heap.greeting"
		lines[2] assertsIs "scoreboard players remove #kore_string_ends_start kore_string_len 2"
		lines.size assertsIs 12
	}

	function("ends_with_dynamic") {
		greeting.endsWith(other)
		lines[1] assertsIs
			"execute store result score #kore_string_ends_suflen kore_string_len run data get storage $LIB heap.other"
		lines[3] assertsIs
			"scoreboard players operation #kore_string_ends_start kore_string_len -= #kore_string_ends_suflen kore_string_len"
		lines.size assertsIs 13
	}

	shouldThrow<IllegalArgumentException> { function("bad_starts") { greeting.startsWith("") } }
	shouldThrow<IllegalArgumentException> { function("bad_ends") { greeting.endsWith("") } }
}

fun findTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("index_of_literal") {
		greeting.indexOf("lo").holder assertsIs "#kore_string_find"
		lines assertsIs listOf(
			"""data modify storage $LIB tmp.kore_string_find_needle set value "lo"""",
			"execute store result score #kore_string_find_srclen kore_string_len run data get storage $LIB heap.greeting",
			"scoreboard players set #kore_string_find_sublen kore_string_len 2",
			"scoreboard players operation #kore_string_find_bound kore_string_len = #kore_string_find_srclen kore_string_len",
			"scoreboard players operation #kore_string_find_bound kore_string_len -= #kore_string_find_sublen kore_string_len",
			"scoreboard players set #kore_string_find_i kore_string_len 0",
			"scoreboard players set #kore_string_find kore_string_len -1",
			"""data modify storage $LIB args.kore_string_find_step.src set value "greeting"""",
			"""data modify storage $LIB args.kore_string_find_step.needlePath set value "tmp.kore_string_find_needle"""",
			"execute if score #kore_string_find_bound kore_string_len matches 0.. run function unit_tests:kore_string_find",
		)
	}

	function("index_of_dynamic") {
		greeting.indexOf(other, "#my_find").holder assertsIs "#my_find"
		lines[1] assertsIs
			"execute store result score #kore_string_find_sublen kore_string_len run data get storage $LIB heap.other 1.0"
		lines.last() assertsIs
			"scoreboard players operation #my_find kore_string_len = #kore_string_find kore_string_len"
	}

	function("contains") {
		(greeting contains "lo").holder assertsIs "#kore_string_contains"
		lines[lines.size - 2] assertsIs "scoreboard players set #kore_string_contains kore_string_len 0"
		lines.last() assertsIs
			"execute if score #kore_string_find kore_string_len matches 0.. run scoreboard players set #kore_string_contains kore_string_len 1"
	}

	function("contains_dynamic") {
		(greeting contains other).holder assertsIs "#kore_string_contains"
		lines.last() assertsIs
			"execute if score #kore_string_find kore_string_len matches 0.. run scoreboard players set #kore_string_contains kore_string_len 1"
	}

	function("count_literal") {
		greeting.count(",").holder assertsIs "#kore_string_count"
		lines.first() assertsIs "data modify storage $LIB tmp.kore_string_count_needle set value \",\""
		lines.last() assertsIs
			"execute if score #kore_string_find_bound kore_string_len matches 0.. run function unit_tests:kore_string_count"
	}

	function("count_dynamic") {
		greeting.count(other, "#my_count").holder assertsIs "#my_count"
		lines.last() assertsIs
			"scoreboard players operation #my_count kore_string_len = #kore_string_count kore_string_len"
	}

	shouldThrow<IllegalArgumentException> { function("bad_index_of") { greeting.indexOf("") } }
	shouldThrow<IllegalArgumentException> { function("bad_count") { greeting.count("") } }
}

fun caseTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("uppercase") {
		greeting.uppercase(other)
		lines assertsIs listOf(
			"""data modify storage $LIB heap.kore_string_case_scratch set value """"",
			"""data modify storage $LIB args.kore_string_upper_step.src set value "greeting"""",
			"""data modify storage $LIB args.kore_string_upper_step.dst set value "kore_string_case_scratch"""",
			"execute store result score #kore_string_case_len kore_string_len run data get storage $LIB heap.greeting",
			"scoreboard players set #kore_string_case_i kore_string_len 0",
			"execute if score #kore_string_case_i kore_string_len < #kore_string_case_len kore_string_len run function unit_tests:kore_string_upper",
			"data modify storage $LIB heap.other set from storage $LIB heap.kore_string_case_scratch",
		)
	}

	function("lowercase") {
		greeting.lowercase()
		lines[1] assertsIs """data modify storage $LIB args.kore_string_lower_step.src set value "greeting""""
		lines.size assertsIs 7
	}

	// A single character goes straight through the table lookup, never through the per-character loop.
	function("capitalize") {
		greeting.capitalize(other)
		lines assertsIs listOf(
			"data modify storage $LIB tmp.kore_string_case_args.c set string storage $LIB heap.greeting 0 1",
			"function unit_tests:kore_string_upper_table_map with storage $LIB tmp.kore_string_case_args",
			"data modify storage $LIB heap.kore_string_cap_rest set string storage $LIB heap.greeting 1",
			"data modify storage $LIB heap.other set from storage $LIB tmp.kore_string_case_args.c",
			"data modify storage $LIB heap.other append string storage $LIB heap.kore_string_cap_rest",
		)
	}

	function("decapitalize") {
		greeting.decapitalize()
		lines[1] assertsIs "function unit_tests:kore_string_lower_table_map with storage $LIB tmp.kore_string_case_args"
		lines.last() assertsIs
			"data modify storage $LIB heap.greeting append string storage $LIB heap.kore_string_cap_rest"
		lines.size assertsIs 5
	}

	// One table lookup per character, instead of scanning the 26 branches of an if-chain.
	functions.first { it.name == "kore_string_upper_step" }.lines assertsIs listOf(
		"\$data modify storage $LIB tmp.kore_string_case_args.c set string storage $LIB heap.\$(src) \$(i) \$(iPlusOne)",
		"function unit_tests:kore_string_upper_table_map with storage $LIB tmp.kore_string_case_args",
		"\$data modify storage $LIB heap.\$(dst) append string storage $LIB tmp.kore_string_case_args.c",
	)

	functions.first { it.name == "kore_string_upper_table_map" }.lines assertsIs listOf(
		"""execute unless data storage $LIB tmp.kore_string_case_args{c:"\""} """ +
			"""unless data storage $LIB tmp.kore_string_case_args{c:"\\"} run """ +
			"""data modify storage $LIB tmp.kore_string_case_args.c set from """ +
			"""storage $LIB tables.kore_string_upper_table."${'$'}(c)"""",
	)

	generatedFunctions.first { it.name == "kore_string_upper_table_init" }.lines assertsIs listOf(
		"""data modify storage $LIB tables.kore_string_upper_table set value """ +
			"""{a:"A",b:"B",c:"C",d:"D",e:"E",f:"F",g:"G",h:"H",i:"I",j:"J",k:"K",l:"L",m:"M",""" +
			"""n:"N",o:"O",p:"P",q:"Q",r:"R",s:"S",t:"T",u:"U",v:"V",w:"W",x:"X",y:"Y",z:"Z"}""",
	)
}

fun replaceTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("replace_range_literal") {
		greeting.replaceRange(0, 2, "zz")
		lines.first() assertsIs
			"data modify storage $LIB heap.kore_string_replace_before set string storage $LIB heap.greeting 0 0"
		lines[1] assertsIs
			"data modify storage $LIB heap.kore_string_replace_after set string storage $LIB heap.greeting 2"
		lines.last() assertsIs
			"data modify storage $LIB heap.greeting append string storage $LIB heap.kore_string_replace_after"
		lines.size assertsIs 6
	}

	function("replace_range_dynamic") {
		greeting.replaceRange(1, 3, other)
		lines[3] assertsIs "data modify storage $LIB heap.greeting append string storage $LIB heap.other"
		lines.size assertsIs 5
	}

	function("replace_all") {
		greeting.replace("a", "aa")
		lines assertsIs listOf(
			"""data modify storage $LIB tmp.kore_string_replace_new set value "aa"""",
			"""data modify storage $LIB tmp.kore_string_replace_needle set value "a"""",
			"""data modify storage $LIB args.kore_string_find_step.src set value "greeting"""",
			"""data modify storage $LIB args.kore_string_find_step.needlePath set value "tmp.kore_string_replace_needle"""",
			"""data modify storage $LIB args.kore_string_replace.srcName set value "greeting"""",
			"""data modify storage $LIB args.kore_string_replace_step.srcName set value "greeting"""",
			"scoreboard players set #kore_string_find_sublen kore_string_len 1",
			"scoreboard players set #kore_string_replace_cap kore_string_len 2147483647",
			"scoreboard players set #kore_string_replace_newlen kore_string_len 2",
			"scoreboard players set #kore_string_replace_start kore_string_len 0",
			"function unit_tests:kore_string_replace with storage $LIB args.kore_string_replace",
		)
	}

	function("replace_first") {
		greeting.replaceFirst("a", "b")
		lines[7] assertsIs "scoreboard players set #kore_string_replace_cap kore_string_len 1"
	}

	// A growing replacement must resume past what it just wrote, otherwise the loop never ends.
	val controller = functions.first { it.name == "kore_string_replace" }
	controller.lines[3] assertsIs
		"scoreboard players operation #kore_string_find_i kore_string_len = #kore_string_replace_start kore_string_len"
	controller.lines.filter { "#kore_string_replace_start" in it && "+=" in it } assertsIs listOf(
		"execute unless score #kore_string_find kore_string_len matches -1 " +
			"if score #kore_string_replace_cap kore_string_len matches 0.. " +
			"run scoreboard players operation #kore_string_replace_start kore_string_len += #kore_string_replace_newlen kore_string_len",
	)

	shouldThrow<IllegalArgumentException> { function("bad_replace") { greeting.replace("", "x") } }
	shouldThrow<IllegalArgumentException> { function("bad_range") { greeting.replaceRange(3, 1, "x") } }
}

fun trimPadRepeatTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")

	function("trim") {
		greeting.trim(other)
		lines.first() assertsIs
			"execute store result score #kore_string_trim_len kore_string_len run data get storage $LIB heap.greeting"
		lines[1] assertsIs "scoreboard players set #kore_string_trim_i kore_string_len 0"
		lines[5] assertsIs
			"execute if score #kore_string_trim_i kore_string_len < #kore_string_trim_end kore_string_len run function unit_tests:kore_string_trim_start"
		lines[6] assertsIs
			"execute if score #kore_string_trim_end kore_string_len > #kore_string_trim_i kore_string_len run function unit_tests:kore_string_trim_end"
	}

	function("trim_start") {
		greeting.trimStart()
		lines[4] assertsIs
			"execute if score #kore_string_trim_i kore_string_len < #kore_string_trim_end kore_string_len run function unit_tests:kore_string_trim_start"
	}

	function("trim_end") {
		greeting.trimEnd()
		lines[4] assertsIs
			"execute if score #kore_string_trim_end kore_string_len > #kore_string_trim_i kore_string_len run function unit_tests:kore_string_trim_end"
	}

	function("pad_start") {
		greeting.padStart(10, '0', other)
		lines.first() assertsIs "data modify storage $LIB heap.kore_string_pad_src set from storage $LIB heap.greeting"
		lines[2] assertsIs "scoreboard players set #kore_string_pad_diff kore_string_len 10"
		lines.last() assertsIs
			"execute if score #kore_string_pad_diff kore_string_len matches 1.. run data modify storage $LIB heap.other prepend string storage $LIB heap.kore_string_pad_scratch"

		// The repeated unit lives in its own slot: reusing the accumulator would double the padding each step.
		lines[5] assertsIs """data modify storage $LIB heap.kore_string_pad_char set value "0""""
		lines[6] assertsIs """data modify storage $LIB heap.kore_string_pad_scratch set value "0""""
		lines[9] assertsIs """data modify storage $LIB args.kore_string_repeat_step.src set value "kore_string_pad_char""""
		lines[10] assertsIs """data modify storage $LIB args.kore_string_repeat_step.dst set value "kore_string_pad_scratch""""
	}

	function("pad_end") {
		greeting.padEnd(4, ' ')
		lines.last() assertsIs
			"execute if score #kore_string_pad_diff kore_string_len matches 1.. run data modify storage $LIB heap.greeting append string storage $LIB heap.kore_string_pad_scratch"
	}

	function("repeat_zero") {
		greeting.repeat(0, other)
		lines assertsIs listOf("""data modify storage $LIB heap.other set value """"")
	}

	function("repeat_once") {
		greeting.repeat(1, other)
		lines assertsIs listOf(
			"data modify storage $LIB heap.kore_string_repeat_src set from storage $LIB heap.greeting",
			"data modify storage $LIB heap.other set from storage $LIB heap.kore_string_repeat_src",
		)
	}

	function("repeat_many") {
		greeting.repeat(3, other)
		lines[2] assertsIs "scoreboard players set #kore_string_repeat_cap kore_string_len 2"
		lines.last() assertsIs "function unit_tests:kore_string_repeat with storage $LIB args.kore_string_repeat"
	}

	// One copy appended per step, staged through a scratch slot so src and dst may be the same slot.
	functions.first { it.name == "kore_string_repeat_step" }.lines assertsIs listOf(
		"\$data modify storage $LIB tmp.kore_string_repeat_buf set from storage $LIB heap.\$(src)",
		"\$data modify storage $LIB heap.\$(dst) append string storage $LIB tmp.kore_string_repeat_buf",
	)

	// The trim step compares against the configured whitespace set, written as SNBT escapes.
	functions.first { it.name == "kore_string_trim_start_step" }.lines.drop(2) assertsIs
		listOf(" ", "\\t", "\\n", "\\r").map {
			"""execute if data storage $LIB tmp{kore_string_trim_c:"$it"} run """ +
				"scoreboard players set #kore_string_trim_ws kore_string_len 1"
		}

	shouldThrow<IllegalArgumentException> { function("bad_repeat") { greeting.repeat(-1) } }
	shouldThrow<IllegalArgumentException> { function("bad_pad") { greeting.padStart(-1) } }
}

fun parseTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")

	function("parse_to") {
		greeting.parseTo(greeting.storage, "out.value")
		lines assertsIs listOf(
			"data modify storage $LIB args.kore_string_parse.value set from storage $LIB heap.greeting",
			"""data modify storage $LIB args.kore_string_parse.dstStorage set value "$LIB"""",
			"""data modify storage $LIB args.kore_string_parse.dstPath set value "out.value"""",
			"function unit_tests:kore_string_parse with storage $LIB args.kore_string_parse",
		)
	}

	function("set_from_nbt") {
		greeting.setFromNbt(greeting.storage, "out.value")
		lines assertsIs listOf(
			"data modify storage $LIB args.kore_string_to_string.value set from storage $LIB out.value",
			"""data modify storage $LIB args.kore_string_to_string.dstName set value "greeting"""",
			"function unit_tests:kore_string_to_string with storage $LIB args.kore_string_to_string",
		)
	}
}

fun stringListTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val current = dynamicString("current")
	val tokens = koreStringList("tokens")

	function("list_basics") {
		tokens.clear() assertsIs "data modify storage $LIB lists.tokens set value []"
		tokens.append("alpha") assertsIs """data modify storage $LIB lists.tokens append value "alpha""""
		tokens.append(current) assertsIs
			"data modify storage $LIB lists.tokens append from storage $LIB heap.current"
		tokens.prepend("beta") assertsIs """data modify storage $LIB lists.tokens prepend value "beta""""
		tokens.prepend(current) assertsIs
			"data modify storage $LIB lists.tokens prepend from storage $LIB heap.current"
		lines.size assertsIs 5
	}

	function("list_indexing") {
		tokens.insertAt(1, "middle") assertsIs """data modify storage $LIB lists.tokens insert 1 value "middle""""
		tokens.insertAt(2, current) assertsIs
			"data modify storage $LIB lists.tokens insert 2 from storage $LIB heap.current"
		tokens.removeAt(2) assertsIs "data remove storage $LIB lists.tokens[2]"
		tokens.setAt(0, "first") assertsIs """data modify storage $LIB lists.tokens[0] set value "first""""
		tokens.setAt(0, current) assertsIs
			"data modify storage $LIB lists.tokens[0] set from storage $LIB heap.current"
		tokens.elementAt(3, current) assertsIs
			"data modify storage $LIB heap.current set from storage $LIB lists.tokens[3]"
		tokens[1].name assertsIs "kore_string_temp_0"
		lines[6] assertsIs "data modify storage $LIB heap.kore_string_temp_0 set from storage $LIB lists.tokens[1]"
		lines.size assertsIs 7
	}

	function("list_size") {
		tokens.size().holder assertsIs "#kore_string_len"
		lines.last() assertsIs
			"execute store result score #kore_string_len kore_string_len run data get storage $LIB lists.tokens"
		lines.size assertsIs 1
	}

	function("list_for_each") {
		tokens.forEach(current) {
			tellraw(allPlayers(), current.asChatComponents())
		}
		lines assertsIs listOf(
			"execute store result score #kore_string_foreach_size kore_string_len run data get storage $LIB lists.tokens",
			"scoreboard players set #kore_string_foreach_i kore_string_len 0",
			"execute store result storage $LIB args.kore_string_foreach_loop_0.index int 1.0 run scoreboard players get #kore_string_foreach_i kore_string_len",
			"execute if score #kore_string_foreach_i kore_string_len < #kore_string_foreach_size kore_string_len " +
				"run function unit_tests:kore_string_foreach_loop_0 with storage $LIB args.kore_string_foreach_loop_0",
		)
	}

	function("list_for_each_twice") {
		tokens.forEach(current) { tellraw(allPlayers(), current.asChatComponents()) }
		tokens.forEach(current) { tellraw(allPlayers(), current.asChatComponents()) }
		functions.map { it.name }.filter { it.startsWith("kore_string_foreach_loop_") } assertsIs
			listOf("kore_string_foreach_loop_0", "kore_string_foreach_loop_1", "kore_string_foreach_loop_2")
	}

	val loop = functions.first { it.name == "kore_string_foreach_loop_0" }
	loop.lines.first() assertsIs
		"\$data modify storage $LIB heap.current set from storage $LIB lists.tokens[\$(index)]"
	loop.lines[1] assertsIs "function unit_tests:kore_string_foreach_body_0"
}

fun joinTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val summary = dynamicString("summary")
	val tokens = koreStringList("tokens")

	function("join") {
		tokens.join(", ", summary, prefix = "[", postfix = "]")
		lines assertsIs listOf(
			"""data modify storage $LIB heap.summary set value "["""",
			"""data modify storage $LIB tmp.kore_string_join_sep set value ", """",
			"""data modify storage $LIB args.kore_string_join_step.dst set value "summary"""",
			"""data modify storage $LIB args.kore_string_join_step.listPath set value "lists.tokens"""",
			"execute store result score #kore_string_join_size kore_string_len run data get storage $LIB lists.tokens",
			"scoreboard players set #kore_string_join_i kore_string_len 0",
			"execute store result storage $LIB args.kore_string_join_step.index int 1.0 run scoreboard players get #kore_string_join_i kore_string_len",
			"execute if score #kore_string_join_i kore_string_len < #kore_string_join_size kore_string_len " +
				"run function unit_tests:kore_string_join_step with storage $LIB args.kore_string_join_step",
			"""data modify storage $LIB tmp.kore_string_concat_tmp set value "]"""",
			"data modify storage $LIB heap.summary append string storage $LIB tmp.kore_string_concat_tmp",
		)
	}

	function("join_no_wrapping") {
		tokens.join("", summary)
		lines.first() assertsIs "data modify storage $LIB heap.summary set value \"\""
		lines.size assertsIs 8
	}

	functions.first { it.name == "kore_string_join_step" }.lines assertsIs listOf(
		"\$execute if score #kore_string_join_i kore_string_len matches 1.. run data modify storage $LIB heap.\$(dst) " +
			"append string storage $LIB tmp.kore_string_join_sep",
		"\$data modify storage $LIB heap.\$(dst) append string storage $LIB \$(listPath)[\$(index)]",
		"scoreboard players add #kore_string_join_i kore_string_len 1",
		"execute store result storage $LIB args.kore_string_join_step.index int 1.0 run scoreboard players get #kore_string_join_i kore_string_len",
		"execute if score #kore_string_join_i kore_string_len < #kore_string_join_size kore_string_len " +
			"run function unit_tests:kore_string_join_step with storage $LIB args.kore_string_join_step",
	)
}

fun registrationTests() = dataPack("unit_tests") {
	val runtime = registerDynamicStrings()

	// Helpers materialise lazily, so a pack touching nothing stays free of macro functions.
	functions.none { it.name.startsWith("kore_string_") } assertsIs true

	val greeting = runtime.dynamicString("greeting")
	function("uses_substring_statically") { greeting.substring(0, 2) }
	functions.none { it.name.startsWith("kore_string_") } assertsIs true

	function("uses_reverse") { greeting.reverse() }
	functions.count { it.name == "kore_string_substring" } assertsIs 1
	functions.count { it.name == "kore_string_reverse" } assertsIs 1

	function("uses_reverse_again") { greeting.reverse() }
	functions.count { it.name == "kore_string_reverse" } assertsIs 1

	shouldThrow<IllegalArgumentException> { runtime.dynamicString("greeting") }
	shouldThrow<IllegalArgumentException> { runtime.dynamicString("kore_string_case_scratch") }
	shouldThrow<IllegalArgumentException> { runtime.koreStringList("kore_string_reserved") }

	val tokens = runtime.koreStringList("tokens")
	tokens.nbtPath assertsIs "lists.tokens"
	shouldThrow<IllegalArgumentException> { runtime.koreStringList("tokens") }
}

fun missingRuntimeTests() = dataPack("unit_tests") {
	shouldThrow<IllegalStateException> { dynamicString("greeting") }
	shouldThrow<IllegalStateException> { koreStringList("tokens") }
}

fun customConfigTests() = dataPack("unit_tests") {
	registerDynamicStrings(
		DynamicStringConfig(
			argsRoot = "a",
			heapRoot = "h",
			lengthHolder = "#my_len",
			lengthObjective = "my_len",
			listsRoot = "ls",
			storageName = "strings",
			storageNamespace = "my_pack",
			tablesRoot = "tb",
			tmpRoot = "t",
			trimWhitespace = listOf(" ", "_"),
		)
	)

	val ping = dynamicString("ping")
	val pong = koreStringList("pong")

	function("custom_paths") {
		ping.set("pong") assertsIs """data modify storage my_pack:strings h.ping set value "pong""""
		pong.clear() assertsIs "data modify storage my_pack:strings ls.pong set value []"
		ping.length().holder assertsIs "#my_len"
		lines.last() assertsIs
			"execute store result score #my_len my_len run data get storage my_pack:strings h.ping"
		pong.size().holder assertsIs "#my_len"
		lines.size assertsIs 4
	}

	function("custom_tmp_and_args") {
		ping.equalsTo("x")
		lines.first() assertsIs """data modify storage my_pack:strings t.kore_string_equality_check set value "x""""
		lines[2] assertsIs "scoreboard players set #kore_string_equals my_len 0"

		val before = lines.size
		ping.takeLast(2)
		lines[before + 3] assertsIs """data modify storage my_pack:strings a.kore_string_substring.src set value "ping""""
	}

	function("custom_tables_and_trim") {
		ping.uppercase()
		ping.trimStart()
	}

	function("custom_parse_roots") {
		ping.setFromNbt(ping.storage, "out.value")
		lines assertsIs listOf(
			"data modify storage my_pack:strings a.kore_string_to_string.value set from storage my_pack:strings out.value",
			"""data modify storage my_pack:strings a.kore_string_to_string.dstName set value "ping"""",
			"function unit_tests:kore_string_to_string with storage my_pack:strings a.kore_string_to_string",
		)
	}

	functions.first { it.name == "kore_string_to_string" }.lines assertsIs listOf(
		"\$data modify storage my_pack:strings h.\$(dstName) set value \"\$(value)\"",
	)

	generatedFunctions.first { it.name == "kore_string_upper_table_init" }.lines.first()
		.startsWith("data modify storage my_pack:strings tb.kore_string_upper_table set value ") assertsIs true

	functions.first { it.name == "kore_string_trim_start_step" }.lines.drop(2) assertsIs listOf(" ", "_").map {
		"""execute if data storage my_pack:strings t{kore_string_trim_c:"$it"} run """ +
			"scoreboard players set #kore_string_trim_ws my_len 1"
	}
}

fun advancedStringsTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")
	val tokens = koreStringList("tokens")

	function("reverse") {
		greeting.reverse(other)
		lines.first() assertsIs """data modify storage $LIB heap.kore_string_rev_out set value """""
		lines[1] assertsIs """data modify storage $LIB args.kore_string_substring.src set value "greeting""""
		// The destination slot never changes, so it is written once instead of on every iteration.
		lines[2] assertsIs """data modify storage $LIB args.kore_string_substring.dst set value "kore_string_reverse_scratch""""
		lines.last() assertsIs "data modify storage $LIB heap.other set from storage $LIB heap.kore_string_rev_out"
	}

	function("split") {
		greeting.split(",", tokens)
		lines.first() assertsIs "data modify storage $LIB lists.tokens set value []"
		lines[1] assertsIs "data modify storage $LIB tmp.kore_string_split_delim set value \",\""
		lines.last() assertsIs
			"execute if score #kore_string_split_start kore_string_len <= #kore_string_split_srclen kore_string_len " +
				"run function unit_tests:kore_string_split_step with storage $LIB args.kore_string_split_step"
	}

	function("to_list") {
		greeting.toList(tokens)
		lines.first() assertsIs "data modify storage $LIB lists.tokens set value []"
		lines.last() assertsIs
			"execute if score #kore_string_tl_i kore_string_len < #kore_string_tl_len kore_string_len " +
				"run function unit_tests:kore_string_to_list"
	}

	// One character extracted then appended per iteration, no intermediate concat helper.
	functions.first { it.name == "kore_string_reverse" }.lines.drop(4) assertsIs listOf(
		"function unit_tests:kore_string_substring with storage $LIB args.kore_string_substring",
		"data modify storage $LIB heap.kore_string_rev_out append string storage $LIB heap.kore_string_reverse_scratch",
		"scoreboard players remove #kore_string_rev_i kore_string_len 1",
		"execute if score #kore_string_rev_i kore_string_len matches 0.. run function unit_tests:kore_string_reverse",
	)

	shouldThrow<IllegalArgumentException> { function("bad_split") { greeting.split("", tokens) } }
}

fun dynamicOperandTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val needle = dynamicString("needle")
	val other = dynamicString("other")
	val replacement = dynamicString("replacement")
	val tokens = koreStringList("tokens")
	val width = ScoreboardEntity("layout", fakePlayer("#width"))

	function("replace_dynamic") {
		greeting.replace(needle, replacement)
		lines assertsIs listOf(
			"data modify storage $LIB tmp.kore_string_replace_new set from storage $LIB heap.replacement",
			"data modify storage $LIB tmp.kore_string_replace_needle set from storage $LIB heap.needle",
			"""data modify storage $LIB args.kore_string_find_step.src set value "greeting"""",
			"""data modify storage $LIB args.kore_string_find_step.needlePath set value "tmp.kore_string_replace_needle"""",
			"""data modify storage $LIB args.kore_string_replace.srcName set value "greeting"""",
			"""data modify storage $LIB args.kore_string_replace_step.srcName set value "greeting"""",
			"execute store result score #kore_string_find_sublen kore_string_len run " +
				"data get storage $LIB tmp.kore_string_replace_needle 1.0",
			"scoreboard players set #kore_string_replace_cap kore_string_len 2147483647",
			"execute store result score #kore_string_replace_newlen kore_string_len run " +
				"data get storage $LIB tmp.kore_string_replace_new 1.0",
			"scoreboard players set #kore_string_replace_start kore_string_len 0",
			"execute if score #kore_string_find_sublen kore_string_len matches 1.. " +
				"run function unit_tests:kore_string_replace with storage $LIB args.kore_string_replace",
		)
	}

	function("replace_first_dynamic") {
		greeting.replaceFirst("a", replacement)
		lines[8] assertsIs "execute store result score #kore_string_replace_newlen kore_string_len run " +
			"data get storage $LIB tmp.kore_string_replace_new 1.0"
		lines.last() assertsIs "function unit_tests:kore_string_replace with storage $LIB args.kore_string_replace"
	}

	function("split_dynamic") {
		greeting.split(needle, tokens)
		lines[1] assertsIs "data modify storage $LIB tmp.kore_string_split_delim set from storage $LIB heap.needle"
		lines[8] assertsIs "execute store result score #kore_string_find_sublen kore_string_len run " +
			"data get storage $LIB tmp.kore_string_split_delim 1.0"
		lines[10] assertsIs "scoreboard players operation #kore_string_find_bound kore_string_len -= " +
			"#kore_string_find_sublen kore_string_len"
		lines.last() assertsIs
			"execute if score #kore_string_find_sublen kore_string_len matches 1.. " +
				"if score #kore_string_split_start kore_string_len <= #kore_string_split_srclen kore_string_len " +
				"run function unit_tests:kore_string_split_step with storage $LIB args.kore_string_split_step"
	}

	function("repeat_dynamic") {
		greeting.repeat(width, target = other)
		lines assertsIs listOf(
			"data modify storage $LIB heap.kore_string_repeat_src set from storage $LIB heap.greeting",
			"""data modify storage $LIB heap.other set value """"",
			"scoreboard players operation #kore_string_repeat_cap kore_string_len = #width layout",
			"execute if score #kore_string_repeat_cap kore_string_len matches 1.. run " +
				"data modify storage $LIB heap.other set from storage $LIB heap.kore_string_repeat_src",
			"scoreboard players remove #kore_string_repeat_cap kore_string_len 1",
			"""data modify storage $LIB args.kore_string_repeat_step.src set value "kore_string_repeat_src"""",
			"""data modify storage $LIB args.kore_string_repeat_step.dst set value "other"""",
			"""data modify storage $LIB args.kore_string_repeat.src set value "kore_string_repeat_src"""",
			"""data modify storage $LIB args.kore_string_repeat.dst set value "other"""",
			"execute if score #kore_string_repeat_cap kore_string_len matches 1.. " +
				"run function unit_tests:kore_string_repeat with storage $LIB args.kore_string_repeat",
		)
	}

	function("pad_dynamic") {
		greeting.padStart(width, '0')
		lines[2] assertsIs "scoreboard players operation #kore_string_pad_diff kore_string_len = #width layout"
		lines[3] assertsIs "scoreboard players operation #kore_string_pad_diff kore_string_len -= " +
			"#kore_string_pad_len kore_string_len"
		lines.last() assertsIs
			"execute if score #kore_string_pad_diff kore_string_len matches 1.. run " +
				"data modify storage $LIB heap.greeting prepend string storage $LIB heap.kore_string_pad_scratch"
	}
}

fun scoreBridgeTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val label = dynamicString("label")
	val kills = ScoreboardEntity("stats", fakePlayer("#kills"))

	function("score_to_string") {
		label.setFrom(kills)
		lines assertsIs listOf(
			"execute store result storage $LIB tmp.kore_string_score int 1.0 run scoreboard players get #kills stats",
			"data modify storage $LIB args.kore_string_to_string.value set from storage $LIB tmp.kore_string_score",
			"""data modify storage $LIB args.kore_string_to_string.dstName set value "label"""",
			"function unit_tests:kore_string_to_string with storage $LIB args.kore_string_to_string",
		)
	}

	function("score_appended") {
		label.appendFrom(kills)
		lines[2] assertsIs """data modify storage $LIB args.kore_string_to_string.dstName set value "kore_string_score_text""""
		lines.last() assertsIs "data modify storage $LIB heap.label append string storage $LIB heap.kore_string_score_text"
	}

	function("string_to_score") {
		label.toScore(kills)
		lines assertsIs listOf(
			"data modify storage $LIB args.kore_string_parse.value set from storage $LIB heap.label",
			"""data modify storage $LIB args.kore_string_parse.dstStorage set value "kore_string_lib:memory"""",
			"""data modify storage $LIB args.kore_string_parse.dstPath set value "tmp.kore_string_score"""",
			"function unit_tests:kore_string_parse with storage $LIB args.kore_string_parse",
			"execute store result score #kills stats run data get storage $LIB tmp.kore_string_score 1.0",
		)
	}
}

fun operatorTests() = dataPack("unit_tests") {
	registerDynamicStrings()

	val greeting = dynamicString("greeting")
	val other = dynamicString("other")
	val tokens = koreStringList("tokens")
	val kills = ScoreboardEntity("stats", fakePlayer("kills"))

	function("index_operators") {
		val initial = greeting[0]
		initial.name assertsIs "kore_string_temp_0"
		lines.last() assertsIs
			"data modify storage $LIB heap.kore_string_temp_0 set string storage $LIB heap.greeting 0 1"

		val slice = greeting[1..3]
		slice.name assertsIs "kore_string_temp_1"
		lines.last() assertsIs
			"data modify storage $LIB heap.kore_string_temp_1 set string storage $LIB heap.greeting 1 4"

		greeting.substring(0..<2) assertsIs
			"data modify storage $LIB heap.greeting set string storage $LIB heap.greeting 0 2"
		lines.size assertsIs 3
	}

	function("minus_assign") {
		greeting -= "-"
		lines.first() assertsIs """data modify storage $LIB tmp.kore_string_replace_new set value """""
	}

	function("times_assign") {
		greeting *= 0
		lines assertsIs listOf("""data modify storage $LIB heap.greeting set value """"")
	}

	function("list_plus_assign") {
		tokens += "a"
		tokens += greeting
		lines assertsIs listOf(
			"""data modify storage $LIB lists.tokens append value "a"""",
			"data modify storage $LIB lists.tokens append from storage $LIB heap.greeting",
		)
	}

	function("score_operand") {
		greeting += kills
		lines.last() assertsIs "data modify storage $LIB heap.greeting append string storage $LIB heap.kore_string_score_text"
	}

	function("expression_transforms") {
		greeting.uppercased().name assertsIs "kore_string_temp_2"
		greeting.trimmed().name assertsIs "kore_string_temp_3"
		greeting.reversed().name assertsIs "kore_string_temp_4"
		greeting.repeated(2).name assertsIs "kore_string_temp_5"
		greeting.paddedStart(4, '0').name assertsIs "kore_string_temp_6"
	}

	function("result_branches") {
		(greeting eq "hi").then { greeting.set("matched") }
		lines.last() assertsIs
			"""execute if score #kore_string_equals kore_string_len matches 1 run data modify storage $LIB heap.greeting set value "matched""""

		(greeting startsWith "h").otherwise { greeting.set("nope") }
		lines.last() assertsIs
			"""execute if score #kore_string_starts kore_string_len matches 0 run data modify storage $LIB heap.greeting set value "nope""""

		(greeting contains other).then { greeting.clear() }
		lines.last() assertsIs
			"execute if score #kore_string_contains kore_string_len matches 1 run data remove storage $LIB heap.greeting"
	}

	// A result is a plain score, so it drives every helper taking a runtime count.
	function("result_as_score") {
		other.repeat(greeting.length())
		lines.first() assertsIs
			"execute store result score #kore_string_len kore_string_len run data get storage $LIB heap.greeting"
	}
}

class StringsTests : FunSpec({
	test("advanced strings") { advancedStringsTests() }
	test("case") { caseTests() }
	test("compare") { compareTests() }
	test("concat") { concatTests() }
	test("custom config") { customConfigTests() }
	test("dynamic operands") { dynamicOperandTests() }
	test("find") { findTests() }
	test("join") { joinTests() }
	test("missing runtime") { missingRuntimeTests() }
	test("operators") { operatorTests() }
	test("parse") { parseTests() }
	test("registration") { registrationTests() }
	test("score bridge") { scoreBridgeTests() }
	test("replace") { replaceTests() }
	test("string list") { stringListTests() }
	test("strings") { stringsTests() }
	test("substring") { substringTests() }
	test("trim pad repeat") { trimPadRepeatTests() }
})
