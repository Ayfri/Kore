package io.github.ayfri.kore.features.slotsources

import io.github.ayfri.kore.generated.arguments.types.SlotSourceArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Selects the slots of the slot source file [name]. */
@Serializable
@SerialName("reference")
data class ReferenceSlotSource(
	var name: SlotSourceArgument,
) : SlotSource()

/** Adds a reference to the slot source file [name]. */
fun SlotSourcesBuilder.reference(name: SlotSourceArgument) {
	sources += ReferenceSlotSource(name)
}
