package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Copies NBT to the item's `minecraft:custom_data` component from a loot-context source or a storage.
 * Mirrors vanilla `minecraft:copy_custom_data`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class CopyCustomData(
	override var conditions: PredicateAsList? = null,
	var source: NbtProvider,
	var ops: List<CopyNbtOperation> = emptyList(),
) : ItemFunction()

/** Source descriptor for copy-custom-data. */
@Serializable
sealed interface NbtProvider

/** Use a loot-context source (e.g. `this`, `attacker`, `block_entity`). */
@Serializable
data class CopyNbtContext(val source: Source) : NbtProvider

@Serializable
@SerialName("minecraft:storage")
data class CopyCustomDataStorage(
	var source: String,
) : NbtProvider

/** Single NBT copy operation. */
@Serializable
data class CopyNbtOperation(
	var op: CopyCustomDataOperationType,
	var source: String,
	var target: String,
)

/** Operation types for copy-custom-data (replace/append/merge). */
@Serializable(with = CopyCustomDataOperationType.Companion.CopyNbtOperationTypeSerializer::class)
enum class CopyCustomDataOperationType {
	REPLACE,
	APPEND,
	MERGE;

	companion object {
		data object CopyNbtOperationTypeSerializer : LowercaseSerializer<CopyCustomDataOperationType>(entries)
	}
}

/** Add a `copy_custom_data` step with a loot-context source. */
fun ItemModifier.copyCustomData(source: Source, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyCustomData(source = CopyNbtContext(source), ops = buildList(ops)).also { modifiers += it }

/** Add a `copy_custom_data` step from a storage id (e.g. `namespace:storage`). */
fun ItemModifier.copyCustomData(source: String, ops: MutableList<CopyNbtOperation>.() -> Unit = {}) =
	CopyCustomData(source = CopyCustomDataStorage(source), ops = buildList(ops)).also { modifiers += it }
