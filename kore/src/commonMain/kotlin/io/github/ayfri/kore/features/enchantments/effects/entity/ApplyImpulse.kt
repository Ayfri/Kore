package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.maths.Vec3fAsArray
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Pushes the affected entity along [direction] with a strength of [magnitude], after scaling its current motion by
 * [coordinateScale] on each axis.
 *
 * A [coordinateScale] of `[1, 1, 1]` keeps the existing motion, `[0, 0, 0]` cancels it before pushing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_mob_effect
 *
 * @property coordinateScale The per-axis factor applied to the current motion of the entity.
 * @property direction The `[X, Y, Z]` direction the entity is pushed towards, normalized by the game.
 * @property magnitude The strength of the push, in blocks per tick.
 */
@Serializable
data class ApplyImpulse(
	var coordinateScale: Vec3fAsArray,
	var direction: Vec3fAsArray,
	var magnitude: LevelBased,
) : EntityEffect(), LevelBasedScope

/** Sets [ApplyImpulse.coordinateScale], the per-axis factor applied to the current motion of the entity. */
fun ApplyImpulse.coordinateScale(x: Float, y: Float, z: Float) {
	coordinateScale = Vec3fAsArray(x, y, z)
}

/** Sets [ApplyImpulse.direction], the direction the entity is pushed towards. */
fun ApplyImpulse.direction(x: Float, y: Float, z: Float) {
	direction = Vec3fAsArray(x, y, z)
}

/** Sets [ApplyImpulse.magnitude] to [value], the same push strength whatever the enchantment level is. */
fun ApplyImpulse.magnitude(value: Float) {
	magnitude = constantLevelBased(value)
}

/** Sets [ApplyImpulse.magnitude] to [value], the same push strength whatever the enchantment level is. */
fun ApplyImpulse.magnitude(value: Int) {
	magnitude = constantLevelBased(value)
}
