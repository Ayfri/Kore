package helpers

import DataPack
import arguments.Argument
import arguments.numbers.TimeNumber
import commands.Command
import commands.function
import commands.schedule
import functions.Function
import functions.generatedFunction
import functions.load

data class Scheduler(val function: Argument.Function, var delay: TimeNumber? = null, var period: TimeNumber? = null) {
	internal var generatedFunction: Argument.Function? = null

	context(Function)
	fun execute() {
		val generatedFunction = datapack.generatedFunction("scheduler_${hashCode()}") {
			if (function is Function) lines += function.lines else function(function)
			if (period != null) schedule(asId()).replace(period!!)
		}

		when {
			delay == null && period == null -> if (function is Function) lines += function.lines else function(function)
			delay != null && period == null -> schedule(function).replace(delay!!)
			delay == null && period != null -> function(generatedFunction)
			else -> schedule(generatedFunction).replace(delay!!)
		}

		if (period != null) this.generatedFunction = generatedFunction
	}
}

data class UnScheduler(private val scheduler: Scheduler) {
	context(Function)
	fun execute() {
		if (scheduler.generatedFunction == null) return
		schedule(scheduler.generatedFunction!!).clear()
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
		Scheduler(Argument.Function(function, namespace), delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	fun addScheduler(function: Argument.Function, delay: TimeNumber? = null, period: TimeNumber? = null) =
		Scheduler(function, delay, period).also {
			with(fn) { it.execute() }
			schedulers.add(it)
		}

	fun clear() = schedulers.clear()

	fun removeScheduler(function: Argument.Function) = schedulers.removeIf { it.function.asId() == function.asId() }
	fun removeScheduler(function: String, namespace: String = dp.name) =
		schedulers.removeIf { it.function.asId() == Argument.Function(function, namespace).asId() }

	context(Function)
	fun unSchedule(function: Argument.Function) =
		schedulers.find { it.function.asId() == function.asId() }?.let { UnScheduler(it).execute() }

	context(Function)
	fun unSchedule(function: String, namespace: String = dp.name) = unSchedule(Argument.Function(function, namespace))

	context(Function)
	fun unScheduleAll() = schedulers.filter { it.period != null }.forEach { UnScheduler(it).execute() }

	fun run() = dp.load("scheduler_setup") {
		if (debug) startDebug()
		lines += fn.lines
		if (debug) endDebug()
	}
}

fun DataPack.schedulerManager() = SchedulerManager(this)
fun DataPack.schedulerManager(block: SchedulerManager.() -> Unit) = SchedulerManager(this).apply(block).run()
