package io.github.ayfri.kore.features.predicates.providers

import io.github.ayfri.kore.features.predicates.types.EntityTarget
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Selects the score holder a [ScoreNumberProvider] reads, either an entity of the loot context or a fixed name.
 *
 * Minecraft Wiki: [Number provider - score](https://minecraft.wiki/w/Number_provider#score)
 */
@GeneratedSealedSerializer
@Serializable(with = ScoreProvider.Companion.ScoreProviderSerializer::class)
sealed class ScoreProvider {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ScoreProviderSerializer : NamespacedPolymorphicSerializer<ScoreProvider>(scoreProviderSealedSerializer())
	}
}

/**
 * Reads the score of an entity of the loot context.
 *
 * @property target The loot context entity holding the score.
 */
@SerialName("context")
@Serializable
data class ContextScoreProvider(var target: EntityTarget) : ScoreProvider()

/** Creates a [ContextScoreProvider] reading the score of the loot context entity [target]. */
fun contextScore(target: EntityTarget): ScoreProvider = ContextScoreProvider(target)

/**
 * Reads the score of a fixed score holder, whatever the loot context holds.
 *
 * @property name The player name, UUID or fake player the score belongs to.
 */
@SerialName("fixed")
@Serializable
data class FixedScoreProvider(var name: String) : ScoreProvider()

/** Creates a [FixedScoreProvider] reading the score of the fixed score holder [name]. */
fun fixedScore(name: String): ScoreProvider = FixedScoreProvider(name)
