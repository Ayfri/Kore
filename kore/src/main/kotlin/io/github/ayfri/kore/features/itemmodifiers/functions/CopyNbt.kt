package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CopyNbt(
	override var conditions: PredicateAsList? = null,
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
	CopyNbt(source = CopyNbtContext(source), ops = buildList(ops)).also { modifiers += it }

fun ItemModifier.copyNbt(source: String, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyNbt(source = CopyNbtStorage(source), ops = buildList(ops)).also { modifiers += it }
