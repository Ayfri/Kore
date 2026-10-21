package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.generated.arguments.SlotSourceOrTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.SlotSourceTagArgument
import io.github.ayfri.kore.generated.arguments.types.SlotSourceArgument
import io.github.ayfri.kore.serializers.IdOrTagReferenceSerializer
import kotlinx.serialization.Serializable

/**
 * Selects the slots of the slot source [id] points to, written as the bare string `"my_pack:my_slot_source"` wherever a
 * slot source is expected. A tag (`"#my_pack:my_tag"`) selects the slots of every slot source it holds. A cyclic
 * reference makes the pack fail to load. A list can mix IDs and inline
 * sources, but a tag can't be one of its elements, only the whole list.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/slot-sources
 * Minecraft Wiki: https://minecraft.wiki/w/Slot_source
 *
 * @property id The ID of the slot source file, or of the slot source tag.
 */
@Serializable(with = SlotSourceReference.Companion.SlotSourceReferenceSerializer::class)
data class SlotSourceReference(var id: SlotSourceOrTagArgument) : SlotSource() {
	companion object {
		data object SlotSourceReferenceSerializer : IdOrTagReferenceSerializer<SlotSourceReference, SlotSourceOrTagArgument>(
			SlotSourceReference::id,
			::SlotSourceReference,
			{ name, namespace -> SlotSourceArgument(name, namespace) },
			{ name, namespace -> SlotSourceTagArgument(name, namespace) },
			"slot_source_reference",
		)
	}
}

/**
 * Adds a [SlotSourceReference] pointing at the slot source or slot source tag [id].
 *
 * ```
 * reference(SlotSourceArgument("hands", "my_pack"))  // "my_pack:hands"
 * ```
 */
fun SlotSourcesBuilder.reference(id: SlotSourceOrTagArgument) {
	sources += SlotSourceReference(id)
}
