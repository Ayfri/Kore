package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.Serializable

@Serializable(with = ComponentsSerializer::class)
class Components(components: MutableMap<String, Component> = mutableMapOf()) : ComponentsScope(components) {
	/** Convert the components to a [ComponentsPatch]. */
	fun toPatch() = ComponentsPatch(components)
}
