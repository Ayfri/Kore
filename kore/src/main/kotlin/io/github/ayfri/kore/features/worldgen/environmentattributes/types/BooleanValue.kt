package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** Represents a boolean environment attribute value, used for toggling gameplay or visual features. */
@Serializable(with = BooleanValue.Companion.BooleanValueSerializer::class)
data class BooleanValue(var value: Boolean) : EnvironmentAttributesType() {
	companion object {
		data object BooleanValueSerializer : InlineAutoSerializer<BooleanValue>(BooleanValue::class)
	}
}

/** Controls whether bees stay in their hive. */
fun EnvironmentAttributesScope.beesStayInHive(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.BEES_STAY_IN_HIVE] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Pillager patrols can spawn. Replaces #without_patrol_spawns. */
fun EnvironmentAttributesScope.canPillagerPatrolSpawn(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.CAN_PILLAGER_PATROL_SPAWN] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether a Raid can be started by a player with Raid Omen. */
fun EnvironmentAttributesScope.canStartRaid(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.CAN_START_RAID] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether the Creaking is active. */
fun EnvironmentAttributesScope.creakingActive(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.CREAKING_ACTIVE] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Lava should spread faster and further, with stronger pushing force. */
fun EnvironmentAttributesScope.fastLava(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.FAST_LAVA] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether firefly bush sounds are played. */
fun EnvironmentAttributesScope.fireflyBushSounds(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Audio.FIREFLY_BUSH_SOUNDS] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Fire blocks burn out more rapidly than normal. */
fun EnvironmentAttributesScope.increasedFireBurnout(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.INCREASED_FIRE_BURNOUT] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether monsters burn in sunlight. */
fun EnvironmentAttributesScope.monstersBurn(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.MONSTERS_BURN] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Nether Portal blocks can spawn Piglins. */
fun EnvironmentAttributesScope.netherPortalSpawnsPiglin(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) =
	apply {
	this[EnvironmentAttributes.Gameplay.NETHER_PORTAL_SPAWNS_PIGLIN] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Piglins and Hoglins should zombify. */
fun EnvironmentAttributesScope.piglinsZombify(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.PIGLINS_ZOMBIFY] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Respawn Anchors can be used to set spawn. If false, the Respawn Anchor will explode once charged. */
fun EnvironmentAttributesScope.respawnAnchorWorks(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.RESPAWN_ANCHOR_WORKS] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether a Snow Golem should be damaged. */
fun EnvironmentAttributesScope.snowGolemMelts(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.SNOW_GOLEM_MELTS] = environmentAttributeValue(BooleanValue(value), mod)
}

/** Controls whether Water evaporates when placed, Ice doesn't produce water, and Wet Sponge dries out. */
fun EnvironmentAttributesScope.waterEvaporates(value: Boolean, mod: EnvironmentAttributeModifier.Boolean? = null) = apply {
	this[EnvironmentAttributes.Gameplay.WATER_EVAPORATES] = environmentAttributeValue(BooleanValue(value), mod)
}
