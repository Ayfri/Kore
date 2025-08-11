package io.github.ayfri.kore.pack

import kotlinx.serialization.Serializable

/**
 * Represents the metadata of a Minecraft pack.
 *
 * @property pack The details of the pack.
 * @property features The features of the pack.
 * @property filter The pack.filter applied to the pack, if any.
 *
 * JSON format reference: https://minecraft.wiki/w/Pack.mcmeta
 */
@Serializable
data class PackMCMeta(
	val pack: Pack,
	val features: Features? = null,
	val filter: Filter? = null,
)
