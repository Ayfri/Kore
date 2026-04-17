package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Operation applied when adding an attribute modifier. */
@Serializable(AttributeModifierOperation.Companion.AttributeModifierOperationSerializer::class)
enum class AttributeModifierOperation {
	ADD_VALUE,
	ADD_MULTIPLIED_TOTAL,
	ADD_MULTIPLIED_BASE;

	companion object {
		data object AttributeModifierOperationSerializer : LowercaseSerializer<AttributeModifierOperation>(entries)
	}
}

/** DSL scope for `attribute <target> <attribute> base …`. */
class AttributeBase(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	/** Returns the base value, optionally scaled by [scale]. */
	fun get(scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("base"), literal("get"), float(scale)))

	/** Sets the base value. */
	fun set(value: Double) = fn.addLine(command("attribute", target, attribute, literal("base"), literal("set"), float(value)))

	/** Restores the base value to its default. */
	fun reset() = fn.addLine(command("attribute", target, attribute, literal("base"), literal("reset")))
}

/** DSL scope for `attribute <target> <attribute> modifier …`. */
class AttributeModifiers(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	/** Adds a modifier identified by `[namespace]:[name]`. */
	fun add(name: String, namespace: String = "minecraft", value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				literal("$namespace:$name"),
				float(value),
				literal(operation.asArg())
			)
		)

	/** Adds a modifier identified by [id]. */
	fun add(id: AttributeModifierArgument, value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				id,
				float(value),
				literal(operation.asArg())
			)
		)

	/** Reads the value of the modifier identified by `[namespace]:[name]`, optionally scaled. */
	fun get(name: String, namespace: String = "minecraft", scale: Double? = null) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("value"),
				literal("get"),
				literal("$namespace:$name"),
				float(scale)
			)
		)

	/** Reads the value of the modifier identified by [id], optionally scaled. */
	fun get(id: AttributeModifierArgument, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("value"), literal("get"), id, float(scale)))

	/** Removes the modifier identified by `[namespace]:[name]`. */
	fun remove(name: String, namespace: String = "minecraft") =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), literal("$namespace:$name")))

	/** Removes the modifier identified by [id]. */
	fun remove(id: AttributeModifierArgument) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), id))
}

/** DSL scope representing a single attribute on an entity. */
class Attribute(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	val base = AttributeBase(fn, target, attribute)
	fun base(block: AttributeBase.() -> Command) = base.run(block)

	val modifiers = AttributeModifiers(fn, target, attribute)
	fun modifiers(block: AttributeModifiers.() -> Command) = modifiers.run(block)

	/** Returns the total attribute value (base + modifiers), optionally scaled by [scale]. */
	fun get(scale: Double? = null) = fn.addLine(command("attribute", target, attribute, literal("get"), float(scale)))
}

/** DSL scope bound to an entity target for the `attribute` command. */
class AttributeTarget(private val fn: Function, private val target: EntityArgument) {
	fun attribute(attribute: AttributeArgument) = Attribute(fn, target, attribute)

	fun base(attribute: AttributeArgument, block: AttributeBase.() -> Command) = Attribute(fn, target, attribute).base(block)
	fun get(attribute: AttributeArgument, scale: Double? = null) = Attribute(fn, target, attribute).get(scale)
	fun modifiers(attribute: AttributeArgument, block: AttributeModifiers.() -> Command) =
		Attribute(fn, target, attribute).modifiers(block)
}

/** Top-level `attribute` DSL scope. */
class Attributes(private val fn: Function) {
	/** Reads the total attribute value for [target], optionally scaled. */
	fun get(target: EntityArgument, attribute: AttributeArgument, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("get"), float(scale)))

	fun get(target: EntityArgument, attribute: AttributeArgument, block: Attribute.() -> Command) =
		Attribute(fn, target, attribute).run(block)

	fun get(target: EntityArgument, block: AttributeTarget.() -> Command) = AttributeTarget(fn, target).run(block)
}

/** Opens the top-level [Attributes] DSL. */
fun Function.attributes(block: Attributes.() -> Command) = Attributes(this).run(block)

/** Opens the [AttributeTarget] DSL bound to [target]. */
fun Function.attributes(target: EntityArgument, block: AttributeTarget.() -> Command) = AttributeTarget(this, target).run(block)

/** Opens the [Attribute] DSL bound to [target] and [attribute]. */
fun Function.attributes(target: EntityArgument, attribute: AttributeArgument, block: Attribute.() -> Command) =
	Attribute(this, target, attribute).run(block)

fun Function.attributes(target: EntityArgument) = AttributeTarget(this, target)
fun Function.attributes(target: EntityArgument, attribute: AttributeArgument) = Attribute(this, target, attribute)

val Function.attributes get() = Attributes(this)
