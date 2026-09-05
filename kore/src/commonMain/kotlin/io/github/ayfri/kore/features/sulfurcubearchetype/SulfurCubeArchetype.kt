package io.github.ayfri.kore.features.sulfurcubearchetype

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.generated.arguments.types.SulfurCubeArchetypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents an attribute modifier applied by a sulfur cube archetype.
 *
 * @property amount The amount applied by the modifier.
 * @property attribute The attribute being modified.
 * @property id The identifier of the modifier.
 * @property operation How [amount] is combined with the attribute's base value.
 */
@Serializable
data class SulfurCubeArchetypeAttributeModifier(
	var amount: Double,
	var attribute: AttributeArgument,
	var id: String,
	var operation: AttributeModifierOperation,
)

/**
 * The damage dealt to entities that touch a sulfur cube.
 *
 * @property amount The amount of damage dealt.
 * @property attributeToSource Whether the damage is attributed to the sulfur cube as its source.
 * @property damageType The type of damage dealt.
 */
@Serializable
data class SulfurCubeArchetypeContactDamage(
	var amount: Float,
	var attributeToSource: Boolean,
	var damageType: DamageTypeArgument,
)

/**
 * The explosion triggered by a sulfur cube archetype.
 *
 * @property causesFire Whether the explosion sets blocks on fire.
 * @property fuse The delay, in ticks, before the explosion goes off.
 * @property power The explosion's power.
 */
@Serializable
data class SulfurCubeArchetypeExplosion(
	var causesFire: Boolean,
	var fuse: Int,
	var power: Int,
)

/**
 * The knockback dealt to entities that touch a sulfur cube.
 *
 * @property horizontalPower The horizontal knockback power.
 * @property verticalPower The vertical knockback power.
 */
@Serializable
data class SulfurCubeArchetypeKnockbackModifiers(
	var horizontalPower: Float,
	var verticalPower: Float,
)

/**
 * The archetype-specific sounds a sulfur cube plays.
 *
 * @property hitSound The sound played when the sulfur cube is hit.
 * @property pushSound The sound played when the sulfur cube pushes an entity.
 * @property pushSoundCooldown The delay, in ticks, before [pushSound] can play again.
 * @property pushSoundImpulseThreshold The minimum push impulse required for [pushSound] to play.
 */
@Serializable
data class SulfurCubeArchetypeSoundSettings(
	var hitSound: SoundEventArgument,
	var pushSound: SoundEventArgument,
	var pushSoundCooldown: Float,
	var pushSoundImpulseThreshold: Float,
)

/**
 * Data-driven sulfur cube archetype definition.
 *
 * Controls the attribute modifiers, buoyancy, contact damage, explosion, knockback, and valid item contents of a
 * sulfur cube.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/sulfur-cube-archetypes
 * Minecraft Wiki: https://minecraft.wiki/w/Sulfur_cube_archetype_definition
 */
@Serializable
data class SulfurCubeArchetype(
	@Transient
	override var fileName: String = "sulfur_cube_archetype",
	var attributeModifiers: MutableList<SulfurCubeArchetypeAttributeModifier>,
	var buoyant: Boolean,
	var contactDamage: SulfurCubeArchetypeContactDamage? = null,
	var explosion: SulfurCubeArchetypeExplosion? = null,
	var items: ItemTagArgument,
	var knockbackModifiers: SulfurCubeArchetypeKnockbackModifiers,
	var soundSettings: SulfurCubeArchetypeSoundSettings? = null,
) : Generator("sulfur_cube_archetype") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a sulfur cube archetype using a builder block.
 *
 * Produces `data/<namespace>/sulfur_cube_archetype/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/sulfur-cube-archetypes
 * Minecraft Wiki: https://minecraft.wiki/w/Sulfur_cube_archetype_definition
 */
fun DataPack.sulfurCubeArchetype(
	fileName: String = "sulfur_cube_archetype",
	items: ItemTagArgument,
	horizontalKnockbackPower: Float,
	verticalKnockbackPower: Float,
	buoyant: Boolean = false,
	init: SulfurCubeArchetype.() -> Unit = {},
): SulfurCubeArchetypeArgument {
	val knockbackModifiers = SulfurCubeArchetypeKnockbackModifiers(horizontalKnockbackPower, verticalKnockbackPower)
	val archetype = SulfurCubeArchetype(fileName, mutableListOf(), buoyant, items = items, knockbackModifiers = knockbackModifiers).apply(init)
	sulfurCubeArchetypes += archetype
	return SulfurCubeArchetypeArgument(fileName, archetype.namespace ?: name)
}

fun SulfurCubeArchetype.modifier(
	amount: Double,
	attribute: AttributeArgument,
	id: String,
	operation: AttributeModifierOperation,
) {
	attributeModifiers += SulfurCubeArchetypeAttributeModifier(amount, attribute, id, operation)
}

/**
 * Sets [SulfurCubeArchetype.contactDamage] to a [SulfurCubeArchetypeContactDamage] built from the given parameters.
 */
fun SulfurCubeArchetype.contactDamage(
	amount: Float,
	damageType: DamageTypeArgument,
	attributeToSource: Boolean = false,
) {
	contactDamage = SulfurCubeArchetypeContactDamage(amount, attributeToSource, damageType)
}

/**
 * Sets [SulfurCubeArchetype.explosion] to a [SulfurCubeArchetypeExplosion] built from the given parameters.
 */
fun SulfurCubeArchetype.explosion(
	fuse: Int,
	power: Int,
	causesFire: Boolean = false,
) {
	explosion = SulfurCubeArchetypeExplosion(causesFire, fuse, power)
}

/**
 * Sets [SulfurCubeArchetype.knockbackModifiers] to a [SulfurCubeArchetypeKnockbackModifiers] built from the given parameters.
 */
fun SulfurCubeArchetype.knockbackModifiers(horizontalPower: Float, verticalPower: Float) {
	knockbackModifiers = SulfurCubeArchetypeKnockbackModifiers(horizontalPower, verticalPower)
}

/**
 * Sets [SulfurCubeArchetype.soundSettings] to a [SulfurCubeArchetypeSoundSettings] built from the given parameters.
 */
fun SulfurCubeArchetype.soundSettings(
	hitSound: SoundEventArgument,
	pushSound: SoundEventArgument,
	pushSoundCooldown: Float,
	pushSoundImpulseThreshold: Float,
) {
	soundSettings = SulfurCubeArchetypeSoundSettings(hitSound, pushSound, pushSoundCooldown, pushSoundImpulseThreshold)
}
