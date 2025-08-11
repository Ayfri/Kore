package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureSetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven structure set controlling global structure placement.
 *
 * Groups configured structures with weights and defines high-level placement (random spread or
 * concentric rings) across the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Structure_set
 */
@Serializable
data class StructureSet(
	@Transient
	override var fileName: String = "structure_set",
	var structures: List<Structure> = emptyList(),
	var placement: Placement,
) : Generator("worldgen/structure_set") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a structure set using a builder block (defaults to random-spread placement).
 *
 * Produces `data/<namespace>/worldgen/structure_set/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Structure_set
 */
fun DataPack.structureSet(
	fileName: String = "structure_set",
	init: StructureSet.() -> Unit = {},
): StructureSetArgument {
	val structureSet = StructureSet(fileName, placement = RandomSpreadPlacement()).apply(init)
	structureSets += structureSet
	return StructureSetArgument(fileName, structureSet.namespace ?: name)
}

/** Adds a structure to the structure set. */
fun StructureSet.structure(structure: ConfiguredStructureArgument, weight: Int = 1) = apply {
	structures += Structure(structure, weight)
}

/**
 * Sets the placement to concentric rings, aka rings with the same center but different radius.
 *
 * @param distance - The distance between the center and the first ring.
 * @param spread - The spread of the rings.
 * @param count - The number of rings.
 */
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

/**
 * Sets the placement to random spread.
 *
 * @param spreadType - The type of spread.
 * @param spacing - The spacing between the structures.
 * @param separation - The separation between the structures.
 */
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
