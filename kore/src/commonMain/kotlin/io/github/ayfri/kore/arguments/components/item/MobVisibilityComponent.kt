package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:mob_visibility` item component, which changes the range at which mobs of the given types detect the entity carrying the item.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#mob_visibility
 */
@Serializable
data class MobVisibilityComponent(
	var targetingEntityTypes: InlinableList<EntityTypeOrTagArgument>,
	var visibility: Float,
) : Component()

/**
 * Scales the range at which the mobs of [targetingEntityTypes] detect the wearer, from `0.0` (reduced to 2 blocks) to `10.0` (tenfold). Stacked items never exceed `10.0`.
 *
 * ```
 * mobVisibility(0f, EntityTypes.ZOMBIE, Tags.EntityType.SKELETONS)
 * ```
 */
fun ComponentsScope.mobVisibility(visibility: Float, vararg targetingEntityTypes: EntityTypeOrTagArgument) = apply {
	require(visibility in 0f..10f) { "Visibility must be between 0 and 10, got $visibility." }
	this[ItemComponentTypes.MOB_VISIBILITY] = MobVisibilityComponent(targetingEntityTypes.toList(), visibility)
}
