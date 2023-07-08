package commands

import arguments.Argument
import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(AdvancementRoute.Companion.AdvancementRouteSerializer::class)
enum class AdvancementRoute {
	ONLY,
	THROUGH,
	UNTIL;

	companion object {
		data object AdvancementRouteSerializer : LowercaseSerializer<AdvancementRoute>(entries)
	}
}

class Advancement(private val fn: Function) {
	fun grant(
		target: Argument.Entity,
		route: AdvancementRoute,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun grant(
		targets: Argument.Entity,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), targets, literal("only"), advancement, literal(criterion)))

	fun grantEverything(target: Argument.Entity) = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	fun revoke(
		target: Argument.Entity,
		route: AdvancementRoute,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun revoke(
		targets: Argument.Entity,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), targets, literal("only"), advancement, literal(criterion)))

	fun revokeEverything(target: Argument.Entity) = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

class AdvancementTarget(private val fn: Function, private val target: Argument.Entity) {
	fun grant(
		route: AdvancementRoute,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun grant(
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal("only"), advancement, literal(criterion)))

	fun grantEverything() = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	fun revoke(
		route: AdvancementRoute,
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun revoke(
		advancement: Argument.Advancement,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal("only"), advancement, literal(criterion)))

	fun revokeEverything() = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

fun Function.advancement(block: Advancement.() -> Command) = Advancement(this).block()
fun Function.advancement(target: Argument.Entity, block: AdvancementTarget.() -> Command) = AdvancementTarget(this, target).block()

val Function.advancements get() = Advancement(this)
