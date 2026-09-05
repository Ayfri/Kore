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

		stringifiedNbt(compound) shouldBe """{Count:3b,tag:{x:1,name:"hello"}}"""
		compound shouldBe nbt {
			this["Count"] = 3.toByte()
			nbt("tag") {
				this["x"] = 1
				this["name"] = "hello"
			}
		}
	}

	test("toNbtTag parses values that are not compounds") {
		stringifiedNbt("[1,2,3]".toNbtTag()) shouldBe "[1,2,3]"
		stringifiedNbt("[I;1,2]".toNbtTag()) shouldBe "[I;1,2]"
		stringifiedNbt("12b".toNbtTag()) shouldBe "12b"
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
