package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.predicates.providers.*
import io.github.ayfri.kore.serializers.EnumOrdinalSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AttributeModifier(
	val attribute: AttributeArgument,
	@Serializable(NumberProviderSerializer::class) val amount: NumberProvider = constant(0f),
	@Serializable(AttributeModifierOperationSerializer::class) val operation: AttributeModifierOperation = AttributeModifierOperation.ADD_VALUE,
	val slot: List<EquipmentSlot>? = null,
	val uuid: UUIDArgument? = null,
) {
	companion object {
		object AttributeModifierOperationSerializer : EnumOrdinalSerializer<AttributeModifierOperation>(AttributeModifierOperation.entries)
		private object NumberProviderSerializer : KSerializer<NumberProvider> {
			override val descriptor = buildClassSerialDescriptor("NumberProvider")

			override fun deserialize(decoder: Decoder) = error("NumberProvider is not deserializable")

			override fun serialize(encoder: Encoder, value: NumberProvider) = when (value) {
				is ConstantNumberProvider -> encoder.encodeSerializableValue(ConstantNumberProvider.serializer(), value)
				is UniformNumberProvider -> encoder.encodeSerializableValue(UniformNumberProvider.serializer(), value)
				is BinomialNumberProvider -> encoder.encodeSerializableValue(BinomialNumberProvider.serializer(), value)
				is ScoreNumberProvider -> encoder.encodeSerializableValue(ScoreNumberProvider.serializer(), value)
			}
		}
	}
}
