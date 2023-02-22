package features.advancements.triggers

import features.advancements.EntityOrPredicates
import kotlinx.serialization.Serializable

@Serializable
data class ChanneledLightning(
	var victims: List<EntityOrPredicates>? = null,
) : AdvancementTriggerCondition
