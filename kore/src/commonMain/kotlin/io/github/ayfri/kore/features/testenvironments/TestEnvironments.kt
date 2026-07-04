package io.github.ayfri.kore.features.testenvironments

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.testenvironments.types.*
import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.generated.arguments.types.TestEnvironmentArgument
import io.github.ayfri.kore.generated.arguments.types.TimelineArgument
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import io.github.ayfri.kore.features.testenvironments.enums.Weather as WeatherEnum

/**
 * DSL entry point for creating GameTest test environments.
 *
 * Test environments define the preconditions and context for automated test execution
 * in Minecraft's GameTest framework. They group test instances together and ensure
 * consistent environmental conditions across test runs.
 */
fun DataPack.testEnvironments(init: TestEnvironmentsBuilder.() -> Unit) {
	TestEnvironmentsBuilder(this).apply(init)
}

/**
 * Builder reference for external use in GameTest scenarios.
 *
 * Allows creating test environments outside of the main `testEnvironments` block,
 * useful for reusing environments across multiple test instance definitions.
 */
val DataPack.testEnvironmentsBuilder get() = TestEnvironmentsBuilder(this)

/**
 * Builder class for creating GameTest test environments using Kotlin DSL syntax.
 *
 * Provides methods to create different types of test environments that control
 * the execution context for automated tests in the GameTest framework.
 */
class TestEnvironmentsBuilder(private val dataPack: DataPack) {

	/**
	 * Creates a composite environment that combines multiple test environments.
	 *
	 * This allows applying multiple environmental conditions simultaneously,
	 * such as combining weather settings with game rule modifications and time changes.
	 *
	 * @param fileName The name of the combined environment file
	 * @param environments The test environments to combine into one
	 * @return TestEnvironmentArgument representing the composite environment
	 */
	fun allOf(
		fileName: String = "combined_environment",
		vararg environments: TestEnvironmentArgument
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, AllOf(environments.toList()))

	/**
	 * Creates a function-based test environment with setup and teardown procedures.
	 *
	 * Function environments allow executing specific datapack functions before and after
	 * test execution, enabling complex world state preparation and cleanup procedures.
	 *
	 * @param fileName The name of the function environment file
	 * @param init Configuration block for defining setup and teardown functions
	 * @return TestEnvironmentArgument representing the function environment
	 */
	fun function(
		fileName: String = "function_environment",
		init: FunctionEnvironmentBuilder.() -> Unit
	): TestEnvironmentArgument {
		val builder = FunctionEnvironmentBuilder().apply(init)
		return dataPack.testEnvironment(fileName, builder.build())
	}

	/**
	 * Creates a game rules-based test environment for controlled testing conditions.
	 *
	 * Game rules environments allow setting specific game rules to create predictable
	 * test conditions, such as disabling mob spawning, controlling daylight cycles,
	 * or adjusting game mechanics for consistent test results.
	 *
	 * @param fileName The name of the game rules environment file
	 * @param init Configuration block for setting boolean and integer game rules
	 * @return TestEnvironmentArgument representing the game rules environment
	 */
	fun gameRules(
		fileName: String = "gamerules_environment",
		init: GameRulesBuilder.() -> Unit
	): TestEnvironmentArgument {
		val builder = GameRulesBuilder().apply(init)
		return dataPack.testEnvironment(fileName, builder.build())
	}

	/**
	 * Creates a clock-time test environment that sets a specific world clock to a given tick value.
	 *
	 * Clock-time environments ensure tests run at a consistent time on the specified world clock,
	 * important for testing time-dependent mechanics like mob spawning or daylight sensor behaviour.
	 *
	 * @param fileName The name of the clock-time environment file
	 * @param clock The world clock resource to set
	 * @param time The time in ticks to set on the clock (0–24000, where 6000 = noon, 18000 = midnight)
	 * @return TestEnvironmentArgument representing the clock-time environment
	 */
	fun clockTime(
		fileName: String = "clock_time_environment",
		clock: WorldClockArgument,
		time: Int,
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, ClockTimeEnvironment(clock, time))

	/**
	 * Creates a timeline attributes test environment that applies a set of timelines during test execution.
	 *
	 * Timeline attributes environments allow tests to run under specific timeline configurations,
	 * useful for testing time-driven mechanics driven by timeline definitions.
	 *
	 * @param fileName The name of the timeline attributes environment file
	 * @param timelines The timelines to activate during test execution
	 * @return TestEnvironmentArgument representing the timeline attributes environment
	 */
	fun timelineAttributes(
		fileName: String = "timeline_attributes_environment",
		vararg timelines: TimelineArgument,
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, TimelineAttributesEnvironment(timelines.toList()))

	/**
	 * Creates a weather-based test environment with specific weather conditions.
	 *
	 * Weather environments ensure consistent weather conditions during test execution,
	 * important for testing weather-dependent mechanics like lightning rods, crop growth,
	 * or mob behavior variations.
	 *
	 * @param fileName The name of the weather environment file
	 * @param weather The weather condition to maintain during test execution
	 * @return TestEnvironmentArgument representing the weather environment
	 */
	fun weather(
		fileName: String = "weather_environment",
		weather: WeatherEnum
	): TestEnvironmentArgument = dataPack.testEnvironment(fileName, Weather(weather))
}

/**
 * DSL builder for configuring function environments in the GameTest framework.
 *
 * Allows specifying setup and teardown functions that execute before and after
 * test instance runs, enabling complex world state preparation and cleanup.
 */
class FunctionEnvironmentBuilder {
	var setup: FunctionArgument? = null
	var teardown: FunctionArgument? = null

	fun setup(function: FunctionArgument) {
		setup = function
	}

	fun teardown(function: FunctionArgument) {
		teardown = function
	}

	internal fun build() = Function(setup, teardown)
}

/**
 * DSL builder for configuring game rules environments in the GameTest framework.
 *
 * Provides a type-safe way to set boolean and integer game rules for creating
 * controlled testing conditions. Rules are automatically sorted alphabetically
 * for consistent output.
 */
class GameRulesBuilder {
	private val rules = mutableMapOf<Gamerules, GameRuleValue>()

	operator fun set(rule: Gamerules.Boolean, value: Boolean) {
		rules[rule] = GameRuleBool(value)
	}

	operator fun set(rule: Gamerules.Int, value: Int) {
		rules[rule] = GameRuleInt(value)
	}

	internal fun build() = GameRules(
		rules = rules.toSortedMap(compareBy { it.name })
	)
}
