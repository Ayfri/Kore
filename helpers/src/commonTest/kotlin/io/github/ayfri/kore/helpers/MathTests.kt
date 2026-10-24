package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.maths.*
import io.github.ayfri.kore.helpers.state.scoreboard
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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

	generated("atan2_1") shouldBe """
		execute store result storage math_tests:kore_math atan2.y double 0.001 run scoreboard players get #y kore_math
		execute store result storage math_tests:kore_math atan2.x double 0.001 run scoreboard players get #x kore_math
		execute as 4b4f5245-0000-0000-0000-000000000001 at @s run function math_tests:generated_scopes/kore_math/atan2_facing with storage math_tests:kore_math atan2
		execute store result score #r kore_math run data get entity 4b4f5245-0000-0000-0000-000000000001 Rotation[0] -1
		execute if score #r kore_math matches ..-180 run scoreboard players add #r kore_math 360
		return run scoreboard players get #r kore_math
	""".trimIndent()
	generated("atan2_facing") shouldBe $$"$rotate @s facing ~$(y) ~ ~$(x)"

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
		""".trimIndent()
	}

	function("products") {
		fun read(vector: String, axis: String) = """{type:"minecraft:score",score:${vector}_$axis,target:{type:"minecraft:fixed",name:"#p"}}"""
		fun product(first: String, second: String, axis: String) = """{type:"minecraft:mul",inputs:[${read(first, axis)},${read(second, axis)}]}"""
		fun cross(first: String, second: String, left: String, right: String) =
			"""{type:"minecraft:floor_div",left:{type:"minecraft:sub",left:{type:"minecraft:mul",inputs:[${read(first, left)},${read(second, right)}]},right:{type:"minecraft:mul",inputs:[${read(first, right)},${read(second, left)}]}},right:1000}"""

		a.dot(b, score("dot"))
		lines.last() assertsIs """execute store result score #p dot run compute default integer {type:"minecraft:floor_div",left:{type:"minecraft:add",inputs:[${product("a", "b", "x")},${product("a", "b", "y")},${product("a", "b", "z")}]},right:1000}"""

		a.lengthSquared(score("length_squared"))
		lines.last() assertsIs """execute store result score #p length_squared run compute default integer {type:"minecraft:floor_div",left:{type:"minecraft:add",inputs:[${product("a", "a", "x")},${product("a", "a", "y")},${product("a", "a", "z")}]},right:1000}"""

		a.cross(b, math.vector("c", holder))
		lines.takeLast(3) assertsIs listOf(
			"""execute store result score #p c_x run compute default integer ${cross("a", "b", "y", "z")}""",
			"""execute store result score #p c_y run compute default integer ${cross("a", "b", "z", "x")}""",
			"""execute store result score #p c_z run compute default integer ${cross("a", "b", "x", "y")}""",
		)
	}

	function("products_on_two_entities") {
		val first = math.vector("first", player("P"))
		val second = math.vector("second", player("Q"))
		first.dot(second, score("dot"))
		lines.take(3) assertsIs listOf(
			"scoreboard players operation #s3 kore_math = @e[limit=1,name=Q,type=minecraft:player] second_x",
			"scoreboard players operation #s4 kore_math = @e[limit=1,name=Q,type=minecraft:player] second_y",
			"scoreboard players operation #s5 kore_math = @e[limit=1,name=Q,type=minecraft:player] second_z",
		)
		lines.last().startsWith("execute store result score #p dot run compute entity @e[limit=1,name=P,type=minecraft:player] integer {type:\"minecraft:floor_div\"") assertsIs true
	}

	function("length") {
		fun read(vector: String, axis: String) = """{type:"minecraft:score",score:${vector}_$axis,target:{type:"minecraft:fixed",name:"#p"}}"""
		val axes = listOf("x", "y", "z")
		val length = """{type:"minecraft:length",inputs:[${axes.joinToString(",") { """{type:"minecraft:from_int",input:${read("a", it)}}""" }}]}"""

		a.length(score("length"))
		lines.last() assertsIs "execute store result score #p length run compute default float $length"

		a.distanceTo(b, score("distance"))
		lines.last() assertsIs """execute store result score #p distance run compute default float {type:"minecraft:length",inputs:[${axes.joinToString(",") { """{type:"minecraft:from_int",input:{type:"minecraft:sub",left:${read("a", it)},right:${read("b", it)}}}""" }}]}"""
	}

	function("normalize") {
		fun read(axis: String) = """{type:"minecraft:score",score:a_$axis,target:{type:"minecraft:fixed",name:"#p"}}"""
		val length = """{type:"minecraft:length",inputs:[${"xyz".map { """{type:"minecraft:from_int",input:${read("$it")}}""" }.joinToString(",")}]}"""

		a.normalize()
		lines assertsIs listOf(
			"data modify storage vector_tests:kore_math len set compute default float $length",
			*"xyz".map { axis ->
				"""execute store result score #p a_$axis run compute default float {type:"minecraft:truncate",input:{type:"minecraft:div",left:{type:"minecraft:mul",inputs:[{type:"minecraft:from_int",input:${read("$axis")}},1000]},right:{type:"minecraft:storage",path:len,storage:"vector_tests:kore_math"}}}"""
			}.toTypedArray(),
		)

		math.vector("player_vector", player("P")).normalize()
		lines[4] assertsIs """data modify storage vector_tests:kore_math len set compute entity @e[limit=1,name=P,type=minecraft:player] float {type:"minecraft:length",inputs:[${"xyz".map { """{type:"minecraft:from_int",input:{type:"minecraft:score",score:player_vector_$it,target:{type:"minecraft:context",target:target_entity}}}""" }.joinToString(",")}]}"""
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
		val yaw = """{type:"minecraft:mul",inputs:[{type:"minecraft:storage",path:"rotation[0]",storage:"vector_tests:kore_math"},0.017453292d]}"""
		val pitch = """{type:"minecraft:mul",inputs:[{type:"minecraft:storage",path:"rotation[1]",storage:"vector_tests:kore_math"},0.017453292d]}"""
		val sinYaw = """{type:"minecraft:negate",input:{type:"minecraft:sin",input:$yaw}}"""
		val cosPitch = """{type:"minecraft:cos",input:$pitch}"""
		fun component(axis: String, direction: String) =
			"""execute store result score #p a_$axis run compute default float {type:"minecraft:truncate",input:{type:"minecraft:mul",inputs:[$direction,1000]}}"""
		lines shouldBe listOf(
			"execute in minecraft:overworld rotated as @s run teleport 4b4f5245-0000-0000-0000-000000000001 -30000000.0 0.0 1664.0 ~ ~",
			"data modify storage vector_tests:kore_math rotation set from entity 4b4f5245-0000-0000-0000-000000000001 Rotation",
			component("x", """{type:"minecraft:mul",inputs:[$sinYaw,$cosPitch]}"""),
			component("y", """{type:"minecraft:negate",input:{type:"minecraft:sin",input:$pitch}}"""),
			component("z", """{type:"minecraft:mul",inputs:[{type:"minecraft:cos",input:$yaw},$cosPitch]}"""),
		)
	}

	function("move") {
		a.teleport(self(), TeleportMode.RELATIVE)
		a.applyAsMotion(self())
		a.lookAlong(self())
		lines shouldBe listOf(
			"execute store result storage vector_tests:kore_math teleport.x double 0.001 run scoreboard players get #p a_x",
			"execute store result storage vector_tests:kore_math teleport.y double 0.001 run scoreboard players get #p a_y",
			"execute store result storage vector_tests:kore_math teleport.z double 0.001 run scoreboard players get #p a_z",
			"execute as @s at @s run function vector_tests:generated_scopes/kore_math/teleport_relative with storage vector_tests:kore_math teleport",
			"execute store result entity @s Motion[0] double 0.001 run scoreboard players get #p a_x",
			"execute store result entity @s Motion[1] double 0.001 run scoreboard players get #p a_y",
			"execute store result entity @s Motion[2] double 0.001 run scoreboard players get #p a_z",
			"execute store result storage vector_tests:kore_math look.x double 0.001 run scoreboard players get #p a_x",
			"execute store result storage vector_tests:kore_math look.y double 0.001 run scoreboard players get #p a_y",
			"execute store result storage vector_tests:kore_math look.z double 0.001 run scoreboard players get #p a_z",
			"execute unless data storage vector_tests:kore_math look{x:0.0d,y:0.0d,z:0.0d} as @s at @s run function vector_tests:generated_scopes/kore_math/look_along with storage vector_tests:kore_math look",
		)
	}

	generated("teleport_relative") assertsIs $$"$tp @s ~$(x) ~$(y) ~$(z)"
	generated("look_along") shouldBe $$"$rotate @s facing ~$(x) ~$(y) ~$(z)"

	shouldThrow<IllegalArgumentException> {
		function("bad_cross") { a.cross(b, a) }
	}

	function("many_entities") {
		val crowd = math.vector("crowd", entity(limitToOne = false))
		shouldThrow<IllegalStateException> { crowd.normalize() }
		shouldThrow<IllegalStateException> { crowd.length(score("length")) }
		shouldThrow<IllegalStateException> { crowd.dot(a, score("dot")) }
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
