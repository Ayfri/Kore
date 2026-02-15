package io.github.ayfri.kore.features.itemmodifiers.formulas

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/** Marker interface for `apply_bonus` formulas. */
@Serializable(with = Formula.Companion.FormulaSerializer::class)
sealed interface Formula {
	companion object {
		data object FormulaSerializer : NamespacedPolymorphicSerializer<Formula>(
			Formula::class,
			moveIntoProperty = "parameters",
			outputName = "formula"
		)
	}
}
