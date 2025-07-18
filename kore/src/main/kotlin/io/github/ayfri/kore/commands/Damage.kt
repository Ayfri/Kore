package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument

class Damage(private val fn: Function, private val selector: EntityArgument) {
	fun apply(amount: Float, damageType: DamageTypeArgument? = null) = fn.addLine(command("damage", selector, float(amount), damageType))
	fun applyAt(amount: Float, damageType: DamageTypeArgument, at: Vec3) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("at"), at))

	fun applyBy(amount: Float, damageType: DamageTypeArgument, by: EntityArgument) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by))

	fun applyBy(amount: Float, damageType: DamageTypeArgument, by: EntityArgument, from: EntityArgument) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by, literal("from"), from))
}

fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument? = null) =
	addLine(command("damage", targets, float(amount), damageType))

fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument, at: Vec3) =
	addLine(command("damage", targets, float(amount), damageType, literal("at"), at))

fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument, by: EntityArgument) =
	addLine(command("damage", targets, float(amount), damageType, literal("by"), by))

fun Function.damage(
	targets: EntityArgument,
	amount: Float,
	damageType: DamageTypeArgument,
	by: EntityArgument,
	from: EntityArgument
) = addLine(command("damage", targets, float(amount), damageType, literal("by"), by, literal("from"), from))

fun Function.damages(targets: EntityArgument, block: Damage.() -> Command) = Damage(this, targets).block()
