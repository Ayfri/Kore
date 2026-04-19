package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.execute.Anchor
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg

/** Rotates [target] to [rotation]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/rotate) */
fun Function.rotate(target: EntityArgument, rotation: RotationArgument) = addLine(command("rotate", target, rotation))

/** Rotates [target] to face [facingLocation]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/rotate) */
fun Function.rotateFacing(target: EntityArgument, facingLocation: Vec3) =
	addLine(command("rotate", target, literal("facing"), facingLocation))

/** Rotates [target] to face [facingTarget], optionally using [facingAnchor]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/rotate) */
fun Function.rotateFacingEntity(target: EntityArgument, facingTarget: EntityArgument, facingAnchor: Anchor? = null) =
	addLine(command("rotate", target, literal("facing"), literal("entity"), facingTarget, literal(facingAnchor?.asArg())))
