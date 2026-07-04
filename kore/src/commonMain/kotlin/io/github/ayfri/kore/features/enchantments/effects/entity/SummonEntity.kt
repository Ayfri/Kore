package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SummonEntity(
	var entity: InlinableList<EntityTypeOrTagArgument> = emptyList(),
	var joinTeam: Boolean? = null,
) : EntityEffect()

fun SummonEntity.entity(vararg entity: EntityTypeOrTagArgument) {
	this.entity = entity.toList()
}
