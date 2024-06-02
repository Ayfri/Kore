package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.PredicateArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonPrimitive
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt

sealed class InvertableOption<T>(
	val kSerializer: SerializationStrategy<T>,
	val serializer: (T) -> String = { json.encodeToJsonElement(kSerializer, it).jsonPrimitive.content },
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
class EntityTypeOption(
	override var value: EntityTypeArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<EntityTypeArgument>(EntityTypeArgument.serializer())

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class GamemodeOption(
	override var value: Gamemode? = null,
	override var invert: Boolean = false,
) : InvertableOption<Gamemode>(Gamemode.serializer())

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class NbtCompoundOption(
	override var value: NbtCompound? = null,
	override var invert: Boolean = false,
) : InvertableOption<NbtCompound>(NbtCompound.serializer(), StringifiedNbt::encodeToString)

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class PredicateOption(
	override var value: PredicateArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<PredicateArgument>(PredicateArgument.serializer())

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class StringOption(
	override var value: String? = null,
	override var invert: Boolean = false,
) : InvertableOption<String>(String.serializer()) {
	init {
		value?.let {
			if (it.startsWith("!")) {
				value = it.substring(1)
				invert = true
			}
		}
	}
}
