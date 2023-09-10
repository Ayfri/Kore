package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.blockpredicate.allOf
import io.github.ayfri.kore.features.worldgen.blockpredicate.matchingBlockTag
import io.github.ayfri.kore.features.worldgen.blockpredicate.trueBlockPredicate
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.blockPredicateFilter
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.noiseThresholdCount
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.rarityFilter
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.generated.ConfiguredFeatures
import io.github.ayfri.kore.generated.Tags

fun DataPack.placedFeatureTests() {
	placedFeature("test", ConfiguredFeatures.ACACIA) {
		noiseThresholdCount {
			noiseLevel = 2.0
			belowNoise = 2
			aboveNoise = 4
		}

		rarityFilter(5)

		blockPredicateFilter(allOf {
			matchingBlockTag(tag = Tags.Blocks.DIRT)
			trueBlockPredicate()
		})
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:noise_threshold_count",
					"noise_level": 2.0,
					"below_noise": 2,
					"above_noise": 4
				},
				{
					"type": "minecraft:rarity_filter",
					"chance": 5
				},
				{
					"type": "minecraft:block_predicate_filter",
					"predicate": {
						"type": "minecraft:all_of",
						"predicates": [
							{
								"type": "minecraft:matching_block_tag",
								"tag": "minecraft:dirt"
							},
							{
								"type": "minecraft:true"
							}
						]
					}
				}
			]
		}
	""".trimIndent()
}
