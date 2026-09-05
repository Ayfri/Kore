package io.github.ayfri.kore.features.tradesets

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.uniform
import io.github.ayfri.kore.generated.arguments.VillagerTradeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.TradeSetArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition of the pool a merchant draws its offers from, [amount] trades being rolled out of [trades]
 * each time a villager unlocks a level or a wandering trader spawns.
 *
 * Produces `data/<namespace>/trade_set/<fileName>.json`. A [fileName] holding slashes lands in subfolders, the way
 * vanilla groups its sets per profession and level (`farmer/level_1`).
 *
 * The game only reads the sets it knows about, so a set under a new name is dead data until something referencing
 * it, such as an overridden `minecraft:farmer/level_1`, points at it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Trade_set
 *
 * @property fileName The name of the generated file, slashes creating subfolders.
 * @property allowDuplicates Whether one trade can be rolled several times, the same offer then showing up twice.
 * @property amount How many trades are rolled out of the set, `1` by default.
 * @property randomSequence The random sequence the rolls are seeded from, the rolls being non-deterministic when unset.
 * @property trades The trades and trade tags rolled from, a lone entry serializing as a plain string.
 */
@Serializable
data class TradeSet(
	@Transient
	override var fileName: String = "trade_set",
	var allowDuplicates: Boolean? = null,
	var amount: NumberProvider = constant(1f),
	var randomSequence: RandomSequenceArgument? = null,
	var trades: InlinableList<VillagerTradeOrTagArgument> = emptyList(),
) : Generator("trade_set") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Registers a trade set named [fileName], slashes creating subfolders.
 *
 * ```kotlin
 * tradeSet("farmer/level_1") {
 *     trades(wheatForEmerald, Tags.VillagerTrade.Farmer.LEVEL_1)
 *     amount(2f)
 *     allowDuplicates = false
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Trade_set
 */
fun DataPack.tradeSet(fileName: String = "trade_set", init: TradeSet.() -> Unit = {}): TradeSetArgument {
	val tradeSet = TradeSet(fileName = fileName).apply(init)
	tradeSets += tradeSet
	return TradeSetArgument(fileName, tradeSet.namespace ?: name)
}

/** Rolls exactly [value] trades out of the set. */
fun TradeSet.amount(value: Float) {
	amount = constant(value)
}

/** Rolls between [min] and [max] trades out of the set, both included. */
fun TradeSet.amount(min: Float, max: Float) {
	amount = uniform(min, max)
}

/** Seeds the rolls from the random sequence [name], making them reproducible. */
fun TradeSet.randomSequence(name: String, namespace: String = "minecraft") {
	randomSequence = RandomSequenceArgument(name, namespace)
}

/** Appends [trade] to the trades rolled from. */
fun TradeSet.trade(trade: VillagerTradeOrTagArgument) {
	trades += trade
}

/** Sets the trades rolled from to [trades], replacing the ones already there. */
fun TradeSet.trades(vararg trades: VillagerTradeOrTagArgument) {
	this.trades = trades.toList()
}

/** Sets the trades rolled from to [trades], replacing the ones already there. */
fun TradeSet.trades(trades: List<VillagerTradeOrTagArgument>) {
	this.trades = trades
}
