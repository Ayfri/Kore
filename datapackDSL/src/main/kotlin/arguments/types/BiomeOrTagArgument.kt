package arguments.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomeOrTagArgument : Argument
