package io.github.ayfri.kore.features.trimpattern

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven armor trim pattern.
 *
 * Armor trim patterns define the texture and appearance when trims are applied to armor. Patterns work in conjunction with trim materials.
 *
 * Each pattern can specify:
 * - The asset name which points to the actual textures to use
 * - A description that appears in-game when hovering over trimmed armor
 * - Whether it should render as a decal (true for netherrite-like overlays)
 *
 * JSON format reference: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 */
@Serializable
data class TrimPattern(
	@Transient
	override var fileName: String = "trim_pattern",
	/** The texture asset to use for the trim pattern. */
	var assetId: ModelArgument,
	/** The description of the trim pattern. */
	var description: ChatComponents,
	/** Whether the pattern should render as a decal. */
	var decal: Boolean = false,
) : Generator("trim_pattern") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Set the description of the trim material. */
fun TrimPattern.description(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	description = textComponent(text, color, block)
}


/**
 * Create and register a trim pattern in this [DataPack].
 *
 * Produces `data/<namespace>/trim_pattern/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 * https://minecraft.wiki/w/Armor#Trimming
 */
fun DataPack.trimPattern(
	fileName: String = "trim_pattern",
	assetId: ModelArgument,
	description: ChatComponents = textComponent(),
	decal: Boolean = false,
	block: TrimPattern.() -> Unit
): TrimPatternArgument {
	val trimPattern = TrimPattern(fileName, assetId, description, decal).apply(block)
	trimPatterns += trimPattern
	return TrimPatternArgument(fileName, trimPattern.namespace ?: name)
}
