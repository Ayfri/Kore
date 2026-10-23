package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsThrows
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.contextproviders.contextFloatProvider
import io.github.ayfri.kore.features.contextproviders.contextIntProvider
import io.github.ayfri.kore.features.predicates.conditions.survivesExplosion
import io.github.ayfri.kore.features.predicates.providers.floatWeightedList
import io.github.ayfri.kore.features.predicates.providers.intNumberDispatcher
import io.github.ayfri.kore.features.predicates.providers.intWeightedList
import io.github.ayfri.kore.features.predicates.providers.reference
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.generated.ContextIntProviders
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.contextProviderFileTests() {
	val alwaysOne = contextIntProvider("always_one", 1)

	alwaysOne.asId() assertsIs "context_provider_file_tests:always_one"
	contextIntProviders.last() assertsIs "1"

	val oneAndAHalf = contextFloatProvider("one_and_a_half", 1.5f)

	oneAndAHalf.asId() assertsIs "context_provider_file_tests:one_and_a_half"
	contextFloatProviders.last() assertsIs "1.5"

	contextIntProvider("range") { uniform(1, 3) }

	contextIntProviders.last() assertsIs """
		{
			"type": "minecraft:uniform",
			"max": 3,
			"min": 1
		}
	""".trimIndent()

	contextFloatProvider("float_range") { uniform(1f, 3f) }

	contextFloatProviders.last() assertsIs """
		{
			"type": "minecraft:uniform",
			"max": 3.0,
			"min": 1.0
		}
	""".trimIndent()

	contextIntProvider("compostable/low") {
		intNumberDispatcher(
			default = intWeightedList {
				entry(30, 1)
				entry(70, 0)
			},
		) {
			case(1) { survivesExplosion() }
		}
	}

	contextIntProviders.last() assertsIs """
		{
			"type": "minecraft:number_dispatcher",
			"cases": [
				{
					"condition": {
						"type": "minecraft:survives_explosion"
					},
					"value": 1
				}
			],
			"default": {
				"type": "minecraft:weighted_list",
				"distribution": [
					{
						"data": 1,
						"weight": 30
					},
					{
						"data": 0,
						"weight": 70
					}
				]
			}
		}
	""".trimIndent()

	contextFloatProvider("float_weights") {
		floatWeightedList {
			entry(30, 1f)
			entry(70, 0f)
		}
	}

	contextFloatProviders.last() assertsIs """
		{
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
	""".trimIndent()

	ContextIntProviders.Compostable.LOW.asId() assertsIs "minecraft:compostable/low"

	contextIntProvider("reference_file") { reference(ContextIntProviderArgument("range", "context_provider_file_tests")) }

	assertsThrows("Context int provider file 'reference_file' must hold an inline provider, not a reference.") {
		contextIntProviders.last().generateJson(this)
	}

	contextFloatProvider("float_reference_file") { reference(ContextFloatProviderArgument("float_range", "context_provider_file_tests")) }

	assertsThrows("Context float provider file 'float_reference_file' must hold an inline provider, not a reference.") {
		contextFloatProviders.last().generateJson(this)
	}
}

class ContextProviderFileTests : FunSpec({
	test("context provider files") {
		dataPack("context_provider_file_tests") {
			pretty()
			contextProviderFileTests()
		}
	}
})
