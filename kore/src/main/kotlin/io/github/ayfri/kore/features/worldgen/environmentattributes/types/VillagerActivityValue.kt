package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.ActivityArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** Represents a mob activity environment attribute value. */
@Serializable(with = VillagerActivityValue.Companion.VillagerActivityValueSerializer::class)
data class VillagerActivityValue(
	var value: ActivityArgument,
) : EnvironmentAttributesType() {
	companion object {
		data object VillagerActivityValueSerializer : InlineAutoSerializer<VillagerActivityValue, ActivityArgument>(
			serializer<ActivityArgument>(),
			VillagerActivityValue::value,
			::VillagerActivityValue
		)
	}
}

/** Controls the activity of baby villagers. Default is "minecraft:idle". */
fun EnvironmentAttributesScope.babyVillagerActivity(
	value: ActivityArgument,
	mod: EnvironmentAttributeModifier? = null,
) = apply {
	this[EnvironmentAttributes.Gameplay.BABY_VILLAGER_ACTIVITY] = environmentAttributeValue(VillagerActivityValue(value), mod)
}

/** Controls the activity of villagers. Default is "minecraft:idle". */
fun EnvironmentAttributesScope.villagerActivity(
	value: ActivityArgument,
	mod: EnvironmentAttributeModifier? = null,
) = apply {
	this[EnvironmentAttributes.Gameplay.VILLAGER_ACTIVITY] = environmentAttributeValue(VillagerActivityValue(value), mod)
}
