package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class Custom(
	var id: String,
	var payload: String? = null,
) : ClickEvent()

fun ActionWrapper<*>.custom(id: String, payload: String? = null) = apply { action = Custom(id, payload) }
