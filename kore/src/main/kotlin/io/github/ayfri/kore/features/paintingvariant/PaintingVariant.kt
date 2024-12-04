package io.github.ayfri.kore.features.paintingvariant

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.types.resources.PaintingVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class PaintingVariant(
	@Transient
	override var fileName: String = "painting_variant",
	var assetId: String,
	var height: Int,
	var width: Int,
	var author: ChatComponents? = null,
	var title: ChatComponents? = null,
) : Generator("painting_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.paintingVariant(
	fileName: String = "painting_variant",
	assetId: String,
	height: Int = 1,
	width: Int = 1,
	block: PaintingVariant.() -> Unit = {},
): PaintingVariantArgument {
	val paintingVariant = PaintingVariant(fileName, assetId, height, width).apply(block)
	paintingVariants += paintingVariant
	return PaintingVariantArgument(fileName, paintingVariant.namespace ?: name)
}
