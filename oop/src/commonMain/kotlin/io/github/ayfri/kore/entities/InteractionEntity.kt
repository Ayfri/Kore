package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt

/**
 * OOP entity handle targeting a specific spawned Interaction by UUID.
 *
 * Obtain one via [io.github.ayfri.kore.helpers.interactions.summon].
 */
class InteractionEntity(val uuid: UUIDArgument) : Entity() {
	override val type = EntityTypes.INTERACTION

	init {
		selector.type = EntityTypes.INTERACTION
		selector.nbt = nbt { put("UUID", uuid.toNBTIntArray()) }
	}
}
