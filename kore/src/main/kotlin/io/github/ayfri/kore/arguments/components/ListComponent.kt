package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonEncoder

@Serializable(with = ListComponent.Companion.ListComponentSerializer::class)
abstract class ListComponent<T> : Component() {
	abstract val list: Iterable<T>

	abstract fun elementAsString(element: T): String

	companion object {
		class ListComponentSerializer<T> : ToStringSerializer<ListComponent<T>>(transform = { encoder ->
			require(encoder is JsonEncoder) { "ListComponent can only be serialized to JSON." }
			"[${list.joinToString(",") { elementAsString(it) }}]"
		})
	}
}
