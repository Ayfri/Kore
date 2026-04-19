package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/**
 * Builder for the `/ride` command.
 *
 * `/ride` can mount an entity onto a vehicle or force it to dismount. Kore exposes both the
 * direct helpers and a small DSL wrapper when the same target is reused.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/ride)
 */
class Ride(private val fn: Function, private val selector: EntityArgument) {
	/** Mounts [vehicle] as the ride target for the bound entity. */
	fun mount(vehicle: EntityArgument) = fn.addLine(command("ride", selector, literal("mount"), vehicle))
	/** Forces the bound entity to dismount. */
	fun dismount() = fn.addLine(command("ride", selector, literal("dismount")))
}

/** Mounts [target] onto [vehicle]. */
fun Function.rideMount(target: EntityArgument, vehicle: EntityArgument) = addLine(command("ride", target, literal("mount"), vehicle))

/** Forces [target] to dismount. */
fun Function.rideDismount(target: EntityArgument) = addLine(command("ride", target, literal("dismount")))

/** Opens the [Ride] DSL bound to [target]. */
fun Function.ride(target: EntityArgument, block: Ride.() -> Command) = Ride(this, target).block()
