package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.PointOfInterestTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.ConfiguredStructureOrTagArgument

/** Finds the closest matching biome and reports its coordinates in chat. */
fun Function.locateBiome(biome: BiomeOrTagArgument) = addLine(command("locate", literal("biome"), biome))

/** Finds the closest matching configured structure and reports its coordinates in chat. */
fun Function.locateStructure(structure: ConfiguredStructureOrTagArgument) =
	addLine(command("locate", literal("structure"), structure))

/** Finds the closest point of interest of the given type or tag. */
fun Function.locatePointOfInterest(pointOfInterest: PointOfInterestTypeOrTagArgument) =
	addLine(command("locate", literal("poi"), pointOfInterest))
