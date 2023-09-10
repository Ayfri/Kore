package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.PossessorArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class LiteralArgument(val text: String) : Argument, PossessorArgument, ScoreHolderArgument {
	override fun asString() = text
}

fun bool(value: Boolean) = LiteralArgument(value.toString())
internal fun bool(value: Boolean?) = value?.let { LiteralArgument(it.toString()) }

fun entity(name: String) = literal(name)

fun literal(name: String) = LiteralArgument(name)

@JvmName("literalNullable")
internal fun literal(name: String?) = name?.let { LiteralArgument(it) }
fun tag(name: String, group: Boolean = true) = LiteralArgument(if (group) "#$name" else name)
fun tag(name: String, namespace: String, group: Boolean = true) = LiteralArgument(if (group) "#$namespace:$name" else "$namespace:$name")
