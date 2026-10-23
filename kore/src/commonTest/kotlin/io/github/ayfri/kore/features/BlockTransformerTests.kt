package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.item.blockTransformer
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.blocktransformers.*
import io.github.ayfri.kore.features.worldgen.blockpredicate.matchingBlocks
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.copyPropertiesProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.rule
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.ruleBasedStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.BlockTransformers
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun DataPack.blockTransformerTests() {
	val strip = blockTransformer("strip_logs") {
		rule(ruleBasedStateProvider {
			rule(copyPropertiesProvider(simpleStateProvider(Blocks.STRIPPED_OAK_LOG, mapOf("axis" to "y")))) {
				matchingBlocks(Blocks.OAK_LOG)
			}
		}) {
			itemDamagePerUse = 1
			sound = SoundEvents.Item.Axe.STRIP
		}
		rule(simpleStateProvider(Blocks.DIRT)) {
			consumeOnUse = false
			disallowedFaces = listOf(Direction.DOWN, Direction.UP)
			dropStrategy = BlockTransformDropStrategy.CLICKED_FACE
			loot = LootTables.Till.ROOTED_DIRT
			particle = BlockTransformParticle.WAX_OFF
			transformType = BlockTransformType.COPPER_CHEST
			updateFromNeighbors = false
		}
	}

	blockTransformers.last() assertsIs """
		[
			{
				"block_state_provider": {
					"type": "minecraft:rule_based_state_provider",
					"rules": [
						{
							"if_true": {
								"type": "minecraft:matching_blocks",
								"blocks": "minecraft:oak_log"
							},
							"then": {
								"type": "minecraft:copy_properties_provider",
								"source": {
									"type": "minecraft:simple_state_provider",
									"state": {
										"id": "minecraft:stripped_oak_log",
										"properties": {
											"axis": "y"
										}
									}
								}
							}
						}
					]
				},
				"item_damage_per_use": 1,
				"sound": "minecraft:item.axe.strip"
			},
			{
				"block_state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": "minecraft:dirt"
				},
				"consume_on_use": false,
				"disallowed_faces": [
					"down",
					"up"
				],
				"drop_strategy": "clicked_face",
				"loot": "minecraft:till/rooted_dirt",
				"particle": "wax_off",
				"transform_type": "copper_chest",
				"update_from_neighbors": false
			}
		]
	""".trimIndent()

	Items.IRON_AXE { blockTransformer(BlockTransformers.AXE) }.asString() assertsIs """minecraft:iron_axe[block_transformer="minecraft:axe"]"""
	Items.IRON_AXE { blockTransformer(strip) }.asString() assertsIs """minecraft:iron_axe[block_transformer="$name:strip_logs"]"""
}

class BlockTransformerTests : FunSpec({
	test("block transformer") {
		dataPack("blockTransformer") {
			pretty()
			blockTransformerTests()
		}
	}

	test("block transformer without rules is rejected") {
		dataPack("emptyBlockTransformer") {
			blockTransformer("empty") {}
			shouldThrow<IllegalArgumentException> { blockTransformers.last().generateJson(this) }
		}
	}

	test("block transformer with more than 200 rules is rejected") {
		dataPack("hugeBlockTransformer") {
			blockTransformer("huge") {
				repeat(201) { rule(simpleStateProvider(Blocks.DIRT)) }
			}
			shouldThrow<IllegalArgumentException> { blockTransformers.last().generateJson(this) }
			blockTransformers.last().rules = blockTransformers.last().rules.take(200)
			blockTransformers.last().generateJson(this)
		}
	}
})
