package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class OpenUrl(
	var url: String,
) : Action(), ClickEvent, DialogAction

fun ActionWrapper<*>.openUrl(url: String) = apply { action = OpenUrl(url) }
