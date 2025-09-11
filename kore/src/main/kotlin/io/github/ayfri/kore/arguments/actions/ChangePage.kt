package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class ChangePage(
	var page: Int,
) : ClickEvent()

fun ActionWrapper<*>.changePage(page: Int) = apply { action = ChangePage(page) }
