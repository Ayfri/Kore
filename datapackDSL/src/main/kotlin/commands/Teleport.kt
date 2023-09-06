package commands

import arguments.maths.Vec3
import arguments.types.EntityArgument
import arguments.types.literals.RotationArgument
import arguments.types.literals.literal
import commands.execute.Anchor
import functions.Function
import utils.asArg

fun Function.teleport(destination: EntityArgument) = addLine(command("teleport", destination))
fun Function.teleport(target: EntityArgument, destination: EntityArgument) =
	addLine(command("teleport", target, destination))

fun Function.teleport(location: Vec3) = addLine(command("teleport", location))
fun Function.teleport(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) =
	addLine(command("teleport", target, location, rotation))

fun Function.teleport(target: EntityArgument, location: Vec3, facing: Vec3) =
	addLine(command("teleport", target, location, literal("facing"), facing))

fun Function.teleport(
	target: EntityArgument,
	location: Vec3,
	facing: EntityArgument,
	facingAnchor: Anchor,
) = addLine(command("teleport", target, location, literal("facing"), facing, literal(facingAnchor.asArg())))

fun Function.tp(destination: EntityArgument) = teleport(destination)
fun Function.tp(target: EntityArgument, destination: EntityArgument) = teleport(target, destination)
fun Function.tp(location: Vec3) = teleport(location)
fun Function.tp(target: EntityArgument, location: Vec3, rotation: RotationArgument? = null) = teleport(target, location, rotation)
fun Function.tp(target: EntityArgument, location: Vec3, facing: Vec3) = teleport(target, location, facing)
fun Function.tp(target: EntityArgument, location: Vec3, facing: EntityArgument, facingAnchor: Anchor) =
	teleport(target, location, facing, facingAnchor)
