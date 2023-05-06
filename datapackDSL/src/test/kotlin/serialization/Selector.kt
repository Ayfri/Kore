package serialization

import arguments.*
import arguments.enums.Gamemode
import arguments.numbers.rangeOrDouble
import arguments.numbers.rangeOrDoubleEnd
import arguments.numbers.rangeOrInt
import arguments.numbers.rangeOrIntEnd
import arguments.scores.lessThan
import arguments.scores.score
import arguments.selector.SelectorType
import arguments.selector.Sort
import arguments.selector.scores
import dataPack
import features.advancements.types.block
import features.predicates.conditions.entityProperties
import features.predicates.predicate
import generated.Blocks
import generated.Entities
import generated.type
import setTestPath
import utils.assertsIs

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
			Argument.Advancement("foo", "test")() {
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
		type(Entities.MARKER)
	} assertsIs "@e[type=minecraft:marker]"

	allEntities {
		xRotation = rangeOrDouble(1.5)
		y = 5.0
	} assertsIs "@e[x_rotation=1.5,y=5.0]"
}.generate()
