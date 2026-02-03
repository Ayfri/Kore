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

data object TrimColorPaletteShortSerializer : ToStringSerializer<TrimColorPaletteArgument>({ "$namespace:${name.lowercase()}"})

/**
 * Data-driven armor trim material.
 *
 * Armor trim materials define the texture and appearance when trims are applied to armor. Materials work in conjunction with trim patterns
 * to create unique visual styles for armor pieces.
 *
 * Each material can specify:
 * - The asset name which points to the actual textures to use
 * - A description that appears in-game when hovering over trimmed armor
 * - Whether it should render as a decal (true for netherrite-like overlays)
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trims
 * JSON format reference: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 */
@Serializable
data class TrimMaterial(
	@Transient
	override var fileName: String = "trim_material",
	/** The texture asset to use for the trim material. */
	@Serializable(TrimColorPaletteShortSerializer::class) var assetName: TrimColorPaletteArgument,
	/** The description of the trim material. */
	var description: ChatComponents,
	/** Armor materials that should have a different color palette. */
	var overrideArmorMaterials: Map<ArmorMaterial, Color>? = null,
) : Generator("trim_material") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Set the description of the trim material. */
fun TrimMaterial.description(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	description = textComponent(text, color, block)
}

/** Set the color palette for the specified [ArmorMaterial]. */
fun TrimMaterial.overrideArmorMaterial(armorMaterial: ArmorMaterial, color: Color) = apply {
	if (overrideArmorMaterials == null) overrideArmorMaterials = mutableMapOf()
	overrideArmorMaterials = overrideArmorMaterials!! + mapOf(armorMaterial to color)
}

/** Set the color palette for the specified [ArmorMaterial]s. */
fun TrimMaterial.overrideArmorMaterials(vararg armorMaterials: Pair<ArmorMaterial, Color>) = apply {
	if (overrideArmorMaterials == null) overrideArmorMaterials = mutableMapOf()
	overrideArmorMaterials = overrideArmorMaterials!! + armorMaterials.toMap()
}

/**
 * Create and register a trim material in this [DataPack].
 *
 * Produces `data/<namespace>/trim_material/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trims
 * JSON format reference: https://minecraft.wiki/w/Tutorial:Adding_custom_trims
 */
fun DataPack.trimMaterial(
	fileName: String = "trim_material",
	assetName: TrimColorPaletteArgument,
	description: ChatComponents = textComponent(),
	block: TrimMaterial.() -> Unit
): TrimMaterialArgument {
	val trimMaterial = TrimMaterial(fileName, assetName, description).apply(block)
	trimMaterials += trimMaterial
	return TrimMaterialArgument(fileName, trimMaterials.last().namespace ?: name)
}
