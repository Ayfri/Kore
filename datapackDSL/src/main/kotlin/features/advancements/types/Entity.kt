package features.advancements.types

import arguments.Argument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class Entity(
	var distance: Distance? = null,
	var effects: Map<Argument.MobEffect, Effect>? = null,
	var equipment: Equipment? = null,
	var flags: EntityFlags? = null,
	var location: Location? = null,
	var nbt: NbtCompound? = null,
	var passenger: Entity? = null,
	var steppingOn: Block? = null,
	var team: String? = null,
	var type: Argument.EntitySummon? = null,
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

fun entity(init: Entity.() -> Unit = {}): Entity {
	val entity = Entity()
	entity.init()
	return entity
}

fun Entity.flags(init: EntityFlags.() -> Unit = {}) {
	flags = EntityFlags().apply(init)
}
