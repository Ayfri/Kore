package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.arguments.colors.FormattingColor

/**
 * Represents a display slot for scoreboard objectives.
 *
 * Display slots control where and how objectives are shown to players:
 * - 'list': Shows scores in the player list/tab menu.
 * - 'sidebar': Shows scores on the right side of the screen.
 * - 'sidebar.team.<color>': Shows scores in the sidebar for players on a team with the given color.
 * - 'below_name': Shows scores below a player's name tag.
 *
 * Only one objective can be shown per slot at a time.
 * Entities' scores are only visible in the sidebar.
 * See: https://minecraft.wiki/w/Scoreboard#Display_slots
 */
fun interface DisplaySlot : Argument

/**
 * Display slots for the client, can be used to display the score of an objective.
 * See: https://minecraft.wiki/w/Scoreboard#Display_slots
 */
data object DisplaySlots {
	/** Shows the score below the player's name tag. */
	val belowName = DisplaySlot { "below_name" }

	/** Shows the score in the player list/tab menu. */
	val list = DisplaySlot { "list" }

	/** Shows the score in the sidebar on the right side of the screen. */
	val sidebar = DisplaySlot { "sidebar" }

	/**
	 * Shows the score in the sidebar for players on a team with the given color.
	 * @param color The team color (e.g., red, blue, green, etc.).
	 */
	fun sidebarTeam(color: FormattingColor) = DisplaySlot { "sidebar.team.${color.name}" }
}
