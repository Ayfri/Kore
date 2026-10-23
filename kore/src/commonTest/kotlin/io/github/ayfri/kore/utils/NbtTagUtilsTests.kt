package io.github.ayfri.kore.utils

import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.function
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtString

class NbtTagUtilsTests : FunSpec({
	test("snbt strings round-trip through toNbt") {
		val compound = """{Count:3b,tag:{x:1,name:"hello"}}""".toNbt()

		compound.toSnbt() shouldBe """{Count:3b,tag:{x:1,name:hello}}"""
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
		nbtListOf("a", "b").toSnbt() shouldBe "[a,b]"
		nbtListOf(nbt { this["x"] = 1 }).toSnbt() shouldBe "[{x:1}]"
	}

	test("floats and doubles are written with a decimal point on every platform") {
		NbtFloat(5f).toSnbt() shouldBe "5.0f"
		NbtDouble(1.0).toSnbt() shouldBe "1.0d"
		NbtFloat(0.1f).toSnbt() shouldBe "0.1f"
		NbtFloat(1e-5f).toSnbt() shouldBe "1.0E-5f"
		NbtFloat(16777216f).toSnbt() shouldBe "1.6777216E7f"
		NbtDouble(3e7).toSnbt() shouldBe "3.0E7d"
		NbtDouble(Double.NaN).toSnbt() shouldBe "NaNd"

		nbt {
			this["byte"] = 1.toByte()
			this["double"] = -3.0
			this["float"] = 2f
			this["int"] = 4
			this["list"] = nbtListOf(1f, 2.5f)
			this["long"] = 5L
		}.toSnbt() shouldBe "{byte:1b,double:-3.0d,float:2.0f,int:4,list:[1.0f,2.5f],long:5L}"
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

	test("string values are bare only when the game reads them back as the same string") {
		mapOf(
			"aqua" to "aqua",
			"Aqua" to "Aqua",
			"_x" to "_x",
			"snake_case-1" to "snake_case-1",
			"trueish" to "trueish",
			"5abc" to "\"5abc\"",
			"-x" to "\"-x\"",
			"+x" to "\"+x\"",
			".x" to "\".x\"",
			"minecraft:stone" to "\"minecraft:stone\"",
			"@s" to "\"@s\"",
			"hello world" to "\"hello world\"",
			"a.b" to "\"a.b\"",
			"é" to "\"é\"",
			"" to "\"\"",
			"true" to "\"true\"",
			"TRUE" to "\"TRUE\"",
			"False" to "\"False\"",
		).forEach { (value, snbt) ->
			NbtString(value).toSnbt() shouldBe snbt
			nbt { this["k"] = value }.toSnbt().toNbt() shouldBe nbt { this["k"] = value }
		}
	}

	test("keys stay bare when they only hold letters, digits, underscores and dashes") {
		val compound = nbt {
			this["true"] = "x"
			this["1"] = "x"
			this["a:b"] = "x"
			this[""] = "x"
		}

		compound.toSnbt() shouldBe """{true:x,1:x,"a:b":x,"":x}"""
		compound.toSnbt().toNbt() shouldBe compound
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
