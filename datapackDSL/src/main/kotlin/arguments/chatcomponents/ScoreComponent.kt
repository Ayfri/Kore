package arguments.chatcomponents

import arguments.types.ScoreHolderArgument
import arguments.types.literals.AllArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import utils.set

@Serializable
data class ScoreComponent(
	var score: ScoreComponentEntry,
) : ChatComponent() {
	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["score"] = score.toNbtTag()
	}
}

@Serializable
data class ScoreComponentEntry(
	@SerialName("name") var entity: String,
	var objective: String,
	var value: String? = null,
) {
	fun toNbtTag() = buildNbtCompound {
		this["name"] = entity
		this["objective"] = objective
		value?.let { this["value"] = it }
	}
}

fun scoreComponent(
	objective: String,
	selector: ScoreHolderArgument = AllArgument,
	value: String? = null,
	block: ScoreComponent.() -> Unit = {}
) =
	ChatComponents(ScoreComponent(ScoreComponentEntry(selector.asString(), objective, value)).apply(block))
