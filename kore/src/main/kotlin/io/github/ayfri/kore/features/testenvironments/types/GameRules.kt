package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GameRuleValue.Companion.GameRuleValueSerializer::class)
sealed class GameRuleValue {
	companion object {
		data object GameRuleValueSerializer : NamespacedPolymorphicSerializer<GameRuleValue>(
			GameRuleValue::class,
			skipOutputName = true
		)
	}
}

@Serializable(with = GameRuleBool.Companion.GameRuleBoolSerializer::class)
data class GameRuleBool(
	var value: Boolean,
) : GameRuleValue() {
	companion object {
		data object GameRuleBoolSerializer : InlineAutoSerializer<GameRuleBool>(GameRuleBool::class)
	}
}

@Serializable(with = GameRuleInt.Companion.GameRuleIntSerializer::class)
data class GameRuleInt(
	var value: Int,
) : GameRuleValue() {
	companion object {
		data object GameRuleIntSerializer : InlineAutoSerializer<GameRuleInt>(GameRuleInt::class)
	}
}

@Serializable
data class GameRules(
	val rules: Map<Gamerules, GameRuleValue> = emptyMap(),
) : TestEnvironment()
