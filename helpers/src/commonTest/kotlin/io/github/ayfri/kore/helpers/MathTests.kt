package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.maths.*
import io.github.ayfri.kore.helpers.state.scoreboard
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

private val holder = FakePlayer("#p")
private fun score(name: String) = ScoreboardEntity(name, holder)
private fun DataPack.generated(name: String) = generatedFunctions.first { it.name == name }.toString()

fun mathScalarTests() = dataPack("math_tests") {
	val math = registerMath()

	function("trig") {
		math.sin(score("angle"), score("sin"))
		math.cos(score("angle"), score("cos"), angleScale = 100)
		toString() assertsIs """
			execute store result score #p sin run compute default float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:sin",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:angle,target:{type:"minecraft:fixed",name:"#p"}},right:360}},0.017453292d]}},1000]}}
			execute store result score #p cos run compute default float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:cos",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:angle,target:{type:"minecraft:fixed",name:"#p"}},right:36000}},1.7453292E-4d]}},1000]}}
		""".trimIndent()
	}

	function("trig_on_entity") {
		math.sin(ScoreboardEntity("angle", player("P")), score("sin"), angleScale = 10)
		lines.single() assertsIs """execute store result score #p sin run compute entity @e[limit=1,name=P,type=minecraft:player] float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:sin",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:angle,target:{type:"minecraft:context",target:target_entity}},right:3600}},0.0017453292d]}},1000]}}"""
	}

	function("roots") {
		val root = """{type:"minecraft:from_float",input:{type:"minecraft:sqrt",input:{type:"minecraft:from_int",input:{type:"minecraft:max",inputs:[{type:"minecraft:score",score:in,target:{type:"minecraft:fixed",name:"#p"}},0]}}}}"""
		math.sqrt(score("in"), score("root"))
		lines.single() assertsIs """execute store result score #p root run compute default integer {type:"minecraft:sub",left:$root,right:{type:"minecraft:conditional",condition:{type:"minecraft:int_value_check",test:{max:-1},value:{type:"minecraft:sub",left:{type:"minecraft:max",inputs:[{type:"minecraft:score",score:in,target:{type:"minecraft:fixed",name:"#p"}},0]},right:{type:"minecraft:mul",inputs:[$root,$root]}}},on_true:1}}"""
		math.atan2(score("y"), score("x"), score("angle"))
	}

	generated("atan2_1") assertsIs """
		execute store result entity 4b4f5245-0000-0000-0000-000000000001 Pos[0] double 0.001 run scoreboard players get #y kore_math
		execute store result entity 4b4f5245-0000-0000-0000-000000000001 Pos[2] double 0.001 run scoreboard players get #x kore_math
		execute in minecraft:overworld positioned 0.0 0.0 0.0 facing entity 4b4f5245-0000-0000-0000-000000000001 feet run teleport 4b4f5245-0000-0000-0000-000000000001 -30000000.0 0.0 1664.0 ~ ~
		execute store result score #r kore_math run data get entity 4b4f5245-0000-0000-0000-000000000001 Rotation[0] -1
		execute if score #r kore_math matches ..-180 run scoreboard players add #r kore_math 360
		return run scoreboard players get #r kore_math
	""".trimIndent()

	function("legacy_helpers") {
		val player = player("P")
		fun read(name: String) = """{type:"minecraft:score",score:$name,target:{type:"minecraft:context",target:target_entity}}"""
		fun delta(from: String, to: String) = """{type:"minecraft:sub",left:${read(to)},right:${read(from)}}"""
		fun square(from: String, to: String) = """{type:"minecraft:mul",inputs:[${delta(from, to)},${delta(from, to)}]}"""
		val selector = "@e[limit=1,name=P,type=minecraft:player]"

		math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist")
		lines.single() assertsIs """execute store result score $selector dist run compute entity $selector integer {type:"minecraft:add",inputs:[${square("x1", "x2")},${square("y1", "y2")},${square("z1", "z2")}]}"""
	}

	function("parabola") {
		math.parabola(holder, "t", "#v0", "#g", "height")
		val t = """{type:"minecraft:score",score:t,target:{type:"minecraft:fixed",name:"#p"}}"""
		val v0 = """{type:"minecraft:score",score:kore_math,target:{type:"minecraft:fixed",name:"#v0"}}"""
		val g = """{type:"minecraft:score",score:kore_math,target:{type:"minecraft:fixed",name:"#g"}}"""
		lines.single() assertsIs """execute store result score #p height run compute default integer {type:"minecraft:sub",left:{type:"minecraft:mul",inputs:[$v0,$t]},right:{type:"minecraft:floor_div",left:{type:"minecraft:mul",inputs:[$g,$t,$t]},right:2}}"""
	}

	function("parabola_on_two_entities") {
		val time = ScoreboardEntity("t", player("A"))
		val velocity = ScoreboardEntity("v0", player("B"))
		val gravity = ScoreboardEntity("g", FakePlayer("#g"))
		math.parabola(time, velocity, gravity, score("height"))
		val t = """{type:"minecraft:score",score:t,target:{type:"minecraft:context",target:target_entity}}"""
		val v0 = """{type:"minecraft:score",score:kore_math,target:{type:"minecraft:fixed",name:"#s1"}}"""
		val g = """{type:"minecraft:score",score:g,target:{type:"minecraft:fixed",name:"#g"}}"""
		lines assertsIs listOf(
			"scoreboard players operation #s1 kore_math = @e[limit=1,name=B,type=minecraft:player] v0",
			"""execute store result score #p height run compute entity @e[limit=1,name=A,type=minecraft:player] integer {type:"minecraft:sub",left:{type:"minecraft:mul",inputs:[$v0,$t]},right:{type:"minecraft:floor_div",left:{type:"minecraft:mul",inputs:[$g,$t,$t]},right:2}}""",
		)
	}

	function("delegates") {
		val angle = holder.scoreboard("launch_angle")
		val sine = holder.scoreboard("sin_angle")
		with(math) { angle sinTo sine }
		lines.last() assertsIs """execute store result score #p sin_angle run compute default float {type:"minecraft:round",input:{type:"minecraft:mul",inputs:[{type:"minecraft:sin",input:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:{type:"minecraft:mod",left:{type:"minecraft:score",score:launch_angle,target:{type:"minecraft:fixed",name:"#p"}},right:360}},0.017453292d]}},1000]}}"""
	}

	generated(HelpersConstants.mathInitFunction).lines().run {
		first() assertsIs "scoreboard objectives add kore_math dummy"
		contains("forceload add -30000000 1664") assertsIs true
		contains("execute unless entity 4b4f5245-0000-0000-0000-000000000001 run summon minecraft:marker -30000000.0 0.0 1664.0 {Tags:[\"kore.math\",\"smithed.entity\",\"smithed.strict\"],UUID:[I;1263489605,0,0,1]}") assertsIs true
	}

	registerMath() assertsIs math
	shouldThrow<IllegalArgumentException> {
		function("too_fine") { math.sin(score("angle"), score("sin"), angleScale = 46_604) }
	}
	shouldThrow<IllegalArgumentException> {
		function("no_scale") { math.cos(score("angle"), score("cos"), angleScale = 0) }
	}
}

