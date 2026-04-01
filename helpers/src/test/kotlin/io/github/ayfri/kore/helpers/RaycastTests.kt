package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.assertions.assertsIs
import io.github.ayfri.kore.helpers.raycast.RaycastConfig
import io.github.ayfri.kore.helpers.raycast.raycast
import io.github.ayfri.kore.helpers.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

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
		lines[0] assertsIs "function raycast_tests:raycast_tests:generated_scopes/raycast_basic_start"
		lines[1] assertsIs "function raycast_tests:raycast_tests:generated_scopes/raycast_full_start"
		lines.size assertsIs 2
	}

	val basicStart = generatedFunctions.first { it.name == HelpersConstants.raycastStartFunctionName("basic") }
	basicStart.lines[0] assertsIs "tag @s add kore_raycasting"
	basicStart.lines[1] assertsIs "scoreboard players set @s kore_raycast 0"
	basicStart.lines[2] assertsIs "execute anchored eyes positioned ^ ^ ^0 run function raycast_tests:generated_scopes/raycast_basic_step"
	basicStart.lines.size assertsIs 3

	val basicStep = generatedFunctions.first { it.name == HelpersConstants.raycastStepFunctionName("basic") }
	basicStep.lines[0] assertsIs "scoreboard players add @s kore_raycast 1"
	basicStep.lines[1] assertsIs "execute unless block ~ ~ ~ minecraft:air run function raycast_tests:generated_scopes/raycast_basic_hit"
	basicStep.lines[2] assertsIs "execute if score @s kore_raycast matches 50 run tag @s remove kore_raycasting"
	basicStep.lines[3].startsWith("execute if entity @s[tag=kore_raycasting] positioned ^ ^ ^0 run function raycast_tests:") assertsIs true
	basicStep.lines.size assertsIs 4

	val basicHit = generatedFunctions.first { it.name == HelpersConstants.raycastHitFunctionName("basic") }
	basicHit.lines[0] assertsIs "say Hit a block!"
	basicHit.lines[1] assertsIs "tag @s remove kore_raycasting"
	basicHit.lines.size assertsIs 2

	val fullStart = generatedFunctions.first { it.name == HelpersConstants.raycastStartFunctionName("full") }
	fullStart.lines[0] assertsIs "tag @s add kore_raycasting"
	fullStart.lines[1] assertsIs "scoreboard players set @s kore_raycast 0"
	fullStart.lines[2] assertsIs "execute anchored eyes positioned ^ ^ ^0 run function raycast_tests:generated_scopes/raycast_full_step"
	fullStart.lines.size assertsIs 3

	val fullStep = generatedFunctions.first { it.name == HelpersConstants.raycastStepFunctionName("full") }
	fullStep.lines[0] assertsIs "say Step..."
	fullStep.lines[1] assertsIs "scoreboard players add @s kore_raycast 1"
	fullStep.lines[2] assertsIs "execute unless block ~ ~ ~ minecraft:air run function raycast_tests:generated_scopes/raycast_full_hit"
	fullStep.lines[3] assertsIs "execute if score @s kore_raycast matches 100 run function raycast_tests:generated_scopes/raycast_full_max"
	fullStep.lines[4].startsWith("execute if entity @s[tag=kore_raycasting] positioned ^ ^ ^0 run function raycast_tests:") assertsIs true
	fullStep.lines.size assertsIs 5

	val fullHit = generatedFunctions.first { it.name == HelpersConstants.raycastHitFunctionName("full") }
	fullHit.lines[0] assertsIs "say Hit!"
	fullHit.lines[1] assertsIs "tag @s remove kore_raycasting"
	fullHit.lines.size assertsIs 2

	val fullMax = generatedFunctions.first { it.name == HelpersConstants.raycastMaxFunctionName("full") }
	fullMax.lines[0] assertsIs "say Too far!"
	fullMax.lines[1] assertsIs "tag @s remove kore_raycasting"
	fullMax.lines.size assertsIs 2

	generatedFunctions.any { it.name == HelpersConstants.raycastInitFunction } assertsIs true
}.apply {
	generate()
}

class RaycastTests : FunSpec({
	test("raycast") {
		raycastTests()
	}
})
