package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import kotlin.jvm.JvmName

@Serializable(with = Argument.ArgumentSerializer::class)
data class CompoundArgument(val value: NbtCompound) : Argument {
	override fun asString() = value.toString()
}

@JvmName("compoundNullable")
internal fun compound(value: NbtCompound?) = value?.let { CompoundArgument(it) }
fun compound(value: NbtCompound) = CompoundArgument(value)
