package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.Gamerules
import kotlinx.serialization.Serializable

@Serializable
data class GameRuleEntry(val rule: String, val value: Boolean)

@Serializable
data class GameRuleIntEntry(val rule: String, val value: Int)

@Serializable
data class GameRules(
	val boolRules: List<GameRuleEntry> = emptyList(),
	val intRules: List<GameRuleIntEntry> = emptyList()
) : TestEnvironment()
