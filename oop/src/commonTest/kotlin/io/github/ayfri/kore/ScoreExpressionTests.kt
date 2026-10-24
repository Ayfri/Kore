package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.features.predicates.providers.plus
import io.github.ayfri.kore.features.predicates.providers.times
import io.github.ayfri.kore.features.predicates.providers.toFloat
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.computeScores
import io.github.ayfri.kore.scoreboard.divAssign
import io.github.ayfri.kore.scoreboard.setTo
import io.github.ayfri.kore.scoreboard.timesAssign
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class ScoreExpressionTests : FunSpec({
	test("an expression reading fake players only runs a compute with no target") {
		val datapack = dataPack("oop_tests") {}

		val sum = datapack.function("fake_only") {
			val a = ScoreboardEntity("game", fakePlayer("a"))
			val b = ScoreboardEntity("game", fakePlayer("b"))
			ScoreboardEntity("game", fakePlayer("sum")) setTo { score(a) + score(b) * 2 }
		}

		sum.toString() assertsIs """
			execute store result score #sum game run compute default integer {type:"minecraft:add",inputs:[{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#a"}},{type:"minecraft:mul",inputs:[{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#b"}},2]}]}
		""".trimIndent()
	}

	test("an expression reading one real entity computes on it and reads it as the target entity") {
		val datapack = dataPack("oop_tests") {}

		val own = datapack.function("self_entity") {
			val kills = ScoreboardEntity("kills", player("Ayfri"))
			val total = ScoreboardEntity("total", fakePlayer("total"))
			total setTo { score(kills) }
			kills setTo { score(kills) + 1 }
		}

		own.toString() assertsIs """
			execute store result score #total total run compute entity @e[limit=1,name=Ayfri,type=minecraft:player] integer {type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}
			execute store result score @e[limit=1,name=Ayfri,type=minecraft:player] kills run compute entity @e[limit=1,name=Ayfri,type=minecraft:player] integer {type:"minecraft:add",inputs:[{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}},1]}
		""".trimIndent()
	}

	test("an expression mixing a fake player and an entity computes on the entity") {
		val datapack = dataPack("oop_tests") {}

		val mixed = datapack.function("mixed") {
			val kills = ScoreboardEntity("kills", player("Ayfri"))
			val goal = ScoreboardEntity("goal", fakePlayer("goal"))
			ScoreboardEntity("left", fakePlayer("left")) setTo { score(goal) + score(kills) }
		}

		mixed.toString() assertsIs """
			execute store result score #left left run compute entity @e[limit=1,name=Ayfri,type=minecraft:player] integer {type:"minecraft:add",inputs:[{type:"minecraft:score",score:goal,target:{type:"minecraft:fixed",name:"#goal"}},{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}]}
		""".trimIndent()
	}

	test("two different real entities in one expression throw, the same entity twice is fine") {
		val datapack = dataPack("oop_tests") {}

		datapack.function("two_entities") {
			val first = ScoreboardEntity("kills", player("Ayfri"))
			val second = ScoreboardEntity("kills", player("Notch"))
			val out = ScoreboardEntity("out", fakePlayer("out"))

			shouldThrow<IllegalStateException> { out setTo { score(first) + score(second) } }
			out setTo { score(first) + score(ScoreboardEntity("deaths", player("Ayfri"))) }
		}
	}

	test("a selector that can match several entities throws") {
		val datapack = dataPack("oop_tests") {}

		datapack.function("many_entities") {
			val zombies = ScoreboardEntity("kills", entity(limitToOne = false))
			val out = ScoreboardEntity("out", fakePlayer("out"))

			shouldThrow<IllegalStateException> { out setTo { score(zombies) } }
			shouldThrow<IllegalStateException> { zombies *= 1.5f }
		}
	}

	test("computeScores writes a score expression into data and checks the entity like setTo") {
		val datapack = dataPack("oop_tests") {}

		val stored = datapack.function("stored") {
			val kills = ScoreboardEntity("kills", player("Ayfri"))
			val bonus = ScoreboardEntity("game", fakePlayer("bonus"))
			data(storage("foo")).computeScores("bonus_only") { score(bonus).toFloat() }
			data(storage("foo")).computeScores("kills") { score(kills).toFloat() }
			shouldThrow<IllegalStateException> {
				data(storage("foo")).computeScores("many") { score(ScoreboardEntity("kills", entity(limitToOne = false))).toFloat() }
			}
		}

		stored.toString() assertsIs """
			data modify storage minecraft:foo bonus_only set compute default float {type:"minecraft:from_int",input:{type:"minecraft:score",score:game,target:{type:"minecraft:fixed",name:"#bonus"}}}
			data modify storage minecraft:foo kills set compute entity @e[limit=1,name=Ayfri,type=minecraft:player] float {type:"minecraft:from_int",input:{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}}
		""".trimIndent()
	}

	test("a float factor multiplies and divides in one floored compute") {
		val datapack = dataPack("oop_tests") {}

		val factors = datapack.function("float_factors") {
			val score = ScoreboardEntity("score", fakePlayer("total"))
			val kills = ScoreboardEntity("kills", player("Ayfri"))
			score *= 1.5f
			score /= 2.5f
			kills *= 2f
		}

		factors.toString() assertsIs """
			execute store result score #total score run compute default float {type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:score",score:score,target:{type:"minecraft:fixed",name:"#total"}}},1.5d]}
			execute store result score #total score run compute default float {type:"minecraft:div",left:{type:"minecraft:from_int",input:{type:"minecraft:score",score:score,target:{type:"minecraft:fixed",name:"#total"}}},right:2.5d}
			execute store result score @e[limit=1,name=Ayfri,type=minecraft:player] kills run compute entity @e[limit=1,name=Ayfri,type=minecraft:player] float {type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:score",score:kills,target:{type:"minecraft:context",target:target_entity}}},2]}
		""".trimIndent()
	}

	test("a float expression takes a scale") {
		val datapack = dataPack("oop_tests") {}

		val scaled = datapack.function("scaled") {
			val chance = ScoreboardEntity("chance", fakePlayer("chance"))
			val kills = ScoreboardEntity("kills", fakePlayer("kills"))
			chance.setTo(1000f) { uniform(0.5f, 2f) }
			chance.setTo(100f) { score(kills).toFloat() * 0.5f }
		}

		scaled.toString() assertsIs """
			execute store result score #chance chance run compute default float {type:"minecraft:uniform",max:2,min:0.5d} 1000
			execute store result score #chance chance run compute default float {type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:score",score:kills,target:{type:"minecraft:fixed",name:"#kills"}}},0.5d]} 100
		""".trimIndent()
	}
})
