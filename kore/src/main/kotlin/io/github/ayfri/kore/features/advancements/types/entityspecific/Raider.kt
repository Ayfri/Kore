package io.github.ayfri.kore.features.advancements.types.entityspecific

import kotlinx.serialization.Serializable

@Serializable
data class Raider(
	var hasRaid: Boolean? = null,
	var isCaptain: Boolean? = null,
) : EntityTypeSpecific()
