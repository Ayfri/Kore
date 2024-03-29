package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlin.math.roundToInt
import kotlinx.serialization.Serializable

@Serializable
data class LodestoneTargetComponent(
	var pos: List<Int>,
	var dimension: DimensionArgument,
	var tracked: Boolean? = null,
) : Component()

fun Components.lodestoneTarget(pos: List<Int>, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TARGET] = LodestoneTargetComponent(pos, dimension, tracked)
}

fun Components.lodestoneTarget(x: Int, y: Int, z: Int, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TARGET] = LodestoneTargetComponent(listOf(x, y, z), dimension, tracked)
}

fun Components.lodestoneTarget(pos: Vec3, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TARGET] = LodestoneTargetComponent(pos.values.map { it.value.roundToInt() }, dimension, tracked)
}
