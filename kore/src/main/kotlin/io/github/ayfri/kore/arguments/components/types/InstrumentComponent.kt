package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.InstrumentArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = InstrumentComponent.Companion.InstrumentComponentSerializer::class)
data class InstrumentComponent(var instrument: InstrumentArgument) : Component() {
	companion object {
		object InstrumentComponentSerializer : InlineSerializer<InstrumentComponent, InstrumentArgument>(
			InstrumentArgument.serializer(),
			InstrumentComponent::instrument
		)
	}
}

fun ComponentsScope.instrument(instrument: InstrumentArgument) = apply {
	this[ComponentTypes.INSTRUMENT] = InstrumentComponent(instrument)
}
