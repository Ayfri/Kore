package io.github.ayfri.kore.events

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.timeCheck
import io.github.ayfri.kore.features.predicates.conditions.weatherCheck
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.world.World
import io.github.ayfri.kore.commands.function as functionCommand

private val initializedWorldState = mutableSetOf<String>()
private val initializedWorldDispatchers = mutableSetOf<String>()

private fun DataPack.ensureWorldStateSetup() {
	if (name in initializedWorldState) return
	initializedWorldState += name

	load(OopConstants.worldStateInitFunction) {
		scoreboard {
			objectives {
				add(OopConstants.worldStateObjective, ScoreboardCriteria.DUMMY)
			}
		}
	}
}

/** Calls the [handlers] tag, scoping the call to [dimension] when one is set. */
private fun Function.callHandlers(handlers: FunctionTagArgument, dimension: DimensionArgument?) {
	if (dimension == null) functionCommand(handlers)
	else execute {
		inDimension(dimension)
		run { functionCommand(handlers) }
	}
}

/**
 * Registers a world [event] handler [block] (added to a per-event function tag) and, once per datapack/dimension,
 * a [dispatcher] that calls that tag from the `tick` or `load` tag.
 */
private fun DataPack.registerWorldEvent(
	event: String,
	dimension: DimensionArgument?,
	hashCode: Int,
	intoLoad: Boolean,
	needsState: Boolean,
	block: Function.() -> Unit,
	dispatcher: Function.(handlers: FunctionTagArgument, tagName: String) -> Unit,
): FunctionArgument {
	val ns = name
	val tagName = OopConstants.worldEventTag(event, dimension?.asId())
	val handlers = FunctionTagArgument(tagName, ns)
	val handlerFn = addHandler(tagName, ns, OopConstants.eventHandlerName(tagName, hashCode), block)

	val key = "$name:$tagName"
	if (key !in initializedWorldDispatchers) {
		initializedWorldDispatchers += key
		if (needsState) ensureWorldStateSetup()

		val dispatchName = OopConstants.dispatchFunctionName(tagName)
		val body: Function.() -> Unit = { dispatcher(handlers, tagName) }
		if (intoLoad) load(dispatchName, block = body) else tick(dispatchName, block = body)
	}

	return handlerFn
}

/** Edge-triggered dispatcher: fires the handlers when [predicateBlock] crosses from off to on ([rising]) or on to off. */
private fun edgeDispatcher(
	rising: Boolean,
	dimension: DimensionArgument?,
	predicateBlock: Predicate.() -> Unit,
): Function.(FunctionTagArgument, String) -> Unit = { handlers, tagName ->
	val obj = OopConstants.worldStateObjective
	val now = literal(OopConstants.worldStateScore(tagName, "now"))
	val prev = literal(OopConstants.worldStateScore(tagName, "prev"))

	execute {
		storeResult { score(now, obj) }
		ifCondition { predicate(predicateBlock) }
	}
	execute {
		ifCondition {
			score(now, obj, rangeOrInt(if (rising) 1 else 0))
			score(prev, obj, rangeOrInt(if (rising) 0 else 1))
		}
		dimension?.let { inDimension(it) }
		run { functionCommand(handlers) }
	}
	scoreboard {
		players {
			operation(prev, obj, Operation.SET, now, obj)
		}
	}
}

/** Runs [block] the tick daytime begins (dawn, `daytime` entering `0..11999`), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onDayStart(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.dayStartEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = true, dimension) { timeCheck(0f..11999f) })

/**
 * Runs [block] every [period] ticks, backed by a scoreboard counter so it is unaffected by the day/night cycle being frozen.
 * Scoped to this world's dimension when set.
 */
context(dp: DataPack)
fun World.onInterval(period: TimeNumber, block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.intervalEvent,
		dimension,
		block.hashCode(),
		intoLoad = false,
		needsState = true,
		block
	) { handlers, tagName ->
		val obj = OopConstants.worldStateObjective
		val counter = literal(OopConstants.worldStateScore(tagName, "counter"))
		val target = period.inTicks().value.toInt()

		scoreboard { players { add(counter, obj, 1) } }
		execute {
			ifCondition { score(counter, obj, rangeOrIntStart(target)) }
			dimension?.let { inDimension(it) }
			run { functionCommand(handlers) }
		}
		execute {
			ifCondition { score(counter, obj, rangeOrIntStart(target)) }
			run { scoreboard { players { set(counter, obj, 0) } } }
		}
	}

/** Runs [block] once on datapack load / `/reload`, scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onLoad(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.worldLoadEvent,
		dimension,
		block.hashCode(),
		intoLoad = true,
		needsState = false,
		block
	) { handlers, _ ->
		callHandlers(handlers, dimension)
	}

/** Runs [block] the tick it turns midnight (`daytime` reaches `18000`), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onMidnight(block: Function.() -> Unit) = onTimeOfDay(18000, block)

/** Runs [block] the tick night begins (`daytime` entering `13000..23999`), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onNightStart(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.nightStartEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = true, dimension) { timeCheck(13000f..23999f) })

/** Runs [block] the tick it turns noon (`daytime` reaches `6000`), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onNoon(block: Function.() -> Unit) = onTimeOfDay(6000, block)

/** Runs [block] the tick precipitation starts (rain or thunder), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onRainStart(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.rainStartEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = true, dimension) { weatherCheck(raining = true) })

/** Runs [block] the tick precipitation stops, scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onRainStop(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.rainStopEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = false, dimension) { weatherCheck(raining = true) })

/** Runs [block] the tick a thunderstorm starts, scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onThunderStart(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.thunderStartEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = true, dimension) { weatherCheck(thundering = true) })

/** Runs [block] the tick a thunderstorm stops, scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onThunderStop(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.thunderStopEvent, dimension, block.hashCode(), intoLoad = false, needsState = true, block,
		edgeDispatcher(rising = false, dimension) { weatherCheck(thundering = true) })

/** Runs [block] every tick (20 times per second), scoped to this world's dimension when set. */
context(dp: DataPack)
fun World.onTick(block: Function.() -> Unit) =
	dp.registerWorldEvent(
		OopConstants.worldTickEvent,
		dimension,
		block.hashCode(),
		intoLoad = false,
		needsState = false,
		block
	) { handlers, _ ->
		callHandlers(handlers, dimension)
	}

/**
 * Runs [block] the tick `daytime` reaches [time] (`0..23999`, e.g. `6000` noon, `18000` midnight).
 *
 * Fires once per day under a normal cycle; a `/time set` that skips past [time] will skip that day.
 * Scoped to this world's dimension when set.
 */
context(dp: DataPack)
fun World.onTimeOfDay(time: Int, block: Function.() -> Unit) =
	dp.registerWorldEvent(
		"${OopConstants.timeOfDayEvent}_$time",
		dimension,
		block.hashCode(),
		intoLoad = false,
		needsState = false,
		block
	) { handlers, _ ->
		execute {
			ifCondition { predicate { timeCheck(time) } }
			dimension?.let { inDimension(it) }
			run { functionCommand(handlers) }
		}
	}
