package features.advancements.types

import kotlinx.serialization.Serializable

@Serializable
data class DamageSource(
	var bypassesArmor: Boolean? = null,
	var bypassesInvulnerability: Boolean? = null,
	var bypassesMagic: Boolean? = null,
	var directEntity: Entity? = null,
	var isExplosion: Boolean? = null,
	var isFire: Boolean? = null,
	var isLightning: Boolean? = null,
	var isMagic: Boolean? = null,
	var isProjectile: Boolean? = null,
	var sourceEntity: Entity? = null,
)

fun damageSource(init: DamageSource.() -> Unit = {}): DamageSource {
	val damageSource = DamageSource()
	damageSource.init()
	return damageSource
}
