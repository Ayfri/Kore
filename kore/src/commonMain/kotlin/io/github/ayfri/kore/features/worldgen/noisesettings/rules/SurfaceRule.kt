package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base type for the rules a [io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings] evaluates, in
 * order, to pick the block state placed at a given surface position.
 */
@GeneratedSealedSerializer
@Serializable(with = SurfaceRule.Companion.SurfaceRuleSerializer::class)
sealed class SurfaceRule {
	companion object {
		data object SurfaceRuleSerializer : NamespacedPolymorphicSerializer<SurfaceRule>(surfaceRuleSealedSerializer())
	}
}
