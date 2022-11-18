package commands

import arguments.Argument
import arguments.Color
import arguments.bool
import arguments.literal
import arguments.nbt
import functions.Function
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag
import serializers.CamelcaseSerializer

@Serializable(Visibility.Companion.NametagVisibilitySerializer::class)
enum class Visibility {
	ALWAYS,
	HIDE_FOR_OTHER_TEAMS,
	HIDE_FOR_OWN_TEAM,
	NEVER;
	
	companion object {
		val values = values()
		
		object NametagVisibilitySerializer : CamelcaseSerializer<Visibility>(values)
	}
}

@Serializable(CollisionRule.Companion.CollisionRuleSerializer::class)
enum class CollisionRule {
	ALWAYS,
	PUSH_OTHER_TEAMS,
	PUSH_OWN_TEAM,
	NEVER;
	
	companion object {
		val values = values()
		
		object CollisionRuleSerializer : CamelcaseSerializer<CollisionRule>(values)
	}
}

class Modify(private val fn: Function, val team: String) {
	fun collisionRule(rule: CollisionRule) = fn.addLine(command("team", literal("modify"), literal(team), literal("collisionRule"), literal(rule.asArg())))
	fun color(color: Color) = fn.addLine(command("team", literal("modify"), literal(team), literal("color"), literal(color.asArg())))
	fun deathMessageVisibility(visibility: Visibility) = fn.addLine(command("team", literal("modify"), literal(team), literal("deathMessageVisibility"), literal(visibility.asArg())))
	fun displayName(name: NbtTag) = fn.addLine(command("team", literal("modify"), literal(team), literal("displayName"), fn.nbt(name)))
	fun friendlyFire(allowed: Boolean) = fn.addLine(command("team", literal("modify"), literal(team), literal("friendlyFire"), bool(allowed)))
	fun nametagVisibility(visibility: Visibility) = fn.addLine(command("team", literal("modify"), literal(team), literal("nametagVisibility"), literal(visibility.asArg())))
	fun prefix(prefix: NbtTag) = fn.addLine(command("team", literal("modify"), literal(team), literal("prefix"), fn.nbt(prefix)))
	fun seeFriendlyInvisibles(allowed: Boolean) = fn.addLine(command("team", literal("modify"), literal(team), literal("seeFriendlyInvisibles"), bool(allowed)))
	fun suffix(suffix: NbtTag) = fn.addLine(command("team", literal("modify"), literal(team), literal("suffix"), fn.nbt(suffix)))
}

class Team(private val fn: Function) {
	fun add(team: String, displayName: NbtTag? = null) = fn.addLine(command("team", literal("add"), literal(team), fn.nbt(displayName)))
	fun empty(team: String) = fn.addLine(command("team", literal("empty"), literal(team)))
	fun join(team: String, entity: Argument.ScoreHolder) = fn.addLine(command("team", literal("join"), literal(team), entity))
	fun leave(entity: Argument.ScoreHolder) = fn.addLine(command("team", literal("leave"), entity))
	fun list(team: String? = null) = fn.addLine(command("team", literal("list"), literal(team)))
	fun modify(team: String, block: Modify.() -> Unit) = Modify(fn, team).block()
	fun remove(team: String) = fn.addLine(command("team", literal("remove"), literal(team)))
}

val Function.teams get() = Team(this)
fun Function.team(block: Team.() -> Unit) = Team(this).block()
