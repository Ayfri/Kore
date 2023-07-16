package arguments.types.literals

import arguments.Argument
import arguments.types.PossessorArgument
import arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data object AllArgument : Argument, PossessorArgument, ScoreHolderArgument {
	override fun asString() = "*"
}

fun all() = AllArgument
