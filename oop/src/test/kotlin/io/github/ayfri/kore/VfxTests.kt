package io.github.ayfri.kore

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

    val circle = generatedFunctions.first { it.name == OopConstants.vfxShapeFunctionName("fire_circle") }
    circle.lines.size assertsIs 16
    circle.lines.all { it.startsWith("particle minecraft:flame") } assertsIs true

    val helix = generatedFunctions.first { it.name == OopConstants.vfxShapeFunctionName("helix_soul") }
    helix.lines.size assertsIs 40
    helix.lines.all { it.startsWith("particle minecraft:soul_fire_flame") } assertsIs true

    val line = generatedFunctions.first { it.name == OopConstants.vfxShapeFunctionName("test_line") }
    line.lines.size assertsIs 20
    line.lines.all { it.startsWith("particle minecraft:end_rod") } assertsIs true

    val sphere = generatedFunctions.first { it.name == OopConstants.vfxShapeFunctionName("test_sphere") }
    sphere.lines.size assertsIs 30
    sphere.lines.all { it.startsWith("particle minecraft:heart") } assertsIs true
}.apply {
	generate()
}
