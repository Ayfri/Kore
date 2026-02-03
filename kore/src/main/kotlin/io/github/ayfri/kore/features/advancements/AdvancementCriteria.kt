package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.advancements.triggers.AdvancementTriggerCondition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

typealias AdvancementCriteria = @Serializable(with = AdvancementCriteriaSurrogate.Companion.AdvancementCriteriaSerializer::class) AdvancementCriteriaSurrogate

/**
 * Container used for serializing advancement criteria as a name->trigger map as in vanilla JSON.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 * Triggers: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@Serializable
data class AdvancementCriteriaSurrogate(var criteria: List<AdvancementTriggerCondition> = emptyList()) {
	companion object {
		data object AdvancementCriteriaSerializer : JsonTransformingSerializer<AdvancementCriteriaSurrogate>(serializer()) {
			override fun transformSerialize(element: JsonElement): JsonElement {
				// elements are serialized as an array in an object named criteria, we instead want to only serialize an inline map with the element `name` as key
				val entries = element.jsonObject["criteria"]!!.jsonArray.map { it.jsonObject }

				val finalObject = buildJsonObject {
					entries.forEach { entry ->
						val triggerName = entry["trigger"]!!.jsonPrimitive.content
						val criterion = entry["conditions"]!!.jsonObject

						val name = criterion["name"]!!.jsonPrimitive.content
						val conditions = criterion["conditions"]?.jsonArray
						val objectWithoutNameAndConditions = criterion.toMutableMap().apply {
							remove("name")
							remove("conditions")
						}

						put(name, buildJsonObject {
							put("trigger", triggerName)
							if (conditions != null || objectWithoutNameAndConditions.isNotEmpty()) {
								put("conditions", buildJsonObject {
									conditions?.let { put("player", it) }
									objectWithoutNameAndConditions.forEach { (key, value) ->
										put(key, value)
									}
								})
							}
						})
					}
				}

				return finalObject
			}
		}
	}
}

/** Append a trigger to the criteria. */
operator fun AdvancementCriteria.plusAssign(trigger: AdvancementTriggerCondition) {
	criteria += trigger
}

/** Set a trigger by name. */
operator fun AdvancementCriteria.set(name: String, trigger: AdvancementTriggerCondition) {
	criteria += trigger.apply { this.name = name }
}
