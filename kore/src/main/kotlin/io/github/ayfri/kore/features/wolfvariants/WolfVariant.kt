package io.github.ayfri.kore.features.wolfvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.generated.arguments.types.WolfVariantArgument
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
	/** The different textures for the wolf variant, defaulting to vanilla. */
	var assets: Assets = Assets(),
	var spawnConditions: List<VariantSpawnEntry> = emptyList()
) : Generator("wolf_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** The different textures for a wolf variant, defaulting to vanilla. */
@Serializable
data class Assets(
	/** The texture for the angry state of the wolf variant. */
	var angry: ModelArgument = Textures.Entity.Wolf.WOLF_ANGRY,
	/** The texture for the tame state of the wolf variant. */
	var tame: ModelArgument = Textures.Entity.Wolf.WOLF_TAME,
	/** The texture for the wild state of the wolf variant. */
	var wild: ModelArgument = Textures.Entity.Wolf.WOLF,
)

/** Sets the different textures for this wolf variant. */
fun WolfVariant.assets(angry: ModelArgument, tame: ModelArgument, wild: ModelArgument) {
	assets = Assets(angry, tame, wild)
}

/** Sets the spawn conditions for this wolf variant. */
fun WolfVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply { spawnConditions = SpawnConditions().apply(block).entries }

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
