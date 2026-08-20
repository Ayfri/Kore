package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Summons one of the [entity] types, picked at random, at the position of the affected entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#summon_entity
 *
 * @property entity The entity types and entity type tags one entity is picked from.
 * @property joinTeam Whether the summoned entity joins the team of the enchanted entity, `false` when `null`.
 */
@Serializable
data class SummonEntity(
	var entity: InlinableList<EntityTypeOrTagArgument> = emptyList(),
	var joinTeam: Boolean? = null,
) : EntityEffect()

/** Sets [SummonEntity.entity], the entity types and entity type tags one entity is picked from. */
fun SummonEntity.entity(vararg entity: EntityTypeOrTagArgument) {
	this.entity = entity.toList()
}
