package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument

/** DSL scope for applying damage to a selected entity. */
class Damage(private val fn: Function, private val selector: EntityArgument) {
	/** Applies [amount] of damage, optionally using [damageType]. */
	fun apply(amount: Float, damageType: DamageTypeArgument? = null) = fn.addLine(command("damage", selector, float(amount), damageType))

	/** Applies [amount] of [damageType] damage at [at]. */
	fun applyAt(amount: Float, damageType: DamageTypeArgument, at: Vec3) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("at"), at))

	/** Applies [amount] of [damageType] damage caused by [by]. */
	fun applyBy(amount: Float, damageType: DamageTypeArgument, by: EntityArgument) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by))

	/** Applies [amount] of [damageType] damage caused by [by] and originating from [from]. */
	fun applyBy(amount: Float, damageType: DamageTypeArgument, by: EntityArgument, from: EntityArgument) =
		fn.addLine(command("damage", selector, float(amount), damageType, literal("by"), by, literal("from"), from))
}

/** Damages [targets] by [amount], optionally using [damageType]. */
fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument? = null) =
	addLine(command("damage", targets, float(amount), damageType))

/** Damages [targets] at [at] using [damageType]. */
fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument, at: Vec3) =
	addLine(command("damage", targets, float(amount), damageType, literal("at"), at))

/** Damages [targets] by [by] using [damageType]. */
fun Function.damage(targets: EntityArgument, amount: Float, damageType: DamageTypeArgument, by: EntityArgument) =
	addLine(command("damage", targets, float(amount), damageType, literal("by"), by))

/** Damages [targets] by [by], originating from [from], using [damageType]. */
fun Function.damage(
	targets: EntityArgument,
	amount: Float,
	damageType: DamageTypeArgument,
	by: EntityArgument,
	from: EntityArgument
) = addLine(command("damage", targets, float(amount), damageType, literal("by"), by, literal("from"), from))

/** Opens the [Damage] DSL for [targets]. */
fun Function.damages(targets: EntityArgument, block: Damage.() -> Command) = Damage(this, targets).block()
