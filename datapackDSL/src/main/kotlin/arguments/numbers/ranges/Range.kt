package arguments.numbers.ranges

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
sealed interface Range : Argument
