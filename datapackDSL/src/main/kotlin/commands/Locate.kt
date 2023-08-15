package commands

import arguments.types.BiomeOrTagArgument
import arguments.types.ConfiguredStructureOrTagArgument
import arguments.types.literals.literal
import functions.Function

fun Function.locateBiome(biome: BiomeOrTagArgument) = addLine(command("locate", literal("biome"), biome))
fun Function.locateStructure(structure: ConfiguredStructureOrTagArgument) = addLine(command("locate", literal("structure"), structure))
fun Function.locatePointOfInterest(pointOfInterest: String) = addLine(command("locate", literal("poi"), literal(pointOfInterest)))
