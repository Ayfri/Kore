package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * The variant of an [io.github.ayfri.kore.features.worldgen.structures.types.Mineshaft], changing the wood it is built
 * from and how it is carved into the terrain.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable(with = MineshaftType.Companion.MineshaftTypeSerializer::class)
enum class MineshaftType {
	/** Dark oak mineshaft, generating much closer to the surface, as in the badlands. */
	MESA,

	/** Oak mineshaft, generating deep underground. */
	NORMAL;

	companion object {
		data object MineshaftTypeSerializer : LowercaseSerializer<MineshaftType>(entries)
	}
}
