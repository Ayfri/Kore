package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.numberproviders.numberProvider
import io.github.ayfri.kore.features.predicates.conditions.blockStateProperty
import io.github.ayfri.kore.features.predicates.providers.numberDispatcher
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.features.predicates.providers.weightedListNumber
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.NumberProviders
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.numberProviderFileTests() {
	val alwaysAddOne = numberProvider("always_add_one", 1f)

	alwaysAddOne.asId() assertsIs "number_provider_file_tests:always_add_one"
	numberProviders.last() assertsIs "1.0"

	numberProvider("range") { uniform(1f, 3f) }

	numberProviders.last() assertsIs """
		{
			"type": "minecraft:uniform",
			"min": 1.0,
			"max": 3.0
		}
	""".trimIndent()

	numberProvider("compostable/low") {
		numberDispatcher(
			default = weightedListNumber {
				entry(30, 1f)
				entry(70, 0f)
			},
		) {
			case(1f) {
				blockStateProperty(Blocks.COMPOSTER) {
					this["level"] = "0"
				}
			}
		}
	}

	numberProviders.last() assertsIs """
		{
			"type": "minecraft:number_dispatcher",
			"cases": [
				{
					"condition": {
						"type": "minecraft:block_state_property",
						"block": "minecraft:composter",
						"properties": {
							"level": "0"
						}
					},
					"number_provider": 1.0
				}
			],
			"default": {
				"type": "minecraft:weighted_list",
				"distribution": [
					{
						"data": 1.0,
						"weight": 30
					},
					{
						"data": 0.0,
						"weight": 70
					}
				]
			}
		}
	""".trimIndent()

	NumberProviders.Compostable.LOW.asId() assertsIs "minecraft:compostable/low"
}

class NumberProviderFileTests : FunSpec({
	test("number provider file") {
		dataPack("number_provider_file_tests") {
			pretty()
			numberProviderFileTests()
		}
	}
})
