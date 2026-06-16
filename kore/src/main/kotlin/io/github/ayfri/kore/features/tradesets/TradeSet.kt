package io.github.ayfri.kore.features.tradesets

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.VillagerTradeOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.TradeSetArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven trade set definition.
 *
 * A trade set groups a list of villager trade references with a count provider controlling how many
 * trades are drawn from the set when a villager gains a new level, and an optional random sequence
 * for reproducible sampling.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Trade_set_definition
 */
@Serializable
data class TradeSet(
	@Transient
	override var fileName: String = "trade_set",
	var allowDuplicates: Boolean? = null,
	var amount: NumberProvider,
	var randomSequence: RandomSequenceArgument? = null,
	var trades: InlinableList<VillagerTradeOrTagArgument>,
) : Generator("trade_set") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a trade set using a builder block.
 *
 * Produces `data/<namespace>/trade_set/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Trade_set_definition
 */
fun DataPack.tradeSet(
	fileName: String = "trade_set",
	trades: InlinableList<VillagerTradeOrTagArgument>,
	amount: NumberProvider = constant(1f),
	init: TradeSet.() -> Unit = {},
): TradeSetArgument {
	val tradeSet = TradeSet(fileName = fileName, amount = amount, trades = trades).apply(init)
	tradeSets += tradeSet
	return TradeSetArgument(fileName, tradeSet.namespace ?: name)
}
