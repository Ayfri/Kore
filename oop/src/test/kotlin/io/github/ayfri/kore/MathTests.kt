package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.maths.MATH_TABLE_SIZE
import io.github.ayfri.kore.maths.registerMath
import io.github.ayfri.kore.utils.testDataPack

fun mathTests() = testDataPack("math_tests") {
	val player = player("TestPlayer")
	val math = registerMath()

	function("test_cos") {
		math.apply {
			cos(player, "angle", "cos_result")
		}
		lines[0] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] cos_result = @e[limit=1,name=TestPlayer,type=minecraft:player] angle"
		lines[1] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] cos_result %= #360 kore_math"
		lines.size assertsIs 2 + MATH_TABLE_SIZE
	}

	function("test_sin") {
		math.apply {
			sin(player, "angle", "sin_result")
		}
		lines[0] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] sin_result = @e[limit=1,name=TestPlayer,type=minecraft:player] angle"
		lines[1] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] sin_result %= #360 kore_math"
		lines[2] assertsIs "execute if score @e[limit=1,name=TestPlayer,type=minecraft:player] sin_result matches 0 run scoreboard players set @e[limit=1,name=TestPlayer,type=minecraft:player] sin_result 0"
		lines.size assertsIs 2 + MATH_TABLE_SIZE
	}

	function("test_sqrt") {
		math.apply {
			sqrt(player, "input_val", "sqrt_result")
		}
		lines[0] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _sqrt_x = @e[limit=1,name=TestPlayer,type=minecraft:player] input_val"
		lines[1] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _sqrt_g = @e[limit=1,name=TestPlayer,type=minecraft:player] input_val"
		lines[2] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _sqrt_g /= #2 kore_math"
		lines.size assertsIs 3 + OopConstants.mathSqrtIterations * 5
	}

	function("test_distance_squared") {
		math.apply {
			distanceSquared(player, "x1", "y1", "z1", "x2", "y2", "z2", "dist_sq")
		}
		lines[0] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _dist_dx = @e[limit=1,name=TestPlayer,type=minecraft:player] x2"
		lines[1] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _dist_dx -= @e[limit=1,name=TestPlayer,type=minecraft:player] x1"
		lines.size assertsIs 12
	}

	function("test_parabola") {
		math.apply {
			parabola(player, "time", "#v0", "#gravity", "para_y")
		}
		lines[0] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _para_a = #v0 kore_math"
		lines[1] assertsIs "scoreboard players operation @e[limit=1,name=TestPlayer,type=minecraft:player] _para_a *= @e[limit=1,name=TestPlayer,type=minecraft:player] time"
		lines.size assertsIs 8
	}

	val initFn = generatedFunctions.first { it.name == OopConstants.mathInitFunction }
	initFn.lines[0] assertsIs "scoreboard objectives add kore_math dummy"
	initFn.lines[1] assertsIs "scoreboard players set #2 kore_math 2"
	initFn.lines[2] assertsIs "scoreboard players set #360 kore_math 360"
	initFn.lines[3] assertsIs "scoreboard players set #scale kore_math 1000"
	initFn.lines.size assertsIs 4
}.apply {
	generate()
}
