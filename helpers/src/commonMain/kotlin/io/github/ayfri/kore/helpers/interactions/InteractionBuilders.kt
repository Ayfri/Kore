package io.github.ayfri.kore.helpers.interactions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.entityUUID
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.InteractionEntity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.HelpersConstants
import io.github.ayfri.kore.helpers.interactions.utils.ClickKind
import io.github.ayfri.kore.helpers.interactions.utils.generateClick
import io.github.ayfri.kore.helpers.npcs.NpcDsl
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

private val declaredInteractions = DataPackStateKey<MutableSet<String>>("helpers.interactions")
private val validName = Regex("[a-z0-9_]+")

/**
 * DSL builder for a raw [Interaction], without click callbacks: nothing is generated for it, use
 * [DataPack.interaction] to react to clicks.
 *
 * @param block The builder block.
 * @return The created [Interaction].
 */
fun interaction(block: Interaction.() -> Unit) = Interaction().apply(block)

/**
 * Declares the interaction [name] in this pack and generates the advancements and functions of the callbacks added in
 * [block], one advancement and three functions per click kind used. Every instance summoned with [summon] carries the tag
 * `<namespace>.interaction.<name>`, which the generated advancements filter on.
 *
 * The callbacks are generated when [block] returns, so [onLeftClick] and [onRightClick] only count inside it.
 *
 * ```kotlin
 * val lever = interaction("lever") {
 * 	height = 0.5f
 * 	width = 0.5f
 * 	onRightClick { say("Pulled") }
 * }
 *
 * function("setup") { lever.summon(vec3(0, 64, 0)) }  // summon minecraft:interaction 0 64 0 {Tags:["my_pack.interaction.lever"],height:0.5f,width:0.5f,UUID:[I;...]}
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/interactions
 *
 * @param name The name of the interaction, lowercase letters, digits and underscores, unique in the pack.
 * @param block The builder block, where the click callbacks are added.
 * @return The declared [Interaction].
 */
fun DataPack.interaction(name: String, block: Interaction.() -> Unit): Interaction =
	registerInteraction(name, Interaction().apply(block))

/**
 * Adds a callback run when a player left clicks (attacks) the interaction.
 * Several callbacks run in declaration order. Inside it `@s` is the player, the position is the clicked interaction's and
 * `clicked` selects the interaction.
 *
 * ```kotlin
 * onLeftClick { clicked -> clicked.kill() }  // kill @e[limit=1,tag=my_pack.interaction.clicked,type=minecraft:interaction]
 * ```
 *
 * Only callbacks added inside [DataPack.interaction] are generated.
 */
fun Interaction.onLeftClick(block: @NpcDsl Function.(clicked: Entity) -> Unit) {
	leftClicks += block
}

/**
 * Adds a callback run when a player right clicks the interaction.
 * Several callbacks run in declaration order. Inside it `@s` is the player, the position is the clicked interaction's and
 * `clicked` selects the interaction.
 *
 * ```kotlin
 * onRightClick { say("Pulled") }  // execute at @s on target run say Pulled
 * ```
 *
 * Only callbacks added inside [DataPack.interaction] are generated.
 */
fun Interaction.onRightClick(block: @NpcDsl Function.(clicked: Entity) -> Unit) {
	rightClicks += block
}

/**
 * Summons this interaction at [position] and returns an OOP handle targeting it by UUID.
 *
 * The UUID is embedded in the summon NBT so the returned [InteractionEntity] uniquely identifies this instance, it hashes
 * the interaction and [position] so every build yields the same one. The instance carries the tag of its declaration.
 *
 * @throws IllegalStateException when the interaction has click callbacks but wasn't declared with [DataPack.interaction].
 */
context(fn: Function)
fun Interaction.summon(position: Vec3): InteractionEntity {
	check(definitionTag != null || (leftClicks.isEmpty() && rightClicks.isEmpty())) {
		"Interaction has click callbacks but wasn't declared with dataPack.interaction(name) { }, so they were never generated."
	}

	val ownTags = tags
	definitionTag?.let { tags = ownTags.orEmpty() + it }
	val baseNbt = toNbt()
	tags = ownTags
	val uuid = fn.datapack.entityUUID("interaction:$baseNbt:${position.asString()}")
	fn.summon(entityType, position, nbt {
		baseNbt.forEach { (key, value) -> this[key] = value }
		this["UUID"] = uuid.toNBTIntArray()
	})
	return InteractionEntity(uuid)
}

internal fun DataPack.registerInteraction(name: String, interaction: Interaction): Interaction {
	require(name.matches(validName)) { "Interaction name '$name' must only contain lowercase letters, digits and underscores." }
	require(state(declaredInteractions) { mutableSetOf() }.add(name)) { "Interaction '$name' is already declared in this pack." }

	val definitionTag = HelpersConstants.interactionTag(this.name, name)
	interaction.definitionTag = definitionTag
	val radius = interaction.searchRadius
	if (interaction.leftClicks.isNotEmpty()) generateClick(name, definitionTag, ClickKind.LEFT, radius, interaction.leftClicks.toList())
	if (interaction.rightClicks.isNotEmpty()) generateClick(name, definitionTag, ClickKind.RIGHT, radius, interaction.rightClicks.toList())
	return interaction
}
