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
			scoreboard players operation #x kore_math = #p angle
			execute store result score #p sin run function math_tests:generated_scopes/kore_math/sin_1
			scoreboard players operation #x kore_math = #p angle
			execute store result score #p cos run function math_tests:generated_scopes/kore_math/cos_100
		""".trimIndent()
	}

	generated("sin_1") assertsIs """
		scoreboard players add #x kore_math 90
		scoreboard players operation #x kore_math %= #360 kore_math
		scoreboard players remove #x kore_math 180
		execute if score #x kore_math matches ..-1 run scoreboard players operation #x kore_math *= #-1 kore_math
		scoreboard players remove #x kore_math 90
		execute store result score #q kore_math run scoreboard players operation #x kore_math *= #-112 kore_math
		scoreboard players operation #q kore_math *= #x kore_math
		scoreboard players operation #q kore_math /= #10080 kore_math
		scoreboard players operation #h kore_math = #q kore_math
		scoreboard players operation #h kore_math *= #7186 kore_math
		scoreboard players operation #h kore_math /= #10080 kore_math
		scoreboard players remove #h kore_math 64211
		scoreboard players operation #h kore_math *= #q kore_math
		scoreboard players operation #h kore_math /= #10080 kore_math
		scoreboard players add #h kore_math 157032
		scoreboard players operation #h kore_math *= #x kore_math
		scoreboard players add #h kore_math 504000
		return run scoreboard players operation #h kore_math /= #1008000 kore_math
	""".trimIndent()
	generated("cos_100").lines().take(2) assertsIs listOf(
		"scoreboard players add #x kore_math 18000",
		"scoreboard players operation #x kore_math %= #36000 kore_math",
	)

	function("roots") {
		math.sqrt(score("in"), score("root"))
		math.atan2(score("y"), score("x"), score("angle"))
	}

	generated("sqrt") assertsIs """
		execute if score #x kore_math matches ..0 run return 0
		execute store result score #s kore_math store result score #t kore_math store result score #u kore_math store result score #v kore_math run scoreboard players operation #r kore_math = #x kore_math
		execute if score #x kore_math matches ..1515359 run scoreboard players operation #r kore_math /= #559 kore_math
		execute if score #x kore_math matches ..1515359 run scoreboard players add #r kore_math 15
		execute if score #x kore_math matches 1515360.. run scoreboard players operation #r kore_math /= #32768 kore_math
		execute if score #x kore_math matches 1515360.. run scoreboard players add #r kore_math 2456
		scoreboard players operation #s kore_math /= #r kore_math
		scoreboard players operation #r kore_math += #s kore_math
		scoreboard players operation #r kore_math /= #2 kore_math
		scoreboard players operation #t kore_math /= #r kore_math
		scoreboard players operation #r kore_math += #t kore_math
		scoreboard players operation #r kore_math /= #2 kore_math
		scoreboard players operation #u kore_math /= #r kore_math
		scoreboard players operation #r kore_math += #u kore_math
		scoreboard players operation #r kore_math /= #2 kore_math
		scoreboard players operation #v kore_math /= #r kore_math
		execute if score #r kore_math > #v kore_math run scoreboard players remove #r kore_math 1
		return run scoreboard players get #r kore_math
	""".trimIndent()

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
		math.distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist")
		math.parabola(holder, "t", "#v0", "#g", "height")
		lines.take(5) assertsIs listOf(
			"scoreboard players set @e[limit=1,name=P,type=minecraft:player] dist 0",
			"scoreboard players operation #d kore_math = @e[limit=1,name=P,type=minecraft:player] x2",
			"scoreboard players operation #d kore_math -= @e[limit=1,name=P,type=minecraft:player] x1",
			"scoreboard players operation #d kore_math *= #d kore_math",
			"scoreboard players operation @e[limit=1,name=P,type=minecraft:player] dist += #d kore_math",
		)
		lines.takeLast(7).joinToString("\n") assertsIs """
			scoreboard players operation #p kore_math = #g kore_math
			scoreboard players operation #p kore_math *= #p t
			scoreboard players operation #p kore_math *= #p t
			scoreboard players operation #p kore_math /= #2 kore_math
			scoreboard players operation #p height = #v0 kore_math
			scoreboard players operation #p height *= #p t
			scoreboard players operation #p height -= #p kore_math
		""".trimIndent()
	}

	function("delegates") {
		val angle = holder.scoreboard("launch_angle")
		val sine = holder.scoreboard("sin_angle")
		with(math) { angle sinTo sine }
		lines.last() assertsIs "execute store result score #p sin_angle run function math_tests:generated_scopes/kore_math/sin_1"
	}

	generated(HelpersConstants.mathInitFunction).lines().run {
		first() assertsIs "scoreboard objectives add kore_math dummy"
		count { it == "scoreboard players set #360 kore_math 360" } assertsIs 1
		contains("forceload add -30000000 1664") assertsIs true
		contains("execute unless entity 4b4f5245-0000-0000-0000-000000000001 run summon minecraft:marker -30000000.0 0.0 1664.0 {Tags:[\"kore.math\",\"smithed.entity\",\"smithed.strict\"],UUID:[I;1263489605,0,0,1]}") assertsIs true
	}

	registerMath() assertsIs math
	shouldThrow<IllegalArgumentException> {
		function("too_fine") { math.sin(score("angle"), score("sin"), angleScale = 1000) }
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
