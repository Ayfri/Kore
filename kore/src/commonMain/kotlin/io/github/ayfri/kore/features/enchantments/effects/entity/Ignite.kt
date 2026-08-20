package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import kotlinx.serialization.Serializable

/**
 * Sets the affected entity on fire for [duration] seconds, keeping the longest of the current and new burn times.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#ignite
 *
 * @property duration The burn time in seconds.
 */
@Serializable
data class Ignite(
	var duration: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope

/** Sets [Ignite.duration] to [value], the same burn time whatever the enchantment level is. */
fun Ignite.duration(value: Float) {
	duration = constantLevelBased(value)
}

/** Sets [Ignite.duration] to [value], the same burn time whatever the enchantment level is. */
fun Ignite.duration(value: Int) {
	duration = constantLevelBased(value)
}
