package io.github.ayfri.kore.features.pigvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.arguments.types.PigVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a pig variant, as used in Minecraft Java Edition 1.21+.
 *
 * A pig variant specifies a unique model and texture for pigs. It can be associated with a biome
 * and is used to create different colored pigs in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Pig
 */
@Serializable
data class PigVariant(
	@Transient
	override var fileName: String = "pig_variant",
	/** The texture asset to use for the pig variant. */
	var assetId: ModelArgument,
	/** The model to use for the pig variant. */
	var model: PigModel = PigModel.NORMAL,
	/** The spawn conditions for this pig variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList(),
) : Generator("pig_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the spawn conditions for this pig variant. */
fun PigVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply { spawnConditions = SpawnConditions().apply(block).entries }


/**
 * Create and register a pig variant in this [DataPack].
 *
 * Produces `data/<namespace>/pig_variant/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Pig#Variants
 */
fun DataPack.pigVariant(
	fileName: String = "pig_variant",
	assetId: ModelArgument,
	model: PigModel = PigModel.NORMAL,
	block: PigVariant.() -> Unit = {},
): PigVariantArgument {
	val pigVariant = PigVariant(fileName, assetId, model).apply(block)
	pigVariants += pigVariant
	return PigVariantArgument(fileName, pigVariant.namespace ?: name)
}
