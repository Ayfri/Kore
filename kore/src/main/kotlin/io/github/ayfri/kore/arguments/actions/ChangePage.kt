package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

/** Turns to [page] in an open book GUI when the component is clicked. Does nothing if no book is currently open. */
@Serializable
data class ChangePage(
	/** 1-based page number to navigate to. */
	var page: Int,
) : Action(), ClickEvent, DialogAction

/** Change the current page of an open book, does nothing if no book is open. */
fun ActionWrapper<*>.changePage(page: Int) = apply { action = ChangePage(page) }
