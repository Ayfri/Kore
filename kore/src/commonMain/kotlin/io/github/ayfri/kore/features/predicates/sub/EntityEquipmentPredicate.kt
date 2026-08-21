package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Matches the items an entity has equipped, keyed under `minecraft:equipment`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class EntityEquipmentPredicate(
	var body: ItemStackPredicate? = null,
	var chest: ItemStackPredicate? = null,
	var feet: ItemStackPredicate? = null,
	var head: ItemStackPredicate? = null,
	var legs: ItemStackPredicate? = null,
	@SerialName("mainhand")
	var mainHand: ItemStackPredicate? = null,
	@SerialName("offhand")
	var offHand: ItemStackPredicate? = null,
	var saddle: ItemStackPredicate? = null,
)

/** Creates an [EntityEquipmentPredicate]. */
fun equipmentPredicate(init: EntityEquipmentPredicate.() -> Unit = {}) = EntityEquipmentPredicate().apply(init)
