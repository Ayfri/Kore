package io.github.ayfri.kore.helpers

object HelpersConstants {
	var mathConst2 = "#2"
	var mathConst360 = "#360"
	var mathConstScale = "#scale"
	var mathInitFunction = "kore_math_init"
	var mathObjective = "kore_math"
	var mathSqrtIterations = 8
	var raycastInitFunction = "kore_raycast_init"
	var raycastObjective = "kore_raycast"
	var raycastTag = "kore_raycasting"

	fun raycastHitFunctionName(name: String) = "raycast_${name}_hit"
	fun raycastMaxFunctionName(name: String) = "raycast_${name}_max"
	fun raycastStartFunctionName(name: String) = "raycast_${name}_start"
	fun raycastStepFunctionName(name: String) = "raycast_${name}_step"
	fun vfxShapeFunctionName(name: String) = "vfx_$name"
}