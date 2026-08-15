package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.helpers.vfx.drawCircle
import io.github.ayfri.kore.helpers.vfx.drawShape
import io.kotest.core.spec.style.FunSpec

fun vfxTests() = dataPack("vfx_tests") {
	drawCircle("fire_circle", Particles.FLAME, radius = 5.0, points = 16)

	drawCircle("fire_circle_world", Particles.FLAME, radius = 5.0, points = 4, positionType = PosNumber.Type.WORLD)

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

	drawShape("test_local") {
		shape = Shape.CIRCLE
		particle = Particles.FLAME
		radius = 2.0
		points = 4
		positionType = PosNumber.Type.LOCAL
	}

	val circle = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("fire_circle") }
	circle.lines.size assertsIs 16
	circle.lines.all { it.startsWith("particle minecraft:flame") } assertsIs true
	circle.lines.all { it.contains(" ~") } assertsIs true

	val circleWorld = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("fire_circle_world") }
	circleWorld.lines.first() assertsIs "particle minecraft:flame 5.0 0.0 0.0"

	val helix = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("helix_soul") }
	helix.lines.size assertsIs 40
	helix.lines.all { it.startsWith("particle minecraft:soul_fire_flame") } assertsIs true
	helix.lines.all { it.contains(" ~") } assertsIs true

	val line = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("test_line") }
	line.lines.size assertsIs 20
	line.lines.all { it.startsWith("particle minecraft:end_rod") } assertsIs true
	line.lines.all { it.contains(" ~") } assertsIs true

	val sphere = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("test_sphere") }
	sphere.lines.size assertsIs 30
	sphere.lines.all { it.startsWith("particle minecraft:heart") } assertsIs true
	sphere.lines.all { it.contains(" ~") } assertsIs true

	val local = generatedFunctions.first { it.name == HelpersConstants.vfxShapeFunctionName("test_local") }
	local.lines.first() assertsIs "particle minecraft:flame ^2 ^ ^"
}

class VfxTests : FunSpec({
	test("vfx") {
		vfxTests()
	}
})
