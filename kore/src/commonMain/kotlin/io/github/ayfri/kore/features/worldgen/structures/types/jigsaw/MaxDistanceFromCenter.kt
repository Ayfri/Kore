package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * How far the pieces of an [io.github.ayfri.kore.features.worldgen.structures.types.Jigsaw] structure may grow from its
 * center, cutting off any branch that would reach past it.
 *
 * Set it through [io.github.ayfri.kore.features.worldgen.structures.types.maxDistanceFromCenter], which writes a bare
 * number when only [horizontal] is given.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property horizontal Between `1` and `128` blocks.
 * @property vertical Between `1` and `4064` blocks, `null` meaning the vanilla default of `4064`.
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = MaxDistanceFromCenter.Companion.MaxDistanceFromCenterSerializer::class)
data class MaxDistanceFromCenter(
	var horizontal: Int,
	var vertical: Int? = null,
) {
	companion object {
		data object MaxDistanceFromCenterSerializer :
			SinglePropertySimplifierSerializer<MaxDistanceFromCenter>(generatedSerializer(), "horizontal")
	}
}
