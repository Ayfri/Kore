package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import net.benwoodworth.knbt.NbtCompound
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class CompoundArgument(val value: NbtCompound) : Argument {
	override fun asString() = value.toString()
}

@JvmName("compoundNullable")
internal fun compound(value: NbtCompound?) = value?.let { CompoundArgument(it) }
fun compound(value: NbtCompound) = CompoundArgument(value)
