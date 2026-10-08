package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.MaterialRulesScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.MaterialConditionArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.serializer

/**
 * Base type for the conditions usable in a [io.github.ayfri.kore.features.worldgen.noisesettings.rules.Condition]
 * material rule.
 */
@GeneratedSealedSerializer
@Serializable(with = MaterialCondition.Companion.MaterialConditionSerializer::class)
sealed class MaterialCondition {
	companion object {
		data object MaterialConditionSerializer :
			NamespacedPolymorphicSerializer<MaterialCondition>(materialConditionSealedSerializer())
	}
}

@Deprecated("Renamed to MaterialCondition, like the game's registry since 26.3.", ReplaceWith("MaterialCondition"))
typealias SurfaceRuleCondition = MaterialCondition

/** A material condition declared in its own file, written as its ID. */
@Serializable(with = MaterialConditionReference.Companion.MaterialConditionReferenceSerializer::class)
data class MaterialConditionReference(var condition: MaterialConditionArgument) : MaterialCondition() {
	companion object {
		data object MaterialConditionReferenceSerializer : InlineAutoSerializer<MaterialConditionReference, MaterialConditionArgument>(
			serializer<MaterialConditionArgument>(),
			MaterialConditionReference::condition,
			::MaterialConditionReference,
			"MaterialConditionReference",
		)
	}
}

/**
 * Data-driven material condition, reusable by ID from material rules and other material conditions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Surface_rule
 */
data class MaterialConditionFile(
	@Transient
	override var fileName: String = "material_condition",
	var condition: MaterialCondition,
) : Generator("worldgen/material_condition") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(condition)
}

/**
 * Creates a material condition file holding the condition returned by [block].
 *
 * ```kotlin
 * val highlands = materialCondition("highlands") { yAbove(absolute(120)) }
 *
 * materialRule("snowy_peaks") {
 *     condition(reference(highlands)) { block(Blocks.SNOW_BLOCK) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/material_condition/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Surface_rule
 */
fun DataPack.materialCondition(
	fileName: String = "material_condition",
	block: MaterialRulesScope.() -> MaterialCondition,
): MaterialConditionArgument {
	val file = MaterialConditionFile(fileName, MaterialRulesScope().block())
	materialConditions += file
	return MaterialConditionArgument(fileName, file.namespace ?: name)
}

/** Creates a reference to the material condition file [condition]. */
fun MaterialRulesScope.reference(condition: MaterialConditionArgument) = MaterialConditionReference(condition)
