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

class Modify(private val fn: Function, val team: String) {
	fun collisionRule(rule: CollisionRule) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("collisionRule"), literal(rule.asArg())))

	fun color(color: FormattingColor) = fn.addLine(command("team", literal("modify"), literal(team), literal("color"), color))

	fun deathMessageVisibility(visibility: Visibility) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("deathMessageVisibility"), literal(visibility.asArg())))

	fun displayName(name: ChatComponents) = fn.addLine(command("team", literal("modify"), literal(team), literal("displayName"), name))

	fun displayName(name: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("displayName"), textComponent(name, color, block)))

	fun friendlyFire(allowed: Boolean) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("friendlyFire"), bool(allowed)))

	fun nametagVisibility(visibility: Visibility) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("nametagVisibility"), literal(visibility.asArg())))

	fun prefix(prefix: ChatComponents) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("prefix"), prefix.asJsonArg()))

	fun prefix(prefix: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("prefix"), textComponent(prefix, color, block).asJsonArg()))

	fun seeFriendlyInvisibles(allowed: Boolean) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("seeFriendlyInvisibles"), bool(allowed)))

	fun suffix(suffix: ChatComponents) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("suffix"), suffix.asJsonArg()))

	fun suffix(suffix: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("modify"), literal(team), literal("suffix"), textComponent(suffix, color, block).asJsonArg()))
}

class Teams(private val fn: Function) {
	fun add(team: String, displayName: ChatComponents? = null) =
		fn.addLine(command("team", literal("add"), literal(team), displayName?.asJsonArg()))

	fun add(team: String, displayName: String, color: FormattingColor? = null, block: PlainTextComponent.() -> Unit = {}) =
		fn.addLine(command("team", literal("add"), literal(team), textComponent(displayName, color, block).asJsonArg()))

	fun empty(team: String) = fn.addLine(command("team", literal("empty"), literal(team)))
	fun join(team: String, entity: ScoreHolderArgument) = fn.addLine(command("team", literal("join"), literal(team), entity))
	fun leave(entity: ScoreHolderArgument) = fn.addLine(command("team", literal("leave"), entity))
	fun list(team: String? = null) = fn.addLine(command("team", literal("list"), literal(team)))
	fun modify(team: String, block: Modify.() -> Command) = Modify(fn, team).block()
	fun remove(team: String) = fn.addLine(command("team", literal("remove"), literal(team)))
}

val Function.teams get() = Teams(this)
fun Function.teams(block: Teams.() -> Command) = Teams(this).block()
