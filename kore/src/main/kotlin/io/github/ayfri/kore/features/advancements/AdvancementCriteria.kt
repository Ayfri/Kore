package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.advancements.triggers.AdvancementTriggerCondition
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Container used for serializing advancement criteria as a name->trigger map as in vanilla JSON.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 * Triggers: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@Serializable(with = AdvancementCriteria.Companion.AdvancementCriteriaSerializer::class)
data class AdvancementCriteria(var criteria: AdvancementCriteriaMap = AdvancementCriteriaMap()) {
	companion object {
		data object AdvancementCriteriaSerializer : InlineSerializer<AdvancementCriteria, Map<String, AdvancementTriggerCondition>>(
			MapSerializer(String.serializer(), AdvancementTriggerCondition.serializer()),
			AdvancementCriteria::criteria
		)
	}
}

/** Map wrapper for advancement criteria. */
class AdvancementCriteriaMap : LinkedHashMap<String, AdvancementTriggerCondition>()
