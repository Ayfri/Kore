package io.github.ayfri.kore

import io.github.ayfri.kore.assertions.assertFileGenerated
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.raycast.RaycastConfig
import io.github.ayfri.kore.raycast.raycast
import io.github.ayfri.kore.utils.testDataPack

fun raycastTests() = testDataPack("raycast_tests") {
	val basicRay = raycast {
		name = "basic"
		maxDistance = 50
		onHitBlock = { say("Hit a block!") }
	}

	val fullRay = raycast(
		RaycastConfig(
			name = "full",
			step = 0.25,
			onStep = { say("Step...") },
			onHitBlock = { say("Hit!") },
			onMaxDistance = { say("Too far!") },
		)
	)

	function("use_raycast") {
		with(basicRay) { cast() }
		with(fullRay) { cast() }
	}

	generatedFunctions.any { it.name == OopConstants.Raycast.startFunctionName("basic") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Raycast.stepFunctionName("basic") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Raycast.hitFunctionName("basic") } assertsIs true

	generatedFunctions.any { it.name == OopConstants.Raycast.startFunctionName("full") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Raycast.stepFunctionName("full") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Raycast.hitFunctionName("full") } assertsIs true
	generatedFunctions.any { it.name == OopConstants.Raycast.maxFunctionName("full") } assertsIs true

	generatedFunctions.any { it.name == OopConstants.Raycast.INIT_FUNCTION } assertsIs true

	basicRay.config.name assertsIs "basic"
	basicRay.config.maxDistance assertsIs 50
	fullRay.config.step assertsIs 0.25
}.apply {
	val n = "raycast_tests"
	val d = "$n/data/$n"
	val g = DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER

	assertFileGenerated("$d/function/use_raycast.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_basic_start.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_basic_step.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_basic_hit.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_full_start.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_full_step.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_full_hit.mcfunction")
	assertFileGenerated("$d/function/$g/raycast_full_max.mcfunction")
	assertGeneratorsGenerated()
	generate()
}
