package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.generated.Tags
import kotlinx.serialization.Serializable

@Serializable
data class DamageTagEntry(
	var id: Tags.DamageType? = null,
	var expected: Boolean? = null,
)

@Serializable
data class DamageSource(
	var directEntity: Entity? = null,
	var isDirect: Boolean? = null,
	var sourceEntity: Entity? = null,
	var tags: List<DamageTagEntry>? = null,
)

fun DamageSource.tag(id: Tags.DamageType? = null, expected: Boolean? = null) {
	tags = (tags ?: mutableListOf()) + DamageTagEntry(id, expected)
}
