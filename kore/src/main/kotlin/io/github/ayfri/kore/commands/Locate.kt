package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

fun Function.locateBiome(biome: BiomeOrTagArgument) = addLine(command("locate", literal("biome"), biome))
fun Function.locateStructure(structure: ConfiguredStructureOrTagArgument) = addLine(command("locate", literal("structure"), structure))
fun Function.locatePointOfInterest(pointOfInterest: String) = addLine(command("locate", literal("poi"), literal(pointOfInterest)))
