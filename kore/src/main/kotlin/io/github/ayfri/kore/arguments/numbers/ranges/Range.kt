package io.github.ayfri.kore.arguments.numbers.ranges

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
sealed interface Range : Argument
