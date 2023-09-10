package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.AdvancementArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

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
		target: EntityArgument,
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun grant(
		targets: EntityArgument,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), targets, literal("only"), advancement, literal(criterion)))

	fun grantEverything(target: EntityArgument) = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	fun revoke(
		target: EntityArgument,
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun revoke(
		targets: EntityArgument,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), targets, literal("only"), advancement, literal(criterion)))

	fun revokeEverything(target: EntityArgument) = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

class AdvancementTarget(private val fn: Function, private val target: EntityArgument) {
	fun grant(
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun grant(
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal("only"), advancement, literal(criterion)))

	fun grantEverything() = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	fun revoke(
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	fun revoke(
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal("only"), advancement, literal(criterion)))

	fun revokeEverything() = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

fun Function.advancement(block: Advancement.() -> Command) = Advancement(this).block()
fun Function.advancement(target: EntityArgument, block: AdvancementTarget.() -> Command) = AdvancementTarget(this, target).block()

val Function.advancements get() = Advancement(this)
