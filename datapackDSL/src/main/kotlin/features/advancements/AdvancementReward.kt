package features.advancements

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class AdvancementReward(
	var experience: Int? = null,
	var function: String? = null,
	var loot: List<Argument.LootTable>? = null,
	var recipes: List<Argument.Recipe>? = null,
)
