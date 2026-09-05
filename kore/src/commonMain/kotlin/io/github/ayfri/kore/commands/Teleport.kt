package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg

/**
 * Teleports the executing entity to the [destination] entity and copies its rotation.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.teleport(destination: EntityArgument) = addLine(command("teleport", destination))

/**
 * Teleports [target] to the [destination] entity and copies its rotation.
 *
 * This is the entity-to-entity form of `/teleport`: the target is moved to the
 * destination entity and adopts its orientation.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.teleport(target: EntityArgument, destination: EntityArgument) =
	addLine(command("teleport", target, destination))

/**
 * Teleports the executing entity to the given [location].
 *
 * This is the coordinate form of `/teleport`: the command source is moved to the
 * destination coordinates without targeting another entity.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.teleport(location: Vec3) = addLine(command("teleport", location))

/**
 * Teleports [target] to [location] with an optional explicit [rotation].
 *
 * Use this overload when you want to move an entity to coordinates and optionally
 * force a final orientation.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.teleport(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) =
	addLine(command("teleport", target, location, rotation))

/**
 * Teleports [target] to [location] and makes it face the given [facing] position.
 *
 * This uses the `facing <position>` form of `/teleport`, which keeps the target at
 * the destination coordinates and rotates it toward the supplied point.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.teleport(target: EntityArgument, location: Vec3, facing: Vec3) =
	addLine(command("teleport", target, location, literal("facing"), facing))

/**
 * Teleports [target] to [location] and makes it face the given [facing] entity.
 *
 * An optional [facingAnchor] controls whether the target faces the entity's eyes or feet.
 * This is the entity-facing form of `/teleport`, and it is the most flexible overload
 * when the destination should look at another entity.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
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

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(destination: EntityArgument) = teleport(destination)

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(target: EntityArgument, destination: EntityArgument) = teleport(target, destination)

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(location: Vec3) = teleport(location)

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) = teleport(target, location, rotation)

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(target: EntityArgument, location: Vec3, facing: Vec3) = teleport(target, location, facing)

/** Alias for [teleport].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/teleport)
 */
fun Function.tp(target: EntityArgument, location: Vec3, facing: EntityArgument, facingAnchor: Anchor? = null) =
	teleport(target, location, facing, facingAnchor)
