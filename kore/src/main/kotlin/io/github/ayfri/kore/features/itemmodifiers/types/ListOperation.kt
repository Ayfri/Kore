package io.github.ayfri.kore.features.itemmodifiers.types

import kotlinx.serialization.Serializable

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
