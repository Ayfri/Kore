package io.github.ayfri.kore.features.pigvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.arguments.types.PigVariantArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a pig variant, as used in Minecraft Java Edition 1.21+.
 *
 * A pig variant specifies a unique model and texture for pigs. It can be associated with a biome
 * and is used to create different colored pigs in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Pig
 *
 * @param model - The model to use for the pig variant.
 * @param texture - The texture to use for the pig variant.
 * @param biome - The biome to associate with the pig variant.
 */
@Serializable
data class PigVariant(
	@Transient
	override var fileName: String = "pig_variant",
	var model: PigModel = PigModel.NORMAL,
	var texture: String = "minecraft:entity/pig/pig",
	var biome: InlinableList<BiomeArgument> = listOf(Biomes.PLAINS),
) : Generator("pig_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a pig variant in this [DataPack].
 *
 * Produces `data/<namespace>/pig_variant/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Pig#Variants
 */
fun DataPack.pigVariant(
	fileName: String = "pig_variant",
	block: PigVariant.() -> Unit = {},
): PigVariantArgument {
	val pigVariant = PigVariant(fileName).apply(block)
	pigVariants += pigVariant
	return PigVariantArgument(fileName, pigVariant.namespace ?: name)
}
