package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * A selection of inventory slots, used by the `slots` loot entry, `/item`, `/execute if items|slots` and slot source files.
 * A [SlotSourceReference] is the bare string of the ID of a slot source file.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/slot-sources
 * Minecraft Wiki: https://minecraft.wiki/w/Slot_source
 */
@GeneratedSealedSerializer
@Serializable(with = SlotSource.Companion.SlotSourceSerializer::class)
sealed class SlotSource {
	companion object {
		data object SlotSourceSerializer : NamespacedPolymorphicSerializer<SlotSource>(slotSourceSealedSerializer())
	}
}

/** The loot context entity or block entity whose inventory a [SlotRangeSlotSource] reads, [CONTAINER] being the context's container. */
@Serializable(with = SlotSourceOrigin.Companion.SlotSourceOriginSerializer::class)
enum class SlotSourceOrigin {
	ATTACKING_ENTITY,
	BLOCK_ENTITY,
	CONTAINER,
	DIRECT_ATTACKER,
	INTERACTING_ENTITY,
	LAST_DAMAGE_PLAYER,
	TARGET_ENTITY,
	THIS,
	;

	companion object {
		data object SlotSourceOriginSerializer : LowercaseSerializer<SlotSourceOrigin>(entries)
	}
}

/** The item component whose stored items a [ContentsSlotSource] selects. */
@Serializable(with = InventoryComponentType.Companion.InventoryComponentTypeSerializer::class)
enum class InventoryComponentType {
	BUNDLE_CONTENTS,
	CHARGED_PROJECTILES,
	CONTAINER,
	;

	companion object {
		data object InventoryComponentTypeSerializer : LowercaseSerializer<InventoryComponentType>(entries, {
			"minecraft:${name.lowercase()}"
		})
	}
}

/** Builder scope for constructing a list of [SlotSource] instances. */
class SlotSourcesBuilder {
	val sources = mutableListOf<SlotSource>()

	fun build() = sources.toList()
}

/** Collects the slot sources appended in [block] into a list. */
internal fun buildSlotSources(block: SlotSourcesBuilder.() -> Unit) = SlotSourcesBuilder().apply(block).build()

/** Slot source wrapping the slot sources it applies to. */
interface SlotSourceHolder {
	var slotSource: InlinableList<SlotSource>
}

/** Configure the slot sources this slot source applies to. */
fun SlotSourceHolder.slotSource(block: SlotSourcesBuilder.() -> Unit) {
	slotSource = buildSlotSources(block)
}
