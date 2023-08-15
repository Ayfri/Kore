package data.item

import arguments.types.literals.UUIDArgument
import arguments.types.resources.AttributeArgument
import commands.AttributeModifierOperation
import features.predicates.providers.*
import serializers.EnumOrdinalSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AttributeModifier(
	val attribute: AttributeArgument,
	@Serializable(NumberProviderSerializer::class) val amount: NumberProvider = constant(0f),
	@Serializable(AttributeModifierOperationSerializer::class) val operation: AttributeModifierOperation = AttributeModifierOperation.ADD,
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
