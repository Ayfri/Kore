package commands

import arguments.types.EntityArgument
import arguments.types.literals.literal
import functions.Function

class Ride(private val fn: Function, private val selector: EntityArgument) {
	fun mount(vehicle: EntityArgument) = fn.addLine(command("ride", selector, literal("mount"), vehicle))
	fun dismount() = fn.addLine(command("ride", selector, literal("dismount")))
}

fun Function.rideMount(target: EntityArgument, vehicle: EntityArgument) = addLine(command("ride", target, literal("mount"), vehicle))
fun Function.rideDismount(target: EntityArgument) = addLine(command("ride", target, literal("dismount")))

fun Function.ride(target: EntityArgument, block: Ride.() -> Command) = Ride(this, target).block()
