package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.ItemSlot
import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.LazySerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/**
 * One entry of an [Entity] predicate. Minecraft matches entities against an identifier-keyed sub-predicate registry -
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

@Serializable(with = ComponentsSubPredicate.Companion.Serializer::class)
data class ComponentsSubPredicate(val components: ComponentsPatch) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<ComponentsSubPredicate, ComponentsPatch>(
			ComponentsPatch.serializer(), ComponentsSubPredicate::components, ::ComponentsSubPredicate, "ComponentsSubPredicate"
		)
	}
}

@Serializable(with = DistanceSubPredicate.Companion.Serializer::class)
data class DistanceSubPredicate(val distance: Distance) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<DistanceSubPredicate, Distance>(
			Distance.serializer(), DistanceSubPredicate::distance, ::DistanceSubPredicate, "DistanceSubPredicate"
		)
	}
}

@Serializable(with = EffectsSubPredicate.Companion.Serializer::class)
data class EffectsSubPredicate(val effects: Map<MobEffectArgument, Effect>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EffectsSubPredicate, Map<MobEffectArgument, Effect>>(
			serializer<Map<MobEffectArgument, Effect>>(), EffectsSubPredicate::effects, ::EffectsSubPredicate, "EffectsSubPredicate"
		)
	}
}

@Serializable(with = EntityTagsSubPredicate.Companion.Serializer::class)
data class EntityTagsSubPredicate(val tags: EntityTagsPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EntityTagsSubPredicate, EntityTagsPredicate>(
			EntityTagsPredicate.serializer(), EntityTagsSubPredicate::tags, ::EntityTagsSubPredicate, "EntityTagsSubPredicate"
		)
	}
}

@Serializable(with = EntityTypeSubPredicate.Companion.Serializer::class)
data class EntityTypeSubPredicate(val types: InlinableList<EntityTypeOrTagArgument>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EntityTypeSubPredicate, InlinableList<EntityTypeOrTagArgument>>(
			InlinableListSerializer(serializer<EntityTypeOrTagArgument>()), EntityTypeSubPredicate::types, ::EntityTypeSubPredicate, "EntityTypeSubPredicate"
		)
	}
}

@Serializable(with = EquipmentSubPredicate.Companion.Serializer::class)
data class EquipmentSubPredicate(val equipment: Equipment) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<EquipmentSubPredicate, Equipment>(
			Equipment.serializer(), EquipmentSubPredicate::equipment, ::EquipmentSubPredicate, "EquipmentSubPredicate"
		)
	}
}

@Serializable(with = FlagsSubPredicate.Companion.Serializer::class)
data class FlagsSubPredicate(val flags: EntityFlags) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<FlagsSubPredicate, EntityFlags>(
			EntityFlags.serializer(), FlagsSubPredicate::flags, ::FlagsSubPredicate, "FlagsSubPredicate"
		)
	}
}

@Serializable(with = LocationSubPredicate.Companion.Serializer::class)
data class LocationSubPredicate(val location: Location) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<LocationSubPredicate, Location>(
			Location.serializer(), LocationSubPredicate::location, ::LocationSubPredicate, "LocationSubPredicate"
		)
	}
}

@Serializable(with = MovementSubPredicate.Companion.Serializer::class)
data class MovementSubPredicate(val movement: Movement) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<MovementSubPredicate, Movement>(
			Movement.serializer(), MovementSubPredicate::movement, ::MovementSubPredicate, "MovementSubPredicate"
		)
	}
}

@Serializable(with = MovementAffectedBySubPredicate.Companion.Serializer::class)
data class MovementAffectedBySubPredicate(val location: Location) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<MovementAffectedBySubPredicate, Location>(
			Location.serializer(), MovementAffectedBySubPredicate::location, ::MovementAffectedBySubPredicate, "MovementAffectedBySubPredicate"
		)
	}
}

@Serializable(with = NbtSubPredicate.Companion.Serializer::class)
data class NbtSubPredicate(val nbt: NbtTag) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<NbtSubPredicate, NbtTag>(NbtAsJsonSerializer, NbtSubPredicate::nbt, ::NbtSubPredicate, "NbtSubPredicate")
	}
}

@Serializable(with = PassengerSubPredicate.Companion.Serializer::class)
data class PassengerSubPredicate(val entity: Entity) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<PassengerSubPredicate> by LazySerializer(
			buildClassSerialDescriptor("PassengerSubPredicate"),
			{ InlineAutoSerializer(Entity.serializer(), PassengerSubPredicate::entity, ::PassengerSubPredicate, "PassengerSubPredicate") },
		)
	}
}

@Serializable(with = PeriodicTickSubPredicate.Companion.Serializer::class)
data class PeriodicTickSubPredicate(val ticks: Int) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<PeriodicTickSubPredicate, Int>(
			Int.serializer(), PeriodicTickSubPredicate::ticks, ::PeriodicTickSubPredicate, "PeriodicTickSubPredicate"
		)
	}
}

@Serializable(with = PredicatesSubPredicate.Companion.Serializer::class)
data class PredicatesSubPredicate(val predicates: ItemStackSubPredicates) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<PredicatesSubPredicate, ItemStackSubPredicates>(
			ItemStackSubPredicates.serializer(), PredicatesSubPredicate::predicates, ::PredicatesSubPredicate, "PredicatesSubPredicate"
		)
	}
}

@Serializable(with = SlotsSubPredicate.Companion.Serializer::class)
data class SlotsSubPredicate(val slots: Map<ItemSlot, ItemStack>) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<SlotsSubPredicate, Map<ItemSlot, ItemStack>>(
			EntitySlotsSerializer, SlotsSubPredicate::slots, ::SlotsSubPredicate, "SlotsSubPredicate"
		)
	}
}

@Serializable(with = SteppingOnSubPredicate.Companion.Serializer::class)
data class SteppingOnSubPredicate(val location: Location) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<SteppingOnSubPredicate, Location>(
			Location.serializer(), SteppingOnSubPredicate::location, ::SteppingOnSubPredicate, "SteppingOnSubPredicate"
		)
	}
}

@Serializable(with = TargetedEntitySubPredicate.Companion.Serializer::class)
data class TargetedEntitySubPredicate(val entity: Entity) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<TargetedEntitySubPredicate> by LazySerializer(
			buildClassSerialDescriptor("TargetedEntitySubPredicate"),
			{ InlineAutoSerializer(Entity.serializer(), TargetedEntitySubPredicate::entity, ::TargetedEntitySubPredicate, "TargetedEntitySubPredicate") },
		)
	}
}

@Serializable(with = TeamSubPredicate.Companion.Serializer::class)
data class TeamSubPredicate(val team: String) : EntitySubPredicate() {
	companion object {
		data object Serializer : InlineAutoSerializer<TeamSubPredicate, String>(String.serializer(), TeamSubPredicate::team, ::TeamSubPredicate, "TeamSubPredicate")
	}
}

@Serializable(with = VehicleSubPredicate.Companion.Serializer::class)
data class VehicleSubPredicate(val entity: Entity) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<VehicleSubPredicate> by LazySerializer(
			buildClassSerialDescriptor("VehicleSubPredicate"),
			{ InlineAutoSerializer(Entity.serializer(), VehicleSubPredicate::entity, ::VehicleSubPredicate, "VehicleSubPredicate") },
		)
	}
}

/** Serializes an [ItemSlot]-keyed map the same way [Entity]'s other slot maps are: write-only, keyed by [ItemSlot.asString]. */
internal data object EntitySlotsSerializer : KSerializer<Map<ItemSlot, ItemStack>> {
	override val descriptor = MapSerializer(String.serializer(), ItemStack.serializer()).descriptor

