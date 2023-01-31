package arguments

fun interface SetDisplaySlot : Argument

object DisplaySlot {
	val belowName = SetDisplaySlot { "belowName" }
	val list = SetDisplaySlot { "list" }
	val sidebar = SetDisplaySlot { "sidebar" }
	fun sidebarTeam(color: NamedColor) = SetDisplaySlot { "sidebar.team.${color.name}" }
}
