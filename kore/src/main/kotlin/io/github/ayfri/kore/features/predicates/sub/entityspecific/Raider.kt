package io.github.ayfri.kore.features.predicates.sub.entityspecific

import kotlinx.serialization.Serializable

@Serializable
data class Raider(
	var hasRaid: Boolean? = null,
	var isCaptain: Boolean? = null,
) : EntityTypeSpecific()
