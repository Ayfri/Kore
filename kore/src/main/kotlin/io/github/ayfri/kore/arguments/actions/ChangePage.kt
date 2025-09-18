package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class ChangePage(
	var page: Int,
) : Action(), ClickEvent, DialogAction

/** Change the current page of an open book, does nothing if no book is open. */
fun ActionWrapper<*>.changePage(page: Int) = apply { action = ChangePage(page) }
