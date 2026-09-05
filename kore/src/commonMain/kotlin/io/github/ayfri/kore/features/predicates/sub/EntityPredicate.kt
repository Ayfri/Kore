package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject

/**
 * An entity predicate, matching an entity against a set of identifier-keyed [EntitySubPredicate] entries.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(with = EntityPredicate.Companion.EntityPredicateSerializer::class)
data class EntityPredicate(val subPredicates: MutableList<EntitySubPredicate> = mutableListOf()) {
	companion object {
		data object EntityPredicateSerializer : KSerializer<EntityPredicate> {
			private val mapSerializer = MapSerializer(String.serializer(), EntitySubPredicate.Companion.EntitySubPredicateSerializer)
			override val descriptor = mapSerializer.descriptor

			override fun serialize(encoder: Encoder, value: EntityPredicate) {
				val map = value.subPredicates.associateBy { EntitySubPredicate.Companion.EntitySubPredicateSerializer.getContentName(it) }
				encoder.encodeSerializableValue(mapSerializer, map)
			}

			override fun deserialize(decoder: Decoder): EntityPredicate {
				require(decoder is JsonDecoder) { "Entity predicate can only be deserialized as Json" }
				val subPredicates = decoder.decodeJsonElement().jsonObject.map { (key, element) ->
					EntitySubPredicate.Companion.EntitySubPredicateSerializer.deserializeJsonElement(decoder.json, key, element)
				}
				return EntityPredicate(subPredicates.toMutableList())
			}
		}
	}
}

/**
 * Matches the boolean state flags of an entity, keyed under `minecraft:flags`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class EntityFlagsPredicate(
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

/** Creates an [EntityPredicate]. */
fun entityPredicate(init: EntityPredicate.() -> Unit = {}) = EntityPredicate().apply(init)
