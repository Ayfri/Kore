package io.github.ayfri.kore.features.decoratedpotpatterns

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.arguments.types.DecoratedPotPatternArgument
import io.github.ayfri.kore.generated.arguments.types.DecoratedPotPatternAssetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven decorated pot pattern, the texture a pottery sherd shows on the face of a decorated pot.
 *
 * An item shows it through its `provides_pottery_pattern` component.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * Minecraft Wiki: https://minecraft.wiki/w/Decorated_Pot
 *
 * @param assetId - The texture, read from `assets/<namespace>/textures/entity/decorated_pot/<path>.png`.
 */
@Serializable
data class DecoratedPotPattern(
	@Transient
	override var fileName: String = "decorated_pot_pattern",
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var assetId: DecoratedPotPatternAssetArgument,
) : Generator("decorated_pot_pattern") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a decorated pot pattern in this [DataPack].
 *
 * ```kotlin
 * val pattern = decoratedPotPattern("star", DecoratedPotPatternAssetArgument("star_pottery_pattern", "my_pack"))
 *
 * item(Items.BRICK) {
 *     providesPotteryPattern(pattern)
 * }
 * ```
 *
 * Produces `data/<namespace>/decorated_pot_pattern/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * Minecraft Wiki: https://minecraft.wiki/w/Decorated_Pot
 */
fun DataPack.decoratedPotPattern(
	fileName: String = "decorated_pot_pattern",
	assetId: DecoratedPotPatternAssetArgument,
): DecoratedPotPatternArgument {
	val pattern = DecoratedPotPattern(fileName, assetId)
	decoratedPotPatterns += pattern
	return DecoratedPotPatternArgument(fileName, pattern.namespace ?: name)
}
