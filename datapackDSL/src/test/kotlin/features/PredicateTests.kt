package features

import DataPack
import commands.execute.execute
import commands.execute.run
import features.advancements.types.item
import features.predicates.conditions.matchTool
import features.predicates.predicate
import functions.function
import generated.Items

fun DataPack.predicateTests() {
	val myPredicate = predicate("test1") {
		matchTool {
			item(Items.DIAMOND_PICKAXE)
		}
	}

	function("test") {
		execute {
			ifCondition {
				predicate(myPredicate)
			}

			run {
				debug("predicate validated !")
			}
		}
	}
}
