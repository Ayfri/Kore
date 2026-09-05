package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.arguments.types.TestEnvironmentArgument
import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	val definitions: List<TestEnvironmentArgument>
) : TestEnvironment()
