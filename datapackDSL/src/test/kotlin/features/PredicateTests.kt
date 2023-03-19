package features

import commands.execute.execute
import commands.execute.run
import dataPack
import features.advancements.types.item
import features.predicates.conditions.matchTool
import features.predicates.predicate
import functions.function
import functions.load
import generated.Items
import setTestPath

fun predicateTests() = dataPack("predicate_tests") {
	setTestPath()
	val myPredicate = predicate("test1") {
		matchTool {
			item(Items.DIAMOND_PICKAXE)
		}
	}

	load {
		debug("predicate test")
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
}.generate()
