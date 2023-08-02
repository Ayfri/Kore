package features.worldgen.structures.types

import arguments.types.BiomeOrTagArgument
import features.worldgen.biome.types.Spawners
import features.worldgen.structures.*
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class RuinedPortal(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: Spawners = Spawners(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var setups: List<RuinedPortalSetup> = emptyList(),
) : StructureType()

@Serializable
data class RuinedPortalSetup(
	var placement: RuinedPortalPlacement,
	var airPocketProbability: Float,
	var mossiness: Float,
	var overgrown: Boolean,
	var vines: Boolean,
	var canBeCold: Boolean,
	var replaceWithBlackstone: Boolean,
	var weight: Float,
)

fun StructuresBuilder.ruinedPortal(
	filename: String = "ruined_portal",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	init: RuinedPortal.() -> Unit = {},
): RuinedPortal {
	val ruinedPortal = RuinedPortal(step = step).apply(init)
	dp.structures += Structure(filename, ruinedPortal)
	return ruinedPortal
}

fun RuinedPortal.setup(
	placement: RuinedPortalPlacement = RuinedPortalPlacement.ON_LAND_SURFACE,
	airPocketProbability: Float = 0.5f,
	mossiness: Float = 0.2f,
	overgrown: Boolean = false,
	vines: Boolean = false,
	canBeCold: Boolean = false,
	replaceWithBlackstone: Boolean = false,
	weight: Float = 1f,
	block: RuinedPortalSetup.() -> Unit = {},
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
	).apply(block)
}
