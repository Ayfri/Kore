package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.arguments.colors.color
import kotlinx.serialization.Serializable

@Serializable
data class BiomeEffects(
	@Serializable(ColorAsDecimalSerializer::class) var waterColor: Color = color(4159204),
	@Serializable(ColorAsDecimalSerializer::class) var grassColor: Color? = null,
	@Serializable(ColorAsDecimalSerializer::class) var foliageColor: Color? = null,
	@Serializable(ColorAsDecimalSerializer::class) var dryFoliageColor: Color? = null,
	var grassColorModifier: GrassColorModifier? = null,
)
