package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = RuleTest.Companion.RuleTestSerializer::class)
sealed class RuleTest {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object RuleTestSerializer :
			NamespacedPolymorphicSerializer<RuleTest>(ruleTestSealedSerializer(), outputName = "predicate_type")
	}
}
