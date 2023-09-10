package io.github.ayfri.kore.features.advancements.states

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

typealias State<T> = @Serializable(with = StateEntry.Companion.StateSerializer::class) StateEntry<T>

@Serializable
data class StateEntry<T : StateData> internal constructor(
	var value: T? = null,
	var max: T? = null,
) {
	companion object {
		class StateSerializer<T : StateData>(
			dataSerializer: KSerializer<T>
		) : JsonTransformingSerializer<StateEntry<T>>(serializer(dataSerializer)) {
			override fun transformSerialize(element: JsonElement): JsonElement {
				return buildJsonObject {
					val value = element.jsonObject["value"]?.jsonPrimitive?.content
					val max = element.jsonObject["max"]?.jsonPrimitive?.content

					when {
						max != null -> {
							put("min", value)
							put("max", max)
						}

						else -> put("value", value)
					}
				}
			}
		}
	}
}