	override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder) = error("Entity slots cannot be deserialized.")

	override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Map<ItemSlot, ItemStack>) {
		val map = value.mapKeys { it.key.asString() }
		MapSerializer(String.serializer(), ItemStack.serializer()).serialize(encoder, map)
	}
}

fun Entity.components(init: ComponentsPatch.() -> Unit) {
	subPredicates += ComponentsSubPredicate(ComponentsPatch().apply(init))
}

fun Entity.distance(init: Distance.() -> Unit = {}) {
	subPredicates += DistanceSubPredicate(Distance().apply(init))
}

fun Entity.effects(block: MutableMap<MobEffectArgument, Effect>.() -> Unit) {
	subPredicates += EffectsSubPredicate(buildMap(block))
}

fun Entity.effects(vararg effects: Pair<MobEffectArgument, Effect>) {
	subPredicates += EffectsSubPredicate(effects.toMap())
}

fun Entity.entityTags(init: EntityTagsPredicate.() -> Unit = {}) {
	subPredicates += EntityTagsSubPredicate(EntityTagsPredicate().apply(init))
}

fun Entity.entityType(vararg types: EntityTypeOrTagArgument) {
	subPredicates += EntityTypeSubPredicate(types.toList())
}

fun Entity.equipment(init: Equipment.() -> Unit = {}) {
	subPredicates += EquipmentSubPredicate(Equipment().apply(init))
}

fun Entity.flags(init: EntityFlags.() -> Unit = {}) {
	subPredicates += FlagsSubPredicate(EntityFlags().apply(init))
}

fun Entity.location(init: Location.() -> Unit = {}) {
	subPredicates += LocationSubPredicate(Location().apply(init))
}

fun Entity.movement(init: Movement.() -> Unit = {}) {
	subPredicates += MovementSubPredicate(Movement().apply(init))
}

fun Entity.movementAffectedBy(init: Location.() -> Unit = {}) {
	subPredicates += MovementAffectedBySubPredicate(Location().apply(init))
}

fun Entity.nbt(block: NbtCompoundBuilder.() -> Unit) {
	subPredicates += NbtSubPredicate(io.github.ayfri.kore.utils.nbt(block))
}

fun Entity.passenger(init: Entity.() -> Unit = {}) {
	subPredicates += PassengerSubPredicate(Entity().apply(init))
}

fun Entity.predicates(block: ItemStackSubPredicates.() -> Unit) {
	subPredicates += PredicatesSubPredicate(ItemStackSubPredicates().apply(block))
}

fun Entity.slots(init: MutableMap<ItemSlot, ItemStack>.() -> Unit) {
	subPredicates += SlotsSubPredicate(buildMap(init))
}

fun Entity.slots(vararg slots: Pair<ItemSlot, ItemStack>) {
	subPredicates += SlotsSubPredicate(slots.toMap())
}

fun Entity.steppingOn(init: Location.() -> Unit = {}) {
	subPredicates += SteppingOnSubPredicate(Location().apply(init))
}

fun Entity.targetedEntity(init: Entity.() -> Unit = {}) {
	subPredicates += TargetedEntitySubPredicate(Entity().apply(init))
}

fun Entity.vehicle(init: Entity.() -> Unit = {}) {
	subPredicates += VehicleSubPredicate(Entity().apply(init))
}

fun Entity.team(team: String) {
	subPredicates += TeamSubPredicate(team)
}

fun Entity.periodicTick(ticks: Int) {
	subPredicates += PeriodicTickSubPredicate(ticks)
}
