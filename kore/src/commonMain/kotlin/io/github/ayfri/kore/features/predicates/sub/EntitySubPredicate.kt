package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.ItemSlot
import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.components.matchers.DataComponentPredicate
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LazySerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt as buildNbt
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/**
 * One entry of an [EntityPredicate]. Minecraft matches entities against an identifier-keyed sub-predicate registry -
 * `{ "minecraft:distance": {...}, "minecraft:equipment": {...}, ... }` - instead of a single flat object.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@GeneratedSealedSerializer
@Serializable(with = EntitySubPredicate.Companion.EntitySubPredicateSerializer::class)
sealed class EntitySubPredicate {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object EntitySubPredicateSerializer : NamespacedPolymorphicSerializer<EntitySubPredicate>(
			entitySubPredicateSealedSerializer(),
			skipOutputName = true,
			contentName = { it.substringAfterLast('.').removeSuffix("SubPredicate").snakeCase() },
		)
	}
}

/** Matches exact data component values on the entity, keyed under `minecraft:components`. */
@Serializable(with = ComponentsSubPredicate.Companion.Serializer::class)
data class ComponentsSubPredicate(val components: ComponentsPatch) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<ComponentsSubPredicate, ComponentsPatch>(
			ComponentsPatch.serializer(), ComponentsSubPredicate::components, ::ComponentsSubPredicate, "ComponentsSubPredicate"
		)
	}
}

/** Matches the distance to the loot context origin, keyed under `minecraft:distance`. */
@Serializable(with = DistanceSubPredicate.Companion.Serializer::class)
data class DistanceSubPredicate(val distance: DistancePredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<DistanceSubPredicate, DistancePredicate>(
			DistancePredicate.serializer(), DistanceSubPredicate::distance, ::DistanceSubPredicate, "DistanceSubPredicate"
		)
	}
}

/** Matches the active status effects of the entity, keyed under `minecraft:effects`. */
@Serializable(with = EffectsSubPredicate.Companion.Serializer::class)
data class EffectsSubPredicate(val effects: Map<MobEffectArgument, MobEffectPredicate>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EffectsSubPredicate, Map<MobEffectArgument, MobEffectPredicate>>(
			serializer<Map<MobEffectArgument, MobEffectPredicate>>(),
			EffectsSubPredicate::effects,
			::EffectsSubPredicate,
			"EffectsSubPredicate"
		)
	}
}

/** Matches the scoreboard-style tags of the entity, keyed under `minecraft:entity_tags`. */
@Serializable(with = EntityTagsSubPredicate.Companion.Serializer::class)
data class EntityTagsSubPredicate(val tags: EntityTagsPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EntityTagsSubPredicate, EntityTagsPredicate>(
			EntityTagsPredicate.serializer(), EntityTagsSubPredicate::tags, ::EntityTagsSubPredicate, "EntityTagsSubPredicate"
		)
	}
}

/** Matches the type of the entity, keyed under `minecraft:entity_type`. */
@Serializable(with = EntityTypeSubPredicate.Companion.Serializer::class)
data class EntityTypeSubPredicate(val types: InlinableList<EntityTypeOrTagArgument>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EntityTypeSubPredicate, InlinableList<EntityTypeOrTagArgument>>(
			InlinableListSerializer(serializer<EntityTypeOrTagArgument>()),
			EntityTypeSubPredicate::types,
			::EntityTypeSubPredicate,
			"EntityTypeSubPredicate"
		)
	}
}

/** Matches the equipped items of the entity, keyed under `minecraft:equipment`. */
@Serializable(with = EquipmentSubPredicate.Companion.Serializer::class)
data class EquipmentSubPredicate(val equipment: EntityEquipmentPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EquipmentSubPredicate, EntityEquipmentPredicate>(
			EntityEquipmentPredicate.serializer(), EquipmentSubPredicate::equipment, ::EquipmentSubPredicate, "EquipmentSubPredicate"
		)
	}
}

