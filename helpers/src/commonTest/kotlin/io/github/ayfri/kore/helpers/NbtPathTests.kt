package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.nbt.NbtPath
import io.github.ayfri.kore.helpers.nbt.nbtPath
import io.github.ayfri.kore.helpers.nbt.resolveNbt
import io.github.ayfri.kore.helpers.nbt.toNbtPath
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.utils.set
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NbtPathTests : FunSpec({
	test("path segments are quoted only when vanilla requires it") {
		(nbtPath("equipment") / "mainhand" / "components" / "minecraft:enchantments").toString() shouldBe
			"""equipment.mainhand.components."minecraft:enchantments""""

		nbtPath("Inventory")[0].toString() shouldBe "Inventory[0]"
		nbtPath("Items").all().toString() shouldBe "Items[]"
		nbtPath("Items").matching { this["Slot"] = 0.toByte() }.toString() shouldBe "Items[{Slot:0b}]"
		NbtPath().isRoot shouldBe true
	}

	test("resolveNbt splits one tree into a static merge plus one store per score") {
		val datapack = dataPack("helpers_tests") {}
		val shop = storage("shop", "helpers")

		val resolve = datapack.function("resolve_nbt") {
			val price = ScoreboardEntity("shop", fakePlayer("price"))
			val stock = ScoreboardEntity("shop", fakePlayer("stock"))

			resolveNbt(shop, nbtPath("offer")) {
				this["item"] = "minecraft:diamond"
				this["price"] = price
				compound("meta") {
					this["featured"] = true
					this["stock"] = stock
				}
			}
		}

		resolve.toString() assertsIs """
			data modify storage helpers:shop offer set value {item:"minecraft:diamond",meta:{featured:1b}}
			execute store result storage helpers:shop offer.price int 1.0 run scoreboard players get #price shop
			execute store result storage helpers:shop offer.meta.stock int 1.0 run scoreboard players get #stock shop
		""".trimIndent()
	}

	test("resolveNbt emits only stores when every value is dynamic") {
		val datapack = dataPack("helpers_tests") {}
		val macro = storage("macro", "helpers")

		val resolve = datapack.function("resolve_nbt_dynamic") {
			resolveNbt(macro) {
				this["x"] = ScoreboardEntity("pos", fakePlayer("x"))
			}
		}

		resolve.toString() assertsIs
			"execute store result storage helpers:macro x int 1.0 run scoreboard players get #x pos"
	}

	test("toNbtPath keeps quoted keys, indices and filters as single segments") {
		val path = """Items[{Slot:0b}].components."minecraft:custom_data".id""".toNbtPath()

		path.segments shouldBe listOf("Items[{Slot:0b}]", "components", "\"minecraft:custom_data\"", "id")
		path.toString() shouldBe """Items[{Slot:0b}].components."minecraft:custom_data".id"""

		"".toNbtPath().isRoot shouldBe true
		(nbtPath("root") / "a.b".toNbtPath()).toString() shouldBe "root.a.b"
	}
})
