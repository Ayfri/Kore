package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.Serializable

@Serializable
data class DamageTagEntry(
	var id: String? = null,
	var expected: Boolean? = null,
)

@Serializable
data class DamageSource(
	var directEntity: Entity? = null,
	var isDirect: Boolean? = null,
	var sourceEntity: Entity? = null,
	var tags: MutableList<DamageTagEntry>? = null,
)

fun damageSource(init: DamageSource.() -> Unit = {}) = DamageSource().apply(init)

fun DamageSource.tag(id: String? = null, expected: Boolean? = null) {
	tags = tags ?: mutableListOf()
	tags!!.add(DamageTagEntry(id, expected))
}
