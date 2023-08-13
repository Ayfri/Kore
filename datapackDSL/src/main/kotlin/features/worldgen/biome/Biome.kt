package features.worldgen.biome

import DataPack
import Generator
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.worldgen.BiomeArgument
import features.worldgen.biome.types.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class Biome(
	@Transient
	override var fileName: String = "biome",
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
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.biome(fileName: String = "biome", init: Biome.() -> Unit = {}): BiomeArgument {
	biomes += Biome(fileName).apply(init)
	return BiomeArgument(fileName, name)
}

fun Biome.effects(init: BiomeEffects.() -> Unit) {
	effects = BiomeEffects().apply(init)
}

fun Biome.spawners(init: Spawners.() -> Unit) {
	spawners = Spawners().apply(init)
}

fun Biome.spawnCosts(init: MutableMap<EntityTypeArgument, SpawnCost>.() -> Unit) {
	spawnCosts = buildMap(init)
}

fun MutableMap<EntityTypeArgument, SpawnCost>.spawnCost(type: EntityTypeArgument, energyBudget: Float, charge: Float) {
	this[type] = SpawnCost(energyBudget, charge)
}

fun Biome.carvers(init: Carvers.() -> Unit) {
	carvers = Carvers().apply(init)
}

fun Biome.features(init: Features.() -> Unit) {
	features = Features().apply(init)
}
