package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class RuinedPortal(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
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
): StructureArgument {
	val ruinedPortal = RuinedPortal(step = step).apply(init)
	dp.structures += Structure(filename, ruinedPortal)
	return StructureArgument(filename, ruinedPortal.namespace ?: dp.name)
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
