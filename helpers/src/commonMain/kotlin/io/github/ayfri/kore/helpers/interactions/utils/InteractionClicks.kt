package io.github.ayfri.kore.helpers.interactions.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.ranges.rangeEnd
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.advancement
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.Relation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.returnFail
import io.github.ayfri.kore.commands.returnValue
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.conditionEntity
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.entity
import io.github.ayfri.kore.features.advancements.triggers.playerHurtEntity
import io.github.ayfri.kore.features.advancements.triggers.playerInteractedWithEntity
import io.github.ayfri.kore.features.predicates.sub.allOf
import io.github.ayfri.kore.features.predicates.sub.entityTags
import io.github.ayfri.kore.features.predicates.sub.entityType
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.helpers.HelpersConstants

/** A click the game reports on an interaction entity: its advancement event, the `execute on` relation to the player and the NBT record to clear. */
internal enum class ClickKind(val event: String, val relation: Relation, val record: String) {
	LEFT("left_click", Relation.ATTACKER, "attack"),
	RIGHT("right_click", Relation.TARGET, "interaction");
}

/**
 * Generates the advancement and the functions running [callbacks] when a player clicks an instance of the interaction
 * [name] carrying [definitionTag] within [searchRadius] blocks of the player, as the player and at the clicked interaction.
 */
internal fun DataPack.generateClick(
	name: String,
	definitionTag: String,
	kind: ClickKind,
	searchRadius: Int,
	callbacks: List<Function.(clicked: Entity) -> Unit>,
) {
	val namespace = this.name
	val clickedTag = HelpersConstants.interactionClickedTag(namespace)
	val sourceTag = HelpersConstants.interactionSourceTag(namespace)
	val clicked = entity {
		type = EntityTypes.INTERACTION
		tag = clickedTag
	}
	val advancementName = "${HelpersConstants.interactionAdvancementFolder}/${name}_${kind.event}"
	val advancementArgument = AdvancementArgument(advancementName, namespace)

	val target = generatedFunction(HelpersConstants.interactionTargetFunctionName(kind.event)) {
		execute {
			on(kind.relation)
			ifCondition { entity(self { tag = sourceTag }) }
			run { returnValue(1) }
		}
		returnFail()
	}

	val hit = generatedFunction(HelpersConstants.interactionHitFunctionName(name, kind.event)) {
		tag(self()) { add(clickedTag) }
		execute {
			at(self())
			on(kind.relation)
			run { callbacks.forEach { callback -> callback(clicked) } }
		}
		data(self()).remove(kind.record)
		tag(self()) { remove(clickedTag) }
	}

	val reward = generatedFunction(HelpersConstants.interactionClickFunctionName(name, kind.event)) {
		advancement(self()) { revoke(advancementArgument) }
		tag(self()) { add(sourceTag) }
		execute {
			asTarget(allEntities {
				distance = rangeEnd(searchRadius)
				tag = definitionTag
				type = EntityTypes.INTERACTION
			})
			ifCondition { function(target) }
			run(hit)
		}
		tag(self()) { remove(sourceTag) }
	}

	val isDefinition: EntityOrPredicates.() -> Unit = {
		conditionEntity {
			entityType(EntityTypes.INTERACTION)
			entityTags { allOf(definitionTag) }
		}
	}

	advancement(advancementName) {
		criteria {
			when (kind) {
				ClickKind.LEFT -> playerHurtEntity(kind.event) { entity(isDefinition) }
				ClickKind.RIGHT -> playerInteractedWithEntity(kind.event) { entity(isDefinition) }
			}
		}
		rewards { function = reward }
	}
}
