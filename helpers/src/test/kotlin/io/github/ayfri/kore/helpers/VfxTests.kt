package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.helpers.assertions.assertsIs
import io.github.ayfri.kore.helpers.utils.testDataPack
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.helpers.vfx.drawCircle
import io.github.ayfri.kore.helpers.vfx.drawShape
import io.kotest.core.spec.style.FunSpec

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

	val circle = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("fire_circle") }
	circle.lines.size assertsIs 16
	circle.lines.all { it.startsWith("particle minecraft:flame") } assertsIs true

	val helix = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("helix_soul") }
	helix.lines.size assertsIs 40
	helix.lines.all { it.startsWith("particle minecraft:soul_fire_flame") } assertsIs true

	val line = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("test_line") }
	line.lines.size assertsIs 20
	line.lines.all { it.startsWith("particle minecraft:end_rod") } assertsIs true

	val sphere = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("test_sphere") }
	sphere.lines.size assertsIs 30
	sphere.lines.all { it.startsWith("particle minecraft:heart") } assertsIs true
}.apply {
	generate()
}

class VfxTests : FunSpec({
	test("vfx") {
		vfxTests()
	}
})
