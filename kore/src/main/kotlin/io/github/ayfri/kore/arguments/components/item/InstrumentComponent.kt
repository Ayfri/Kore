package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.InstrumentArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = InstrumentComponent.Companion.InstrumentComponentSerializer::class)
data class InstrumentComponent(var instrument: InstrumentArgument) : Component() {
	companion object {
		data object InstrumentComponentSerializer : InlineAutoSerializer<InstrumentComponent>(InstrumentComponent::class)
	}
}

fun ComponentsScope.instrument(instrument: InstrumentArgument) = apply {
	this[ItemComponentTypes.INSTRUMENT] = InstrumentComponent(instrument)
}
