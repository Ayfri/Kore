package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = SurfaceRule.Companion.SurfaceRuleSerializer::class)
sealed class SurfaceRule {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object SurfaceRuleSerializer : NamespacedPolymorphicSerializer<SurfaceRule>(surfaceRuleSealedSerializer())
	}
}
