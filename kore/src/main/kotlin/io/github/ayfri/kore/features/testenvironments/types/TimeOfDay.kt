package io.github.ayfri.kore.features.testenvironments.types

import kotlinx.serialization.Serializable

@Serializable
data class TimeOfDay(
	val time: Int
) : TestEnvironment()
