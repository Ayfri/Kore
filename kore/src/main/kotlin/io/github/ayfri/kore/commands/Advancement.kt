package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/**
 * Route modifier used by `advancement grant|revoke`.
 *
 * - [FROM]: target advancement plus every advancement that unlocks through it
 * - [ONLY]: only the target advancement (and optionally one of its criteria)
 * - [THROUGH]: every advancement leading to the target, including the target itself
 * - [UNTIL]: every advancement leading up to (but not including) the target
 */
@Serializable(AdvancementRoute.Companion.AdvancementRouteSerializer::class)
enum class AdvancementRoute {
	FROM,
	ONLY,
	THROUGH,
	UNTIL;

	companion object {
		data object AdvancementRouteSerializer : LowercaseSerializer<AdvancementRoute>(entries)
	}
}

/**
 * DSL scope for the `/advancement` command.
 *
 * `/advancement` grants or revokes advancements with a route selector. Use [grant] or [revoke]
 * to target a single advancement, every advancement, or the chain leading to a given node.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
 */
class Advancement(private val fn: Function) {
	/**
	 * Grants [advancement] to [target] following the given [route].
	 *
	 * [criterion] only matters when the route is [AdvancementRoute.ONLY].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grant(
		target: EntityArgument,
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	/**
	 * Grants [advancement] only to [targets], optionally restricted to a single [criterion].
	 *
	 * This is the direct form of `/advancement grant ... only ...`.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grant(
		targets: EntityArgument,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), targets, literal("only"), advancement, literal(criterion)))

	/**
	 * Grants every known advancement to [target].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grantEverything(target: EntityArgument) = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	/**
	 * Revokes [advancement] from [target] following the given [route].
	 *
	 * [criterion] only matters when the route is [AdvancementRoute.ONLY].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revoke(
		target: EntityArgument,
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	/**
	 * Revokes [advancement] only from [targets], optionally restricted to a single [criterion].
	 *
	 * This is the direct form of `/advancement revoke ... only ...`.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revoke(
		targets: EntityArgument,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), targets, literal("only"), advancement, literal(criterion)))

	/**
	 * Revokes every known advancement from [target].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revokeEverything(target: EntityArgument) = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

/**
 * Advancement DSL bound to a pre-selected [target].
 *
 * This wrapper is useful when several advancement operations should target the same entity or
 * selector without repeating it in every call.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
 */
class AdvancementTarget(private val fn: Function, private val target: EntityArgument) {
	/**
	 * Grants [advancement] following the given [route] for the bound target.
	 *
	 * [criterion] only matters when the route is [AdvancementRoute.ONLY].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grant(
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal(route.asArg()), advancement, literal(criterion)))

	/**
	 * Grants [advancement] only to the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grant(
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("grant"), target, literal("only"), advancement, literal(criterion)))

	/**
	 * Grants every known advancement to the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun grantEverything() = fn.addLine(command("advancement", literal("grant"), target, literal("everything")))

	/**
	 * Revokes [advancement] following the given [route] for the bound target.
	 *
	 * [criterion] only matters when the route is [AdvancementRoute.ONLY].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revoke(
		route: AdvancementRoute,
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal(route.asArg()), advancement, literal(criterion)))

	/**
	 * Revokes [advancement] only from the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revoke(
		advancement: AdvancementArgument,
		criterion: String? = null,
	) = fn.addLine(command("advancement", literal("revoke"), target, literal("only"), advancement, literal(criterion)))

	/**
	 * Revokes every known advancement from the bound target.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
	 */
	fun revokeEverything() = fn.addLine(command("advancement", literal("revoke"), target, literal("everything")))
}

/**
 * Opens the [Advancement] DSL without a pre-selected target.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
 */
fun Function.advancement(block: Advancement.() -> Command) = Advancement(this).block()

/**
 * Opens the [AdvancementTarget] DSL bound to [target].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
 */
fun Function.advancement(target: EntityArgument, block: AdvancementTarget.() -> Command) = AdvancementTarget(this, target).block()

/**
 * Returns the reusable top-level advancement DSL.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/advancement)
 */
val Function.advancements get() = Advancement(this)
