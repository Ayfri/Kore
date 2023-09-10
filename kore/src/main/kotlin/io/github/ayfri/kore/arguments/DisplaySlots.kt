package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.colors.FormattingColor

fun interface DisplaySlot : Argument

object DisplaySlots {
	val belowName = DisplaySlot { "below_name" }
	val list = DisplaySlot { "list" }
	val sidebar = DisplaySlot { "sidebar" }
	fun sidebarTeam(color: FormattingColor) = DisplaySlot { "sidebar.team.${color.name}" }
}
