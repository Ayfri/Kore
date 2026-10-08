package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings
import io.github.ayfri.kore.generated.arguments.worldgen.types.MaterialRuleArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.serializer

/**
 * Base type for the rules a [io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings] evaluates, in
 * order, to pick the block state placed at a given position.
 */
@GeneratedSealedSerializer
@Serializable(with = MaterialRule.Companion.MaterialRuleSerializer::class)
sealed class MaterialRule {
	companion object {
		data object MaterialRuleSerializer : NamespacedPolymorphicSerializer<MaterialRule>(materialRuleSealedSerializer())
	}
}

@Deprecated("Renamed to MaterialRule, like the game's registry since 26.3.", ReplaceWith("MaterialRule"))
typealias SurfaceRule = MaterialRule

/** A material rule declared in its own file, written as its ID. */
@Serializable(with = MaterialRuleReference.Companion.MaterialRuleReferenceSerializer::class)
data class MaterialRuleReference(var rule: MaterialRuleArgument) : MaterialRule() {
	companion object {
		data object MaterialRuleReferenceSerializer : InlineAutoSerializer<MaterialRuleReference, MaterialRuleArgument>(
			serializer<MaterialRuleArgument>(),
			MaterialRuleReference::rule,
			::MaterialRuleReference,
			"MaterialRuleReference",
		)
	}
}

/**
 * Data-driven material rule, reusable by ID from noise settings and other material rules.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Surface_rule
 */
data class MaterialRuleFile(
	@Transient
	override var fileName: String = "material_rule",
	var rule: MaterialRule,
) : Generator("worldgen/material_rule") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(rule)
}

/**
 * Creates a material rule file holding the rules appended in [block], a single rule as-is and several in a [Sequence].
 *
 * ```kotlin
 * val surface = materialRule("surface") {
 *     condition(water()) { block(Blocks.GRASS_BLOCK) }
 *     block(Blocks.DIRT)
 * }
 *
 * noiseSettings("my_settings") {
 *     materialRule(surface)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/material_rule/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Surface_rule
 */
fun DataPack.materialRule(fileName: String = "material_rule", block: MaterialRulesScope.() -> Unit): MaterialRuleArgument {
	val rules = buildMaterialRules(block)
	val file = MaterialRuleFile(fileName, rules.singleOrNull() ?: Sequence(rules))
	materialRules += file
	return MaterialRuleArgument(fileName, file.namespace ?: name)
}

/** Sets the material rule of the noise settings to the material rule file [rule]. */
fun NoiseSettings.materialRule(rule: MaterialRuleArgument) {
	materialRule = MaterialRuleReference(rule)
}

/** Appends a reference to the material rule file [rule]. */
fun MaterialRulesScope.rule(rule: MaterialRuleArgument) = apply { rules += MaterialRuleReference(rule) }
