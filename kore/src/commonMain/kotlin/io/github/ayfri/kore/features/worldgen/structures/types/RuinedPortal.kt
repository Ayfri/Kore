package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places a ruined portal, a broken nether portal frame surrounded by rubble and a loot chest.
 *
 * The structure needs at least one [setup], which is what decides where the portal lands and how weathered it looks.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property setups The weighted variants one is drawn from per instance, which vanilla requires to be non-empty.
 */
@Serializable
data class RuinedPortal(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var setups: List<RuinedPortalSetup> = emptyList(),
) : StructureType()

/**
 * One variant of a [RuinedPortal], declared with [setup].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property placement Where the portal is put, which also decides the height it is looked up at.
 * @property airPocketProbability Between `0.0` and `1.0`, the chance the portal is wrapped in an air pocket.
 * @property mossiness Between `0.0` and `1.0`, how much of the rubble is replaced by its mossy variant.
 * @property overgrown Whether vegetation grows over the portal.
 * @property vines Whether vines hang from the portal frame.
 * @property canBeCold Whether the portal may generate in a cold biome, wrapped in snow and ice.
 * @property replaceWithBlackstone Whether the stone blocks are swapped for blackstone ones, as in the Nether.
 * @property weight The relative chance this setup is the one drawn, against the other setups of the structure.
 */
@Serializable
data class RuinedPortalSetup(
	var placement: RuinedPortalPlacement = RuinedPortalPlacement.ON_LAND_SURFACE,
	var airPocketProbability: Float = 0.5f,
	var mossiness: Float = 0.2f,
	var overgrown: Boolean = false,
	var vines: Boolean = false,
	var canBeCold: Boolean = false,
	var replaceWithBlackstone: Boolean = false,
	var weight: Float = 1f,
)

/**
 * Creates a `ruined_portal` structure, configured in [init].
 *
 * ```kotlin
 * structures {
 *     ruinedPortal("my_ruined_portal") {
 *         biomes(Biomes.DESERT)
 *
 *         setup(RuinedPortalPlacement.PARTLY_BURIED, mossiness = 0.0f, weight = 1f)
 *         setup(RuinedPortalPlacement.ON_LAND_SURFACE, overgrown = true, vines = true, weight = 0.5f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.ruinedPortal(
	fileName: String = "ruined_portal",
	init: RuinedPortal.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, RuinedPortal().apply(init))

/** Adds a [RuinedPortalSetup] variant to the structure, the remaining fields being reachable through [init]. */
fun RuinedPortal.setup(
	placement: RuinedPortalPlacement = RuinedPortalPlacement.ON_LAND_SURFACE,
	airPocketProbability: Float = 0.5f,
	mossiness: Float = 0.2f,
	overgrown: Boolean = false,
	vines: Boolean = false,
	canBeCold: Boolean = false,
	replaceWithBlackstone: Boolean = false,
	weight: Float = 1f,
	init: RuinedPortalSetup.() -> Unit = {},
) {
	setups += RuinedPortalSetup(
		placement,
		airPocketProbability,
		mossiness,
		overgrown,
		vines,
		canBeCold,
		replaceWithBlackstone,
		weight,
	).apply(init)
}
