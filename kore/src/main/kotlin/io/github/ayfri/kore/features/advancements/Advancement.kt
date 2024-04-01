package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.*
import io.github.ayfri.kore.features.advancements.triggers.AdvancementTriggerCondition
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import net.benwoodworth.knbt.NbtTag

@Serializable
data class Advancement internal constructor(
	@Transient
	override var fileName: String = "advancement",
	var display: AdvancementDisplay? = null,
	var parent: AdvancementArgument? = null,
	var criteria: AdvancementCriteria = AdvancementCriteria(),
	var requirements: List<List<String>>? = null,
	var rewards: AdvancementReward? = null,
	var sendsTelemetryEvent: Boolean? = null,
) : Generator("advancements") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.advancement(fileName: String, block: Advancement.() -> Unit = {}): AdvancementArgument {
	advancements += Advancement(fileName).apply(block)
	return AdvancementArgument(fileName, name)
}

fun Advancement.display(icon: AdvancementIcon, title: String = "", description: String = "", block: AdvancementDisplay.() -> Unit = {}) {
	display = AdvancementDisplay(icon, textComponent(title), textComponent(description)).apply(block)
}

fun Advancement.display(
	icon: AdvancementIcon,
	title: ChatComponents,
	description: ChatComponents,
	block: AdvancementDisplay.() -> Unit = {},
) {
	display = AdvancementDisplay(icon, title, description).apply(block)
}

fun Advancement.display(icon: ItemArgument, title: String = "", description: String = "", block: AdvancementDisplay.() -> Unit = {}) {
	display = AdvancementDisplay(AdvancementIcon(icon), textComponent(title), textComponent(description)).apply(block)
}

fun Advancement.display(
	icon: ItemArgument,
	title: ChatComponents,
	description: ChatComponents,
	block: AdvancementDisplay.() -> Unit = {},
) {
	display = AdvancementDisplay(AdvancementIcon(icon), title, description).apply(block)
}

fun AdvancementDisplay.icon(icon: ItemArgument, nbtData: NbtTag? = null) {
	this.icon = AdvancementIcon(icon, nbtData)
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditionEntity(condition) } }",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	@Suppress("unused_parameter") name: String,
	triggerCondition: AdvancementTriggerCondition,
	condition: Entity? = null,
) {
	criteria += triggerCondition.apply { this.conditions = EntityOrPredicates(condition) }
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditions(conditions) }}",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	@Suppress("unused_parameter") name: String,
	triggerCondition: AdvancementTriggerCondition,
	vararg conditions: PredicateCondition,
) {
	criteria += triggerCondition.apply { this.conditions = EntityOrPredicates(predicateConditions = conditions.toList()) }
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditions { block() } } }",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	@Suppress("unused_parameter") name: String,
	triggerCondition: AdvancementTriggerCondition,
	block: Predicate.() -> Unit,
) {
	criteria += triggerCondition.apply {
		this.conditions = EntityOrPredicates(predicateConditions = Predicate().apply(block).predicateConditions)
	}
}

fun Advancement.criteria(block: AdvancementCriteria.() -> Unit) {
	criteria = AdvancementCriteria().apply(block)
}

fun Advancement.requirements(vararg requirements: List<String>) {
	this.requirements = listOf(*requirements)
}

fun Advancement.requirements(vararg requirements: String) {
	this.requirements = listOf(listOf(*requirements))
}

fun Advancement.rewards(block: AdvancementReward.() -> Unit) {
	rewards = AdvancementReward().apply(block)
}

fun Advancement.rewards(
	function: FunctionArgument? = null,
	experience: Int? = null,
	loot: List<LootTableArgument>? = null,
	recipes: List<RecipeArgument>? = null,
) {
	rewards = AdvancementReward(experience, function, loot, recipes)
}
