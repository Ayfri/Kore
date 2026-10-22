package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.arguments.enums.Difficulty
import kotlinx.serialization.Serializable

@Serializable
data class DifficultyEnvironment(
	var difficulty: Difficulty,
) : TestEnvironment()
