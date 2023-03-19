package commands

import arguments.Argument
import arguments.Vec3
import arguments.float
import arguments.literal
import functions.Function

class Damage(private val fn: Function, private val selector: Argument.Selector) {
	fun apply(amount: Float, damageType: Argument.DamageType? = null) = fn.addLine(command("damage", selector, float(amount), damageType))
	fun applyAt(amount: Float, damageType: Argument.DamageType, at: Vec3) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("at"), at))

	fun applyBy(amount: Float, damageType: Argument.DamageType, by: Argument.Entity) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by))

	fun applyBy(amount: Float, damageType: Argument.DamageType, by: Argument.Entity, from: Argument.Entity) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by, literal("from"), from))
}

fun Function.damage(targets: Argument.Selector, amount: Float, damageType: Argument.DamageType? = null) =
	addLine(command("damage", targets, float(amount), damageType))

fun Function.damage(targets: Argument.Selector, amount: Float, damageType: Argument.DamageType, at: Vec3) =
	addLine(command("damage", targets, float(amount), damageType, literal("at"), at))

fun Function.damage(targets: Argument.Selector, amount: Float, damageType: Argument.DamageType, by: Argument.Entity) =
	addLine(command("damage", targets, float(amount), damageType, literal("by"), by))

fun Function.damage(
	targets: Argument.Selector,
	amount: Float,
	damageType: Argument.DamageType,
	by: Argument.Entity,
	from: Argument.Entity
) = addLine(command("damage", targets, float(amount), damageType, literal("by"), by, literal("from"), from))

fun Function.damages(targets: Argument.Selector, block: Damage.() -> Command) = Damage(this, targets).block()
