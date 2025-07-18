package io.github.ayfri.kore.features.wolfvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.arguments.types.WolfVariantArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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

fun DataPack.wolfVariant(
	fileName: String = "wolf_variant",
	block: WolfVariant.() -> Unit = {},
): WolfVariantArgument {
	val wolfVariant = WolfVariant(fileName).apply(block)
	wolfVariants += wolfVariant
	return WolfVariantArgument(fileName, wolfVariant.namespace ?: name)
}
