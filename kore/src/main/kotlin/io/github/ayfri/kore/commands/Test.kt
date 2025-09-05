package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.TestSelectorArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.TestInstanceArgument

class Test(val fn: Function) {

	fun clearAll(radius: Int? = null) =
		fn.addLine(command("test", literal("clearall"), int(radius)))

	fun clearThat() =
		fn.addLine(command("test", literal("clearthat")))

	fun clearThese() =
		fn.addLine(command("test", literal("clearthese")))

	fun create(
		test: TestInstanceArgument,
		width: Int? = null,
		height: Int? = null,
		depth: Int? = null,
	) = fn.addLine(command("test", literal("create"), test, int(width), int(height), int(depth)))

	fun locate(selector: TestSelectorArgument) =
		fn.addLine(command("test", literal("locate"), selector))

	fun pos(variable: String? = null) =
		fn.addLine(command("test", literal("pos"), literal(variable)))

	fun resetClosest() =
		fn.addLine(command("test", literal("resetclosest")))

	fun resetThat() =
		fn.addLine(command("test", literal("resetthat")))

	fun resetThese() =
		fn.addLine(command("test", literal("resetthese")))

	fun run(
		selector: TestSelectorArgument,
		numberOfTimes: Int? = null,
		untilFailed: Boolean? = null,
		rotationSteps: Int? = null,
		testsPerRow: Int? = null,
	) = fn.addLine(
		command(
			"test",
			literal("run"),
			selector,
			int(numberOfTimes),
			bool(untilFailed),
			int(rotationSteps),
			int(testsPerRow)
		)
	)

	fun runClosest(
		numberOfTimes: Int? = null,
		untilFailed: Boolean? = null,
		rotationSteps: Int? = null,
		testsPerRow: Int? = null,
	) = fn.addLine(
		command(
			"test",
			literal("runclosest"),
			int(numberOfTimes),
			bool(untilFailed),
			int(rotationSteps),
			int(testsPerRow)
		)
	)

	fun runFailed(
		numberOfTimes: Int? = null,
		untilFailed: Boolean? = null,
		rotationSteps: Int? = null,
		testsPerRow: Int? = null,
	) = fn.addLine(
		command(
			"test",
			literal("runfailed"),
			int(numberOfTimes),
			bool(untilFailed),
			int(rotationSteps),
			int(testsPerRow)
		)
	)

	fun runMultiple(selector: TestSelectorArgument, amount: Int? = null) =
		fn.addLine(command("test", literal("runmultiple"), selector, int(amount)))

	fun runThat(numberOfTimes: Int? = null, untilFailed: Boolean? = null) =
		fn.addLine(command("test", literal("runthat"), int(numberOfTimes), bool(untilFailed)))

	fun runThese(numberOfTimes: Int? = null, untilFailed: Boolean? = null) =
		fn.addLine(command("test", literal("runthese"), int(numberOfTimes), bool(untilFailed)))

	fun stop() =
		fn.addLine(command("test", literal("stop")))

	fun verify(tests: List<TestInstanceArgument>) =
		fn.addLine(command("test", literal("verify"), *tests.toTypedArray()))

	fun verify(vararg tests: TestInstanceArgument) =
		fn.addLine(command("test", literal("verify"), *tests))
}

fun Function.test(block: Test.() -> Command) = Test(this).block()
fun Function.test() = Test(this)
