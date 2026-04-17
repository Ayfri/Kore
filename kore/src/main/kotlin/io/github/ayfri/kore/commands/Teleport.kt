package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg

/** Teleports the executing entity to the [destination] entity. */
fun Function.teleport(destination: EntityArgument) = addLine(command("teleport", destination))

/** Teleports [target] to the [destination] entity. */
fun Function.teleport(target: EntityArgument, destination: EntityArgument) =
	addLine(command("teleport", target, destination))

/** Teleports the executing entity to the given [location]. */
fun Function.teleport(location: Vec3) = addLine(command("teleport", location))

/** Teleports [target] to [location] with an optional [rotation]. */
fun Function.teleport(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) =
	addLine(command("teleport", target, location, rotation))

/** Teleports [target] to [location] and makes it face the given [facing] position. */
fun Function.teleport(target: EntityArgument, location: Vec3, facing: Vec3) =
	addLine(command("teleport", target, location, literal("facing"), facing))

/**
 * Teleports [target] to [location] and makes it face the given [facing] entity.
 *
 * An optional [facingAnchor] controls whether the target faces the entity's eyes or feet.
 */
fun Function.teleport(
	target: EntityArgument,
	location: Vec3,
	facing: EntityArgument,
	facingAnchor: Anchor? = null,
) = addLine(
	command(
		"teleport",
		target,
		location,
		literal("facing"),
		literal("entity"),
		facing,
		literal(facingAnchor?.asArg())
	)
)

/** Alias for [teleport]. */
fun Function.tp(destination: EntityArgument) = teleport(destination)

/** Alias for [teleport]. */
fun Function.tp(target: EntityArgument, destination: EntityArgument) = teleport(target, destination)

/** Alias for [teleport]. */
fun Function.tp(location: Vec3) = teleport(location)

/** Alias for [teleport]. */
fun Function.tp(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) = teleport(target, location, rotation)

/** Alias for [teleport]. */
fun Function.tp(target: EntityArgument, location: Vec3, facing: Vec3) = teleport(target, location, facing)

/** Alias for [teleport]. */
fun Function.tp(target: EntityArgument, location: Vec3, facing: EntityArgument, facingAnchor: Anchor? = null) =
	teleport(target, location, facing, facingAnchor)