/** Matches the boolean state flags of the entity, keyed under `minecraft:flags`. */
@Serializable(with = FlagsSubPredicate.Companion.Serializer::class)
data class FlagsSubPredicate(val flags: EntityFlagsPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<FlagsSubPredicate, EntityFlagsPredicate>(
			EntityFlagsPredicate.serializer(), FlagsSubPredicate::flags, ::FlagsSubPredicate, "FlagsSubPredicate"
		)
	}
}

/** Matches the location of the entity, keyed under `minecraft:location`. */
@Serializable(with = LocationSubPredicate.Companion.Serializer::class)
data class LocationSubPredicate(val location: LocationPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<LocationSubPredicate, LocationPredicate>(
			LocationPredicate.serializer(), LocationSubPredicate::location, ::LocationSubPredicate, "LocationSubPredicate"
		)
	}
}

/** Matches the velocity and fall distance of the entity, keyed under `minecraft:movement`. */
@Serializable(with = MovementSubPredicate.Companion.Serializer::class)
data class MovementSubPredicate(val movement: MovementPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<MovementSubPredicate, MovementPredicate>(
			MovementPredicate.serializer(), MovementSubPredicate::movement, ::MovementSubPredicate, "MovementSubPredicate"
		)
	}
}

/** Matches the block at most half a block below the entity that can affect its movement, keyed under `minecraft:movement_affected_by`. */
@Serializable(with = MovementAffectedBySubPredicate.Companion.Serializer::class)
data class MovementAffectedBySubPredicate(val location: LocationPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<MovementAffectedBySubPredicate, LocationPredicate>(
			LocationPredicate.serializer(),
			MovementAffectedBySubPredicate::location,
			::MovementAffectedBySubPredicate,
			"MovementAffectedBySubPredicate"
		)
	}
}

/** Matches the NBT of the entity, keyed under `minecraft:nbt`. */
@Serializable(with = NbtSubPredicate.Companion.Serializer::class)
data class NbtSubPredicate(val nbt: NbtTag) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<NbtSubPredicate, NbtTag>(
			NbtAsJsonSerializer, NbtSubPredicate::nbt, ::NbtSubPredicate, "NbtSubPredicate"
		)
	}
}

/** Matches the entity riding this one, keyed under `minecraft:passenger`. */
@Serializable(with = PassengerSubPredicate.Companion.Serializer::class)
data class PassengerSubPredicate(val entity: EntityPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<PassengerSubPredicate> by LazySerializer(
			buildClassSerialDescriptor("PassengerSubPredicate"),
			{
				InlineAutoSerializer(
					EntityPredicate.serializer(), PassengerSubPredicate::entity, ::PassengerSubPredicate, "PassengerSubPredicate"
				)
			},
		)
	}
}

/** Passes every `ticks` ticks of the entity's lifetime, keyed under `minecraft:periodic_tick`. */
@Serializable(with = PeriodicTickSubPredicate.Companion.Serializer::class)
data class PeriodicTickSubPredicate(val ticks: Int) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<PeriodicTickSubPredicate, Int>(
			Int.serializer(), PeriodicTickSubPredicate::ticks, ::PeriodicTickSubPredicate, "PeriodicTickSubPredicate"
		)
	}
}

/** Tests data component values on the entity, keyed under `minecraft:predicates`. */
@Serializable(with = PredicatesSubPredicate.Companion.Serializer::class)
data class PredicatesSubPredicate(val predicates: DataComponentPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<PredicatesSubPredicate, DataComponentPredicate>(
			DataComponentPredicate.serializer(), PredicatesSubPredicate::predicates, ::PredicatesSubPredicate, "PredicatesSubPredicate"
		)
	}
}

