package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.TemplatePoolArgument
import io.github.ayfri.kore.features.worldgen.HeightMap
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.*
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Jigsaw(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var startPool: TemplatePoolArgument,
	var size: Int = 0,
	var startHeight: HeightProvider = constantAbsolute(0),
	var startJigsawName: String? = null,
	var projectStartToHeightmap: HeightMap? = null,
	var maxDistanceFromCenter: Int = 80,
	var useExpansionHack: Boolean = false,
	var poolAliases: List<PoolAlias>? = null,
	var dimensionPadding: DimensionPadding? = null,
	var liquidSettings: LiquidSettings? = null,
) : StructureType()

fun StructuresBuilder.jigsaw(
	filename: String = "jigsaw",
	step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	startPool: TemplatePoolArgument,
	init: Jigsaw.() -> Unit = {},
): StructureArgument {
	val jigsaw = Jigsaw(step = step, startPool = startPool).apply(init)
	dp.structures += Structure(filename, jigsaw)
	return StructureArgument(filename, jigsaw.namespace ?: dp.name)
}

fun Jigsaw.poolAliases(block: MutableList<PoolAlias>.() -> Unit = {}) {
	poolAliases = buildList(block)
}

fun MutableList<PoolAlias>.directPoolAlias(
	alias: TemplatePoolArgument,
	target: TemplatePoolArgument,
) = add(Direct(alias, target))

fun MutableList<PoolAlias>.randomPoolAlias(
	alias: TemplatePoolArgument,
	targets: List<TemplatePoolArgument> = emptyList(),
) = add(Random(alias, targets))

fun MutableList<PoolAlias>.randomGroupPoolAlias(
	alias: TemplatePoolArgument,
	groups: MutableList<PoolAlias>.() -> Unit = {},
) = add(RandomGroup(alias, buildList(groups)))

fun Jigsaw.dimensionPadding(value: Int) {
	dimensionPadding = DimensionPadding(value)
}

fun Jigsaw.dimensionPadding(top: Int, bottom: Int) {
	dimensionPadding = DimensionPadding(top = top, bottom = bottom)
}
