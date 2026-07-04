package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Raider(
	var hasRaid: Boolean? = null,
	var isCaptain: Boolean? = null,
) : EntityTypeSpecific()

fun Entity.raiderTypeSpecific(hasRaid: Boolean? = null, isCaptain: Boolean? = null, block: Raider.() -> Unit = {}) = apply {
	typeSpecific = Raider(hasRaid, isCaptain).apply(block)
}
