package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.arguments.types.TimelineArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A test environment that sets a list of timelines to control game behavior during test execution.
 *
 * Timeline attributes environments allow tests to run under specific timeline configurations,
 * useful for testing time-driven mechanics driven by [Timeline][io.github.ayfri.kore.features.timelines.Timeline]
 * definitions.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Test_environment
 */
@SerialName("timeline_attributes")
@Serializable
data class TimelineAttributesEnvironment(
	var timelines: List<TimelineArgument>,
) : TestEnvironment()
