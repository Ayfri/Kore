package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureSetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StructureSet(
	@Transient
	override var fileName: String = "structure_set",
	var structures: List<Structure> = emptyList(),
	var placement: Placement,
) : Generator("worldgen/structure_set") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.structureSet(
	fileName: String = "structure_set",
	init: StructureSet.() -> Unit = {},
): StructureSetArgument {
	val structureSet = StructureSet(fileName, placement = RandomSpreadPlacement()).apply(init)
	structureSets += structureSet
	return StructureSetArgument(fileName, structureSet.namespace ?: name)
}

fun StructureSet.structure(structure: ConfiguredStructureArgument, weight: Int = 1) = apply {
	structures += Structure(structure, weight)
}

fun StructureSet.concentricRingsPlacement(
	distance: Int = 0,
	spread: Int = 0,
	count: Int = 1,
	block: ConcentricRingsPlacement.() -> Unit = {},
) = run {
	placement = ConcentricRingsPlacement(
		distance = distance,
		spread = spread,
		count = count,
	).apply(block)
}

fun StructureSet.randomSpreadPlacement(
	spreadType: SpreadType? = null,
	spacing: Int = 1,
	separation: Int = 0,
	block: RandomSpreadPlacement.() -> Unit = {},
) = run {
	placement = RandomSpreadPlacement(
		spreadType = spreadType,
		spacing = spacing,
		separation = separation,
	).apply(block)
}