/** Matches the items in arbitrary slots of the entity, keyed under `minecraft:slots`. */
@Serializable(with = SlotsSubPredicate.Companion.Serializer::class)
data class SlotsSubPredicate(val slots: Map<ItemSlot, ItemStackPredicate>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<SlotsSubPredicate, Map<ItemSlot, ItemStackPredicate>>(
			EntitySlotsSerializer, SlotsSubPredicate::slots, ::SlotsSubPredicate, "SlotsSubPredicate"
		)
	}
}

/** Matches the block the entity is standing on, keyed under `minecraft:stepping_on`. */
@Serializable(with = SteppingOnSubPredicate.Companion.Serializer::class)
data class SteppingOnSubPredicate(val location: LocationPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<SteppingOnSubPredicate, LocationPredicate>(
			LocationPredicate.serializer(), SteppingOnSubPredicate::location, ::SteppingOnSubPredicate, "SteppingOnSubPredicate"
		)
	}
}

/** Matches the entity this one's AI is targeting, keyed under `minecraft:targeted_entity`. */
@Serializable(with = TargetedEntitySubPredicate.Companion.Serializer::class)
data class TargetedEntitySubPredicate(val entity: EntityPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<TargetedEntitySubPredicate> by LazySerializer(
			buildClassSerialDescriptor("TargetedEntitySubPredicate"),
			{
				InlineAutoSerializer(
					EntityPredicate.serializer(),
					TargetedEntitySubPredicate::entity,
					::TargetedEntitySubPredicate,
					"TargetedEntitySubPredicate"
				)
			},
		)
	}
}

/** Matches the team of the entity, keyed under `minecraft:team`. */
@Serializable(with = TeamSubPredicate.Companion.Serializer::class)
data class TeamSubPredicate(val team: String) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<TeamSubPredicate, String>(
			String.serializer(), TeamSubPredicate::team, ::TeamSubPredicate, "TeamSubPredicate"
		)
	}
}

/** Matches the entity this one is riding, keyed under `minecraft:vehicle`. */
@Serializable(with = VehicleSubPredicate.Companion.Serializer::class)
data class VehicleSubPredicate(val entity: EntityPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<VehicleSubPredicate> by LazySerializer(
			buildClassSerialDescriptor("VehicleSubPredicate"),
			{ InlineAutoSerializer(EntityPredicate.serializer(), VehicleSubPredicate::entity, ::VehicleSubPredicate, "VehicleSubPredicate") },
		)
	}
}

/** Serializes the `minecraft:slots` map, whose keys are slot names rather than the slot indices [ItemSlot] normally encodes. */
internal data object EntitySlotsSerializer : KSerializer<Map<ItemSlot, ItemStackPredicate>> {
	private val stringKeyedSerializer = MapSerializer(String.serializer(), ItemStackPredicate.serializer())
	override val descriptor = stringKeyedSerializer.descriptor

	override fun deserialize(decoder: Decoder) = stringKeyedSerializer.deserialize(decoder)
		.mapKeys { (name, _) -> ItemSlotType { name } as ItemSlot }

	override fun serialize(encoder: Encoder, value: Map<ItemSlot, ItemStackPredicate>) =
		stringKeyedSerializer.serialize(encoder, value.mapKeys { it.key.asString() })
}

/** Matches exact data component values on the entity. */
fun EntityPredicate.components(init: ComponentsPatch.() -> Unit) {
	subPredicates += ComponentsSubPredicate(ComponentsPatch().apply(init))
}

/** Matches the distance between the entity and the loot context origin. */
fun EntityPredicate.distance(init: DistancePredicate.() -> Unit = {}) {
	subPredicates += DistanceSubPredicate(DistancePredicate().apply(init))
}

/** Matches the active status effects of the entity. */
fun EntityPredicate.effects(block: MutableMap<MobEffectArgument, MobEffectPredicate>.() -> Unit) {
	subPredicates += EffectsSubPredicate(buildMap(block))
}

