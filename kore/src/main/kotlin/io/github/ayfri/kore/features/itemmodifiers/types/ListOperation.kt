package io.github.ayfri.kore.features.itemmodifiers.types

import kotlinx.serialization.Serializable

/**
 * Generic list operation wrapper used by several item functions to control how lists are edited
 * (replace all, insert at offset, replace section, append).
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 */
@Serializable
data class ListOperation<T>(
	var values: List<T>,
	@Serializable
	override var mode: Mode = Mode.REPLACE_ALL,
	@Serializable
	override var offset: Int? = null,
	@Serializable
	override var size: Int? = null,
) : ModeHandler
