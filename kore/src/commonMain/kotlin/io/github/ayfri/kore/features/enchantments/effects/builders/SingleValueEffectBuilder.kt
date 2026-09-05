package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.value.Add
import io.github.ayfri.kore.features.enchantments.effects.value.ValueEffect
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The lone value effect of a component such as `crossbow_charge_time` or `trident_spin_attack_strength`.
 *
 * Unlike the other value components, these two hold a single effect instead of a list of conditional ones, so the
 * effect is serialized on its own and `requirements { }` has nowhere to go. Wrap several operations in an
 * [`all_of`][allOf] block to apply more than one, and put the conditions on that block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Value_effects
 *
 * @property effect The effect applied to the number the component computes.
 */
@Serializable(with = SingleValueEffectBuilder.Companion.SingleValueEffectBuilderSerializer::class)
data class SingleValueEffectBuilder(var effect: ValueEffect = Add(Constant(0f))) : EffectBuilder(), ValueEffectScope {
	override fun addEffect(effect: ValueEffect) {
		this.effect = effect
	}

	companion object {
		data object SingleValueEffectBuilderSerializer : InlineAutoSerializer<SingleValueEffectBuilder, ValueEffect>(
			serializer<ValueEffect>(),
			SingleValueEffectBuilder::effect,
			::SingleValueEffectBuilder,
			serialName = "SingleValueEffectBuilder",
		)
	}
}
