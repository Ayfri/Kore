package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.maths.registerMath
import io.github.ayfri.kore.utils.testDataPack

fun mathTests() = testDataPack("math_tests") {
	val player = player("TestPlayer")
	val math = registerMath()

	function("test_math") {
		math.apply {
			cos(player, "angle", "cos_result")
			sin(player, "angle", "sin_result")
			sqrt(player, "input_val", "sqrt_result")
			distanceSquared(
				player,
				"x1", "y1", "z1",
				"x2", "y2", "z2",
				"dist_sq",
			)
			parabola(player, "time", "#v0", "#gravity", "para_y")
		}
	}

	math.objective assertsIs OopConstants.Math.OBJECTIVE

	generatedFunctions.any { it.name.contains("kore_math_init") } assertsIs true
}.apply {
	val n = "math_tests"
	val d = "$n/data/$n"
	val g = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/test_math.mcfunction")
	assertFileGenerated("$d/function/$g/kore_math_init.mcfunction")
	assertGeneratorsGenerated()
	generate()
}
