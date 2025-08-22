package io.github.ayfri.kore.features.frogvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven frog variant definition.
 *
 * Frog variants are used to define the appearance of frogs in the world.
 * They can be associated with a biome and are used to create different colored frogs in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Frog
 */
@Serializable
data class FrogVariant(
	@Transient
	override var fileName: String = "frog_variant",
	/** The texture asset to use for the frog variant. */
	var assetId: ModelArgument,
	/** The spawn conditions for this frog variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList()
) : Generator("frog_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the spawn conditions for this frog variant. */
fun FrogVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply { spawnConditions = SpawnConditions().apply(block).entries }

/**
 * Create and register a frog variant in this [DataPack].
 *
 * Produces `data/<namespace>/frog_variant/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Frog#Variants
 */
fun DataPack.frogVariant(
	fileName: String = "frog_variant",
	assetId: ModelArgument,
	spawnConditions: List<VariantSpawnEntry> = emptyList(),
	init: FrogVariant.() -> Unit = {},
): ModelArgument {
	val frogVariant = FrogVariant(fileName, assetId, spawnConditions).apply(init)
	frogVariants += frogVariant
	return ModelArgument(fileName, frogVariant.namespace ?: name)
}
