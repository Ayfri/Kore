package io.github.ayfri.kore.arguments.chatcomponents.click

import kotlinx.serialization.Serializable

@Serializable
data class Custom(var id: String, var payload: String? = null) : ClickEvent()

fun ClickEventContainer.custom(id: String, payload: String? = null) = apply { event = Custom(id, payload) }
