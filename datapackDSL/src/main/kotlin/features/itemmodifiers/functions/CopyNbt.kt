package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable
data class CopyNbt(
	var source: NbtProvider,
	var ops: List<CopyNbtOperation> = emptyList(),
) : ItemFunction()

@Serializable
sealed interface NbtProvider

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

fun ItemModifier.copyNbt(source: Source, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyNbt(CopyNbtContext(source), buildList(ops)).also { modifiers += it }


fun ItemModifier.copyNbt(source: String, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyNbt(CopyNbtStorage(source), buildList(ops)).also { modifiers += it }
