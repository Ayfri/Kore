package io.github.ayfri.kore.features.damagetypes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.damagetypes.types.DeathMessageType
import io.github.ayfri.kore.features.damagetypes.types.Effects
import io.github.ayfri.kore.features.damagetypes.types.Scaling
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
