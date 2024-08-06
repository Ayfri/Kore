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

@Serializable(with = EquipmentDropsBuilder.Companion.EquipmentDropsBuilderSerializer::class)
data class EquipmentDropsBuilder(
	var effects: List<EquipmentDropsConditionalEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object EquipmentDropsBuilderSerializer : InlineSerializer<EquipmentDropsBuilder, List<EquipmentDropsConditionalEffect>>(
			kSerializer = ListSerializer(EquipmentDropsConditionalEffect.serializer()),
			property = EquipmentDropsBuilder::effects
		)
	}
}

fun EquipmentDropsBuilder.add(
	specifier: EquipmentDropsSpecifier,
	value: LevelBased,
	block: EquipmentDropsConditionalEffect.() -> Unit = {},
) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Add(value)).apply(block) }

fun EquipmentDropsBuilder.add(specifier: EquipmentDropsSpecifier, value: Int, block: EquipmentDropsConditionalEffect.() -> Unit = {}) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Add(constantLevelBased(value))).apply(block) }


fun EquipmentDropsBuilder.allOf(specifier: EquipmentDropsSpecifier, block: ValueEffectAllOfTopBuilder.() -> Unit) = apply {
	val builder = ValueEffectAllOfTopBuilder().apply(block)
	effects += EquipmentDropsConditionalEffect(specifier, builder.effects, builder.requirements)
}


fun EquipmentDropsBuilder.multiply(
	specifier: EquipmentDropsSpecifier,
	value: LevelBased, block: EquipmentDropsConditionalEffect.() -> Unit = {},
) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Multiply(value)).apply(block) }

fun EquipmentDropsBuilder.multiply(specifier: EquipmentDropsSpecifier, value: Int, block: EquipmentDropsConditionalEffect.() -> Unit = {}) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Multiply(constantLevelBased(value))).apply(block) }


fun EquipmentDropsBuilder.removeBinomial(
	specifier: EquipmentDropsSpecifier,
	chance: LevelBased, block: EquipmentDropsConditionalEffect.() -> Unit = {},
) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, RemoveBinomial(chance)).apply(block) }

fun EquipmentDropsBuilder.removeBinomial(
	specifier: EquipmentDropsSpecifier,
	chance: Int, block: EquipmentDropsConditionalEffect.() -> Unit = {},
) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, RemoveBinomial(constantLevelBased(chance))).apply(block) }


fun EquipmentDropsBuilder.set(
	specifier: EquipmentDropsSpecifier,
	value: LevelBased,
	block: EquipmentDropsConditionalEffect.() -> Unit = {},
) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Set(value)).apply(block) }

fun EquipmentDropsBuilder.set(specifier: EquipmentDropsSpecifier, value: Int, block: EquipmentDropsConditionalEffect.() -> Unit = {}) =
	apply { effects += EquipmentDropsConditionalEffect(specifier, Set(constantLevelBased(value))).apply(block) }
