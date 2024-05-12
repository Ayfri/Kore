package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.components.*
import io.github.ayfri.kore.arguments.components.matchers.damage
import io.github.ayfri.kore.arguments.components.matchers.potionContents
import io.github.ayfri.kore.arguments.components.types.customData
import io.github.ayfri.kore.arguments.components.types.damage
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.set

fun itemPredicatesTests() {
	val countTest = Items.STONE.predicate {
		count(1..5)
	}

	countTest assertsIs """minecraft:stone[count~{min:1,max:5}]"""

	countTest.apply { clearPredicate("count") } assertsIs """minecraft:stone"""
	countTest.count(1) assertsIs """minecraft:stone[count=1]"""

	countTest.apply { negate("count") } assertsIs """minecraft:stone[!count=1]"""

	val anyItemTest = itemPredicate {
		isPresent(ComponentTypes.DAMAGE)
	}

	anyItemTest.toString() assertsIs """*[damage]"""
	anyItemTest.apply { clearPredicate(ComponentTypes.DAMAGE) } assertsIs """*"""

	val subPredicateTest = Items.STONE.predicate {
		subPredicates {
			this.damage {
				durability = rangeOrInt(1..5)
			}
		}
	}

	subPredicateTest.toString() assertsIs """minecraft:stone[damage~{durability:{min:1,max:5}}]"""
	subPredicateTest.apply { clearPredicate("damage") } assertsIs """minecraft:stone"""


	val multipleCountTest = Items.STONE.predicate {
		count(1) or count(2..5) or count(6..10).apply {
			setNegated("count")
		}
	}

	multipleCountTest.toString() assertsIs """minecraft:stone[count=1|count~{min:2,max:5}|!count~{min:6,max:10}]"""


	val multipleSubPredicatesTest = Items.STONE.predicate {
		subPredicates {
			potionContents(Effects.SPEED, Effects.SLOWNESS)
		}
		subPredicates {
			potionContents(Effects.STRENGTH)
		}
	}

	multipleSubPredicatesTest.toString() assertsIs """minecraft:stone[potion_contents=["minecraft:speed","minecraft:slowness"]|potion_contents="minecraft:strength"]"""


	val multipleComponentTest = Items.STONE.predicate {
		damage(1)
		damage(2)
		!damage(4)
	}

	multipleComponentTest.toString() assertsIs """minecraft:stone[damage=1|damage=2|!damage=4]"""


	val partialComponentTest = Items.STONE.predicate {
		customData {
			this["test"] = 1
		}
		partial(ComponentTypes.CUSTOM_DATA)
	}

	partialComponentTest.toString() assertsIs """minecraft:stone[custom_data~{test:1}]"""

	val customPartialDataComponent = Items.STONE.predicate {
		buildPartial("my_data") {
			this["value"] = "test"
		}
	}

	customPartialDataComponent.toString() assertsIs """minecraft:stone[my_data~{value:"test"}]"""
}
