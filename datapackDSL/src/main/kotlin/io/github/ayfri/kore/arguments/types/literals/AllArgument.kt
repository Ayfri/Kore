package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.PossessorArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data object AllArgument : Argument, PossessorArgument, ScoreHolderArgument {
	override fun asString() = "*"
}

fun all() = AllArgument
