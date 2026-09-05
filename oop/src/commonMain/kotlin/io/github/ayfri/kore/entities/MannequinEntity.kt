package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt

/**
 * OOP entity handle targeting a specific spawned Mannequin by UUID.
 *
 * Obtain one via [io.github.ayfri.kore.helpers.mannequins.summon].
 */
class MannequinEntity(val uuid: UUIDArgument) : Entity() {
	override val type = EntityTypes.MANNEQUIN

	init {
		selector.type = EntityTypes.MANNEQUIN
		selector.nbt = nbt { put("UUID", uuid.toNBTIntArray()) }
	}
}
