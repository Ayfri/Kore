package io.github.ayfri.kore.features.paintingvariant

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.arguments.types.PaintingAssetArgument
import io.github.ayfri.kore.generated.arguments.types.PaintingVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a painting variant, as used in Minecraft Java Edition 1.21+.
 *
 * A painting variant specifies a unique artwork that can be placed in the world. It references a sprite
 * in the `painting` atlas via `assetId`, and defines its size in blocks (`width`, `height`).
 * Optionally, it can include a `title` and `author` as text components, which are shown in the creative menu tooltip.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Painting_variant_definition
 *
 * @param assetId - Resource location of the sprite in the painting atlas.
 * @param width - Width in blocks (1-16).
 * @param height - Height in blocks (1-16).
 * @param title - (optional) Text component for the painting's title.
 * @param author - (optional) Text component for the painting's author.
 */
@Serializable
data class PaintingVariant(
	@Transient
	override var fileName: String = "painting_variant",
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var assetId: PaintingAssetArgument,
	var height: Int,
	var width: Int,
	var author: ChatComponents? = null,
	var title: ChatComponents? = null,
) : Generator("painting_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a painting variant in this [DataPack].
 *
 * Produces `data/<namespace>/painting_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Painting#Variants
 */
fun DataPack.paintingVariant(
	fileName: String = "painting_variant",
	assetId: PaintingAssetArgument,
	height: Int = 1,
	width: Int = 1,
	block: PaintingVariant.() -> Unit = {},
): PaintingVariantArgument {
	val paintingVariant = PaintingVariant(fileName, assetId, height, width).apply(block)
	paintingVariants += paintingVariant
	return PaintingVariantArgument(fileName, paintingVariant.namespace ?: name)
}
