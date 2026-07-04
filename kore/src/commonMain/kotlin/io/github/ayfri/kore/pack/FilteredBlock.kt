package io.github.ayfri.kore.pack

import kotlinx.serialization.Serializable

/**
 * Represents a filtered block.
 *
 * @param namespace The namespace of the filtered block, can be a Regex or a string.
 * @param path The path of the filtered block, can be a Regex or a string.
 */
@Serializable
data class FilteredBlock(
	var namespace: String? = null,
	var path: String? = null,
)
