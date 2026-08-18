package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.arguments.enums.Difficulty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("difficulty")
@Serializable
data class DifficultyEnvironment(
	var difficulty: Difficulty,
) : TestEnvironment()
