package io.github.ayfri.kore.helpers.npcs

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.ranges.rangeEnd
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.entity
import io.github.ayfri.kore.entities.summon.tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.interactions.Interaction
import io.github.ayfri.kore.helpers.interactions.registerInteraction
import io.github.ayfri.kore.helpers.interactions.summon
import io.github.ayfri.kore.helpers.mannequins.Mannequin
import io.github.ayfri.kore.helpers.mannequins.MannequinPose
import io.github.ayfri.kore.helpers.mannequins.summon

private const val MANNEQUIN_RADIUS = 0.5

/**
 * A clickable NPC: a [mannequin] that is seen and an [interaction] that is clicked, summoned together at one position.
 *
 * Declared with `dataPack.npc(name) { }`. Everything is declared inside the block, which registers the NPC when it returns:
 * the callbacks, the [objective]s and the interaction size, derived from the mannequin pose unless set, only count there.
 *
 * ```kotlin
 * val bob = npc("bob") {
 * 	mannequin { playerProfile("Ayfri") }
 * 	onRightClick { say("Hello") }
 * }
 *
 * function("setup") { bob.summon(vec3(0, 64, 0)) }  // summon minecraft:mannequin 0 64 0 {...}, then summon minecraft:interaction 0 64 0 {...}
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/interactions#npcs
 *
 * @property dataPack The data pack declaring the NPC.
 * @property name The name of the NPC, lowercase letters, digits and underscores, unique in the pack.
 */
@NpcDsl
class Npc internal constructor(val dataPack: DataPack, val name: String) {
	private val objectives = linkedSetOf<String>()

	/** The invisible hitbox clicks are reported on, its tag is `<namespace>.interaction.npc_<name>`. */
	val interaction = Interaction()

	/** The visible body, immovable and invulnerable by default, carrying the [tag]. */
	val mannequin = Mannequin().apply {
		immovable = true
		invulnerable = true
	}

	/** The tag every mannequin of this NPC carries, `<namespace>.npc.<name>`. */
	val tag = HelpersConstants.npcTag(dataPack.name, name)

	private val clickedMannequin = entity(EntityTypes.MANNEQUIN) {
		distance = rangeEnd(MANNEQUIN_RADIUS)
		sort = Sort.NEAREST
		tag = this@Npc.tag
	}

	/**
	 * Configures the [interaction], whose `width` and `height` default to the mannequin pose size, see [mannequin].
	 *
	 * ```kotlin
	 * interaction { width = 2f }  // the hitbox is 2 blocks wide and still as tall as the pose
	 * ```
	 */
	fun interaction(block: Interaction.() -> Unit) {
		interaction.apply(block)
	}

	/**
	 * Configures the [mannequin]. The interaction is as tall and wide as its `pose` plus 0.1 so it is hit before the mannequin:
	 * 0.7 x 1.9 standing, 0.7 x 1.6 crouching, 0.7 x 0.7 swimming or fall flying, 0.3 x 0.3 sleeping.
	 * The mannequin of a lying pose is drawn outside its hitbox, set the [interaction] size by hand to cover it.
	 *
	 * ```kotlin
	 * mannequin { pose = MannequinPose.CROUCHING }  // the interaction is 0.7 wide and 1.6 tall
	 * ```
	 */
	fun mannequin(block: Mannequin.() -> Unit) {
		mannequin.apply(block)
	}

	/**
	 * Declares the dummy objective `<namespace>.npc.<name>.<key>`, created on load, and returns its name.
	 * Declaring the same [key] twice returns the same objective.
	 *
	 * ```kotlin
	 * objective("visits")  // "my_pack.npc.bob.visits"
	 * ```
	 */
	fun objective(key: String): String {
		val objective = HelpersConstants.npcObjective(dataPack.name, name, key)
		objectives += objective
		return objective
	}

	/**
	 * Adds a callback run when a player left clicks (attacks) the NPC, see [io.github.ayfri.kore.helpers.interactions.onLeftClick].
	 * Inside it `@s` is the player and `clicked` selects the mannequin, not the interaction.
	 *
	 * ```kotlin
	 * onLeftClick { clicked -> clicked.kill() }  // kill @e[distance=..0.5,limit=1,sort=nearest,tag=my_pack.npc.bob,type=minecraft:mannequin]
	 * ```
	 */
	fun onLeftClick(block: Function.(clicked: Entity) -> Unit) {
		interaction.leftClicks += { _ -> block(clickedMannequin) }
	}

	/**
	 * Adds a callback run when a player right clicks the NPC, see [io.github.ayfri.kore.helpers.interactions.onRightClick].
	 * Inside it `@s` is the player and `clicked` selects the mannequin, not the interaction.
	 *
	 * ```kotlin
	 * onRightClick { clicked -> clicked.swing(SwingHand.MAINHAND) }  // swing @e[distance=..0.5,limit=1,sort=nearest,tag=my_pack.npc.bob,type=minecraft:mannequin] mainhand
	 * ```
	 */
	fun onRightClick(block: Function.(clicked: Entity) -> Unit) {
		interaction.rightClicks += { _ -> block(clickedMannequin) }
	}

	/**
	 * Summons the mannequin then the interaction at [position], and returns a handle on both.
	 * They share the position instead of riding each other, a passenger would sit at the attachment point and shift the hitbox.
	 *
	 * ```kotlin
	 * bob.summon(vec3(0, 64, 0))  // summon minecraft:mannequin 0 64 0 {...} then summon minecraft:interaction 0 64 0 {...}
	 * ```
	 */
	context(fn: Function)
	fun summon(position: Vec3): NpcEntity {
		val summonedMannequin = mannequin.summon(position)
		return NpcEntity(interaction.summon(position), summonedMannequin)
	}

	internal fun register() {
		mannequin.tag(tag)
		val (width, height) = hitboxOf(mannequin.pose)
		interaction.height = interaction.height ?: height
		interaction.width = interaction.width ?: width
		dataPack.registerInteraction(HelpersConstants.npcInteractionName(name), interaction)

		if (objectives.isEmpty()) return
		dataPack.load(HelpersConstants.npcLoadFunctionName(name)) {
			objectives.forEach { scoreboard.objectives.add(it, ScoreboardCriteria.DUMMY) }
		}
	}

	private fun hitboxOf(pose: MannequinPose?) = when (pose ?: MannequinPose.STANDING) {
		MannequinPose.STANDING -> 0.7f to 1.9f
		MannequinPose.CROUCHING -> 0.7f to 1.6f
		MannequinPose.SWIMMING, MannequinPose.FALL_FLYING -> 0.7f to 0.7f
		MannequinPose.SLEEPING -> 0.3f to 0.3f
	}
}

/**
 * Declares the NPC [name] in this pack: a [Npc.mannequin] with a clickable [Npc.interaction], the click callbacks added in
 * [block] generated as advancements and functions. Everything is declared inside [block], the NPC is registered when it returns.
 *
 * ```kotlin
 * val shopkeeper = npc("shopkeeper") {
 * 	mannequin { customName("Bob") }
 * 	onRightClick { say("Welcome") }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/interactions#npcs
 *
 * @param name The name of the NPC, lowercase letters, digits and underscores, unique in the pack.
 * @param block The builder block, where the mannequin, interaction, objectives and callbacks are declared.
 * @return The declared [Npc].
 */
fun DataPack.npc(name: String, block: Npc.() -> Unit): Npc = Npc(this, name).apply(block).also(Npc::register)
