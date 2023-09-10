package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SurfaceRuleCondition.Companion.SurfaceRuleConditionSerializer::class)
sealed class SurfaceRuleCondition {
	companion object {
		data object SurfaceRuleConditionSerializer : NamespacedPolymorphicSerializer<SurfaceRuleCondition>(SurfaceRuleCondition::class)
	}
}
