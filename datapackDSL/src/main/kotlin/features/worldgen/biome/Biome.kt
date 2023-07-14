package features.worldgen.biome

import DataPack
import Generator
import arguments.Argument
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
	var spawnCosts: Map<Argument.EntitySummon, SpawnCost> = mapOf(),
	var carvers: Carvers = Carvers(),
	var features: Features = Features(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.biome(fileName: String, init: Biome.() -> Unit = {}): Argument.Biome {
	biomes += Biome(fileName).apply(init)
	return Argument.Biome(fileName, name)
}

fun Biome.effects(init: BiomeEffects.() -> Unit) {
	effects = BiomeEffects().apply(init)
}

fun Biome.spawners(init: Spawners.() -> Unit) {
	spawners = Spawners().apply(init)
}

fun Biome.spawnCosts(init: MutableMap<Argument.EntitySummon, SpawnCost>.() -> Unit) {
	spawnCosts = buildMap(init)
}

fun MutableMap<Argument.EntitySummon, SpawnCost>.spawnCost(type: Argument.EntitySummon, energyBudget: Float, charge: Float) {
	this[type] = SpawnCost(energyBudget, charge)
}

fun Biome.carvers(init: Carvers.() -> Unit) {
	carvers = Carvers().apply(init)
}

fun Biome.features(init: Features.() -> Unit) {
	features = Features().apply(init)
}
