package arguments.selector

import arguments.enums.Gamemode
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.PredicateArgument
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt
import serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

sealed class InvertableOption<T>(
	val serializer: (T) -> String,
) {
	abstract var value: T?
	abstract var invert: Boolean

	override fun toString() = when {
		value == null -> ""
		invert -> "!${serializer(value!!)}"
		else -> serializer(value!!)
	}

	override fun hashCode(): Int {
		var result = serializer.hashCode()
		result = 31 * result + (value?.hashCode() ?: 0)
		result = 31 * result + invert.hashCode()
		return result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is InvertableOption<*>) return false

		if (serializer != other.serializer) return false
		if (value != other.value) return false
		if (invert != other.invert) return false

		return true
	}

	companion object {
		data object InvertableOptionSerializer : ToStringSerializer<InvertableOption<*>>()
	}
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class GamemodeOption(override var value: Gamemode? = null, override var invert: Boolean = false) : InvertableOption<Gamemode>(
	{ json.encodeToJsonElement(it).jsonPrimitive.content }
)

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class NbtCompoundOption(
	override var value: NbtCompound? = null,
	override var invert: Boolean = false,
) : InvertableOption<NbtCompound>(StringifiedNbt::encodeToString)

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class EntityTypeOption(
	override var value: EntityTypeArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<EntityTypeArgument>({ json.encodeToJsonElement(it).jsonPrimitive.content })

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class PredicateOption(
	override var value: PredicateArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<PredicateArgument>({ json.encodeToJsonElement(it).jsonPrimitive.content })
