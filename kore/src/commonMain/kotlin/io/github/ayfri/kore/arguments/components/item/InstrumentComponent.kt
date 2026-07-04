package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.InstrumentArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:instrument` item component, which specifies the goat horn sound variant when the item is used.
 *
 * Serializes as the instrument id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#instrument
 */
@Serializable(with = InstrumentComponent.Companion.InstrumentComponentSerializer::class)
data class InstrumentComponent(var instrument: InstrumentArgument) : Component() {
	companion object {
		data object InstrumentComponentSerializer : InlineAutoSerializer<InstrumentComponent, InstrumentArgument>(
			serializer<InstrumentArgument>(),
			InstrumentComponent::instrument,
			::InstrumentComponent
		)
	}
}

/** Specifies the goat horn sound variant when the item is used. */
fun ComponentsScope.instrument(instrument: InstrumentArgument) = apply {
	this[ItemComponentTypes.INSTRUMENT] = InstrumentComponent(instrument)
}
