package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.maths.Vec3fAsArray
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

@Serializable
data class ApplyImpulse(
	var coordinateScale: Vec3fAsArray,
	var direction: Vec3fAsArray,
	var magnitude: LevelBased,
) : EntityEffect()

fun ApplyImpulse.coordinateScale(x: Float, y: Float, z: Float) {
	coordinateScale = Vec3fAsArray(x, y, z)
}

fun ApplyImpulse.direction(x: Float, y: Float, z: Float) {
	direction = Vec3fAsArray(x, y, z)
}

fun ApplyImpulse.magnitude(value: Int) {
	magnitude = constantLevelBased(value)
}