/** Matches the active status effects of the entity. */
fun EntityPredicate.effects(vararg effects: Pair<MobEffectArgument, MobEffectPredicate>) {
	subPredicates += EffectsSubPredicate(effects.toMap())
}

/** Matches the scoreboard-style tags of the entity. */
fun EntityPredicate.entityTags(init: EntityTagsPredicate.() -> Unit = {}) {
	subPredicates += EntityTagsSubPredicate(EntityTagsPredicate().apply(init))
}

/** Matches the entity against any of [types]. */
fun EntityPredicate.entityType(vararg types: EntityTypeOrTagArgument) {
	subPredicates += EntityTypeSubPredicate(types.toList())
}

/** Matches the equipped items of the entity. */
fun EntityPredicate.equipment(init: EntityEquipmentPredicate.() -> Unit = {}) {
	subPredicates += EquipmentSubPredicate(EntityEquipmentPredicate().apply(init))
}

/** Matches the boolean state flags of the entity. */
fun EntityPredicate.flags(init: EntityFlagsPredicate.() -> Unit = {}) {
	subPredicates += FlagsSubPredicate(EntityFlagsPredicate().apply(init))
}

/** Matches the location of the entity. */
fun EntityPredicate.location(init: LocationPredicate.() -> Unit = {}) {
	subPredicates += LocationSubPredicate(LocationPredicate().apply(init))
}

/** Matches the velocity and fall distance of the entity. */
fun EntityPredicate.movement(init: MovementPredicate.() -> Unit = {}) {
	subPredicates += MovementSubPredicate(MovementPredicate().apply(init))
}

/** Matches the block at most half a block below the entity that can affect its movement. */
fun EntityPredicate.movementAffectedBy(init: LocationPredicate.() -> Unit = {}) {
	subPredicates += MovementAffectedBySubPredicate(LocationPredicate().apply(init))
}

/** Matches the NBT of the entity. */
fun EntityPredicate.nbt(block: NbtCompoundBuilder.() -> Unit) {
	subPredicates += NbtSubPredicate(buildNbt(block))
}

/** Matches the entity riding this one. */
fun EntityPredicate.passenger(init: EntityPredicate.() -> Unit = {}) {
	subPredicates += PassengerSubPredicate(EntityPredicate().apply(init))
}

/** Passes every [ticks] ticks of the entity's lifetime. */
fun EntityPredicate.periodicTick(ticks: Int) {
	subPredicates += PeriodicTickSubPredicate(ticks)
}

/** Tests data component values on the entity. */
fun EntityPredicate.predicates(block: DataComponentPredicate.() -> Unit) {
	subPredicates += PredicatesSubPredicate(DataComponentPredicate().apply(block))
}

/** Matches the items in arbitrary slots of the entity. */
fun EntityPredicate.slots(init: MutableMap<ItemSlot, ItemStackPredicate>.() -> Unit) {
	subPredicates += SlotsSubPredicate(buildMap(init))
}

/** Matches the items in arbitrary slots of the entity. */
fun EntityPredicate.slots(vararg slots: Pair<ItemSlot, ItemStackPredicate>) {
	subPredicates += SlotsSubPredicate(slots.toMap())
}

/** Matches the block the entity is standing on. */
fun EntityPredicate.steppingOn(init: LocationPredicate.() -> Unit = {}) {
	subPredicates += SteppingOnSubPredicate(LocationPredicate().apply(init))
}

/** Matches the entity this one's AI is targeting. */
fun EntityPredicate.targetedEntity(init: EntityPredicate.() -> Unit = {}) {
	subPredicates += TargetedEntitySubPredicate(EntityPredicate().apply(init))
}

/** Matches the team of the entity. */
fun EntityPredicate.team(team: String) {
	subPredicates += TeamSubPredicate(team)
}

/** Matches the entity this one is riding. */
fun EntityPredicate.vehicle(init: EntityPredicate.() -> Unit = {}) {
	subPredicates += VehicleSubPredicate(EntityPredicate().apply(init))
}
