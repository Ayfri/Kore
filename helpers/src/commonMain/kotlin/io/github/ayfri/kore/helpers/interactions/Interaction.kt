package io.github.ayfri.kore.helpers.interactions

import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.summon.EntitySummonData
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.encodeToNbtTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.nbtCompound

/**
 * Represents an Interaction entity helper: an invisible hitbox the game reports clicks on.
 *
 * Declared with `dataPack.interaction(name) { }`, the left and right click callbacks added in the block are generated as
 * advancements and functions, see [onLeftClick] and [onRightClick].
 *
 * NBT format reference: https://minecraft.wiki/w/Interaction
 * Docs: https://kore.ayfri.com/docs/helpers/interactions
 *
 * @property height The height of the hitbox, growing upwards from the entity position, the game defaults to 1.
 * @property response Whether the game plays the attack sound on a left click and swings the arm on a right click.
 * @property width The width of the hitbox, centered on the entity position, the game defaults to 1.
 */
@Serializable
data class Interaction(
	var height: Float? = null,
	var response: Boolean? = null,
	var width: Float? = null,
) : EntitySummonData() {
	/** The entity type of the interaction. */
	@Transient
	val entityType = EntityTypes.INTERACTION

	/** The tag every summoned instance carries and the generated advancements filter on, `<namespace>.interaction.<name>` once declared. */
	@Transient
	var definitionTag: String? = null
		internal set

	/**
	 * How far from the clicking player, in blocks, the generated functions look for the clicked instances, 24 by default.
	 * Raise it for a wide hitbox or a boosted `entity_interaction_range`, a click farther away runs no callback.
	 *
	 * ```kotlin
	 * interaction("gate") { searchRadius = 40 }  // execute as @e[distance=..40,tag=my_pack.interaction.gate,type=minecraft:interaction] ...
	 * ```
	 */
	@Transient
	var searchRadius: Int = 24

	@Transient
	internal val leftClicks = mutableListOf<Function.(clicked: Entity) -> Unit>()

	@Transient
	internal val rightClicks = mutableListOf<Function.(clicked: Entity) -> Unit>()

	/**
	 * Converts the interaction to an NBT compound.
	 *
	 * @return The NBT compound.
	 */
	fun toNbt() = encodeToNbtTag(serializer(), this).nbtCompound
}
