package io.github.ayfri.kore.features.damagetypes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.damagetypes.types.DeathMessageType
import io.github.ayfri.kore.features.damagetypes.types.Effects
import io.github.ayfri.kore.features.damagetypes.types.Scaling
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a damage type.
 *
 * A damage type defines the properties of a specific kind of damage that entities can receive.
 * It controls the exhaustion applied, the translation key for death messages, scaling behavior,
 * and optional visual or message effects.
 *
 * JSON format reference: https://minecraft.wiki/w/Damage_type
 *
 * @param messageId The translation key used for death messages (e.g. "arrow", "fall").
 * @param exhaustion The amount of hunger exhaustion caused by this damage type.
 * @param scaling The scaling behavior for this damage type (see Scaling).
 * @param effects Optional. Visual effects shown to the player when taking this damage (see Effects).
 * @param deathMessageType Optional. The type of death message to use (see Death messages).
 */
@Serializable
data class DamageType(
	@Transient
	override var fileName: String = "damage_type",
	var messageId: String,
	var exhaustion: Float = 0f,
	var scaling: Scaling,
	var effects: Effects? = null,
	var deathMessageType: DeathMessageType? = null,
) : Generator("damage_type") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a damage type in this [DataPack].
 *
 * Produces `data/<namespace>/damage_type/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Damage_type
 */
fun DataPack.damageType(
	fileName: String = "damage_type",
	messageId: String,
	scaling: Scaling,
	init: DamageType.() -> Unit = {},
): DamageTypeArgument {
	val damageType = DamageType(fileName, messageId, scaling = scaling).apply(init)
	damageTypes += damageType
	return DamageTypeArgument(fileName, damageType.namespace ?: name)
}

/**
 * Create and register a damage type in this [DataPack], with inline parameters.
 *
 * Produces `data/<namespace>/damage_type/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Damage_type
 */
fun DataPack.damageType(
	fileName: String = "damage_type",
	messageId: String,
	exhaustion: Float = 0f,
	scaling: Scaling,
	effects: Effects? = null,
	deathMessageType: DeathMessageType? = null,
	namespace: String? = null,
): DamageTypeArgument {
	damageTypes += DamageType(fileName, messageId, exhaustion, scaling, effects, deathMessageType).apply {
		this.namespace = namespace
	}
	return DamageTypeArgument(fileName, namespace ?: name)
}
