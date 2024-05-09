package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.entityspecific.*
import io.github.ayfri.kore.generated.CatVariants
import io.github.ayfri.kore.generated.FrogVariants
import io.github.ayfri.kore.generated.PaintingVariants
import io.github.ayfri.kore.generated.WolfVariants

fun DataPack.predicateEntityTypeSpecificTests() {
	predicate("cat_type_specific") {
		entityProperties {
			catTypeSpecific(CatVariants.WHITE)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:cat",
					"variant": "minecraft:white"
				}
			}
		}
	""".trimIndent()

	predicate("frog_type_specific") {
		entityProperties {
			frogTypeSpecific(FrogVariants.COLD, FrogVariants.WARM)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:frog",
					"variant": [
						"minecraft:cold",
						"minecraft:warm"
					]
				}
			}
		}
	""".trimIndent()

	predicate("painting_type_specific") {
		entityProperties {
			paintingTypeSpecific(PaintingVariants.ALBAN, PaintingVariants.AZTEC)
		}
	}

	predicates.last() assertsIs """
		{
			"condition": "minecraft:entity_properties",
			"predicate": {
				"type_specific": {
					"type": "minecraft:painting",
					"variant": [
						"minecraft:alban",
						"minecraft:aztec"
					]
				}
			}
		}
	""".trimIndent()

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
