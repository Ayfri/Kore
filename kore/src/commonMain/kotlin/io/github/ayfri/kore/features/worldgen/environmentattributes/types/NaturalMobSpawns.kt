package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.biome.types.SpawnCost
import io.github.ayfri.kore.features.worldgen.biome.types.Spawners
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import kotlinx.serialization.Serializable

/**
 * The mobs spawned naturally, one weighted list per mob category, and the spawn costs capping their density.
 *
 * Resolved at the spawn position, so it belongs in the attributes of a biome or a dimension type.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/environment-attributes
 * Minecraft Wiki: https://minecraft.wiki/w/Biome_definition
 *
 * @property spawnCosts The energy budget and charge of the mobs whose density is capped.
 * @property spawnsByCategory The weighted spawn entries of each mob category.
 */
@Serializable
data class NaturalMobSpawns(
	var spawnCosts: Map<EntityTypeArgument, SpawnCost> = emptyMap(),
	var spawnsByCategory: Spawners = Spawners(),
) : EnvironmentAttributesType()

/**
 * Sets the natural mob spawns attribute.
 *
 * ```
 * naturalMobSpawns {
 *     spawnsByCategory { monster { spawner(EntityTypes.ZOMBIE, weight = 100, minCount = 4) } }
 * }
 * ```
 * With [EnvironmentAttributeModifier.OVERLAY], the categories and spawn costs set here replace the matching ones of the
 * lower-priority source and the others are kept, while the default override replaces everything.
 */
fun EnvironmentAttributesScope.naturalMobSpawns(
	mod: EnvironmentAttributeModifier.NaturalMobSpawns? = null,
	block: NaturalMobSpawns.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Gameplay.NATURAL_MOB_SPAWNS] = environmentAttributeValue(NaturalMobSpawns().apply(block), mod)
}

/** Sets the spawn costs, one `spawnCost(type, energyBudget, charge)` per mob. */
fun NaturalMobSpawns.spawnCosts(init: MutableMap<EntityTypeArgument, SpawnCost>.() -> Unit) {
	spawnCosts = buildMap(init)
}

/** Sets the spawn entries, one list per mob category. */
fun NaturalMobSpawns.spawnsByCategory(init: Spawners.() -> Unit) {
	spawnsByCategory = Spawners().apply(init)
}
