package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class OpenUrl(
	var url: String,
) : Action(), ClickEvent, DialogAction

/** Opens the given url in the default browser. */
fun ActionWrapper<*>.openUrl(url: String) = apply { action = OpenUrl(url) }
