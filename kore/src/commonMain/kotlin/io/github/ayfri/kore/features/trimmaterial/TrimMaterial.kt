package io.github.ayfri.kore.features.trimmaterial

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.generated.arguments.types.TrimColorPaletteArgument
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Writes a palette relative to `textures/palettes/`, so `Textures.Palettes.Trim.GOLD` becomes `minecraft:trim/gold`. */
data object TrimColorPaletteIdSerializer : ToStringSerializer<TrimColorPaletteArgument>({ asId().replaceFirst(":palettes/", ":") })

/**
 * Data-driven armor trim material.
 *
 * Armor trim materials define the texture and appearance when trims are applied to armor. Materials work in conjunction with trim patterns
 * to create unique visual styles for armor pieces.
 *
 * Each material specifies:
 * - The palette texture coloring the trim
 * - A description that appears in-game when hovering over trimmed armor
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trims
 * JSON format reference: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 */
@Serializable
data class TrimMaterial(
	@Transient
	override var fileName: String = "trim_material",
	/** The description of the trim material. */
	var description: ChatComponents,
	/** The palette texture coloring the trim, read from `assets/<namespace>/textures/palettes/<path>.png`. */
	@Serializable(TrimColorPaletteIdSerializer::class) var paletteId: TrimColorPaletteArgument,
) : Generator("trim_material") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Set the description of the trim material. */
fun TrimMaterial.description(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	description = textComponent(text, color, block)
}

/**
 * Create and register a trim material in this [DataPack].
 *
 * ```kotlin
 * trimMaterial("ruby", Textures.Palettes.Trim.AMETHYST) {
 *     description("Ruby", Color.RED)
 * }
 * ```
 *
 * Produces `data/<namespace>/trim_material/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trims
 * JSON format reference: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 */
fun DataPack.trimMaterial(
	fileName: String = "trim_material",
	paletteId: TrimColorPaletteArgument,
	description: ChatComponents = textComponent(),
	block: TrimMaterial.() -> Unit
): TrimMaterialArgument {
	val trimMaterial = TrimMaterial(fileName, description, paletteId).apply(block)
	trimMaterials += trimMaterial
	return TrimMaterialArgument(fileName, trimMaterials.last().namespace ?: name)
}
