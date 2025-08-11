package io.github.ayfri.kore.features.wolfvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.arguments.types.WolfVariantArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a wolf variant, as used in Minecraft Java Edition 1.21+.
 *
 * A wolf variant specifies a unique model and texture for wolves. It can be associated with a biome
 * and is used to create different colored wolves in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Wolf
 *
 * @param model - The model to use for the wolf variant.
 * @param texture - The texture to use for the wolf variant.
 * @param biome - The biome to associate with the wolf variant.
 */
@Serializable
data class WolfVariant(
	@Transient
	override var fileName: String = "wolf_variant",
	var wildTexture: String = "minecraft:entity/wolf/wolf",
	var angryTexture: String = "minecraft:entity/wolf/wolf_angry",
	var tameTexture: String = "minecraft:entity/wolf/wolf_tame",
	var biomes: InlinableList<BiomeArgument> = listOf(Biomes.TAIGA),
) : Generator("wolf_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a wolf variant in this [DataPack].
 *
 * Produces `data/<namespace>/wolf_variant/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Wolf#Variants
 */
fun DataPack.wolfVariant(
	fileName: String = "wolf_variant",
	block: WolfVariant.() -> Unit = {},
): WolfVariantArgument {
	val wolfVariant = WolfVariant(fileName).apply(block)
	wolfVariants += wolfVariant
	return WolfVariantArgument(fileName, wolfVariant.namespace ?: name)
}
