package io.github.ayfri.kore.interop

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsThrows
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.interop.lanternload.LanternVersion
import io.github.ayfri.kore.interop.lanternload.lanternLoad
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun DataPack.functionNamed(namespace: String, directory: String, name: String) =
	functions.first { it.namespace == namespace && it.directory == directory && it.name == name }

private fun DataPack.functionTagNamed(namespace: String, fileName: String) =
	tags.first { it.type == "function" && it.namespace == namespace && it.fileName == fileName }

fun DataPack.lanternLoadTests() {
	lanternLoad {
		version("1.2.3")
		dependency("bs.math", 3, 1)
		dependency("smithed.actionbar")
		preLoad { say("pre") }
		load { say("loaded") }
		postLoad { say("post") }
	}

	functionNamed("load", "_private", "init").toString() assertsIs """
		# Reset scoreboards so packs can set values accurate for current load.
		scoreboard objectives add load.status dummy
		scoreboard players reset * load.status
	""".trimIndent()

	functionNamed(name, "load", "enumerate").toString() assertsIs """
		scoreboard players set lantern_tests.major load.status 1
		scoreboard players set lantern_tests.minor load.status 2
		scoreboard players set lantern_tests.patch load.status 3
	""".trimIndent()

	functionNamed(name, "load", "init").toString() assertsIs "say loaded"

	functionNamed(name, "load", "resolve").toString() assertsIs """
		execute if score bs.math.major load.status matches 3 if score bs.math.minor load.status matches 1.. run function lantern_tests:load/init
		execute unless score bs.math.major load.status matches 3 run tellraw @a {type:"text",color:"red",text:"[lantern_tests] Missing dependency bs.math 3.1."}
		execute if score bs.math.major load.status matches 3 unless score bs.math.minor load.status matches 1.. run tellraw @a {type:"text",color:"red",text:"[lantern_tests] Missing dependency bs.math 3.1."}
	""".trimIndent()

	functionTagNamed("minecraft", "load") assertsIs """
		{
			"replace": false,
			"values": [
				"#load:_private/load"
			]
		}
	""".trimIndent()

	functionTagNamed("load", "_private/load") assertsIs """
		{
			"replace": false,
			"values": [
				"#load:_private/init",
				{
					"id": "#load:pre_load",
					"required": false
				},
				{
					"id": "#load:load",
					"required": false
				},
				{
					"id": "#load:post_load",
					"required": false
				}
			]
		}
	""".trimIndent()

	functionTagNamed("load", "_private/init") assertsIs """
		{
			"replace": false,
			"values": [
				"load:_private/init"
			]
		}
	""".trimIndent()

	functionTagNamed("load", "load") assertsIs """
		{
			"replace": false,
			"values": [
				"#lantern_tests:load"
			]
		}
	""".trimIndent()

	functionTagNamed("load", "pre_load") assertsIs """
		{
			"replace": false,
			"values": [
				"lantern_tests:load/pre_load"
			]
		}
	""".trimIndent()

	functionTagNamed("load", "post_load") assertsIs """
		{
			"replace": false,
			"values": [
				"lantern_tests:load/post_load"
			]
		}
	""".trimIndent()

	functionTagNamed(name, "load/dependencies") assertsIs """
		{
			"replace": false,
			"values": [
				{
					"id": "#bs.math:load",
					"required": false
				},
				{
					"id": "#smithed.actionbar:load",
					"required": false
				}
			]
		}
	""".trimIndent()

	functionTagNamed(name, "load") assertsIs """
		{
			"replace": false,
			"values": [
				"#lantern_tests:load/dependencies",
				"lantern_tests:load/enumerate",
				"lantern_tests:load/resolve"
			]
		}
	""".trimIndent()
}

class LanternLoadTests : FunSpec({
	test("lantern load") {
		dataPack("lantern_tests") {
			pretty()
			lanternLoadTests()
		}
	}

	test("version parsing") {
		LanternVersion.of("2") shouldBe LanternVersion(2)
		LanternVersion.of("v2.4") shouldBe LanternVersion(2, 4)
		LanternVersion.of("2.4.6-pre.1") shouldBe LanternVersion(2, 4, 6)
		LanternVersion.of("1.0.0").toString() shouldBe "1.0.0"
		(LanternVersion(1, 2, 3) > LanternVersion(1, 2)) shouldBe true

		assertsThrows("Invalid Lantern Load version '1.2.3.4', expected 'major[.minor[.patch]]'.") {
			LanternVersion.of("1.2.3.4")
		}

		assertsThrows("Invalid Lantern Load version 'beta', 'beta' is not a number.") {
			LanternVersion.of("beta")
		}
	}

	test("no dependency skips the resolve function") {
		dataPack("no_deps") {
			pretty()
			lanternLoad { load { say("loaded") } }

			functions.none { it.name == "resolve" } shouldBe true
			tags.none { it.fileName == "load/dependencies" } shouldBe true

			functionTagNamed(name, "load") assertsIs """
				{
					"replace": false,
					"values": [
						"no_deps:load/enumerate",
						"no_deps:load/init"
					]
				}
			""".trimIndent()
		}
	}

	test("boilerplate can be disabled and is generated once") {
		dataPack("no_boilerplate") {
			pretty()
			lanternLoad { generateBoilerplate = false }

			functions.none { it.namespace == "load" } shouldBe true
			tags.none { it.namespace == "minecraft" } shouldBe true
		}

		dataPack("twice") {
			pretty()
			lanternLoad { namespace = "first" }
			lanternLoad { namespace = "second" }

			functions.count { it.namespace == "load" } shouldBe 1

			functionTagNamed("load", "load") assertsIs """
				{
					"replace": false,
					"values": [
						"#first:load",
						"#second:load"
					]
				}
			""".trimIndent()
		}
	}

	test("score holder and objective are configurable") {
		dataPack("custom") {
			pretty()
			lanternLoad {
				scoreHolder = "#custom.pack"
				objective = "my.status"
				version(4, 5, 6)
			}

			functionNamed(name, "load", "enumerate").toString() assertsIs """
				scoreboard players set #custom.pack.major my.status 4
				scoreboard players set #custom.pack.minor my.status 5
				scoreboard players set #custom.pack.patch my.status 6
			""".trimIndent()

			functionNamed("load", "_private", "init").toString() assertsIs """
				# Reset scoreboards so packs can set values accurate for current load.
				scoreboard objectives add my.status dummy
				scoreboard players reset * my.status
			""".trimIndent()
		}
	}
})
