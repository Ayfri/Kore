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

data class Scheduler(val function: FunctionArgument, var delay: TimeNumber? = null, var period: TimeNumber? = null) {
	internal var generatedFunction: FunctionArgument? = null

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

data class UnScheduler(private val scheduler: Scheduler) {
	context(fn: Function)
	fun execute() {
		if (scheduler.generatedFunction == null) return
		fn.schedule(scheduler.generatedFunction!!).clear()
	}
}

data class SchedulerManager(private val dp: DataPack) {
	var debug = false
	val schedulers = mutableListOf<Scheduler>()

	private val fn = object : Function("", "", "", dp) {}

	fun addScheduler(delay: TimeNumber? = null, period: TimeNumber? = null, block: Function.() -> Command) =
		Scheduler(dp.generatedFunction("scheduler_${block.hashCode()}") { block() }, delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	fun addScheduler(function: String, namespace: String = dp.name, delay: TimeNumber? = null, period: TimeNumber? = null) =
		Scheduler(FunctionArgument(function, namespace), delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	fun addScheduler(function: FunctionArgument, delay: TimeNumber? = null, period: TimeNumber? = null) =
		Scheduler(function, delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	fun clear() = schedulers.clear()

	fun removeScheduler(function: FunctionArgument) = schedulers.removeIf { it.function.asId() == function.asId() }
	fun removeScheduler(function: String, namespace: String = dp.name) =
		schedulers.removeIf { it.function.asId() == FunctionArgument(function, namespace).asId() }

	context(fn: Function)
	fun unSchedule(function: FunctionArgument) =
		schedulers.find { it.function.asId() == function.asId() }?.let { UnScheduler(it).execute() }

	context(fn: Function)
	fun unSchedule(function: String, namespace: String = dp.name) = unSchedule(FunctionArgument(function, namespace))

	context(fn: Function)
	fun unScheduleAll() = schedulers.filter { it.period != null }.forEach { UnScheduler(it).execute() }

	fun run() = dp.load("scheduler_setup") {
		if (debug) startDebug()
		lines += fn.lines
		if (debug) endDebug()
	}
}

fun DataPack.schedulerManager() = SchedulerManager(this)
fun DataPack.schedulerManager(block: SchedulerManager.() -> Unit) = SchedulerManager(this).apply(block).run()
