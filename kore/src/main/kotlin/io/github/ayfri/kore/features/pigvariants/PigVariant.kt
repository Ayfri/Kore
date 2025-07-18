package io.github.ayfri.kore.features.pigvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.arguments.types.PigVariantArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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

fun DataPack.pigVariant(
	fileName: String = "pig_variant",
	block: PigVariant.() -> Unit = {},
): PigVariantArgument {
	val pigVariant = PigVariant(fileName).apply(block)
	pigVariants += pigVariant
	return PigVariantArgument(fileName, pigVariant.namespace ?: name)
}
