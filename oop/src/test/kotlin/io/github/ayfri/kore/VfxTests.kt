package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.utils.testDataPack
import io.github.ayfri.kore.vfx.Shape
import io.github.ayfri.kore.vfx.drawCircle
import io.github.ayfri.kore.vfx.drawShape

fun vfxTests() = testDataPack("vfx_tests") {
	drawCircle("fire_circle", Particles.FLAME, radius = 5.0, points = 16)

	drawShape("helix_soul") {
		shape = Shape.HELIX
		particle = Particles.SOUL_FIRE_FLAME
		radius = 2.0
		points = 40
		height = 5.0
		turns = 4
	}

	drawShape("test_line") {
		shape = Shape.LINE
		particle = Particles.END_ROD
		length = 10.0
		points = 20
	}

	drawShape("test_sphere") {
		shape = Shape.SPHERE
		particle = Particles.HEART
		radius = 3.0
		points = 30
	}

	generatedFunctions.any { it.name.contains("vfx_fire_circle") } assertsIs true
	generatedFunctions.any { it.name.contains("vfx_helix_soul") } assertsIs true
	generatedFunctions.any { it.name.contains("vfx_test_line") } assertsIs true
	generatedFunctions.any { it.name.contains("vfx_test_sphere") } assertsIs true
}.apply {
	val n = "vfx_tests"
	val d = "$n/data/$n"
	val g = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/$g/vfx_fire_circle.mcfunction")
	assertFileGenerated("$d/function/$g/vfx_helix_soul.mcfunction")
	assertFileGenerated("$d/function/$g/vfx_test_line.mcfunction")
	assertFileGenerated("$d/function/$g/vfx_test_sphere.mcfunction")
	assertGeneratorsGenerated()
	generate()
}
