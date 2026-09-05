package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = FoliagePlacer.Companion.FoliagePlacerSerializer::class)
sealed class FoliagePlacer : IntProviderScope {
	abstract var radius: IntProvider
	abstract var offset: IntProvider

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object FoliagePlacerSerializer :
			NamespacedPolymorphicSerializer<FoliagePlacer>(foliagePlacerSealedSerializer())
	}
}
