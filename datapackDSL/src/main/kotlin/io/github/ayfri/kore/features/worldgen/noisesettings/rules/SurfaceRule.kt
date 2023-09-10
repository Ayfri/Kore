package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SurfaceRule.Companion.SurfaceRuleSerializer::class)
sealed class SurfaceRule {
	companion object {
		data object SurfaceRuleSerializer : NamespacedPolymorphicSerializer<SurfaceRule>(SurfaceRule::class)
	}
}
