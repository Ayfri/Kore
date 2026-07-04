package io.github.ayfri.kore.features.testenvironments.types

import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@GeneratedSealedSerializer
@Serializable(with = GameRuleValue.Companion.GameRuleValueSerializer::class)
sealed class GameRuleValue {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object GameRuleValueSerializer : NamespacedPolymorphicSerializer<GameRuleValue>(
			gameRuleValueSealedSerializer(),
			skipOutputName = true
		)
	}
}

@Serializable(with = GameRuleBool.Companion.GameRuleBoolSerializer::class)
data class GameRuleBool(
	var value: Boolean,
) : GameRuleValue() {
	companion object {
		data object GameRuleBoolSerializer :
			InlineAutoSerializer<GameRuleBool, Boolean>(serializer<Boolean>(), GameRuleBool::value, ::GameRuleBool)
	}
}

@Serializable(with = GameRuleInt.Companion.GameRuleIntSerializer::class)
data class GameRuleInt(
	var value: Int,
) : GameRuleValue() {
	companion object {
		data object GameRuleIntSerializer :
			InlineAutoSerializer<GameRuleInt, Int>(serializer<Int>(), GameRuleInt::value, ::GameRuleInt)
	}
}

@Serializable
data class GameRules(
	val rules: Map<Gamerules, GameRuleValue> = emptyMap(),
) : TestEnvironment()