fun mathVectorTests() = dataPack("vector_tests") {
	val math = registerMath()
	val a = math.vector("a", holder)
	val b = math.vector("b", holder)

	function("arithmetic") {
		a.set(0.5, 1.0, -2.0)
		a += b
		a *= 3
		a.dot(b, score("dot"))
		toString() assertsIs """
			scoreboard players set #p a_x 500
			scoreboard players set #p a_y 1000
			scoreboard players set #p a_z -2000
			scoreboard players operation #p a_x += #p b_x
			scoreboard players operation #p a_y += #p b_y
			scoreboard players operation #p a_z += #p b_z
			scoreboard players operation #p a_x *= #3 kore_math
			scoreboard players operation #p a_y *= #3 kore_math
			scoreboard players operation #p a_z *= #3 kore_math
			scoreboard players operation #p dot = #p a_x
			scoreboard players operation #p dot *= #p b_x
			scoreboard players operation #t kore_math = #p a_y
			scoreboard players operation #t kore_math *= #p b_y
			scoreboard players operation #p dot += #t kore_math
			scoreboard players operation #t kore_math = #p a_z
			scoreboard players operation #t kore_math *= #p b_z
			scoreboard players operation #p dot += #t kore_math
			scoreboard players operation #p dot /= #1000 kore_math
		""".trimIndent()
	}

	function("length") {
		a.length(score("length"))
		toString() assertsIs """
			execute store result storage vector_tests:kore_math matrix[0] float 1 run scoreboard players get #p a_x
			execute store result storage vector_tests:kore_math matrix[4] float 1 run scoreboard players get #p a_y
			execute store result storage vector_tests:kore_math matrix[8] float 1 run scoreboard players get #p a_z
			data modify entity 4b4f5245-0000-0000-0000-000000000002 transformation set from storage vector_tests:kore_math matrix
			execute store result score #p length run data get entity 4b4f5245-0000-0000-0000-000000000002 transformation.scale[0]
		""".trimIndent()
	}

	function("normalize") {
		a.normalize()
		toString() assertsIs """
			execute store result entity 4b4f5245-0000-0000-0000-000000000001 Pos[0] double 0.001 run scoreboard players get #p a_x
			execute store result entity 4b4f5245-0000-0000-0000-000000000001 Pos[1] double 0.001 run scoreboard players get #p a_y
			execute store result entity 4b4f5245-0000-0000-0000-000000000001 Pos[2] double 0.001 run scoreboard players get #p a_z
			execute in minecraft:overworld positioned 0.0 0.0 0.0 facing entity 4b4f5245-0000-0000-0000-000000000001 feet run teleport 4b4f5245-0000-0000-0000-000000000001 ^ ^ ^1
			execute store result score #p a_x run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[0] 1000
			execute store result score #p a_y run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[1] 1000
			execute store result score #p a_z run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[2] 1000
			execute in minecraft:overworld run teleport 4b4f5245-0000-0000-0000-000000000001 -30000000.0 0.0 1664.0
		""".trimIndent()
	}

	function("read_position_and_motion") {
		a.setToPosition(self())
		a.setToMotion(self())
		toString() assertsIs """
			teleport 4b4f5245-0000-0000-0000-000000000001 @s
			execute store result score #p a_x run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[0] 1000
			execute store result score #p a_y run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[1] 1000
			execute store result score #p a_z run data get entity 4b4f5245-0000-0000-0000-000000000001 Pos[2] 1000
			execute in minecraft:overworld run teleport 4b4f5245-0000-0000-0000-000000000001 -30000000.0 0.0 1664.0
			data modify storage vector_tests:kore_math read set from entity @s Motion
			execute store result score #p a_x run data get storage vector_tests:kore_math read[0] 1000
			execute store result score #p a_y run data get storage vector_tests:kore_math read[1] 1000
			execute store result score #p a_z run data get storage vector_tests:kore_math read[2] 1000
		""".trimIndent()
	}

	function("look_direction") {
		a.setToLookDirection(self())
		lines.first() assertsIs "execute in minecraft:overworld positioned 0.0 0.0 0.0 rotated as @s run teleport 4b4f5245-0000-0000-0000-000000000001 ^ ^ ^1"
	}

	function("move") {
		a.teleport(self(), TeleportMode.RELATIVE)
		a.applyAsMotion(self())
		a.lookAlong(self())
		lines[3] assertsIs "execute as @s at @s run function vector_tests:generated_scopes/kore_math/teleport_relative with storage vector_tests:kore_math teleport"
		lines[4] assertsIs "execute store result entity @s Motion[0] double 0.001 run scoreboard players get #p a_x"
		lines[10] assertsIs "execute as @s at @s positioned 0.0 0.0 0.0 facing entity 4b4f5245-0000-0000-0000-000000000001 feet positioned as @s run teleport @s ~ ~ ~ ~ ~"
	}

	generated("teleport_relative") assertsIs $$"$tp @s ~$(x) ~$(y) ~$(z)"

	shouldThrow<IllegalArgumentException> {
		function("bad_cross") { a.cross(b, a) }
	}
}

class MathTests : FunSpec({
	test("scalar math") {
		mathScalarTests()
	}

	test("score vectors") {
		mathVectorTests()
	}
})
