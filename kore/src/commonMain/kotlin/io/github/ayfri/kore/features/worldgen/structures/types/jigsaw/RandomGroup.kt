package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import kotlinx.serialization.Serializable

/**
 * Draws one of [groups] by weight and applies every alias it holds at once.
 *
 * Grouping the aliases keeps a themed variant coherent: drawing the desert group rewires the houses and the streets to
 * their desert pools together, instead of mixing them with the plains ones.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property groups The weighted alias groups one is drawn from.
 */
@Serializable
data class RandomGroup(
	var groups: List<WeightedGroupEntry> = emptyList(),
) : PoolAlias()
