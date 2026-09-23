package io.github.ayfri.kore.helpers

object HelpersConstants {
	var mathDisplayUuid = "4b4f5245-0000-0000-0000-000000000002"
	var mathEntitiesX = -30_000_000
	var mathEntitiesZ = 1664
	var mathFunctionsDirectory = "kore_math"
	var mathInitFunction = "kore_math_init"
	var mathMarkerUuid = "4b4f5245-0000-0000-0000-000000000001"
	var mathObjective = "kore_math"
	var mathStorage = "kore_math"
	var raycastInitFunction = "kore_raycast_init"
	var raycastObjective = "kore_raycast"
	var raycastTag = "kore_raycasting"

	fun raycastHitFunctionName(name: String) = "raycast_${name}_hit"
	fun raycastMaxFunctionName(name: String) = "raycast_${name}_max"
	fun raycastStartFunctionName(name: String) = "raycast_${name}_start"
	fun raycastStepFunctionName(name: String) = "raycast_${name}_step"
	fun vfxShapeFunctionName(name: String) = "vfx_$name"
}