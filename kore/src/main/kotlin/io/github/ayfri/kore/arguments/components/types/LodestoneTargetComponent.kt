package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class LodestoneTargetComponent(
	var pos: List<Int>,
	var dimension: DimensionArgument,
	var tracked: Boolean? = null,
) : Component()

fun ComponentsScope.lodestoneTarget(pos: List<Int>, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TRACKER] = LodestoneTargetComponent(pos, dimension, tracked)
}

fun ComponentsScope.lodestoneTarget(x: Int, y: Int, z: Int, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TRACKER] = LodestoneTargetComponent(listOf(x, y, z), dimension, tracked)
}

fun ComponentsScope.lodestoneTarget(pos: Vec3, dimension: DimensionArgument, tracked: Boolean? = null) = apply {
	this[ComponentTypes.LODESTONE_TRACKER] = LodestoneTargetComponent(pos.values.map { it.value.roundToInt() }, dimension, tracked)
}
