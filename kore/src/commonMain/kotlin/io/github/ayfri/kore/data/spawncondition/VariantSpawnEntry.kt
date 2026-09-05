package io.github.ayfri.kore.data.spawncondition

import io.github.ayfri.kore.data.spawncondition.types.VariantCondition
import kotlinx.serialization.Serializable

@Serializable
data class VariantSpawnEntry(
	var priority: Int,
	var condition: VariantCondition? = null
)
