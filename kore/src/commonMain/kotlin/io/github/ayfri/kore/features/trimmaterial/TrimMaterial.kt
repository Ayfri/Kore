package io.github.ayfri.kore.features.trimmaterial

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.generated.arguments.types.EquipmentAssetArgument
import io.github.ayfri.kore.generated.arguments.types.TrimColorPaletteArgument
import io.github.ayfri.kore.generated.arguments.types.TrimMaterialArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Writes a palette as its bare name (`gold_darker`), the game rejecting any `:` in a trim palette suffix. */
data object TrimColorPaletteShortSerializer : ToStringSerializer<TrimColorPaletteArgument>({ name.lowercase() })

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
	/** Palettes used instead of [assetName] on specific equipment, like the darker gold palette on gold armor. */
	var overrideArmorAssets: Map<EquipmentAssetArgument, @Serializable(TrimColorPaletteShortSerializer::class) TrimColorPaletteArgument>? = null,
	/** Ignored by the game, which reads [overrideArmorAssets] instead. */
	@Deprecated("Ignored by the game, which reads `override_armor_assets`. Use overrideArmorAssets instead.")
	@Transient
	var overrideArmorMaterials: Map<ArmorMaterial, Color>? = null,
) : Generator("trim_material") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Set the description of the trim material. */
fun TrimMaterial.description(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	description = textComponent(text, color, block)
}

/**
 * Uses [palette] instead of [TrimMaterial.assetName] when the trim is applied on [equipment].
 *
 * ```kotlin
 * trimMaterial("gold", Textures.Trims.ColorPalettes.GOLD) {
 *     overrideArmorAsset(EquipmentAssets.GOLD, Textures.Trims.ColorPalettes.GOLD_DARKER)
 * }
 * ```
 * writes `"override_armor_assets": { "minecraft:gold": "gold_darker" }`.
 */
fun TrimMaterial.overrideArmorAsset(equipment: EquipmentAssetArgument, palette: TrimColorPaletteArgument) = apply {
	overrideArmorAssets = overrideArmorAssets.orEmpty() + (equipment to palette)
}

/** Uses each palette instead of [TrimMaterial.assetName] when the trim is applied on its equipment. */
fun TrimMaterial.overrideArmorAssets(vararg overrides: Pair<EquipmentAssetArgument, TrimColorPaletteArgument>) = apply {
	overrideArmorAssets = overrideArmorAssets.orEmpty() + overrides
}

@Suppress("DEPRECATION")
@Deprecated("Ignored by the game, which reads `override_armor_assets`. Use overrideArmorAsset instead.")
fun TrimMaterial.overrideArmorMaterial(armorMaterial: ArmorMaterial, color: Color) = apply {
	overrideArmorMaterials = overrideArmorMaterials.orEmpty() + (armorMaterial to color)
}

@Suppress("DEPRECATION")
@Deprecated("Ignored by the game, which reads `override_armor_assets`. Use overrideArmorAssets instead.")
fun TrimMaterial.overrideArmorMaterials(vararg armorMaterials: Pair<ArmorMaterial, Color>) = apply {
	overrideArmorMaterials = overrideArmorMaterials.orEmpty() + armorMaterials
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
