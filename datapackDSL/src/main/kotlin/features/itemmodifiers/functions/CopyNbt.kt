package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

@Serializable
data class CopyNbt(
	var source: NbtProvider,
	var ops: List<CopyNbtOperation> = emptyList(),
) : ItemFunctionSurrogate

@Serializable
/* (with = NbtProvider.Companion.NbtProviderSerializer::class) */
sealed interface NbtProvider {
	companion object {
		data object NbtProviderSerializer : KSerializer<NbtProvider> {
			override val descriptor = buildClassSerialDescriptor("NbtProviderSurrogate")

			override fun deserialize(decoder: Decoder) = error("NbtProviderSurrogate cannot be deserialized")

			override fun serialize(encoder: Encoder, value: NbtProvider) {
				if (value is CopyNbtContext) {
					encoder.encodeSerializableValue(Source.serializer(), value.source)
				} else if (value is CopyNbtStorage) {
					encoder.encodeSerializableValue(CopyNbtStorage.serializer(), value)
				}
			}
		}
	}
}

@Serializable
data class CopyNbtContext(val source: Source) : NbtProvider

@Serializable
@SerialName("minecraft:storage")
data class CopyNbtStorage(
	var source: String,
) : NbtProvider

@Serializable
data class CopyNbtOperation(
	var op: CopyNbtOperationType,
	var source: String,
	var target: String,
)

@Serializable(with = CopyNbtOperationType.Companion.CopyNbtOperationTypeSerializer::class)
enum class CopyNbtOperationType {
	REPLACE,
	APPEND,
	MERGE;

	companion object {
		data object CopyNbtOperationTypeSerializer : LowercaseSerializer<CopyNbtOperationType>(entries)
	}
}

fun ItemModifierEntry.copyNbt(source: Source, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) {
	function = CopyNbt(CopyNbtContext(source), buildList(ops))
}

fun ItemModifierEntry.copyNbt(source: String, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) {
	function = CopyNbt(CopyNbtStorage(source), buildList(ops))
}
