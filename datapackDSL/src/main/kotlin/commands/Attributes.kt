package commands

import arguments.Argument
import arguments.float
import arguments.literal
import arguments.uuid
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg
import java.util.*

@Serializable(AttributeModifierOperation.Companion.AttributeModifierOperationSerializer::class)
enum class AttributeModifierOperation {
	ADD,
	MULTIPLY,
	MULTIPLY_BASE;

	companion object {
		val values = values()

		object AttributeModifierOperationSerializer : LowercaseSerializer<AttributeModifierOperation>(values)
	}
}

class AttributeBase(private val fn: Function, private val target: Argument.Entity, private val attribute: Argument.Attribute) {
	fun get() = fn.addLine(command("attribute", target, attribute, literal("base"), literal("get")))
	fun set(value: Double) = fn.addLine(command("attribute", target, attribute, literal("base"), literal("set"), float(value)))
}

class AttributeModifiers(private val fn: Function, private val target: Argument.Entity, private val attribute: Argument.Attribute) {
	fun add(id: UUID, name: String, value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				uuid(id),
				literal(name),
				float(value),
				literal(operation.asArg())
			)
		)

	fun add(name: Argument.Attribute, value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				uuid(UUID.randomUUID()),
				name,
				float(value),
				literal(operation.asArg())
			)
		)

	fun get(id: UUID, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("value"), literal("get"), uuid(id), float(scale)))

	fun remove(id: UUID) = fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), uuid(id)))
}

class Attribute(private val fn: Function, private val target: Argument.Entity, private val attribute: Argument.Attribute) {
	val base = AttributeBase(fn, target, attribute)
	fun base(block: AttributeBase.() -> Command) = base.run(block)

	val modifiers = AttributeModifiers(fn, target, attribute)
	fun modifiers(block: AttributeModifiers.() -> Command) = modifiers.run(block)

	fun get(scale: Double? = null) = fn.addLine(command("attribute", target, attribute, literal("get"), float(scale)))
}

class AttributeTarget(private val fn: Function, private val target: Argument.Entity) {
	fun attribute(attribute: Argument.Attribute) = Attribute(fn, target, attribute)

	fun base(attribute: Argument.Attribute, block: AttributeBase.() -> Command) = Attribute(fn, target, attribute).base(block)
	fun get(attribute: Argument.Attribute, scale: Double? = null) = Attribute(fn, target, attribute).get(scale)
	fun modifiers(attribute: Argument.Attribute, block: AttributeModifiers.() -> Command) =
		Attribute(fn, target, attribute).modifiers(block)
}

class Attributes(private val fn: Function) {
	fun get(target: Argument.Entity, attribute: Argument.Attribute) = Attribute(fn, target, attribute)
	fun get(target: Argument.Entity, block: AttributeTarget.() -> Command) = AttributeTarget(fn, target).run(block)
}

fun Function.attributes(block: Attributes.() -> Command) = Attributes(this).run(block)
fun Function.attributes(target: Argument.Entity, block: AttributeTarget.() -> Command) = AttributeTarget(this, target).run(block)
fun Function.attributes(target: Argument.Entity, attribute: Argument.Attribute, block: Attribute.() -> Command) =
	Attribute(this, target, attribute).run(block)

fun Function.attributes(target: Argument.Entity) = AttributeTarget(this, target)
fun Function.attributes(target: Argument.Entity, attribute: Argument.Attribute) = Attribute(this, target, attribute)

val Function.attributes get() = Attributes(this)
