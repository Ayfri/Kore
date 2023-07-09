package data.item

import arguments.Argument
import commands.AttributeModifierOperation
import features.predicates.providers.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import serializers.EnumOrdinalSerializer

@Serializable
data class AttributeModifier(
	val attribute: Argument.Attribute,
	@Serializable(NumberProviderSerializer::class) val amount: NumberProvider = constant(0f),
	@Serializable(AttributeModifierOperationSerializer::class) val operation: AttributeModifierOperation = AttributeModifierOperation.ADDITION,
	val slot: List<EquipmentSlot>? = null,
	val uuid: Argument.UUID? = null,
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
