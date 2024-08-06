package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.features.enchantment.effects.value.Add
import io.github.ayfri.kore.features.enchantment.effects.value.Multiply
import io.github.ayfri.kore.features.enchantment.effects.value.RemoveBinomial
import io.github.ayfri.kore.features.enchantment.effects.value.Set
import io.github.ayfri.kore.features.enchantment.values.LevelBased
import io.github.ayfri.kore.features.enchantment.values.constantLevelBased
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = ValueEffectBuilder.Companion.ValueEffectBuilderSerializer::class)
data class ValueEffectBuilder(
	var effects: List<ConditionalEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object ValueEffectBuilderSerializer : InlineSerializer<ValueEffectBuilder, List<ConditionalEffect>>(
			kSerializer = ListSerializer(ConditionalEffect.serializer()),
			property = ValueEffectBuilder::effects
		)
	}
}

fun ValueEffectBuilder.add(value: LevelBased, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Add(value)).apply(block) }

fun ValueEffectBuilder.add(value: Int, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Add(constantLevelBased(value))).apply(block) }


fun ValueEffectBuilder.allOf(block: ValueEffectAllOfTopBuilder.() -> Unit) = apply {
	val builder = ValueEffectAllOfTopBuilder().apply(block)
	effects += ConditionalEffect(builder.effects, builder.requirements)
}


fun ValueEffectBuilder.multiply(value: LevelBased, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Multiply(value)).apply(block) }

fun ValueEffectBuilder.multiply(value: Int, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Multiply(constantLevelBased(value))).apply(block) }


fun ValueEffectBuilder.removeBinomial(chance: LevelBased, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(RemoveBinomial(chance)).apply(block) }

fun ValueEffectBuilder.removeBinomial(chance: Int, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(RemoveBinomial(constantLevelBased(chance))).apply(block) }


fun ValueEffectBuilder.set(value: LevelBased, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Set(value)).apply(block) }

fun ValueEffectBuilder.set(value: Int, block: ConditionalEffect.() -> Unit = {}) =
	apply { effects += ConditionalEffect(Set(constantLevelBased(value))).apply(block) }
