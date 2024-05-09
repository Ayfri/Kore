package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.entityspecific.variants
import io.github.ayfri.kore.features.predicates.sub.entityspecific.wolfTypeSpecific
import io.github.ayfri.kore.generated.WolfVariants

fun DataPack.predicateEntityTypeSpecificTests() {
	predicate("wolf_type_specific") {
		entityProperties {
			wolfTypeSpecific {
				variants(WolfVariants.WOODS)
			}
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:wolf",
					"variant": "minecraft:woods"
				}
			}
		}
	""".trimIndent()
}
