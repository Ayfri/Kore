package io.github.ayfri.kore.arguments.chatcomponents.click

import kotlinx.serialization.Serializable

@Serializable
data class ChangePage(
	var page: Int,
) : ClickEvent()

fun ClickEventContainer.changePage(page: Int) = apply { event = ChangePage(page) }
