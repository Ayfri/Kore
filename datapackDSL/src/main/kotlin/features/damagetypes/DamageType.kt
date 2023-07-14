package features.damagetypes

import DataPack
import Generator
import arguments.Argument
import features.damagetypes.types.DeathMessageType
import features.damagetypes.types.Effects
import features.damagetypes.types.Scaling
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class DamageType(
	@Transient
	override var fileName: String = "damage_type",
	var messageId: String,
	var exhaustion: Float = 0f,
	var scaling: Scaling,
	var effects: Effects? = null,
	var deathMessageType: DeathMessageType? = null,
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.damageType(
	fileName: String = "damage_type",
	messageId: String,
	scaling: Scaling,
	damageType: DamageType.() -> Unit = {}
): Argument.DamageType {
	damageTypes += DamageType(fileName, messageId, scaling = scaling).apply(damageType)
	return Argument.DamageType(fileName, name)
}

fun DataPack.damageType(
	fileName: String = "damage_type",
	messageId: String,
	exhaustion: Float = 0f,
	scaling: Scaling,
	effects: Effects? = null,
	deathMessageType: DeathMessageType? = null,
): Argument.DamageType {
	damageTypes += DamageType(fileName, messageId, exhaustion, scaling, effects, deathMessageType)
	return Argument.DamageType(fileName, name)
}
