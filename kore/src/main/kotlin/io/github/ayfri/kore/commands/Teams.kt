package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.CamelcaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Controls when entity nametags are rendered for players in a team. */
@Serializable(Visibility.Companion.NametagVisibilitySerializer::class)
enum class Visibility {
	ALWAYS,
	HIDE_FOR_OTHER_TEAMS,
	HIDE_FOR_OWN_TEAM,
	NEVER;

	companion object {
		data object NametagVisibilitySerializer : CamelcaseSerializer<Visibility>(entries)
	}
}

/** Controls which entities can push and be pushed by team members. */
@Serializable(CollisionRule.Companion.CollisionRuleSerializer::class)
enum class CollisionRule {
	ALWAYS,
	PUSH_OTHER_TEAMS,
	PUSH_OWN_TEAM,
	NEVER;

	companion object {
		data object CollisionRuleSerializer : CamelcaseSerializer<CollisionRule>(entries)
	}
}

/**
 * Builder for `team modify <team> <option>` subcommands.
 *
 * Each method in this class emits one `team modify` line on the parent [Function].
 */
class Modify(private val fn: Function, val team: String) {
	/** Sets the collision behaviour for members of the team. */
	fun collisionRule(rule: CollisionRule) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("collisionRule"), literal(rule.asArg())))

	/** Sets the team color, used by the nametag, chat prefix and score markers. */
	fun color(color: FormattingColor) = fn.addLine(command("team", literal("modify"), literal(team), literal("color"), color))

	/** Controls who can see a member's death message. */
	fun deathMessageVisibility(visibility: Visibility) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("deathMessageVisibility"), literal(visibility.asArg())))

	/** Sets the display [name] of the team using a rich chat component. */
	fun displayName(name: ChatComponents) = fn.addLine(command("team", literal("modify"), literal(team), literal("displayName"), name))

	/** Convenience overload that builds a text component from [name]. */
	fun displayName(name: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("displayName"), textComponent(name, color, block)))

	/** Enables or disables friendly fire between team members. */
	fun friendlyFire(allowed: Boolean) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("friendlyFire"), bool(allowed)))

	/** Controls who can see a team member's nametag. */
	fun nametagVisibility(visibility: Visibility) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("nametagVisibility"), literal(visibility.asArg())))

	/** Sets a chat prefix for team members. */
	fun prefix(prefix: ChatComponents) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("prefix"), prefix.asJsonArg()))

	/** Convenience overload that builds a text component for the prefix. */
	fun prefix(prefix: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("prefix"), textComponent(prefix, color, block).asJsonArg()))

	/** Controls whether team members can see each other while invisible. */
	fun seeFriendlyInvisibles(allowed: Boolean) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("seeFriendlyInvisibles"), bool(allowed)))

	/** Sets a chat suffix for team members. */
	fun suffix(suffix: ChatComponents) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("suffix"), suffix.asJsonArg()))

	/** Convenience overload that builds a text component for the suffix. */
	fun suffix(suffix: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("suffix"), textComponent(suffix, color, block).asJsonArg()))
}

/**
 * Entry point for the `team` command.
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/team).
 */
class Teams(private val fn: Function) {
	/** Creates a new team. */
	fun add(team: String, displayName: ChatComponents? = null) =
		fn.addLine(command("team", literal("add"), literal(team), displayName?.asJsonArg()))

	/** Creates a new team with a quick plain-text display name. */
	fun add(team: String, displayName: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("add"), literal(team), textComponent(displayName, color, block).asJsonArg()))

	/** Removes every member from [team] without deleting the team. */
	fun empty(team: String) = fn.addLine(command("team", literal("empty"), literal(team)))

	/** Adds the executor to [team]. */
	fun join(team: String) = fn.addLine(command("team", literal("join"), literal(team)))

	/** Adds [entity] (or targets) to [team]. */
	fun join(team: String, entity: ScoreHolderArgument) = fn.addLine(command("team", literal("join"), literal(team), entity))

	/** Removes [entity] from its current team. */
	fun leave(entity: ScoreHolderArgument) = fn.addLine(command("team", literal("leave"), entity))

	/** Lists all teams, or the members of [team] if provided. */
	fun list(team: String? = null) = fn.addLine(command("team", literal("list"), literal(team)))

	/** DSL entry point for `team modify <team>`. */
	fun modify(team: String, block: Modify.() -> Command) = Modify(fn, team).block()

	/** Deletes the given [team]. */
	fun remove(team: String) = fn.addLine(command("team", literal("remove"), literal(team)))
}

val Function.teams get() = Teams(this)
fun Function.teams(block: Teams.() -> Command) = Teams(this).block()
