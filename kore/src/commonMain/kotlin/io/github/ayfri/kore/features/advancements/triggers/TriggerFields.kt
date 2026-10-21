package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.LocationOrPredicates
import io.github.ayfri.kore.features.predicates.sub.DamagePredicate
import io.github.ayfri.kore.features.predicates.sub.DamageSourcePredicate
import io.github.ayfri.kore.features.predicates.sub.ItemStackPredicate
import io.github.ayfri.kore.serializers.InlinableList

/** A trigger with a `blocks` field. */
interface BlocksTrigger {
	var blocks: InlinableList<BlockOrTagArgument>?
}

/** Match any of the given [blocks], block IDs or block tags. */
fun BlocksTrigger.blocks(vararg blocks: BlockOrTagArgument) {
	this.blocks = blocks.toList()
}

/** A trigger with a `damage` field. */
interface DamageTrigger {
	var damage: DamagePredicate?
}

/** Set the damage constraints. */
fun DamageTrigger.damage(block: DamagePredicate.() -> Unit) {
	damage = DamagePredicate().apply(block)
}

/** A trigger with an `entity` field. */
interface EntityTrigger {
	var entity: EntityOrPredicates?
}

/** Set the entity constraints. */
fun EntityTrigger.entity(block: EntityOrPredicates.() -> Unit) {
	entity = EntityOrPredicates().apply(block)
}

/** A trigger with an `item` field. */
interface ItemTrigger {
	var item: ItemStackPredicate?
}

/** Set the item constraints. */
fun ItemTrigger.item(block: ItemStackPredicate.() -> Unit) {
	item = ItemStackPredicate().apply(block)
}

/** A trigger with a `killing_blow` field. */
interface KillingBlowTrigger {
	var killingBlow: DamageSourcePredicate?
}

/** Set the killing blow constraints. */
fun KillingBlowTrigger.killingBlow(block: DamageSourcePredicate.() -> Unit) {
	killingBlow = DamageSourcePredicate().apply(block)
}

/** A trigger with a `location` field. */
interface LocationTrigger {
	var location: LocationOrPredicates?
}

/** Set the location constraints. */
fun LocationTrigger.location(block: LocationOrPredicates.() -> Unit) {
	location = LocationOrPredicates().apply(block)
}
