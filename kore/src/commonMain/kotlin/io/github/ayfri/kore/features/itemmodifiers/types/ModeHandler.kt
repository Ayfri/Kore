package io.github.ayfri.kore.features.itemmodifiers.types

import kotlinx.serialization.Serializable

/**
 * Implemented by item functions that support list-operation semantics (mode/offset/size).
 * Provides a convenient `mode(..)` helper to configure all three in one call.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 */
interface ModeHandler {
	@Serializable
	var mode: Mode

	@Serializable
	var offset: Int?

	@Serializable
	var size: Int?

	/**
	 * Set the mode of the function.
	 *
	 * - When the mode is [Mode.APPEND], no other parameters are required.
	 * - When the mode is [Mode.INSERT], the [offset] is required.
	 * - When the mode is [Mode.REPLACE_ALL], no other parameters are required.
	 * - When the mode is [Mode.REPLACE_SECTION], the [offset] and [size] are required.
	 */
	fun mode(mode: Mode, offset: Int? = null, size: Int? = null) = when (mode) {
		Mode.REPLACE_ALL -> {
			this.mode = mode
			this.offset = null
			this.size = null
		}

		Mode.REPLACE_SECTION -> {
			this.mode = mode
			this.offset = offset
			this.size = size
		}

		Mode.INSERT -> {
			this.mode = mode
			this.offset = offset
			this.size = null
		}

		Mode.APPEND -> {
			this.mode = mode
			this.offset = null
			this.size = null
		}
	}
}
