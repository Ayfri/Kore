package io.github.ayfri.kore.utils

import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.function
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NbtTagUtilsTests : FunSpec({
	test("snbt strings round-trip through toNbt") {
		val compound = """{Count:3b,tag:{x:1,name:"hello"}}""".toNbt()

		compound.toSnbt() shouldBe """{Count:3b,tag:{x:1,name:"hello"}}"""
		compound shouldBe nbt {
			this["Count"] = 3.toByte()
			this["tag"] = nbt {
				this["x"] = 1
				this["name"] = "hello"
			}
		}
	}

	test("toNbtTag parses values that are not compounds") {
		"[1,2,3]".toNbtTag().toSnbt() shouldBe "[1,2,3]"
		"[I;1,2]".toNbtTag().toSnbt() shouldBe "[I;1,2]"
		"12b".toNbtTag().toSnbt() shouldBe "12b"
	}

	test("nbtListOf builds typed lists") {
		nbtListOf(1, 2, 3).toSnbt() shouldBe "[1,2,3]"
		nbtListOf(0.5, 1.5).toSnbt() shouldBe "[0.5d,1.5d]"
		nbtListOf("a", "b").toSnbt() shouldBe """["a","b"]"""
		nbtListOf(nbt { this["x"] = 1 }).toSnbt() shouldBe "[{x:1}]"
	}

	test("snbt strings escape backslashes, quotes and line breaks") {
		val compound = nbt {
			this["path"] = """C:\temp"""
			this["quote"] = """say "hi" it's"""
			this["lines"] = "a\nb"
			this["it's \"key\""] = 1
			this["say \"key\""] = 2
		}

		compound.toSnbt() shouldBe """{path:"C:\\temp",quote:"say \"hi\" it's",lines:"a\nb","it's \"key\"":1,'say "key"':2}"""
	}

	test("invalid snbt is rejected") {
		shouldThrowAny { "{unclosed:1".toNbt() }
	}

	test("parsed snbt feeds the data command directly") {
		val datapack = dataPack("kore_tests") {}

		val fn = datapack.function("snbt") {
			data(storage("test", "kore")).modify("value", """{a:1b}""".toNbt())
		}

		fn.toString() assertsIs "data modify storage kore:test value set value {a:1b}"
	}
})
