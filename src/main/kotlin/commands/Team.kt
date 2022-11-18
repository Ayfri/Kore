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
	fun collisionRule(rule: CollisionRule) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("collisionRule"), fn.literal(rule.asArg())))
	fun color(color: Color) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("color"), fn.literal(color.asArg())))
	fun deathMessageVisibility(visibility: Visibility) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("deathMessageVisibility"), fn.literal(visibility.asArg())))
	fun displayName(name: NbtTag) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("displayName"), fn.nbt(name)))
	fun friendlyFire(allowed: Boolean) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("friendlyFire"), fn.bool(allowed)))
	fun nametagVisibility(visibility: Visibility) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("nametagVisibility"), fn.literal(visibility.asArg())))
	fun prefix(prefix: NbtTag) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("prefix"), fn.nbt(prefix)))
	fun seeFriendlyInvisibles(allowed: Boolean) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("seeFriendlyInvisibles"), fn.bool(allowed)))
	fun suffix(suffix: NbtTag) = fn.addLine(command("team", fn.literal("modify"), fn.literal(team), fn.literal("suffix"), fn.nbt(suffix)))
}

class Team(private val fn: Function) {
	fun add(team: String, displayName: NbtTag? = null) = fn.addLine(command("team", fn.literal("add"), fn.literal(team), fn.nbt(displayName)))
	fun empty(team: String) = fn.addLine(command("team", fn.literal("empty"), fn.literal(team)))
	fun join(team: String, entity: Argument.ScoreHolder) = fn.addLine(command("team", fn.literal("join"), fn.literal(team), entity))
	fun leave(entity: Argument.ScoreHolder) = fn.addLine(command("team", fn.literal("leave"), entity))
	fun list(team: String? = null) = fn.addLine(command("team", fn.literal("list"), fn.literal(team)))
	fun modify(team: String, block: Modify.() -> Unit) = Modify(fn, team).block()
	fun remove(team: String) = fn.addLine(command("team", fn.literal("remove"), fn.literal(team)))
}

fun Function.team(block: Team.() -> Unit) = Team(this).block()
