package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.advancements.triggers.AdvancementTriggerCondition
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven advancement definition.
 *
 * Advancements are in-game goals with criteria and optional display properties. They can form trees
 * (via `parent`), award rewards on completion, and control how progress is tracked (requirements).
 * Use criteria to attach triggers and predicate conditions.
 *
 * JSON format reference: https://minecraft.wiki/w/Advancement_definition
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 *
 * @param display - The display properties for the advancement.
 * @param parent - The parent advancement for this advancement.
 * @param criteria - The criteria for the advancement.
 * @param requirements - The requirements for the advancement.
 * @param rewards - The rewards for the advancement.
 * @param sendsTelemetryEvent - Whether to send a telemetry event when the advancement is completed.
 */
@Serializable
data class Advancement(
	@Transient
	override var fileName: String = "advancement",
	var display: AdvancementDisplay? = null,
	var parent: AdvancementArgument? = null,
	var criteria: AdvancementCriteria = AdvancementCriteria(),
	var requirements: List<List<String>>? = null,
	var rewards: AdvancementReward? = null,
	var sendsTelemetryEvent: Boolean? = null,
) : Generator("advancement") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates an advancement using a builder block.
 *
 * Configure display, parent, criteria, requirements and rewards in the builder.
 *
 * Produces `data/<namespace>/advancement/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun DataPack.advancement(fileName: String, block: Advancement.() -> Unit = {}): AdvancementArgument {
	val advancement = Advancement(fileName)
	advancements += advancement.apply(block)
	return AdvancementArgument(fileName, advancement.namespace ?: name)
}

/**
 * Configure the display of the advancement (icon, title, description, frame, etc.).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.display(icon: AdvancementIcon, title: String = "", description: String = "", block: AdvancementDisplay.() -> Unit = {}) {
	display = AdvancementDisplay(icon, textComponent(title), textComponent(description)).apply(block)
}

/**
 * Configure the display of the advancement (icon, title, description, frame, etc.).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.display(
	icon: AdvancementIcon,
	title: ChatComponents,
	description: ChatComponents,
	block: AdvancementDisplay.() -> Unit = {},
) {
	display = AdvancementDisplay(icon, title, description).apply(block)
}

/**
 * Configure the display of the advancement (icon, title, description, frame, etc.).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.display(icon: ItemArgument, title: String = "", description: String = "", block: AdvancementDisplay.() -> Unit = {}) {
	display = AdvancementDisplay(AdvancementIcon(icon), textComponent(title), textComponent(description)).apply(block)
}

/**
 * Configure the display of the advancement (icon, title, description, frame, etc.).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.display(
	icon: ItemArgument,
	title: ChatComponents,
	description: ChatComponents,
	block: AdvancementDisplay.() -> Unit = {},
) {
	display = AdvancementDisplay(AdvancementIcon(icon), title, description).apply(block)
}

/**
 * Configure the icon of the advancement display.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun AdvancementDisplay.icon(icon: ItemArgument, count: Int? = null, components: Components.() -> Unit = {}) {
	this.icon = AdvancementIcon(icon, Components().apply(components), count)
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditionEntity(condition) } }",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	name: String,
	triggerCondition: AdvancementTriggerCondition,
	condition: Entity? = null,
) {
	criteria.criteria[name] = triggerCondition.apply { this.player = EntityOrPredicates(condition) }
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditions(conditions) }}",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	name: String,
	triggerCondition: AdvancementTriggerCondition,
	vararg conditions: PredicateCondition,
) {
	criteria.criteria[name] = triggerCondition.apply { this.player = EntityOrPredicates().conditions(*conditions) }
}

@Deprecated(
	"Use AdvancementDisplay.criteria { ... } instead.",
	ReplaceWith(
		"criteria { this[name] = triggerCondition.apply { conditions { block() } } }",
		"io.github.ayfri.kore.features.advancements.triggers.conditions"
	)
)
fun Advancement.criteria(
	name: String,
	triggerCondition: AdvancementTriggerCondition,
	block: Predicate.() -> Unit,
) {
	criteria.criteria[name] = triggerCondition.apply {
		player = EntityOrPredicates(predicateConditions = Predicate().apply(block))
	}
}

/**
 * Define criteria and triggers for this advancement.
 *
 * Triggers: https://kore.ayfri.com/docs/data-driven/advancements/triggers
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.criteria(block: AdvancementCriteria.() -> Unit) {
	criteria = AdvancementCriteria().apply(block)
}

/**
 * Set the completion requirements for this advancement.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.requirements(vararg requirements: List<String>) {
	this.requirements = listOf(*requirements)
}

/**
 * Set the completion requirements for this advancement.
 * Passing several names creates a single AND-group.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.requirements(vararg requirements: String) {
	this.requirements = listOf(listOf(*requirements))
}

/**
 * Configure the rewards granted when the advancement is completed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.rewards(block: AdvancementReward.() -> Unit) {
	rewards = AdvancementReward().apply(block)
}

/**
 * Set rewards in a single call.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements
 */
fun Advancement.rewards(
	function: FunctionArgument? = null,
	experience: Int? = null,
	loot: List<LootTableArgument>? = null,
	recipes: List<RecipeArgument>? = null,
) {
	rewards = AdvancementReward(experience, function, loot, recipes)
}
