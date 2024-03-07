package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray

@Serializable(with = Components.Companion.ComponentsSerializer::class)
data class Components(
	val components: MutableList<Component> = mutableListOf(),
) {
	override fun toString() =
		jsonSerializer.encodeToJsonElement(components).jsonArray.joinToString(separator = ",", prefix = "[", postfix = "]")

	companion object {
		val jsonSerializer = Json {
			prettyPrint = false
			encodeDefaults = false
			classDiscriminatorMode = ClassDiscriminatorMode.NONE
		}

		object ComponentsSerializer : ToStringSerializer<Components>()
	}
}
