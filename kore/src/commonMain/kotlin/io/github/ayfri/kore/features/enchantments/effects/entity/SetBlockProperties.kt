package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.generated.arguments.types.GameEventArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Overrides [properties] on the block at the target position, leaving the block itself in place.
 *
 * Properties the block does not have are ignored.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#set_block_properties
 *
 * @property properties The block state properties to override, by name.
 * @property offset The `[X, Y, Z]` block offset applied to the target position, `[0, 0, 0]` when `null`.
 * @property triggerGameEvent The game event emitted at the position, none when `null`.
 */
@Serializable
data class SetBlockProperties(
	var properties: Map<String, String> = emptyMap(),
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var triggerGameEvent: GameEventArgument? = null,
) : EntityEffect()

/** Changes the block offset by [x], [y] and [z] from the target position. */
fun SetBlockProperties.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

/** Sets [SetBlockProperties.properties], the block state properties to override. */
fun SetBlockProperties.properties(vararg properties: Pair<String, String>) {
	this.properties = properties.toMap()
}

/** Sets [SetBlockProperties.properties] to the block state properties collected in [block]. */
fun SetBlockProperties.properties(block: MutableMap<String, String>.() -> Unit) {
	properties = buildMap(block)
}
