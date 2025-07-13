package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.types.Component
import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
class Components(components: MutableMap<String, Component> = mutableMapOf()) : ComponentsScope(components) {
	fun toPatch() = ComponentsPatch(components)
}
