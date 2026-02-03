package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.ItemSlot
import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.features.predicates.sub.entityspecific.EntityTypeSpecific
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class Entity(
	var distance: Distance? = null,
	var components: ComponentsPatch? = null,
	var effects: Map<MobEffectArgument, Effect>? = null,
	var equipment: Equipment? = null,
	var flags: EntityFlags? = null,
	var location: Location? = null,
	var movement: Movement? = null,
	var movementAffectedBy: Location? = null,
	@Serializable(NbtAsJsonSerializer::class) var nbt: NbtCompound? = null,
	var passenger: Entity? = null,
	var periodicTicks: Int? = null,
	var predicates: ItemStackSubPredicates? = null,
	@Serializable(EntitySlotsSerializer::class) var slots: Map<ItemSlot, ItemStack>? = null,
	var steppingOn: Block? = null,
	var targetedEntity: Entity? = null,
	var team: String? = null,
	var type: InlinableList<EntityTypeOrTagArgument>? = null,
	var typeSpecific: EntityTypeSpecific? = null,
	var vehicle: Entity? = null,
)

private object EntitySlotsSerializer : KSerializer<Map<ItemSlot, ItemStack>> {
	override val descriptor = MapSerializer(String.serializer(), ItemStack.serializer()).descriptor

	override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder) = error("Entity slots cannot be deserialized.")

	override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Map<ItemSlot, ItemStack>) {
		val map = value.mapKeys { it.key.asString() }
		MapSerializer(String.serializer(), ItemStack.serializer()).serialize(encoder, map)
	}
}

@Serializable
data class EntityFlags(
	var isBaby: Boolean? = null,
	var isFallFlying: Boolean? = null,
	var isFlying: Boolean? = null,
	var isInWater: Boolean? = null,
	var isOnFire: Boolean? = null,
	var isOnGround: Boolean? = null,
	var isSneaking: Boolean? = null,
	var isSprinting: Boolean? = null,
	var isSwimming: Boolean? = null,
)

fun entity(init: Entity.() -> Unit = {}) = Entity().apply(init)

fun Entity.components(init: ComponentsPatch.() -> Unit) {
	components = ComponentsPatch().apply(init)
}

fun Entity.distance(init: Distance.() -> Unit = {}) {
	distance = Distance().apply(init)
}

fun Entity.flags(init: EntityFlags.() -> Unit = {}) {
	flags = EntityFlags().apply(init)
}

fun Entity.equipment(init: Equipment.() -> Unit = {}) {
	equipment = Equipment().apply(init)
}

fun Entity.effects(block: MutableMap<MobEffectArgument, Effect>.() -> Unit) {
	effects = buildMap(block)
}

fun Entity.effects(vararg effects: Pair<MobEffectArgument, Effect>) {
	this.effects = effects.toMap()
}

fun Entity.location(init: Location.() -> Unit = {}) {
	location = Location().apply(init)
}

fun Entity.movement(init: Movement.() -> Unit = {}) {
	movement = Movement().apply(init)
}

fun Entity.movementAffectedBy(init: Location.() -> Unit = {}) {
	movementAffectedBy = Location().apply(init)
}

fun Entity.nbt(block: NbtCompoundBuilder.() -> Unit) {
	nbt = buildNbtCompound(block)
}

fun Entity.passenger(init: Entity.() -> Unit = {}) {
	passenger = Entity().apply(init)
}

fun Entity.predicates(block: ItemStackSubPredicates.() -> Unit) {
	predicates = ItemStackSubPredicates().apply(block)
}

fun Entity.slots(init: MutableMap<ItemSlot, ItemStack>.() -> Unit) {
	slots = buildMap(init)
}

fun Entity.slots(vararg slots: Pair<ItemSlot, ItemStack>) {
	this.slots = slots.toMap()
}

fun Entity.steppingOn(init: Block.() -> Unit = {}) {
	steppingOn = Block().apply(init)
}

fun Entity.targetedEntity(init: Entity.() -> Unit = {}) {
	targetedEntity = Entity().apply(init)
}

fun Entity.type(vararg types: EntityTypeOrTagArgument) {
	type = types.toList()
}

fun Entity.vehicle(init: Entity.() -> Unit = {}) {
	vehicle = Entity().apply(init)
}
