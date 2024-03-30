package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDoubleEnd
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.scores.lessThan
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.SelectorType
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.literals.*
import io.github.ayfri.kore.arguments.types.resources.AdvancementArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.entityProperties
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.block
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.setTestPath
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

fun selectorTests() = dataPack("selector_tests") {
	setTestPath()

	selector(SelectorType.ALL_ENTITIES) assertsIs "@e"

	allEntities() assertsIs "@e"
	allPlayers() assertsIs "@a"
	randomPlayer() assertsIs "@r"
	nearestPlayer() assertsIs "@p"
	self() assertsIs "@s"

	player("foo") assertsIs "@a[limit=1,name=foo]"

	allEntities {
		advancements {
			AdvancementArgument("foo", "test")() {
				this["bar"] = true
			}
		}
	} assertsIs "@e[advancements={test:foo={bar=true}}]"

	allEntities {
		distance = rangeOrDoubleEnd(1.5)
	} assertsIs "@e[distance=..1.5]"

	allEntities {
		dx = 1.0
		dy = 2.0
		dz = 3.0
	} assertsIs "@e[dx=1.0,dy=2.0,dz=3.0]"

	allEntities {
		gamemode = !Gamemode.CREATIVE
	} assertsIs "@e[gamemode=!creative]"

	allEntities {
		level = rangeOrIntEnd(1)
	} assertsIs "@e[level=..1]"

	allEntities {
		limit = 1
	} assertsIs "@e[limit=1]"

	allEntities(true) assertsIs "@e[limit=1]"
	allEntitiesLimitToOne() assertsIs "@e[limit=1]"

	allEntities {
		name = "foo"
	} assertsIs "@e[name=foo]"

	allEntities {
		nbt = nbt {
			this["foo"] = "bar"
		}
	} assertsIs "@e[nbt={foo:\"bar\"}]"

	allEntities {
		predicate = predicate("foo") {
			entityProperties {
				steppingOn = block(Blocks.STONE)
			}
		}
	} assertsIs "@e[predicate=selector_tests:foo]"

	allEntities {
		scores {
			score("foo", rangeOrInt(1))
			score("bar") greaterThanOrEqualTo 1
			"baz" lessThan 1
		}
	} assertsIs "@e[scores={foo=1,bar=1..,baz=..0}]"

	allEntities {
		sort = Sort.RANDOM
	} assertsIs "@e[sort=random]"

	allEntities {
		tag = "foo"
	} assertsIs "@e[tag=foo]"

	allEntities {
		team = !""
	} assertsIs "@e[team=!]"

	allEntities {
		type = EntityTypes.MARKER
	} assertsIs "@e[type=minecraft:marker]"

	allEntities {
		type = !EntityTypes.MARKER
	} assertsIs "@e[type=!minecraft:marker]"

	allEntities {
		xRotation = rangeOrDouble(1.5)
		y = 5.0
	} assertsIs "@e[x_rotation=1.5,y=5.0]"
}.generate()
