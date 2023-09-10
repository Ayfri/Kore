package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import net.benwoodworth.knbt.NbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class Entity(
	var distance: Distance? = null,
	var effects: Map<EffectArgument, Effect>? = null,
	var equipment: Equipment? = null,
	var flags: EntityFlags? = null,
	var location: Location? = null,
	var nbt: NbtCompound? = null,
	var passenger: Entity? = null,
	var steppingOn: Block? = null,
	var team: String? = null,
	var type: EntityTypeArgument? = null,
	var targetedEntity: Entity? = null,
	var vehicle: Entity? = null,
	var typeSpecific: EntityTypeSpecific? = null,
)

@Serializable
data class EntityFlags(
	var isBaby: Boolean? = null,
	var isOnFire: Boolean? = null,
	var isSneaking: Boolean? = null,
	var isSprinting: Boolean? = null,
	var isSwimming: Boolean? = null,
)

fun entity(init: Entity.() -> Unit = {}) = Entity().apply(init)

fun Entity.flags(init: EntityFlags.() -> Unit = {}) {
	flags = EntityFlags().apply(init)
}
