package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("clock_time")
@Serializable
data class ClockTimeEnvironment(
	var clock: WorldClockArgument,
	var time: Int,
) : TestEnvironment()
