package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.arguments.types.EffectOrTagArgument
import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ApplyMobEffect(
	var toApply: InlinableList<EffectOrTagArgument> = emptyList(),
	var minDuration: LevelBased = constantLevelBased(0),
	var maxDuration: LevelBased = constantLevelBased(0),
	var minAmplifier: LevelBased = constantLevelBased(0),
	var maxAmplifier: LevelBased = constantLevelBased(0),
) : EntityEffect()

fun ApplyMobEffect.toApply(vararg effects: EffectOrTagArgument) {
	toApply = effects.toList()
}

fun ApplyMobEffect.toApply(block: MutableList<EffectOrTagArgument>.() -> Unit) {
	toApply = buildList(block)
}

fun ApplyMobEffect.minDuration(value: Int) {
	minDuration = constantLevelBased(value)
}

fun ApplyMobEffect.maxDuration(value: Int) {
	maxDuration = constantLevelBased(value)
}

fun ApplyMobEffect.minAmplifier(value: Int) {
	minAmplifier = constantLevelBased(value)
}

fun ApplyMobEffect.maxAmplifier(value: Int) {
	maxAmplifier = constantLevelBased(value)
}
