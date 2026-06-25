package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.AllArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A `minecraft:score` component that renders the scoreboard value of [score]'s entity for its objective.
 *
 * Docs: [Text component format - Score](https://minecraft.wiki/w/Text_component_format#Score)
 */
@Serializable
data class ScoreComponent(
	/** The entity/objective pair whose score is displayed. */
	var score: ScoreComponentEntry,
) : ChatComponent() {
	override val type = ChatComponentType.SCORE

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["score"] = score.toNbtTag()
	}
}

/** Holds the entity selector and objective name for a [ScoreComponent]. */
@Serializable
data class ScoreComponentEntry(
	/** Entity name or selector whose score is read (serialized as `name`). */
	@SerialName("name")
	var entity: String,
	/** Scoreboard objective to read the score from. */
	var objective: String,
	/** Optional literal value that overrides the actual scoreboard score. */
	var value: String? = null,
) {
	fun toNbtTag() = nbt {
		this["name"] = entity
		this["objective"] = objective
		value?.let { this["value"] = it }
	}
}

/** Creates a [ScoreComponent] displaying [selector]'s [objective] score. */
fun scoreComponent(
	objective: String,
	selector: ScoreHolderArgument = AllArgument,
	value: String? = null,
	block: ScoreComponent.() -> Unit = {},
) =
	ChatComponents(ScoreComponent(ScoreComponentEntry(selector.asString(), objective, value)).apply(block))
