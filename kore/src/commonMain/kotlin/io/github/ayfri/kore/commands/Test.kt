package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.TestSelectorArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.TestInstanceArgument

/**
 * DSL scope for the `/test` command.
 *
 * `/test` manages and runs GameTests. Kore exposes the common commands for clearing, creating,
 * locating, running, and verifying tests.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
 */
class Test(val fn: Function) {

	/**
	 * Clears every test structure within [radius]. The default radius is 200 blocks.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun clearAll(radius: Int? = null) =
		fn.addLine(command("test", literal("clearall"), int(radius)))

	/**
	 * Clears the test structures pointed at by the crosshair.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun clearThat() =
		fn.addLine(command("test", literal("clearthat")))

	/**
	 * Clears all test structures within the default large radius.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun clearThese() =
		fn.addLine(command("test", literal("clearthese")))

	/**
	 * Creates a test structure for [test]. Width defaults to 5, and height and depth default to
	 * width.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun create(
		test: TestInstanceArgument,
		width: Int? = null,
		height: Int? = null,
		depth: Int? = null,
	) = fn.addLine(command("test", literal("create"), test, int(width), int(height), int(depth)))

	/**
	 * Locates the loaded chunks that contain [selector].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun locate(selector: TestSelectorArgument) =
		fn.addLine(command("test", literal("locate"), selector))

	/**
	 * Prints the local position helper for the block under the crosshair.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun pos(variable: String? = null) =
		fn.addLine(command("test", literal("pos"), literal(variable)))

	/**
	 * Resets the closest test structure.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun resetClosest() =
		fn.addLine(command("test", literal("resetclosest")))

	/**
	 * Resets the test structure pointed at by the crosshair.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun resetThat() =
		fn.addLine(command("test", literal("resetthat")))

	/**
	 * Resets all test structures within the default radius.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun resetThese() =
		fn.addLine(command("test", literal("resetthese")))

	/**
	 * Runs [selector] with the given repetition and layout options.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
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

	/**
	 * Runs the closest test, optionally repeating it and changing the layout.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
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

	/**
	 * Runs the test under the crosshair, optionally repeating it and changing the layout.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
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

	/**
	 * Runs [selector] multiple times in the same batch.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun runMultiple(selector: TestSelectorArgument, amount: Int? = null) =
		fn.addLine(command("test", literal("runmultiple"), selector, int(amount)))

	/**
	 * Runs the test under the crosshair multiple times.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun runThat(numberOfTimes: Int? = null, untilFailed: Boolean? = null) =
		fn.addLine(command("test", literal("runthat"), int(numberOfTimes), bool(untilFailed)))

	/**
	 * Runs all tests within the default radius multiple times.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun runThese(numberOfTimes: Int? = null, untilFailed: Boolean? = null) =
		fn.addLine(command("test", literal("runthese"), int(numberOfTimes), bool(untilFailed)))

	/**
	 * Stops all active tests.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun stop() =
		fn.addLine(command("test", literal("stop")))

	/**
	 * Verifies the given tests as a list.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun verify(tests: List<TestInstanceArgument>) =
		fn.addLine(command("test", literal("verify"), *tests.toTypedArray()))

	/**
	 * Verifies the given tests.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/test)
	 */
	fun verify(vararg tests: TestInstanceArgument) =
		fn.addLine(command("test", literal("verify"), *tests))
}

/** Opens the [Test] DSL. */
fun Function.test(block: Test.() -> Command) = Test(this).block()

/** Returns the reusable [Test] DSL. */
fun Function.test() = Test(this)
