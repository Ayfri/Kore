package commands

import arguments.Argument
import arguments.literal
import functions.Function

class Ride(private val fn: Function, private val selector: Argument.Selector) {
	fun mount(vehicle: Argument.Entity) = fn.addLine(command("ride", selector, literal("mount"), vehicle))
	fun dismount() = fn.addLine(command("ride", selector, literal("dismount")))
}

fun Function.rideMount(target: Argument.Selector, vehicle: Argument.Entity) = addLine(command("ride", target, literal("mount"), vehicle))
fun Function.rideDismount(target: Argument.Selector) = addLine(command("ride", target, literal("dismount")))

fun Function.ride(target: Argument.Selector, block: Ride.() -> Command) = Ride(this, target).block()
