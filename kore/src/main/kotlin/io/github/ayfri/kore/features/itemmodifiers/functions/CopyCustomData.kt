package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CopyCustomData(
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
data class CopyCustomDataStorage(
	var source: String,
) : NbtProvider

@Serializable
data class CopyNbtOperation(
	var op: CopyCustomDataOperationType,
	var source: String,
	var target: String,
)

@Serializable(with = CopyCustomDataOperationType.Companion.CopyNbtOperationTypeSerializer::class)
enum class CopyCustomDataOperationType {
	REPLACE,
	APPEND,
	MERGE;

	companion object {
		data object CopyNbtOperationTypeSerializer : LowercaseSerializer<CopyCustomDataOperationType>(entries)
	}
}

fun ItemModifier.copyCustomData(source: Source, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyCustomData(source = CopyNbtContext(source), ops = buildList(ops)).also { modifiers += it }

fun ItemModifier.copyCustomData(source: String, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyCustomData(source = CopyCustomDataStorage(source), ops = buildList(ops)).also { modifiers += it }
