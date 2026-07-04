package io.github.ayfri.kore.features.itemmodifiers.formulas

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Marker interface for `apply_bonus` formulas. */
@GeneratedSealedSerializer
@Serializable(with = Formula.Companion.FormulaSerializer::class)
sealed interface Formula {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object FormulaSerializer : NamespacedPolymorphicSerializer<Formula>(
			formulaSealedSerializer(),
			moveIntoProperty = "parameters",
			outputName = "formula"
		)
	}
}
