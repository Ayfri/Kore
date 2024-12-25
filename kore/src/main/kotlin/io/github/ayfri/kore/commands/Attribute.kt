package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(AttributeModifierOperation.Companion.AttributeModifierOperationSerializer::class)
enum class AttributeModifierOperation {
	ADD_VALUE,
	ADD_MULTIPLIED_TOTAL,
	ADD_MULTIPLIED_BASE;

	companion object {
		data object AttributeModifierOperationSerializer : LowercaseSerializer<AttributeModifierOperation>(entries)
	}
}

class AttributeBase(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	fun get() = fn.addLine(command("attribute", target, attribute, literal("base"), literal("get")))
	fun set(value: Double) = fn.addLine(command("attribute", target, attribute, literal("base"), literal("set"), float(value)))
	fun reset() = fn.addLine(command("attribute", target, attribute, literal("base"), literal("reset")))
}

class AttributeModifiers(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
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

	fun get(name: String, namespace: String, scale: Double? = null) =
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

	fun get(id: AttributeModifierArgument, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("value"), literal("get"), id, float(scale)))

	fun remove(name: String, namespace: String) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), literal("$namespace:$name")))

	fun remove(id: AttributeModifierArgument) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), id))
}

class Attribute(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	val base = AttributeBase(fn, target, attribute)
	fun base(block: AttributeBase.() -> Command) = base.run(block)

	val modifiers = AttributeModifiers(fn, target, attribute)
	fun modifiers(block: AttributeModifiers.() -> Command) = modifiers.run(block)

	fun get(scale: Double? = null) = fn.addLine(command("attribute", target, attribute, literal("get"), float(scale)))
}

class AttributeTarget(private val fn: Function, private val target: EntityArgument) {
	fun attribute(attribute: AttributeArgument) = Attribute(fn, target, attribute)

	fun base(attribute: AttributeArgument, block: AttributeBase.() -> Command) = Attribute(fn, target, attribute).base(block)
	fun get(attribute: AttributeArgument, scale: Double? = null) = Attribute(fn, target, attribute).get(scale)
	fun modifiers(attribute: AttributeArgument, block: AttributeModifiers.() -> Command) =
		Attribute(fn, target, attribute).modifiers(block)
}

class Attributes(private val fn: Function) {
	fun get(target: EntityArgument, attribute: AttributeArgument, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("get"), float(scale)))

	fun get(target: EntityArgument, attribute: AttributeArgument, block: Attribute.() -> Command) =
		Attribute(fn, target, attribute).run(block)

	fun get(target: EntityArgument, block: AttributeTarget.() -> Command) = AttributeTarget(fn, target).run(block)
}

fun Function.attributes(block: Attributes.() -> Command) = Attributes(this).run(block)
fun Function.attributes(target: EntityArgument, block: AttributeTarget.() -> Command) = AttributeTarget(this, target).run(block)
fun Function.attributes(target: EntityArgument, attribute: AttributeArgument, block: Attribute.() -> Command) =
	Attribute(this, target, attribute).run(block)

fun Function.attributes(target: EntityArgument) = AttributeTarget(this, target)
fun Function.attributes(target: EntityArgument, attribute: AttributeArgument) = Attribute(this, target, attribute)

val Function.attributes get() = Attributes(this)
