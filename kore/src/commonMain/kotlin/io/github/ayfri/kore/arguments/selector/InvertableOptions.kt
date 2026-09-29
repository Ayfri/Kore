package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import io.github.ayfri.kore.utils.toSnbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

/**
 * Base class for selector options that can be inverted (prefixed with `!`).
 *
 * Two options are equal when they are of the same type and share their [value] and [invert].
 */
sealed class InvertableOption<T : Any> {
	/** The option value, or null when not set. */
	abstract var value: T?
	/** Whether this option is inverted (prefixed with `!`). */
	abstract var invert: Boolean

	/** How [value] is written in the selector. */
	protected abstract fun render(value: T): String

	override fun toString() = value?.let { if (invert) "!${render(it)}" else render(it) } ?: ""

	override fun hashCode() = 31 * (value?.hashCode() ?: 0) + invert.hashCode()

	override fun equals(other: Any?) =
		other is InvertableOption<*> && other::class == this::class && other.value == value && other.invert == invert

	companion object {
		data object InvertableOptionSerializer : ToStringSerializer<InvertableOption<*>>()
	}
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class EntityTypeOption(
	override var value: EntityTypeOrTagArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<EntityTypeOrTagArgument>() {
	override fun render(value: EntityTypeOrTagArgument) = value.asString()
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class GamemodeOption(
	override var value: Gamemode? = null,
	override var invert: Boolean = false,
) : InvertableOption<Gamemode>() {
	override fun render(value: Gamemode) = value.name.lowercase()
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class NbtCompoundOption(
	override var value: NbtCompound? = null,
	override var invert: Boolean = false,
) : InvertableOption<NbtCompound>() {
	override fun render(value: NbtCompound) = value.toSnbt()
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class PredicateOption(
	override var value: PredicateArgument? = null,
	override var invert: Boolean = false,
) : InvertableOption<PredicateArgument>() {
	override fun render(value: PredicateArgument) = value.asString()
}

@Serializable(InvertableOption.Companion.InvertableOptionSerializer::class)
class StringOption(
	override var value: String? = null,
	override var invert: Boolean = false,
) : InvertableOption<String>() {
	override fun render(value: String) = value

	init {
		value?.let {
			if (it.startsWith("!")) {
				value = it.substring(1)
				invert = true
			}
		}
	}
}
