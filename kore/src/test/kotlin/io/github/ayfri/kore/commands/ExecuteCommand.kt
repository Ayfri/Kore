package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.enums.Dimension
import io.github.ayfri.kore.arguments.enums.HeightMap
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.numbers.relativeRot
import io.github.ayfri.kore.arguments.numbers.rot
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.execute.*
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.helpers.predicateRandomChance
import io.github.ayfri.kore.utils.debugEntity

fun Function.executeTests() {
	val testEntity = debugEntity()
	val scores = listOf("a", "b", "c")
	scores.forEachIndexed { index, it ->
		scoreboard.objectives.add(it)
		scoreboard.players.set(testEntity.selector, it, index)
	}

	val selectorAsString = testEntity.selector.asString()
	execute {
		ifCondition {
			scores.forEachIndexed { index, it -> score(testEntity.selector, it, rangeOrInt(index)) }
		}

		run {
			debug("Validated ifCondition with multiple scores", Color.GREEN)
		}
	} assertsIs """
		execute if score $selectorAsString a matches 0 if score $selectorAsString b matches 1 if score $selectorAsString c matches 2 run tellraw @a {"type":"text","text":"Validated ifCondition with multiple scores","color":"green"}
	""".trimIndent()

	val testFunction = datapack.function("test_execute_direct_return") {
		say("is executing test_execute_direct_return")
	}

	execute {
		asTarget(allEntities(true) {
			sort = Sort.RANDOM
		})

		testFunction
	} assertsIs """
		execute as @e[limit=1,sort=random] run function ${testFunction.namespace}:${testFunction.name}
	""".trimIndent()

	execute {
		at(testEntity.selector)

		run {
			say("test")
		}
	} assertsIs """
		execute at $selectorAsString run say test
	""".trimIndent()

	execute {
		align(Axes.XY)
		anchored(Anchor.EYES)
		facing(vec3())

		run {
			say("test")
		}
	} assertsIs """
		execute align xy anchored eyes facing ~ ~ ~ run say test
	""".trimIndent()

	execute {
		facingEntity(testEntity.selector, Anchor.FEET)
		inDimension(Dimension.THE_END)
		on(Relation.ATTACKER)
		positioned(vec3(1, 2, 3))

		run {
			say("test")
		}
	} assertsIs """
		execute facing entity $selectorAsString feet in minecraft:the_end on attacker positioned 1 2 3 run say test
	""".trimIndent()

	execute {
		positionedAs(testEntity.selector)
		positionedOver(HeightMap.OCEAN_FLOOR)
		rotated(rotation(1.relativeRot, 2.rot))

		run {
			say("test")
		}
	} assertsIs """
		execute positioned as $selectorAsString positioned over ocean_floor rotated ~1 2 run say test
	""".trimIndent()

	execute {
		rotatedAs(testEntity.selector)
		summon(EntityTypes.BLOCK_DISPLAY)

		run {
			say("test")
		}
	} assertsIs """
		execute rotated as $selectorAsString summon minecraft:block_display run say test
	""".trimIndent()

	execute {
		ifCondition {
			biome(vec3(), Tags.Worldgen.Biome.HasStructure.ANCIENT_CITY)
			block(vec3(), Blocks.AIR)
			blocks(vec3(PosNumber.Type.LOCAL), vec3(1, 2, 3), vec3(4, 5, 6), BlocksTestMode.MASKED)
			data(self(), "test")
			dimension(Dimension.THE_END)
			@Suppress("DEPRECATION_ERROR")
			function(FunctionArgument("test", datapack.name))
			loaded(vec3(-2, -2, -2))
			predicate("test")
		}

		unlessCondition {
			score(testEntity.selector, "test", rangeOrIntEnd(1))
		}

		storeResult {
			block(vec3(), "test", DataType.BYTE, 1.0)
		}

		run {
			say("test")
		}
	} assertsIs """
		execute
			if biome ~ ~ ~ #minecraft:has_structure/ancient_city
			if block ~ ~ ~ minecraft:air
			if blocks ^ ^ ^ 1 2 3 4 5 6 masked
			if data entity @s test
			if dimension minecraft:the_end
			if function ${datapack.name}:test
			if loaded -2 -2 -2
			if predicate test
			unless score $selectorAsString test matches ..1
			store result block ~ ~ ~ test byte 1
		run
			say test
	""".trimIndent().replace(Regex("\\s*\n\\s*"), " ")

	val scoreName = "test"

	fun ifScoreMatches(matches: String) = "if score $selectorAsString $scoreName matches $matches"
	fun ifScoreRelation(relation: String) = "if score $selectorAsString $scoreName $relation $selectorAsString $scoreName"

	execute {
		ifCondition {
			val score = score(testEntity.selector, scoreName)

			score lessThan 1
			score lessThanOrEqualTo 1
			score equalTo 1
			score greaterThanOrEqualTo 1
			score greaterThan 1

			score lessThan score
			score lessThanOrEqualTo score
			score equalTo score
			score greaterThanOrEqualTo score
			score greaterThan score

			score matches 1..5
			score matches rangeOrInt(1)
		}

		run {
			say("test")
		}
	} assertsIs """
		execute ${ifScoreMatches("..0")} ${ifScoreMatches("..1")} ${ifScoreMatches("1")} ${ifScoreMatches("1..")} ${ifScoreMatches("2..")}
		${ifScoreRelation("<")} ${ifScoreRelation("<=")} ${ifScoreRelation("=")} ${ifScoreRelation(">=")} ${ifScoreRelation(">")}
		${ifScoreMatches("1..5")} ${ifScoreMatches("1")}
		run say test
	""".trimIndent().replace("\n", " ")

	execute {
		storeSuccess {
			bossBarValue("test")
		}

		run {
			say("test")
		}
	} assertsIs """
		execute store success bossbar test value run say test
	""".trimIndent()

	execute {
		ifCondition(datapack.predicateRandomChance(0.5f))

		run {
			say("Hello 50% of the time!")
		}
	} assertsIs """
		execute if predicate ${datapack.name}:random_chance run say Hello 50% of the time!
	""".trimIndent()
}
