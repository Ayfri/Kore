package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BooleanValue.Companion.BooleanValueSerializer::class)
data class BooleanValue(var value: Boolean) : EnvironmentAttributesType() {
	companion object {
		data object BooleanValueSerializer : InlineAutoSerializer<BooleanValue>(BooleanValue::class)
	}
}

fun EnvironmentAttributesScope.canStartRaid(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.CAN_START_RAID] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.extraFog(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Visual.EXTRA_FOG] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.fastLava(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.FAST_LAVA] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.increasedFireBurnout(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.INCREASED_FIRE_BURNOUT] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.netherPortalSpawnsPiglin(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.NETHER_PORTAL_SPAWNS_PIGLIN] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.piglinsZombify(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.PIGLINS_ZOMBIFY] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.respawnAnchorWorks(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.RESPAWN_ANCHOR_WORKS] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.snowGolemMelts(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.SNOW_GOLEM_MELTS] = environmentAttributeValue(BooleanValue(value), mod)
}

fun EnvironmentAttributesScope.waterEvaporates(value: Boolean, mod: EnvironmentAttributeModifier? = null) = apply {
	this[EnvironmentAttributes.Gameplay.WATER_EVAPORATES] = environmentAttributeValue(BooleanValue(value), mod)
}
