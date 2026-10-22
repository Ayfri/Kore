package io.github.ayfri.kore.helpers.npcs

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.entities.InteractionEntity
import io.github.ayfri.kore.entities.MannequinEntity
import io.github.ayfri.kore.entities.kill
import io.github.ayfri.kore.entities.teleportTo
import io.github.ayfri.kore.functions.Function

/**
 * Handle on a summoned [Npc]: the [mannequin] that is seen and the [interaction] that is clicked, each targeted by UUID.
 * It isn't an `Entity` since it stands for two of them, use [mannequin] or [interaction] for any other entity command.
 *
 * Obtain one via [Npc.summon].
 */
class NpcEntity(val interaction: InteractionEntity, val mannequin: MannequinEntity) {
	/**
	 * Kills the mannequin and the interaction.
	 *
	 * ```kotlin
	 * bob.kill()  // kill @e[limit=1,nbt={UUID:[I;...]},type=minecraft:mannequin], then the same for the interaction
	 * ```
	 */
	context(fn: Function)
	fun kill() {
		mannequin.kill()
		interaction.kill()
	}

	/**
	 * Teleports the mannequin and the interaction to [position], and turns the mannequin to [rotation] when given.
	 *
	 * ```kotlin
	 * bob.teleportTo(vec3(0, 64, 5))  // teleport @e[limit=1,nbt={UUID:[I;...]},type=minecraft:mannequin] 0 64 5, then the same for the interaction
	 * ```
	 */
	context(fn: Function)
	fun teleportTo(position: Vec3, rotation: RotationArgument? = null) {
		mannequin.teleportTo(position, rotation)
		interaction.teleportTo(position)
	}
}
