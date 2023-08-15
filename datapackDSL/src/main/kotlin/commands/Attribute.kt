package commands

import arguments.types.EntityArgument
import arguments.types.literals.*
import arguments.types.resources.AttributeArgument
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import java.util.*
import kotlinx.serialization.Serializable

@Serializable(AttributeModifierOperation.Companion.AttributeModifierOperationSerializer::class)
enum class AttributeModifierOperation {
	ADD,
	MULTIPLY,
	MULTIPLY_BASE;

	companion object {
		data object AttributeModifierOperationSerializer : LowercaseSerializer<AttributeModifierOperation>(entries)
	}
}

class AttributeBase(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
	fun get() = fn.addLine(command("attribute", target, attribute, literal("base"), literal("get")))
	fun set(value: Double) = fn.addLine(command("attribute", target, attribute, literal("base"), literal("set"), float(value)))
}

class AttributeModifiers(private val fn: Function, private val target: EntityArgument, private val attribute: AttributeArgument) {
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

	fun add(id: UUIDArgument, name: String, value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				id,
				literal(name),
				float(value),
				literal(operation.asArg())
			)
		)

	fun add(name: AttributeArgument, value: Double, operation: AttributeModifierOperation) =
		fn.addLine(
			command(
				"attribute",
				target,
				attribute,
				literal("modifier"),
				literal("add"),
				randomUUID(),
				name,
				float(value),
				literal(operation.asArg())
			)
		)

	fun get(id: UUID, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("value"), literal("get"), uuid(id), float(scale)))

	fun get(id: UUIDArgument, scale: Double? = null) =
		fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("value"), literal("get"), id, float(scale)))

	fun remove(id: UUID) = fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), uuid(id)))
	fun remove(id: UUIDArgument) = fn.addLine(command("attribute", target, attribute, literal("modifier"), literal("remove"), id))
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
