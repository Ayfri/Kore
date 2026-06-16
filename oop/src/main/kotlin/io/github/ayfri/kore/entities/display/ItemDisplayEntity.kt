package io.github.ayfri.kore.entities.display

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt

/**
 * OOP entity handle targeting a specific spawned item display entity by UUID.
 *
 * Obtain one via [io.github.ayfri.kore.helpers.displays.toEntity] on a [io.github.ayfri.kore.helpers.displays.DisplayEntityInterpolable].
 */
class ItemDisplayEntity(val uuid: UUIDArgument) : Entity() {
	override val type = EntityTypes.ITEM_DISPLAY

	init {
		selector.type = EntityTypes.ITEM_DISPLAY
		selector.nbt = nbt { put("UUID", uuid.toNBTIntArray()) }
	}
}
