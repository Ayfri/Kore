package features

import dataPack
import features.advancements.types.item
import features.predicates.conditions.*
import features.predicates.predicate
import generated.Items
import setTestPath

fun predicateTests() = dataPack("predicate_tests") {
	setTestPath()
	predicate("test1") {
		damageSourceProperties {
			bypassesArmor = true
			isExplosion = true
		}

		alternative {
			inverted {
				timeCheck(10f..1000f)
			}
		}

		killedByPlayer()

		matchTool {
			item(Items.DIAMOND_PICKAXE)
		}
	}
}.generate()
