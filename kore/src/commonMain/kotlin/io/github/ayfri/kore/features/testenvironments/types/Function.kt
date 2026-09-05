package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class Function(
	val setup: FunctionArgument? = null,
	val teardown: FunctionArgument? = null
) : TestEnvironment()
