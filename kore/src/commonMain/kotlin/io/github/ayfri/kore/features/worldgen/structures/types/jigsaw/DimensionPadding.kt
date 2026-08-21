package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * The empty space an [io.github.ayfri.kore.features.worldgen.structures.types.Jigsaw] structure keeps between itself
 * and the world bounds, so an ancient city never pokes through the bedrock floor.
 *
 * Set it through [io.github.ayfri.kore.features.worldgen.structures.types.dimensionPadding], which writes a bare
 * number when only [top] is given, vanilla then applying it to both directions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property top Blocks kept free above the structure.
 * @property bottom Blocks kept free below the structure, `null` reusing [top].
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = DimensionPadding.Companion.DimensionPaddingSerializer::class)
data class DimensionPadding(
	var top: Int,
	var bottom: Int? = null,
) {
	companion object {
		data object DimensionPaddingSerializer :
			SinglePropertySimplifierSerializer<DimensionPadding>(generatedSerializer(), "top")
	}
}
