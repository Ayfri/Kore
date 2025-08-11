package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load

/**
 * Utilities to schedule and manage delayed or repeating tasks inside a DataPack.
 *
 * The scheduler system lets you schedule a single run after an optional `delay`,
 * or a repeating task using `period`. When a repeating schedule is required the
 * manager generates a wrapper function which re-schedules itself.
 *
 * Documentation: https://kore.ayfri.com/docs/helpers/scheduler
 *
 * @param function - The function to execute (can be an existing function reference or a generated function)
 * @param delay - Optional initial delay before first execution
 * @param period - Optional repeat period; when non-null the scheduler will repeat the execution at this interval
 */
data class Scheduler(
	/** The function to execute (can be an existing function reference or a generated function) */
	val function: FunctionArgument,
	/** Optional initial delay before first execution */
	var delay: TimeNumber? = null,
	/** Optional repeat period; when non-null the scheduler will repeat the execution at this interval */
	var period: TimeNumber? = null
) {
    /** When a repeating schedule is created, a generated wrapper function is stored here. */
    internal var generatedFunction: FunctionArgument? = null

	/**
	 * Build and emit the necessary function(s) and `schedule` commands to run
	 * this scheduled task according to its `delay` and `period` configuration.
	 *
	 * This runs in the context of an internal `Function` writer so it can
	 * generate any wrapper functions and schedule commands required by Minecraft.
	 */
	context(fn: Function)
	fun execute() {
		val generatedFunction = fn.datapack.generatedFunction("scheduler_${hashCode()}") {
			if (function is Function) lines += function.lines else fn.function(function)
			if (period != null) fn.schedule(asId()).replace(period!!)
		}

		when {
			delay == null && period == null -> if (function is Function) fn.lines += function.lines else fn.function(function)
			delay != null && period == null -> fn.schedule(function).replace(delay!!)
			delay == null && period != null -> fn.function(generatedFunction)
			else -> fn.schedule(generatedFunction).replace(delay!!)
		}

		if (period != null) this.generatedFunction = generatedFunction
	}
}

/**
 * Cancels a repeating `Scheduler` by clearing its scheduled generated function.
 *
 * This is executed in the context of a `Function` so it can emit the required
 * `schedule ... clear` game command.
 */
data class UnScheduler(private val scheduler: Scheduler) {
	/**
	 * Clear the generated schedule for the provided `Scheduler` if present.
	 *
	 * This emits a `schedule <function> clear` command inside the current
	 * `Function` context to stop repeating tasks.
	 */
	context(fn: Function)
	fun execute() {
		if (scheduler.generatedFunction == null) return
		fn.schedule(scheduler.generatedFunction!!).clear()
	}
}

/**
 * Manages a collection of `Scheduler` instances for a given `DataPack`.
 *
 * Use the `schedulerManager` extension on `DataPack` to create and configure
 * schedulers. The manager will emit a `scheduler_setup` load function that
 * registers the configured schedules when the datapack is loaded.
 *
 * The manager supports adding schedulers by block (generating a function),
 * by name/namespace or by `FunctionArgument`, and provides methods to remove
 * or clear schedules, and to unschedule running repeating tasks.
 */
data class SchedulerManager(private val dp: DataPack) {
    /** When true the generated load function will include debug markers. */
    var debug = false

    /** The list of currently configured schedulers for this DataPack. */
    val schedulers = mutableListOf<Scheduler>()

	private val fn = object : Function("", "", "", dp) {}

	/**
	* Add a scheduler backed by a newly generated function built from [block].
	* The generated function is stored and scheduled according to [delay] and [period]. Returns the created [Scheduler].
	*/
	fun addScheduler(delay: TimeNumber? = null, period: TimeNumber? = null, block: Function.() -> Command) =
		Scheduler(dp.generatedFunction("scheduler_${block.hashCode()}") { block() }, delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	/**
	* Add a scheduler that references an existing function by name/namespace.
	*/
	fun addScheduler(function: String, namespace: String = dp.name, delay: TimeNumber? = null, period: TimeNumber? = null) =
		Scheduler(FunctionArgument(function, namespace), delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	/**
	* Add a scheduler that references an existing [FunctionArgument].
	*/
	fun addScheduler(function: FunctionArgument, delay: TimeNumber? = null, period: TimeNumber? = null) =
		Scheduler(function, delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	/** Remove all configured schedulers from this manager (does not unschedule running tasks). */
	fun clear() = schedulers.clear()

	/** Remove schedulers that reference the provided function argument. */
	fun removeScheduler(function: FunctionArgument) = schedulers.removeIf { it.function.asId() == function.asId() }

	/** Remove schedulers that reference the function identified by name/namespace. */
	fun removeScheduler(function: String, namespace: String = dp.name) =
		schedulers.removeIf { it.function.asId() == FunctionArgument(function, namespace).asId() }

	/**
	 * Unschedule (cancel) the repeating scheduler that references [function], if any.
	 * This emits the required `schedule ... clear` command inside the current `Function` context.
	 */
	context(fn: Function)
	fun unSchedule(function: FunctionArgument) =
		schedulers.find { it.function.asId() == function.asId() }?.let { UnScheduler(it).execute() }

	/**
	 * Unschedule by function name/namespace.
	 * This emits the required `schedule ... clear` command inside the current `Function` context.
	 */
	context(fn: Function)
	fun unSchedule(function: String, namespace: String = dp.name) = unSchedule(FunctionArgument(function, namespace))

	/**
	 * Unschedule all repeating schedulers by clearing their generated schedules.
	 * This emits the required `schedule ... clear` command inside the current `Function` context.
	 */
	context(fn: Function)
	fun unScheduleAll() = schedulers.filter { it.period != null }.forEach { UnScheduler(it).execute() }

	/**
	 * Emit the `scheduler_setup` load function which contains all the generated
	 * schedule commands for this manager. Call this when you want the schedules
	 * to be registered on datapack load.
	 */
	fun run() = dp.load("scheduler_setup") {
		if (debug) startDebug()
		lines += fn.lines
		if (debug) endDebug()
	}
}


/** Convenience factory returning a `SchedulerManager` bound to this `DataPack`. */
fun DataPack.schedulerManager() = SchedulerManager(this)

/**
 * Create and configure a `SchedulerManager` for this `DataPack` and immediately emit its setup function into the datapack load tag.
 * Documentation: https://kore.ayfri.com/docs/helpers/scheduler
 *
 * Example:
 * ```kotlin
 * datapack {
 *     schedulerManager {
 *         addScheduler(period = 3.seconds) {
 *             say("Hello")
 *         }
 *     }
 * }
 * ```
 */
fun DataPack.schedulerManager(block: SchedulerManager.() -> Unit) = SchedulerManager(this).apply(block).run()
