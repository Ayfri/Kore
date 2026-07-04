package io.github.ayfri.kore.features.worldgen.biome

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.biome.types.*
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven biome definition.
 *
 * Controls temperature, downfall, precipitation, visual effects (sky, fog, water), mob spawns,
 * terrain carvers, and the list of placed features that populate the biome.
 *
 * JSON format reference: https://minecraft.wiki/w/Biome_definition
 */
@Serializable
data class Biome(
	@Transient
	override var fileName: String = "biome",
	var attributes: EnvironmentAttributesScope? = null,
	var temperature: Float = 0.8f,
	var downfall: Float = 0.4f,
	var hasPrecipitation: Boolean = true,
	var temperatureModifier: TemperatureModifier? = null,
	var creatureSpawnProbability: Float? = null,
	var effects: BiomeEffects = BiomeEffects(),
	var spawners: Spawners = Spawners(),
	var spawnCosts: Map<EntityTypeArgument, SpawnCost> = mapOf(),
	var carvers: Carvers = Carvers(),
	var features: Features = Features(),
) : Generator("worldgen/biome") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a biome using a builder block.
 *
 * Configure effects, spawners, carvers, and features in the builder.
 *
 * Produces `data/<namespace>/worldgen/biome/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Biome_definition
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 */
fun DataPack.biome(fileName: String = "biome", init: Biome.() -> Unit = {}): BiomeArgument {
	val biome = Biome(fileName).apply(init)
	biomes += biome
	return BiomeArgument(fileName, biome.namespace ?: name)
}

fun Biome.attributes(init: EnvironmentAttributesScope.() -> Unit) {
	attributes = EnvironmentAttributesScope().apply(init)
}

fun Biome.carvers(init: Carvers.() -> Unit) {
	carvers = Carvers().apply(init)
}

fun Biome.effects(init: BiomeEffects.() -> Unit) {
	effects = BiomeEffects().apply(init)
}

fun Biome.features(init: Features.() -> Unit) {
	features = Features().apply(init)
}

fun Biome.spawnCosts(init: MutableMap<EntityTypeArgument, SpawnCost>.() -> Unit) {
	spawnCosts = buildMap(init)
}

fun MutableMap<EntityTypeArgument, SpawnCost>.spawnCost(type: EntityTypeArgument, energyBudget: Float, charge: Float) {
	this[type] = SpawnCost(energyBudget, charge)
}

fun Biome.spawners(init: Spawners.() -> Unit) {
	spawners = Spawners().apply(init)
}
