package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.relativePos
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.compute
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.helpers.blocks.blockId
import io.github.ayfri.kore.helpers.blocks.blockIdBits
import io.github.ayfri.kore.helpers.blocks.blockIdProvider
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

private fun Tag<*>.ids() = values.map { it.name }

class BlockIdTests : FunSpec({
	test("block IDs are the 1-based enum order, 0 for unknown blocks, states ignored") {
		blockId(Blocks.entries.first()) shouldBe 1
		blockId(Blocks.entries.last()) shouldBe Blocks.entries.size
		blockId(Blocks.OAK_STAIRS(mapOf("half" to "top"))) shouldBe blockId(Blocks.OAK_STAIRS)
		blockId(BlockArgument("ruby_ore", "my_mod")) shouldBe 0
		blockId(BlockArgument("stone", "minecraft")) shouldBe blockId(Blocks.STONE)
		Blocks.entries.map(::blockId).toSet().size shouldBe Blocks.entries.size
	}

	test("bits are the smallest k with 2^k above the block count") {
		((1 shl blockIdBits) > Blocks.entries.size) shouldBe true
		((1 shl (blockIdBits - 1)) > Blocks.entries.size) shouldBe false
	}

	test("each block is listed in exactly the tags of its set bits") {
		dataPack("block_ids") {
			val provider = blockIdProvider()
			provider.asString() shouldBe "block_ids:kore_block_id"

			val tags = tags.filter { it.fileName.startsWith("kore_block_id/bit_") }.associateBy { it.fileName }
			tags.size shouldBe blockIdBits

			for (block in listOf(Blocks.entries.first(), Blocks.STONE, Blocks.WATER, Blocks.entries.last())) {
				val id = blockId(block)
				for (bit in 0 until blockIdBits) {
					(block.asId() in tags.getValue("kore_block_id/bit_$bit").ids()) shouldBe ((id shr bit) and 1 == 1)
				}
			}

			tags.getValue("kore_block_id/bit_0").ids().size shouldBe (Blocks.entries.size + 1) / 2
			tags.values.all { it.type == "block" && it.namespace == "block_ids" } shouldBe true
		}
	}

	test("provider file sums one match_block check per bit") {
		dataPack("block_ids") {
			pretty()
			blockIdProvider()
			val json = contextIntProviders.single().generateJson(this)

			json.lines().take(11) assertsIs listOf(
				"{",
				"\t\"type\": \"minecraft:add\",",
				"\t\"inputs\": [",
				"\t\t{",
				"\t\t\t\"type\": \"minecraft:conditional\",",
				"\t\t\t\"condition\": {",
				"\t\t\t\t\"type\": \"minecraft:match_block\",",
				"\t\t\t\t\"blocks\": \"#block_ids:kore_block_id/bit_0\"",
				"\t\t\t},",
				"\t\t\t\"on_true\": 1",
				"\t\t},",
			)
			json.lines().takeLast(10) assertsIs listOf(
				"\t\t{",
				"\t\t\t\"type\": \"minecraft:conditional\",",
				"\t\t\t\"condition\": {",
				"\t\t\t\t\"type\": \"minecraft:match_block\",",
				"\t\t\t\t\"blocks\": \"#block_ids:kore_block_id/bit_${blockIdBits - 1}\"",
				"\t\t\t},",
				"\t\t\t\"on_true\": ${1 shl (blockIdBits - 1)}",
				"\t\t}",
				"\t]",
				"}",
			)
		}
	}

	test("compute block uses the provider and repeated calls generate once") {
		dataPack("block_ids") {
			val provider = blockIdProvider()
			blockIdProvider() shouldBe provider

			function("check") {
				compute(vec3(0.relativePos, (-1).relativePos, 0.relativePos), provider) assertsIs "compute block ~ ~-1 ~ integer block_ids:kore_block_id"
			}

			contextIntProviders.size shouldBe 1
			tags.size shouldBe blockIdBits
			tags.map { it.fileName } shouldContainExactly List(blockIdBits) { "kore_block_id/bit_$it" }
		}
	}
})
