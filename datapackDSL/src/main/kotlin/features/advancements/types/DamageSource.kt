package features.advancements.types

import kotlinx.serialization.Serializable

@Serializable
data class DamageTagEntry(
	var id: String? = null,
	var expected: Boolean? = null,
)

@Serializable
data class DamageSource(
	var bypassesArmor: Boolean? = null,
	var directEntity: Entity? = null,
	var sourceEntity: Entity? = null,
	var tags: MutableList<DamageTagEntry>? = null,
)

fun damageSource(init: DamageSource.() -> Unit = {}): DamageSource {
	val damageSource = DamageSource()
	damageSource.init()
	return damageSource
}

fun DamageSource.tag(id: String? = null, expected: Boolean? = null) {
	tags = tags ?: mutableListOf()
	tags!!.add(DamageTagEntry(id, expected))
}
