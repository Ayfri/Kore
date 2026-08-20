package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Gives the affected entity one of the [toApply] mob effects, picked at random, for a duration and an amplifier
 * rolled between their min and max bounds.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_mob_effect
 *
 * @property toApply The mob effects and mob effect tags one effect is picked from.
 * @property minDuration The lowest duration in seconds.
 * @property maxDuration The highest duration in seconds.
 * @property minAmplifier The lowest amplifier, `0` being level I.
 * @property maxAmplifier The highest amplifier, `0` being level I.
 */
@Serializable
data class ApplyMobEffect(
	var toApply: InlinableList<MobEffectOrTagArgument> = emptyList(),
	var minDuration: LevelBased = Constant(0f),
	var maxDuration: LevelBased = Constant(0f),
	var minAmplifier: LevelBased = Constant(0f),
	var maxAmplifier: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope

/** Sets [ApplyMobEffect.toApply], the mob effects and mob effect tags one effect is picked from. */
fun ApplyMobEffect.toApply(vararg effects: MobEffectOrTagArgument) {
	toApply = effects.toList()
}

/** Sets [ApplyMobEffect.toApply] to the mob effects and mob effect tags collected in [block]. */
fun ApplyMobEffect.toApply(block: MutableList<MobEffectOrTagArgument>.() -> Unit) {
	toApply = buildList(block)
}

/** Sets [ApplyMobEffect.minDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.minDuration(value: Float) {
	minDuration = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.minDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.minDuration(value: Int) {
	minDuration = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.maxDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.maxDuration(value: Float) {
	maxDuration = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.maxDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.maxDuration(value: Int) {
	maxDuration = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.minAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.minAmplifier(value: Float) {
	minAmplifier = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.minAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.minAmplifier(value: Int) {
	minAmplifier = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.maxAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.maxAmplifier(value: Float) {
	maxAmplifier = constantLevelBased(value)
}

/** Sets [ApplyMobEffect.maxAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.maxAmplifier(value: Int) {
	maxAmplifier = constantLevelBased(value)
}

/** Sets both [ApplyMobEffect.minDuration] and [ApplyMobEffect.maxDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.duration(value: Float) {
	minDuration = constantLevelBased(value)
	maxDuration = constantLevelBased(value)
}

/** Sets both [ApplyMobEffect.minDuration] and [ApplyMobEffect.maxDuration] to a constant [value] in seconds. */
fun ApplyMobEffect.duration(value: Int) {
	minDuration = constantLevelBased(value)
	maxDuration = constantLevelBased(value)
}

/** Sets both [ApplyMobEffect.minAmplifier] and [ApplyMobEffect.maxAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.amplifier(value: Float) {
	minAmplifier = constantLevelBased(value)
	maxAmplifier = constantLevelBased(value)
}

/** Sets both [ApplyMobEffect.minAmplifier] and [ApplyMobEffect.maxAmplifier] to a constant [value], `0` being level I. */
fun ApplyMobEffect.amplifier(value: Int) {
	minAmplifier = constantLevelBased(value)
	maxAmplifier = constantLevelBased(value)
}
