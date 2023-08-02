package features.worldgen.structureset

import DataPack
import Generator
import arguments.types.resources.worldgen.ConfiguredStructureArgument
import arguments.types.resources.worldgen.StructureSetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class StructureSet(
	@Transient
	override var fileName: String = "structure_set",
	var structures: List<Structure> = emptyList(),
	var placement: Placement,
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.structureSet(
	fileName: String = "structure_set",
	block: StructureSet.() -> Unit = {},
): StructureSetArgument {
	structureSets += StructureSet(fileName, placement = RandomSpreadPlacement()).apply(block)
	return StructureSetArgument(fileName, name)
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
